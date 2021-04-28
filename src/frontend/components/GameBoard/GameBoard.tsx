import React, { useEffect, useState } from 'react';
import styles from './GameBoard.module.less';
import { Game } from '../../types/Game';
import GameCell from './GameCell';
import { useAppDispatch, useAppSelector } from '../../pages/single-player/singlePlayerStore';
import { setIsLoading } from '../../pages/single-player/singlePlayerSlice';
import { fetchJson } from '../../utils/fetchJson';

const GameBoard: React.FC = () => {
  const [game, setGame] = useState<Game>();
  const [loadingError, setLoadingError] = useState(false);
  const difficulty = useAppSelector((state) => state.difficulty);
  const isLoading = useAppSelector((state) => state.isLoading);
  const dispatch = useAppDispatch();

  // We'll refetch a new board whenever the difficulty or loading state changes, which will occur
  // on any page refresh or game control change.
  useEffect(() => {
    if (isLoading) {
      setLoadingError(false);
      fetchJson<Game>(`/game/new-game?difficulty=${difficulty}`)
        .then((newGame) => {
          // @ts-ignore
          newGame.board = JSON.parse(newGame.board);
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
  } else {
    gameBoard = game?.board.flatMap((row) =>
      row.map((cell) => <GameCell key={`${difficulty}-${cell.rowIdx}-${cell.colIdx}`} {...cell} />)
    );
  }

  return <div className={styles[`board-${difficulty}`]}>{gameBoard}</div>;
};

export default GameBoard;
