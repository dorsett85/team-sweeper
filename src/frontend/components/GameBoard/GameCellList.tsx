import React from 'react';

interface RenderCellConfig {
  rIdx: number;
  cIdx: number;
}

interface GameCellListProps {
  rows: number;
  cols: number;

  /**
   * Render a cell component given a row and column index
   */
  renderCell(config: RenderCellConfig): React.ReactElement;
}

const GameCellList: React.FC<GameCellListProps> = ({ rows, cols, renderCell }) => {
  const allCells: React.ReactElement[] = [];
  for (let r = 0; r < rows; r++) {
    for (let c = 0; c < cols; c++) {
      allCells.push(renderCell({ rIdx: r, cIdx: c }));
    }
  }
  return <>{allCells}</>;
};

export default GameCellList;
