import React, { memo, useEffect, useRef } from 'react';
import styles from './GameCell.module.less';
import { Cell } from '../../types/Board';
import { useAppSelector } from '../../pages/single-player/singlePlayerStore';
import sock from '../../utils/GameSocket';

const GameCell: React.FC<Cell> = ({ value, rowIdx, colIdx }) => {
  const difficulty = useAppSelector((state) => state.difficulty);
  const coveredCellRef = useRef<HTMLButtonElement | null>(null);

  // Initial event listener setup
  useEffect(() => {
    const cell = coveredCellRef.current;

    // Subscribe to the onUncoverCell Event
    sock.addOnUncoverCell({ rowIdx, colIdx }, () => {
      if (cell && !cell.disabled) {
        cell.disabled = true;
        cell.classList.add(styles.tsCellCoverRemoved);
      }
    });

    // Add a listener to remove the uncovered cell button after all of the css transitions have
    // finished.
    let transitionRunListener: (e: TransitionEvent) => void = () => undefined;
    let transitionEndListener: (e: TransitionEvent) => void = () => undefined;
    if (cell) {
      // Count all of the transition starts so we know when to delete the button element on the
      // last transition end.
      let transitionRunCount = 0;
      let transitionEndCount = 0;
      transitionRunListener = () => transitionRunCount++;
      transitionEndListener = () => {
        transitionEndCount++;
        if (transitionRunCount === transitionEndCount) {
          cell.remove();
        }
      };
      cell.addEventListener('transitionrun', transitionRunListener);
      cell.addEventListener('transitionend', transitionEndListener);
    }
    return () => {
      cell?.removeEventListener('transitionrun', transitionRunListener);
      cell?.removeEventListener('transitionend', transitionEndListener);
      sock.removeOnUncoverCell({ rowIdx, colIdx });
    };
  }, [colIdx, rowIdx, value]);

  const handleOnClick: React.MouseEventHandler<HTMLButtonElement> = async () => {
    sock.sendJson({ rowIdx, colIdx });

    // currentTarget.disabled = true;
    // currentTarget.classList.add(styles.tsCellCoverRemoved);
  };

  return (
    <div className={styles[`tsCellContainer-${difficulty}`]}>
      <button ref={coveredCellRef} className={styles.tsCellCovered} onClick={handleOnClick} />
      <div className={styles.tsCellUncovered}>
        <div className={styles[value === 'x' ? 'mineCell' : `nearbyMineCell-${value}`]}>
          {!['0', 'x'].includes(value) && value}
        </div>
      </div>
    </div>
  );
};

export default memo(GameCell);
