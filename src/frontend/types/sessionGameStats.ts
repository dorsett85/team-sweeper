import { GameDifficulty } from './gameDifficulty';
import { GameStatus } from './gameStatus';

interface CommonStats {
  /**
   * Number of game records for a given difficulty or status
   */
  count: number;
}

interface StatusStats extends CommonStats {
  /**
   * Fastest time from start to end in milliseconds
   */
  fastestTime: number | null;
  /**
   * Most points in a game
   */
  mostPoints: number | null;
}

interface DifficultyStats extends CommonStats {
  statuses: Record<GameStatus, StatusStats>;
}

/**
 * Server response to get aggregated session game stats
 */
export interface SessionGameStats {
  games: Record<GameDifficulty, DifficultyStats>;
}
