import React, { useEffect, useRef } from 'react';
import styles from './TsBoard.module.less';
import { TsCell } from '../../types/TsBoardResponse';
import { useAppSelector } from '../../pages/single-player/singlePlayerStore';

const TsBoardCell: React.FC<TsCell> = ({ value }) => {
  const difficulty = useAppSelector((state) => state.difficulty);
  const coveredCellRef = useRef<HTMLButtonElement | null>(null);

  // Add a listener to remove the uncovered cell button after all of the css transitions have
  // finished
  useEffect(() => {
    const cell = coveredCellRef.current;
    let transitionRunListener: (e: TransitionEvent) => void = () => undefined;
    let transitionEndListener: (e: TransitionEvent) => void = () => undefined;
    if (cell) {
      // Count all of the transition starts so we know when to delete the button element on the
      // last transition end.
      let transitionRunCount = 0;
      let transitionEndCount = 0;
      transitionRunListener = (e) => transitionRunCount++;
      transitionEndListener = (e) => {
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
    };
  }, []);

  const handleOnClick: React.MouseEventHandler<HTMLButtonElement> = ({ currentTarget }) => {
    currentTarget.disabled = true;
    currentTarget.classList.add(styles.tsCellCoverRemoved);
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

export default TsBoardCell;
