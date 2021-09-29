import React from 'react';
import styles from './SinglePlayer.module.less';
import GameControl from '../../components/GameControl/GameControl';
import GameBoard from '../../components/GameBoard/GameBoard';
import { useAppSelector } from './singlePlayerStore';
import GameModal from '../../components/GameModal/GameModal';

const SinglePlayer: React.FC = () => {
  const difficulty = useAppSelector((state) => state.difficulty);

  return (
    <div className={styles[`layout-${difficulty}`]}>
      <GameControl />
      <GameBoard />
      <GameModal />
    </div>
  );
};

export default SinglePlayer;
