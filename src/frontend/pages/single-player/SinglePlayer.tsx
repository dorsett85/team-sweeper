import React from 'react';
import { Provider } from 'react-redux';
import styles from './SinglePlayer.module.less';
import GameControl from '../../components/GameControl/GameControl';
import TsBoard from '../../components/TsBoard/TsBoard';
import singlePlayerStore from './singlePlayerStore';

const SinglePlayer: React.FC = () => {
  return (
    <Provider store={singlePlayerStore}>
      <div className={styles.layout}>
        <GameControl />
        <TsBoard />
      </div>
    </Provider>
  );
};

export default SinglePlayer;
