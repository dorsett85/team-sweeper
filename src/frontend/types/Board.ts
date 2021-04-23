export interface Cell {
  rowIdx: number;
  colIdx: number;
  value: '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | 'x';
  covered: boolean;
}

export interface Board {
  cells2d: Cell[][];
  rows: number;
  cols: number;
  mines: number;
  nonMines: number;
  totalCells: number;
}