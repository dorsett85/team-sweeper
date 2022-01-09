import React, { ReactElement, useEffect, useState } from 'react';
import Modal from '../Modal/Modal';
import { useGameSocket } from '../GameSocketProvider/GameSocketProvider';
import styles from './GameModal.module.less';
import { GameEnd } from '../../types/game';
import { fetchJson } from '../../utils/fetchJson';
import { SessionGameStats } from '../../types/sessionGameStats';
import { GameStatus } from '../../types/gameStatus';
import { GameDifficulty } from '../../types/gamedifficulty';
import { useAppSelector } from '../../pages/single-player/singlePlayerStore';

const statusTextMap: Record<GameStatus, string> = {
  IN_PROGRESS: 'in-progress',
  WON: 'won',
  LOST: 'lost'
};

const difficultyTextMap: Record<GameDifficulty, string> = {
  e: 'Easy',
  m: 'Medium',
  h: 'Hard'
};

const HEADING_ID = 'game-modal-heading';

const GameModal: React.FC = () => {
  const gameDifficulty = useAppSelector((state) => state.difficulty);
  const [gameEnd, setGameEnd] = useState<GameEnd>();
  const [sessionGameStats, setSessionGameStats] = useState<SessionGameStats>();
  const { sock } = useGameSocket();

  useEffect(() => {
    const callbackKey = sock.addOnEndGame((gameEnd) => {
      setGameEnd(gameEnd);

      // Fetch session stats
      fetchJson<SessionGameStats>('/game/session-stats').then(setSessionGameStats);
    });

    return () => {
      sock.removeOnEndGame(callbackKey);
    };
  }, [sock]);

  const renderModalBody = (): React.ReactElement | null => {
    if (!gameEnd || !sessionGameStats) {
      return null;
    }

    const gameDate = new Date(gameEnd.duration);
    const gameMinutes = gameDate.getUTCMinutes().toString().padStart(2, '0');
    const gameSeconds = gameDate.getUTCSeconds().toString().padStart(2, '0');

    // Loop through all the game difficulty stats to populate different list
    // sections
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
        const fastestMins = fastestTimestamp.getUTCMinutes().toString().padStart(2, '0');
        const fastestSeconds = fastestTimestamp.getUTCSeconds().toString().padStart(2, '0');
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

    return (
      <>
        <h2 id={HEADING_ID} className={styles.gameModalHeading}>
          You {statusTextMap[gameEnd.status]}!
        </h2>
        <hr />
        <section className={styles.summary}>
          <div className={styles.timeBox}>
            <span className={styles.timeText}>{gameMinutes}</span>
            <span className={styles.timeType}>minutes</span>
          </div>
          <div className={styles.timeSeparator}>:</div>
          <div className={styles.timeBox}>
            <span className={styles.timeText}>{gameSeconds}</span>
            <span className={styles.timeType}>seconds</span>
          </div>
        </section>
        {isFastestGame && <strong className={styles.fastestWin}>Nice, new fastest time</strong>}
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

  return (
    <Modal
      open={!!gameEnd}
      onClose={() => setGameEnd(undefined)}
      dialogClassName={styles[`gameModal-${gameEnd?.status}`]}
      aria-labelledby={HEADING_ID}
    >
      {renderModalBody()}
    </Modal>
  );
};

export default GameModal;
