import { GameDifficulty } from './gamedifficulty';
import { GameStatus } from './gameStatus';

export interface Cell {
  rowIdx: number;
  colIdx: number;
  value: '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | 'x';
  covered: boolean;
  checked: boolean;
}

export interface GameStart {
  id: number;
  difficulty: GameDifficulty;
  status: GameStatus;
  rows: number;
  cols: number;
  mines: number;
}

export interface GameEnd {
  status: Exclude<GameStatus, 'IN_PROGRESS'>;
  duration: number;
}
