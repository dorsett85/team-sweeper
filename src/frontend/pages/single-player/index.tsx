import React from 'react';
import SinglePlayer from './SinglePlayer';
import { renderReactDom } from '../../utils/renderReactDom';
import { Provider } from 'react-redux';
import singlePlayerStore from './singlePlayerStore';
import GameSocketProvider from '../../components/GameSocketProvider/GameSocketProvider';

renderReactDom(
  <Provider store={singlePlayerStore}>
    <GameSocketProvider>
      <SinglePlayer />
    </GameSocketProvider>
  </Provider>
);
