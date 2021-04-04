import React from 'react';
import styles from './GameControl.module.less';

const GameControl: React.FC = () => {
  return (
    <div className={styles.gameControl}>
      <button className={styles.gameControlBtn}>Difficulty</button>
      <button className={styles.gameControlBtn}>Reset Game</button>
    </div>
  );
};

export default GameControl;
