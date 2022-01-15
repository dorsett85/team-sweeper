import React from 'react';
import { GameEnd } from '../../types/game';
import styles from './GameModal.module.less';
import { GameStatus } from '../../types/gameStatus';

interface GameModalTitleProps {
  /**
   * Passed to the id attribute of the heading element
   */
  id: string;
  gameEnd: GameEnd;
}

const statusTextMap: Record<GameStatus, string> = {
  IN_PROGRESS: 'in-progress',
  WON: 'won',
  LOST: 'lost'
};

const GameModalTitle: React.FC<GameModalTitleProps> = ({ id, gameEnd }) => {
  return (
    <h2 id={id} className={styles.gameModalHeading}>
      You {statusTextMap[gameEnd.status]}!
    </h2>
  );
};

export default GameModalTitle;
