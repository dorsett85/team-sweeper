import React, { useEffect, useState } from 'react';
import styles from './GameBoard.module.less';
import { Board } from '../../types/Board';
import TsBoardCell from './GameCell';
import { useAppDispatch, useAppSelector } from '../../pages/single-player/singlePlayerStore';
import { setIsLoading } from '../../pages/single-player/singlePlayerSlice';
import { fetchJson } from '../../utils/fetchJson';

const GameBoard: React.FC = () => {
  const [board, setBoard] = useState<Board>();
  const [loadingError, setLoadingError] = useState(false);
  const difficulty = useAppSelector((state) => state.difficulty);
  const isLoading = useAppSelector((state) => state.isLoading);
  const dispatch = useAppDispatch();

  // We'll refetch a new board whenever the difficulty or loading state changes, which will occur
  // on any page refresh or game control change.
  useEffect(() => {
    if (isLoading) {
      setLoadingError(false);
      fetchJson<Board>(`/single-player/new-game?difficulty=${difficulty}`)
        .then((newBoard) => {
          setBoard(newBoard);
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
      <span className={styles.tsBoardMessage}>
        {loadingError ? 'Failed to load the game, try refreshing' : 'loading...'}
      </span>
    );
  } else {
    gameBoard = board?.cells2d.flatMap((row) =>
      row.map((cell) => (
        <TsBoardCell key={`${difficulty}-${cell.rowIdx}-${cell.colIdx}`} {...cell} />
      ))
    );
  }

  return <div className={styles[`tsBoard-${difficulty}`]}>{gameBoard}</div>;
};

export default GameBoard;
