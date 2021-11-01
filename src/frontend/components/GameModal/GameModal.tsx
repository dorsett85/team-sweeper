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

  const statsSection = sessionGameStats ? (
    <>
      <h3 className={styles.sessionStatsHeading}>Session Statistics</h3>
      <p className={styles.sessionStatsType}>Games Played</p>
      <ul>
        {Object.entries(sessionGameStats.games).map(([key, difficultyStats]) => {
          return (
            <li key={key}>
              <span className={styles.difficultyStatsText}>
                {difficultyTextMap[key as GameDifficulty]}: {difficultyStats.count}
              </span>
              {Object.entries(difficultyStats.statuses).map(([key, statusStats]) => {
                return (
                  <ul key={key}>
                    <li>
                      <div className={styles.statusStatsText}>
                        {statusTextMap[key as GameStatus]}: {statusStats.count}
                      </div>
                    </li>
                  </ul>
                );
              })}
            </li>
          );
        })}
      </ul>
    </>
  ) : null;

  const modalBody = gameEnd && sessionGameStats && (
    <>
      <h2 id={HEADING_ID} className={styles.gameModalHeading}>
        You {statusTextMap[gameEnd.status]}!
      </h2>
      <hr />
      <section className={styles.summary}>
        <span className={styles[`summaryTime-${gameEnd.status}`]}>
          {new Date(gameEnd.duration).getMinutes()} minutes{' '}
          {new Date(gameEnd.duration).getSeconds()} seconds
        </span>
      </section>
      <section>{statsSection}</section>
    </>
  );

  return (
    <Modal
      open={!!gameEnd}
      onClose={() => setGameEnd(undefined)}
      dialogClassName={styles[`gameModal-${gameEnd?.status}`]}
      aria-labelledby={HEADING_ID}
    >
      {modalBody}
    </Modal>
  );
};

export default GameModal;
