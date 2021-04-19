import React, { memo, useEffect, useRef } from 'react';
import styles from './TsBoardCell.module.less';
import { TsCell } from '../../types/TsBoardResponse';
import { useAppSelector } from '../../pages/single-player/singlePlayerStore';
import { fetchJson } from '../../utils/fetchJson';
import gameEvents from '../../utils/GameEvents';

const TsBoardCell: React.FC<TsCell> = ({ value, rowIdx, colIdx }) => {
  const difficulty = useAppSelector((state) => state.difficulty);
  const coveredCellRef = useRef<HTMLButtonElement | null>(null);

  // Initial event listener setup
  useEffect(() => {
    const cell = coveredCellRef.current;

    // Subscribe to the onUncoverCell Event
    gameEvents.addOnUncoverCell({ rowIdx, colIdx }, () => {
      if (cell && !cell.disabled) {
        cell.disabled = true;
        cell.classList.add(styles.tsCellCoverRemoved);
        if (value === '0') {
          gameEvents.uncoverNearbyCells({ rowIdx, colIdx });
        }
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
      gameEvents.removeOnUncoverCell({ rowIdx, colIdx });
    };
  }, []);

  const handleOnClick: React.MouseEventHandler<HTMLButtonElement> = async ({ currentTarget }) => {
    const data = await fetchJson(`/single-player/uncover-cell?rowIdx=${rowIdx}&colIdx=${colIdx}`, {
      method: 'put'
    });

    currentTarget.disabled = true;
    currentTarget.classList.add(styles.tsCellCoverRemoved);

    if (value === '0') {
      gameEvents.uncoverNearbyCells({ rowIdx, colIdx });
    } else if (value === 'x') {
      gameEvents.uncoverAllCells();
    }
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

export default memo(TsBoardCell);
