import React from 'react';
import styles from './home.module.less';
import { renderReactDom } from '../../utils/renderReactDom';

const Home: React.FC = () => {
  return (
    <div className={styles.layout}>
      <h1>This will be Team Sweeper!</h1>
    </div>
  );
};

renderReactDom(<Home />);
