import React, { useEffect, useState } from 'react';
import styles from './TsBoard.module.less';
import { TsBoardResponse } from '../../types/TsBoardResponse';
import TsBoardCell from './TsBoardCell';
import { useAppDispatch, useAppSelector } from '../../pages/single-player/singlePlayerStore';
import { setIsLoading } from '../../pages/single-player/singlePlayerSlice';
import { fetchJson } from '../../utils/fetchJson';

const TsBoard: React.FC = () => {
  const [boardResponse, setBoardResponse] = useState<TsBoardResponse>();
  const [loadingError, setLoadingError] = useState(false);
  const difficulty = useAppSelector((state) => state.difficulty);
  const isLoading = useAppSelector((state) => state.isLoading);
  const dispatch = useAppDispatch();

  // We'll refetch a new board whenever the difficulty or loading state changes, which will occur
  // on any page refresh or game control change.
  useEffect(() => {
    if (isLoading) {
      setLoadingError(false);
      fetchJson<TsBoardResponse>(`/single-player/new-game?difficulty=${difficulty}`)
        .then((board) => {
          setBoardResponse(board);
        })
        .catch(() => {
          setLoadingError(true);
        })
        .finally(() => {
          dispatch(setIsLoading(false));
        });
    }
  }, [difficulty, dispatch, isLoading]);

  let board: React.ReactNode;
  if (loadingError || isLoading) {
    board = (
      <span className={styles.tsBoardMessage}>
        {loadingError ? 'Failed to load the game, try refreshing' : 'loading...'}
      </span>
    );
  } else {
    board = boardResponse?.board.flatMap((row) =>
      row.map((cell) => (
        <TsBoardCell key={`${difficulty}-${cell.rowIdx}-${cell.colIdx}`} {...cell} />
      ))
    );
  }

  return <div className={styles[`tsBoard-${difficulty}`]}>{board}</div>;
};

export default TsBoard;
