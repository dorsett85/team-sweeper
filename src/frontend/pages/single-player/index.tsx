import React from 'react';
import SinglePlayer from './SinglePlayer';
import { renderReactDom } from '../../utils/renderReactDom';
import { Provider } from 'react-redux';
import singlePlayerStore from './singlePlayerStore';
import GameSocketProvider from '../../components/GameSocketProvider/GameSocketProvider';
import GameSocketNotOpen from '../../components/GameSocketNotOpen/GameSocketNotOpen';

renderReactDom(
  <Provider store={singlePlayerStore}>
    <GameSocketProvider
      url='ws://localhost:8080/game/publish'
      renderGameSocketNotOpen={(readyState, connectToGameSocket) => (
        <GameSocketNotOpen readyState={readyState} onReconnectClick={connectToGameSocket} />
      )}
    >
      <SinglePlayer />
    </GameSocketProvider>
  </Provider>
);
