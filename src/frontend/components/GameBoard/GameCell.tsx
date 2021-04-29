import React, { memo } from 'react';
import styles from './GameCell.module.less';
import { Cell, Game } from '../../types/Game';
import { useCoveredCell } from './useCoveredCell';

interface GameCellProps extends Pick<Cell, 'rowIdx' | 'colIdx'> {
  difficulty: Game['difficulty'];
}

const GameCell: React.FC<GameCellProps> = ({ difficulty, rowIdx, colIdx }) => {
  const { value, ...coveredCell } = useCoveredCell({ rowIdx, colIdx });

  return (
    <div className={styles[`cellContainer-${difficulty}`]}>
      <button {...coveredCell} />
      <div className={styles.cellUncovered}>
        {value && (
          <div className={styles[value === 'x' ? 'mineCell' : `nearbyMineCell-${value}`]}>
            {!['0', 'x'].includes(value) && value}
          </div>
        )}
      </div>
    </div>
  );
};

export default memo(GameCell);
