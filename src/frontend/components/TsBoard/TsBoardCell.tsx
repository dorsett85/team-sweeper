import React from 'react';
import styles from './TsBoard.module.less';
import { TsCell } from '../../types/TsBoardResponse';
import { useAppSelector } from '../../pages/single-player/singlePlayerStore';

const TsBoardCell: React.FC<TsCell> = ({ value }) => {
  const difficulty = useAppSelector((state) => state.difficulty);

  const handleOnClick: React.MouseEventHandler<HTMLButtonElement> = ({ currentTarget }) => {
    currentTarget.disabled = true;
    currentTarget.classList.add(styles.tsCellCoverRemoved);
  };

  return (
    <div className={styles[`tsCellContainer-${difficulty}`]}>
      <button className={styles.tsCellCovered} onClick={handleOnClick} />
      <div className={styles.tsCellUncovered}>
        <div className={styles[value === 'x' ? 'mineCell' : `nearbyMineCell-${value}`]}>
          {!['0', 'x'].includes(value) && value}
        </div>
      </div>
    </div>
  );
};

export default TsBoardCell;
