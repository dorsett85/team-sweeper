import React from 'react';
import SinglePlayer from './SinglePlayer';
import { renderReactDom } from '../../utils/renderReactDom';
import { Provider } from 'react-redux';
import singlePlayerStore from './singlePlayerStore';

renderReactDom(
  <Provider store={singlePlayerStore}>
    <SinglePlayer />
  </Provider>
);
