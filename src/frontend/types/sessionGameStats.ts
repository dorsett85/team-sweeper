import { GameDifficulty } from './gamedifficulty';
import { GameStatus } from './gameStatus';

interface CommonStats {
  /**
   * Number of game records for a given difficulty or status
   */
  count: number;
}

type StatusStats = CommonStats;

interface DifficultyStats extends CommonStats {
  statuses: Record<GameStatus, StatusStats>;
}

/**
 * Server response to get aggregated session game stats
 */
export interface SessionGameStats {
  games: Record<GameDifficulty, DifficultyStats>;
}
