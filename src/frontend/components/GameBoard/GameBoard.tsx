import React, { useEffect, useState } from 'react';
import styles from './GameBoard.module.less';
import { Game } from '../../types/Game';
import GameCell from './GameCell';
import { useAppDispatch, useAppSelector } from '../../pages/single-player/singlePlayerStore';
import { setDifficulty, setIsLoading } from '../../pages/single-player/singlePlayerSlice';
import { fetchJson } from '../../utils/fetchJson';
import { useGameSocket } from '../GameSocketProvider/GameSocketProvider';

const GameBoard: React.FC = () => {
  const [game, setGame] = useState<Game>();
  const [loadingError, setLoadingError] = useState(false);
  const difficulty = useAppSelector((state) => state.difficulty);
  const isLoading = useAppSelector((state) => state.isLoading);
  const dispatch = useAppDispatch();
  const { sock } = useGameSocket();

  // We'll refetch a new board whenever the difficulty or loading state changes,
  // which will occur on any page refresh or game control change.
  useEffect(() => {
    if (isLoading) {
      setLoadingError(false);
      fetchJson<Game>(`/game/new-game?difficulty=${difficulty}`)
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

  useEffect(() => {
    // Need to set the difficulty since it might be a previously created game!
    if (game) {
      dispatch(setDifficulty(game.difficulty));
    }
  }, [game, dispatch]);

  let gameBoard: React.ReactNode;
  if (loadingError || isLoading) {
    gameBoard = (
      <span className={styles.boardMessage}>
        {loadingError ? 'Failed to load the game, try refreshing' : 'loading...'}
      </span>
    );
  } else if (game) {
    const tempBoard: React.ReactNodeArray = [];
    for (let r = 0; r < game.rows; r++) {
      for (let c = 0; c < game.cols; c++) {
        tempBoard.push(
          <GameCell
            key={`${difficulty}-${r}-${c}`}
            onClick={() => {
              sock.sendMsg({
                type: 'uncoverCell',
                payload: { gameId: game.id, rowIdx: r, colIdx: c }
              });
            }}
            difficulty={difficulty}
            rowIdx={r}
            colIdx={c}
          />
        );
      }
    }
    gameBoard = tempBoard;
  }

  return <div className={styles[`board-${difficulty}`]}>{gameBoard}</div>;
};

export default GameBoard;
