export interface Cell {
  rowIdx: number;
  colIdx: number;
  value: '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | 'x';
  covered: boolean;
}

export interface Game {
  id: number;
  difficulty: 'e' | 'm' | 'h';
  rows: number;
  cols: number;
  mines: number;
}
