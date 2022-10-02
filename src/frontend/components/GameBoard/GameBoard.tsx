import React, { useEffect, useState } from 'react';
import styles from './GameBoard.module.less';
import { GameStart } from '../../types/game';
import GameCell from './GameCell';
import { useAppDispatch, useAppSelector } from '../../pages/single-player/singlePlayerStore';
import { setIsLoading } from '../../pages/single-player/singlePlayerSlice';
import { fetchJson } from '../../utils/fetchJson';

interface GameBoardProps {
  className?: string;
}

const GameBoard: React.FC<GameBoardProps> = ({ className = '' }) => {
  const [game, setGame] = useState<GameStart>();
  const [loadingError, setLoadingError] = useState(false);
  const difficulty = useAppSelector((state) => state.difficulty);
  const isLoading = useAppSelector((state) => state.isLoading);
  const dispatch = useAppDispatch();

  // We'll refetch a new board whenever the difficulty or loading state changes,
  // which will occur on any page refresh or game control change.
  useEffect(() => {
    if (isLoading) {
      setLoadingError(false);
      fetchJson<GameStart>(`/game/new-game?difficulty=${difficulty}`)
        .then((newGame) => {
          setGame(newGame);
        })
        .catch(() => {
          setLoadingError(true);
        })
        .finally(() => {
          dispatch(setIsLoading(false));
        });
    }
  }, [difficulty, dispatch, isLoading]);

  let gameBoard: React.ReactNode;
  if (loadingError || isLoading) {
    gameBoard = (
      <span className={styles.boardMessage}>
        {loadingError ? 'Failed to load the game, try refreshing' : 'loading...'}
      </span>
    );
  } else if (game) {
    const tempBoard: React.ReactElement[] = [];
    for (let r = 0; r < game.rows; r++) {
      for (let c = 0; c < game.cols; c++) {
        tempBoard.push(
          <GameCell
            key={`${game.difficulty}-${r}-${c}`}
            gameId={game.id}
            difficulty={game.difficulty}
            rowIdx={r}
            colIdx={c}
          />
        );
      }
    }
    gameBoard = tempBoard;
  }

  return <div className={`${styles[`board-${difficulty}`]} ${className}`}>{gameBoard}</div>;
};

export default GameBoard;
