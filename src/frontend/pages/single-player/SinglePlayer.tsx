import React from 'react';
import styles from './SinglePlayer.module.less';
import GameControl from '../../components/GameControl/GameControl';
import GameBoard from '../../components/GameBoard/GameBoard';

const SinglePlayer: React.FC = () => {
  return (
    <div className={styles.layout}>
      <GameControl />
      <GameBoard />
    </div>
  );
};

export default SinglePlayer;
