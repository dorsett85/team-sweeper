import React, { useRef } from 'react';
import styles from './GameBoard.module.less';

const GameBoard: React.FC = () => {
  const canvasRef = useRef<HTMLCanvasElement | null>(null);

  return <canvas ref={canvasRef} className={styles.gameBoard} />;
};

export default GameBoard;
