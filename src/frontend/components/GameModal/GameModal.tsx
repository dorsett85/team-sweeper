import React, { useEffect, useState } from 'react';
import Modal from '../Modal/Modal';
import { useGameSocket } from '../GameSocketProvider/GameSocketProvider';
import styles from './GameModal.module.less';
import { GameEnd } from '../../types/game';
import GameModalContent from './GameModalContent';
import ModalTitle from '../Modal/ModalTitle';
import { GameStatus } from '../../types/gameStatus';

const HEADING_ID = 'game-modal-heading';

const statusTextMap: Record<GameStatus, string> = {
  IN_PROGRESS: 'in-progress',
  WON: 'won',
  LOST: 'lost'
};

const GameModal: React.FC = () => {
  const [gameEnd, setGameEnd] = useState<GameEnd | undefined>();
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
          <ModalTitle id={HEADING_ID} title={`You ${statusTextMap[gameEnd.status]}!`} />
          <GameModalContent gameEnd={gameEnd} />
        </>
      )}
    </Modal>
  );
};

export default GameModal;
