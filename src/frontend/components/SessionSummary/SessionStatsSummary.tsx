import React, { memo, ReactElement, useEffect, useState } from 'react';
import styles from '../GameModal/GameModal.module.less';
import { GameDifficulty } from '../../types/gameDifficulty';
import { SessionGameStats } from '../../types/sessionGameStats';
import { fetchJson } from '../../utils/fetchJson';
import { useAppSelector } from '../../pages/single-player/singlePlayerStore';
import { dateToMinutesString, dateToSecondsString } from '../../utils/dateToUnitString';

interface SessionSummaryProps {
  /**
   * Callback after the session stats have been fetched
   */
  onStatsLoaded: (
    fastestWinTime: null | number,
    mostWinUncovers: null | number,
    score: null | number
  ) => void;
}

const difficultyTextMap: Record<GameDifficulty, string> = {
  e: 'Easy',
  m: 'Medium',
  h: 'Hard'
};

const SessionStatsSummary: React.FC<SessionSummaryProps> = ({ onStatsLoaded }) => {
  const gameDifficulty = useAppSelector((state) => state.difficulty);
  const [stats, setStats] = useState<SessionGameStats>();

  useEffect(() => {
    let mounted = true;
    if (stats) {
      return;
    }

    // Fetch session stats
    fetchJson<SessionGameStats>('/game/session-stats')
      .then((sessionGameStats) => {
        if (!mounted) {
          return;
        }
        setStats(sessionGameStats);
        // Get the fastest previous win for the same difficulty
        const { fastestTime, mostUncovers, highestScore } =
          sessionGameStats.games[gameDifficulty].statuses.WON;
        onStatsLoaded(fastestTime, mostUncovers, highestScore);
      })
      .catch((e) => {
        console.warn("Couldn't fetch session stats:", e);
      });

    return () => {
      mounted = false;
    };
  }, [gameDifficulty, onStatsLoaded, stats]);

  // TODO add loading indicator
  if (!stats) {
    return null;
  }

  const fastestTimesListItems: ReactElement[] = [];
  const mostUncoversListItems: ReactElement[] = [];
  const highestScoreListItems: ReactElement[] = [];
  const gamesPlayedListItems: ReactElement[] = [];
  // Loop through all the game difficulty stats to populate different list
  // sections.
  Object.entries(stats.games).forEach(([difficultyKey, { count, statuses }]) => {
    // Add the fastest games list items
    const fastestWinTime = statuses.WON.fastestTime;
    const fastestTimestamp = fastestWinTime && new Date(fastestWinTime);
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

    const mostWinUncovers = statuses.WON.mostUncovers || 'NA';
    mostUncoversListItems.push(
      <li key={difficultyKey}>
        <span className={styles.difficultyStats}>
          <span className={styles.difficultyStatsType}>
            {difficultyTextMap[difficultyKey as GameDifficulty]}
          </span>{' '}
          - {mostWinUncovers}
        </span>
      </li>
    );

    const highestWinScore = statuses.WON.highestScore ? statuses.WON.highestScore.toFixed(2) : 'NA';
    highestScoreListItems.push(
      <li key={difficultyKey}>
        <span className={styles.difficultyStats}>
          <span className={styles.difficultyStatsType}>
            {difficultyTextMap[difficultyKey as GameDifficulty]}
          </span>{' '}
          - {highestWinScore}
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

  return (
    <>
      <section>
        <h3>Fastest Win Times</h3>
        <ul>{fastestTimesListItems}</ul>
      </section>
      <section>
        <h3>Most Points Won</h3>
        <ul>{mostUncoversListItems}</ul>
      </section>
      <section>
        <h3>Highest Score Won</h3>
        <ul>{highestScoreListItems}</ul>
      </section>
      <section>
        <h3>Games Played (wins/total)</h3>
        <ul>{gamesPlayedListItems}</ul>
      </section>
    </>
  );
};

export default memo(SessionStatsSummary);
