import React from 'react';
import { Provider } from 'react-redux';
import styles from './SinglePlayer.module.less';
import GameControl from '../../components/GameControl/GameControl';
import GameBoard from '../../components/GameBoard/GameBoard';
import singlePlayerStore from './singlePlayerStore';

const SinglePlayer: React.FC = () => {
  return (
    <Provider store={singlePlayerStore}>
      <div className={styles.layout}>
        <GameControl />
        <GameBoard />
      </div>
    </Provider>
  );
};

export default SinglePlayer;
