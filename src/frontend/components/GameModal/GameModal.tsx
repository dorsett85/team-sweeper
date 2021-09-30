import React, { useEffect, useState } from 'react';
import Modal from '../Modal/Modal';
import { useGameSocket } from '../GameSocketProvider/GameSocketProvider';

const GameModal: React.FC = () => {
  const [status, setStatus] = useState<string>();
  const { sock } = useGameSocket();

  useEffect(() => {
    const callbackKey = sock.addOnEndGame((endGameStatus) => {
      setStatus(endGameStatus);
    });

    return () => {
      sock.removeOnEndGame(callbackKey);
    };
  }, [sock]);

  return (
    <Modal open={!!status} onClose={() => setStatus(undefined)}>
      {status}
      <div>
        <button>a</button>
      </div>
      <div>
        <button>b</button>
      </div>
      <div>
        <button>c</button>
      </div>
    </Modal>
  );
};

export default GameModal;
