import React, { useEffect, useState } from 'react';
import styles from './TsBoard.module.less';
import { TsBoardResponse } from '../../types/TsBoardResponse';
import TsBoardCell from './TsBoardCell';
import { useAppDispatch, useAppSelector } from '../../pages/single-player/singlePlayerStore';
import { setIsLoading } from '../../pages/single-player/singlePlayerSlice';

const TsBoard: React.FC = () => {
  const [boardResponse, setBoardResponse] = useState<TsBoardResponse>();
  const difficulty = useAppSelector((state) => state.difficulty);
  const isLoading = useAppSelector((state) => state.isLoading);
  const dispatch = useAppDispatch();

  // We'll refetch a new board whenever the difficulty or loading state changes, which will occur
  // on any page refresh or game control change.
  useEffect(() => {
    if (isLoading) {
      fetch(`/single-player/new-game?difficulty=${difficulty}`)
        .then((res) => res.json())
        .then((board) => {
          setBoardResponse(board);
        })
        .finally(() => {
          dispatch(setIsLoading(false));
        });
    }
  }, [difficulty, dispatch, isLoading]);

  const board = isLoading ? (
    <span className={styles.tsBoardLoading}>...loading</span>
  ) : (
    boardResponse?.board.flatMap((row) =>
      row.map((cell) => (
        <TsBoardCell key={`${difficulty}-${cell.rowIdx}-${cell.colIdx}`} {...cell} />
      ))
    )
  );

  return <div className={styles[`tsBoard-${difficulty}`]}>{board}</div>;
};

export default TsBoard;
