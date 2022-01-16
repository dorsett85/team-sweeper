import React from 'react';
import styles from './SinglePlayer.module.less';
import { useAppSelector } from './singlePlayerStore';
import GameControl from '../../components/GameControl/GameControl';
import GameBoard from '../../components/GameBoard/GameBoard';
import GameScore from '../../components/GameScore/GameScore';
import GameModal from '../../components/GameModal/GameModal';

const SinglePlayer: React.FC = () => {
  const difficulty = useAppSelector((state) => state.difficulty);

  return (
    <div className={styles[`layout-${difficulty}`]}>
      <GameControl />
      <GameBoard className={styles.gameBoard} />
      <GameScore className={styles.gameScore} />
      <GameModal />
    </div>
  );
};

export default SinglePlayer;
