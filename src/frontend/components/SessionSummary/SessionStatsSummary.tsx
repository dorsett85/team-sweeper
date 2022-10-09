import React, { ReactElement, useEffect, useState } from 'react';
import { GameEnd } from '../../types/game';
import styles from '../GameModal/GameModal.module.less';
import { GameDifficulty } from '../../types/gameDifficulty';
import { SessionGameStats } from '../../types/sessionGameStats';
import { fetchJson } from '../../utils/fetchJson';
import { useAppSelector } from '../../pages/single-player/singlePlayerStore';
import { dateToMinutesString, dateToSecondsString } from '../../utils/dateToUnitString';

interface SessionSummaryProps {
  gameEnd: GameEnd;
  /**
   * Callback after the session stats have been fetched and processed
   */
  onProcessedStats: (isFastestGame: boolean) => void;
}

const difficultyTextMap: Record<GameDifficulty, string> = {
  e: 'Easy',
  m: 'Medium',
  h: 'Hard'
};

const SessionStatsSummary: React.FC<SessionSummaryProps> = ({ gameEnd, onProcessedStats }) => {
  const gameDifficulty = useAppSelector((state) => state.difficulty);
  const [statsItems, setStatsItems] = useState<[React.ReactElement[], React.ReactElement[]]>();

  useEffect(() => {
    let mounted = true;
    if (statsItems) {
      return;
    }

    // Fetch session stats
    fetchJson<SessionGameStats>('/game/session-stats')
      .then((sessionGameStats) => {
        if (!mounted) {
          return;
        }
        // Loop through all the game difficulty stats to populate different list
        // sections.
        const fastestTimesListItems: ReactElement[] = [];
        const gamesPlayedListItems: ReactElement[] = [];
        let isFastestGame = false;
        Object.entries(sessionGameStats.games).forEach(([difficultyKey, { count, statuses }]) => {
          // Check if this is the fastest win
          const fastestWinTime = statuses.WON.fastestTime;
          if (
            gameEnd.status === 'WON' &&
            gameDifficulty === difficultyKey &&
            (fastestWinTime === null || gameEnd.duration <= fastestWinTime)
          ) {
            isFastestGame = true;
          }

          // Add fastest games list items
          const fastestTimestamp = fastestWinTime ? new Date(fastestWinTime) : null;
          let fastestTxt = 'NA';
          if (fastestTimestamp) {
            const fastestMins = dateToMinutesString(fastestTimestamp);
            const fastestSeconds = dateToSecondsString(fastestTimestamp);
            fastestTxt = `${fastestMins}:${fastestSeconds}`;
          }

          fastestTimesListItems.push(
            <li key={difficultyKey}>
              <span className={styles.difficultyStats}>
                <span className={styles.difficultyStatsType}>
                  {difficultyTextMap[difficultyKey as GameDifficulty]}
                </span>{' '}
                - {fastestTxt}
              </span>
            </li>
          );

          // Add games played list items
          const winPct = Math.round((statuses.WON.count / count) * 100);
          const winPctText = isNaN(winPct) ? 'NA' : `${winPct}%`;

          gamesPlayedListItems.push(
            <li key={difficultyKey}>
              <span className={styles.difficultyStats}>
                <span className={styles.difficultyStatsType}>
                  {difficultyTextMap[difficultyKey as GameDifficulty]}
                </span>
                : {statuses.WON.count}/{count} ({winPctText})
              </span>
            </li>
          );
        });

        setStatsItems([fastestTimesListItems, gamesPlayedListItems]);
        onProcessedStats(isFastestGame);
      })
      .catch((e) => {
        console.warn("Couldn't fetch session stats:", e);
      });

    return () => {
      mounted = false;
    };
  }, [gameDifficulty, gameEnd.duration, gameEnd.status, onProcessedStats, statsItems]);

  // TODO add loading indicator
  if (!statsItems) {
    return null;
  }

  const [fastestTimesListItems, gamesPlayedListItems] = statsItems;

  return (
    <>
      <section>
        <h3>Fastest Win Times</h3>
        <ul>{fastestTimesListItems}</ul>
      </section>
      <section>
        <h3>Games Played (wins/total)</h3>
        <ul>{gamesPlayedListItems}</ul>
      </section>
    </>
  );
};

export default SessionStatsSummary;
