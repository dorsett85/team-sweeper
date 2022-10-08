import React from 'react';
import styles from './SinglePlayer.module.less';
import { useAppSelector } from './singlePlayerStore';
import GameControl from '../../components/GameControl/GameControl';
import GameBoard from '../../components/GameBoard/GameBoard';
import GameScore from '../../components/GameScore/GameScore';
import GameModal from '../../components/GameModal/GameModal';

const SinglePlayer: React.FC = () => {
  const difficulty = useAppSelector((state) => state.difficulty);
  const isLoading = useAppSelector((state) => state.isLoading);

  return (
    <div className={styles[`layout-${difficulty}`]}>
      <GameControl difficulty={difficulty} isLoading={isLoading} />
      <GameBoard difficulty={difficulty} isLoading={isLoading} className={styles.gameBoard} />
      <GameScore className={styles.gameScore} />
      <GameModal />
    </div>
  );
};

export default SinglePlayer;
