export interface TsCell {
  rowIdx: number;
  colIdx: number;
  value: '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | 'x';
  covered: boolean;
}

/**
 * Api response for getting a new board
 */
export interface TsBoardResponse {
  board: TsCell[][];
  rows: number;
  cols: number;
  mines: number;
  nonMines: number;
  totalCells: number;
}
