import React from 'react';
import styles from './GameCell.module.less';
import { GameDifficulty } from '../../types/gameDifficulty';

interface GameCellOuterProps {
  difficulty: GameDifficulty;
  className?: string;
}

/**
 * Outer layout component of a game cell
 */
const GameCellOuter: React.FC<GameCellOuterProps> = ({ children, difficulty, className = '' }) => {
  const fullClassName = `${styles[`cellOuter-${difficulty}`]} ${className}`;
  return <div className={fullClassName}>{children}</div>;
};

export default GameCellOuter;
