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
      url={process.env.WS_URL || ''}
      renderGameSocketNotOpen={({ readyState, onSocketConnect, reason }) => (
        <GameSocketNotOpen
          readyState={readyState}
          onReconnectClick={onSocketConnect}
          reason={reason}
        />
      )}
    >
      <SinglePlayer />
    </GameSocketProvider>
  </Provider>
);
