import React, { useEffect, useState } from 'react';
import Modal from '../Modal/Modal';
import { useGameSocket } from '../GameSocketProvider/GameSocketProvider';
import styles from './GameModal.module.less';
import { GameEnd } from '../../types/game';
import { fetchJson } from '../../utils/fetchJson';
import { SessionGameStats } from '../../types/sessionGameStats';
import { GameStatus } from '../../types/gameStatus';
import { GameDifficulty } from '../../types/gamedifficulty';

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
    const gameMinutes = gameDate.getMinutes().toString().padStart(2, '0');
    const gameSeconds = gameDate.getSeconds().toString().padStart(2, '0');

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
        <section>
          <h3>Games Played (wins/total)</h3>
          <ul>
            {Object.entries(sessionGameStats.games).map(([key, { count, statuses }]) => {
              const winPct = Math.round((statuses.WON.count / count) * 100);
              const winPctText = isNaN(winPct) ? 'NA' : `${winPct}%`;

              return (
                <li key={key}>
                  <span className={styles.difficultyStats}>
                    <span className={styles.difficultyStatsType}>
                      {difficultyTextMap[key as GameDifficulty]}
                    </span>
                    : {statuses.WON.count}/{count} ({winPctText})
                  </span>
                </li>
              );
            })}
          </ul>
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
