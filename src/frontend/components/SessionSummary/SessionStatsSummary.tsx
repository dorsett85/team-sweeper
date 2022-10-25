import React, { memo, useEffect, useState } from 'react';
import styles from './SessionSummary.module.less';
import { SessionGameStats } from '../../types/sessionGameStats';
import { fetchJson } from '../../utils/fetchJson';
import { useAppSelector } from '../../pages/single-player/singlePlayerStore';
import { dateToMinutesString, dateToSecondsString } from '../../utils/dateToUnitString';
import { SelectDifficulty } from '../SelectDifficulty/SelectDifficulty';

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

interface StatListItemProps {
  /**
   * Display name of the stat
   */
  name: string;
  /**
   * Display value of the stat
   */
  value: string | number;
}

const StatListItem: React.FC<StatListItemProps> = ({ name, value }) => {
  return (
    <li className={styles.statLi}>
      <span className={styles.statType}>{name}:</span> {value}
    </li>
  );
};

const SessionStatsSummary: React.FC<SessionSummaryProps> = ({ onStatsLoaded }) => {
  const gameDifficulty = useAppSelector((state) => state.difficulty);
  const [difficulty, setDifficulty] = useState(gameDifficulty);
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

  // Gather names and values to display for each stat item
  const { count, statuses } = stats.games[difficulty];
  const statLiProps: StatListItemProps[] = [];

  // Fastest win list item
  const fastestWinTime = statuses.WON.fastestTime;
  const fastestTimestamp = fastestWinTime && new Date(fastestWinTime);
  let fastestTxt = 'NA';
  if (fastestTimestamp) {
    const fastestMinutes = dateToMinutesString(fastestTimestamp);
    const fastestSeconds = dateToSecondsString(fastestTimestamp);
    fastestTxt = `${fastestMinutes}:${fastestSeconds}`;
  }
  statLiProps.push({ name: 'Fastest Win', value: fastestTxt });

  // Most win uncovers list item
  const mostWinUncovers = statuses.WON.mostUncovers || 'NA';
  statLiProps.push({ name: 'Most Win Uncovers', value: mostWinUncovers });

  // Highest win score list item
  const highestWinScore = statuses.WON.highestScore ? statuses.WON.highestScore.toFixed(2) : 'NA';
  statLiProps.push({ name: 'Highest Win Score', value: highestWinScore });

  // Games played list item
  const winPct = Math.round((statuses.WON.count / count) * 100);
  const winPctText = isNaN(winPct) ? 'NA' : `${winPct}%`;
  const gamesPlayedWinPct = `${statuses.WON.count}/${count} (${winPctText})`;
  statLiProps.push({ name: 'Games Played (wins/total)', value: gamesPlayedWinPct });

  // Avg completion percentage
  const allCount = statuses.IN_PROGRESS.count + statuses.LOST.count + statuses.WON.count;
  const inProgressPct =
    (statuses.IN_PROGRESS.count / allCount) * statuses.IN_PROGRESS.avgCompletionPct;
  const lostPct = (statuses.LOST.count / allCount) * statuses.LOST.avgCompletionPct;
  const wonPct = (statuses.WON.count / allCount) * statuses.WON.avgCompletionPct;
  const completionPct = (inProgressPct + lostPct + wonPct).toFixed(2);
  statLiProps.push({ name: 'Average Completion %', value: completionPct });

  return (
    <>
      <h3 className={styles.sessionSummaryHeading}>Session Summary</h3>
      <SelectDifficulty
        className={styles.difficultySelect}
        difficulty={difficulty}
        onSelect={setDifficulty}
      />
      <ul>
        {statLiProps.map(({ name, value }) => (
          <StatListItem key={name} name={name} value={value} />
        ))}
      </ul>
    </>
  );
};

export default memo(SessionStatsSummary);
