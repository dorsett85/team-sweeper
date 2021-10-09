import React, { useEffect, useState } from 'react';
import Modal from '../Modal/Modal';
import { useGameSocket } from '../GameSocketProvider/GameSocketProvider';
import styles from './GameModal.less';
import { EndGameStatus } from '../../utils/GameSocket';

const statusTextMap: Record<EndGameStatus, string> = {
  WON: 'won',
  LOST: 'lost'
};

const HEADING_ID = 'game-modal-heading';

const GameModal: React.FC = () => {
  const [status, setStatus] = useState<EndGameStatus>();
  const { sock } = useGameSocket();

  useEffect(() => {
    const callbackKey = sock.addOnEndGame((endGameStatus) => {
      setStatus(endGameStatus);
    });

    return () => {
      sock.removeOnEndGame(callbackKey);
    };
  }, [sock]);

  const modalBody = status && (
    <>
      <h2 id={HEADING_ID} className={styles.gameModalHeading}>
        You {statusTextMap[status]}!
      </h2>
      <div>
        <button>b</button>
      </div>
      <div>
        <button>c</button>
      </div>
    </>
  );

  return (
    <Modal open={!!status} onClose={() => setStatus(undefined)} aria-labelledby={HEADING_ID}>
      {modalBody}
    </Modal>
  );
};

export default GameModal;
