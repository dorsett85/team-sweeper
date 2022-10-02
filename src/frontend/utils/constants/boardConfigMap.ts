import { GameDifficulty } from '../../types/gameDifficulty';
import { GameBoardConfig } from '../../types/game';

export const boardConfigMap: Record<GameDifficulty, GameBoardConfig> = {
  e: {
    rows: 9,
    cols: 9,
    mines: 10
  },
  m: {
    rows: 16,
    cols: 16,
    mines: 40
  },
  h: {
    rows: 16,
    cols: 30,
    mines: 99
  }
};
