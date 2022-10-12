import { GameDifficulty } from './gameDifficulty';
import { GameStatus } from './gameStatus';

export interface Cell {
  rowIdx: number;
  colIdx: number;
  value: '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | 'x';
  covered: boolean;
  checked: boolean;
}

export interface GameBoardConfig {
  rows: number;
  cols: number;
  mines: number;
}

export interface GameStart extends GameBoardConfig {
  id: number;
  difficulty: GameDifficulty;
}

export interface GameEnd {
  status: Exclude<GameStatus, 'IN_PROGRESS'>;
  /**
   * Time in milliseconds
   */
  duration: number;
  /**
   * Amount of cells that a user has directly uncovered (no cascades)
   */
  uncovers: number;
  /**
   * Combined score between duration and uncovers
   */
  score: number;
}
