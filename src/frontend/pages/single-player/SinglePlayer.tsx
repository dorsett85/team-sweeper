import React from 'react';
import styles from './SinglePlayer.module.less';
import GameControl from '../../components/GameControl/GameControl';
import GameBoard from '../../components/GameBoard/GameBoard';
import { useAppSelector } from './singlePlayerStore';

const SinglePlayer: React.FC = () => {
  const difficulty = useAppSelector((state) => state.difficulty);

  return (
    <div className={styles[`layout-${difficulty}`]}>
      <GameControl />
      <GameBoard />
    </div>
  );
};

export default SinglePlayer;
