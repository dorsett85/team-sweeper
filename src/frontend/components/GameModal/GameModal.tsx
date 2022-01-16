import React, { useEffect, useState } from 'react';
import Modal from '../Modal/Modal';
import { useGameSocket } from '../GameSocketProvider/GameSocketProvider';
import styles from './GameModal.module.less';
import { GameEnd } from '../../types/game';
import GameModalContent from './GameModalContent';
import GameModalTitle from './GameModalTitle';

const HEADING_ID = 'game-modal-heading';

const GameModal: React.FC = () => {
  const [gameEnd, setGameEnd] = useState<GameEnd>();
  const { sock } = useGameSocket();

  useEffect(() => {
    const handleOnEndGame = (gameEnd: GameEnd) => setGameEnd(gameEnd);
    sock.addOnEndGame(handleOnEndGame);

    return () => {
      sock.removeOnEndGame(handleOnEndGame);
    };
  }, [sock]);

  return (
    <Modal
      open={!!gameEnd}
      onClose={() => setGameEnd(undefined)}
      dialogClassName={styles[`gameModal-${gameEnd?.status}`]}
      aria-labelledby={HEADING_ID}
    >
      {gameEnd && (
        <>
          <GameModalTitle id={HEADING_ID} gameEnd={gameEnd} />
          <hr />
          <GameModalContent gameEnd={gameEnd} />
        </>
      )}
    </Modal>
  );
};

export default GameModal;
