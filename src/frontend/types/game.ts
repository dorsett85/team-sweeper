export interface Cell {
  rowIdx: number;
  colIdx: number;
  value: '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | 'x';
  covered: boolean;
  checked: boolean;
}

type GameStatus = 'IN_PROGRESS' | 'WON' | 'LOST';

export interface GameStart {
  id: number;
  difficulty: 'e' | 'm' | 'h';
  status: GameStatus;
  rows: number;
  cols: number;
  mines: number;
}

export interface GameEnd {
  status: Exclude<GameStatus, 'IN_PROGRESS'>;
  duration: number;
}
