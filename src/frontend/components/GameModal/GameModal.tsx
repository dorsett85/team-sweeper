import React, { useEffect, useState } from 'react';
import Modal from '../Modal/Modal';
import { useGameSocket } from '../GameSocketProvider/GameSocketProvider';
import styles from './GameModal.module.less';
import { GameEnd } from '../../types/Game';

const statusTextMap: Record<GameEnd['status'], string> = {
  WON: 'won',
  LOST: 'lost'
};

const HEADING_ID = 'game-modal-heading';

const GameModal: React.FC = () => {
  const [gameEnd, setGameEnd] = useState<GameEnd>();
  const { sock } = useGameSocket();

  useEffect(() => {
    const callbackKey = sock.addOnEndGame((gameEnd) => {
      setGameEnd(gameEnd);
    });

    return () => {
      sock.removeOnEndGame(callbackKey);
    };
  }, [sock]);

  const modalBody = gameEnd && (
    <>
      <h2 id={HEADING_ID} className={styles.gameModalHeading}>
        You {statusTextMap[gameEnd.status]}!
      </h2>
      <hr />
      <div>
        <h3>Time</h3>
        <span>
          {new Date(gameEnd.duration).getMinutes()}m {new Date(gameEnd.duration).getSeconds()}s
        </span>
      </div>
    </>
  );

  return (
    <Modal
      open={!!gameEnd?.status}
      onClose={() => setGameEnd(undefined)}
      dialogClassName={styles[`gameModal-${gameEnd?.status}`]}
      aria-labelledby={HEADING_ID}
    >
      {modalBody}
    </Modal>
  );
};

export default GameModal;
