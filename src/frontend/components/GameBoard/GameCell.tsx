import React, { memo } from 'react';
import styles from './GameCell.module.less';
import { Cell } from '../../types/Game';
import { useAppSelector } from '../../pages/single-player/singlePlayerStore';
import { useCoveredCell } from './useCoveredCell';

const GameCell: React.FC<Cell> = ({ value, rowIdx, colIdx }) => {
  const difficulty = useAppSelector((state) => state.difficulty);
  const coveredCell = useCoveredCell({ rowIdx, colIdx });

  return (
    <div className={styles[`cellContainer-${difficulty}`]}>
      <button {...coveredCell} />
      <div className={styles.cellUncovered}>
        <div className={styles[value === 'x' ? 'mineCell' : `nearbyMineCell-${value}`]}>
          {!['0', 'x'].includes(value) && value}
        </div>
      </div>
    </div>
  );
};

export default memo(GameCell);
