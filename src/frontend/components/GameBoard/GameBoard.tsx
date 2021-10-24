import React, { useEffect, useState } from 'react';
import styles from './GameBoard.module.less';
import { GameStart } from '../../types/Game';
import GameCell from './GameCell';
import { useAppDispatch, useAppSelector } from '../../pages/single-player/singlePlayerStore';
import { setIsLoading } from '../../pages/single-player/singlePlayerSlice';
import { fetchJson } from '../../utils/fetchJson';
import { useGameSocket } from '../GameSocketProvider/GameSocketProvider';
import { SocketMessageType } from '../../utils/GameSocket';

const GameBoard: React.FC = () => {
  const [game, setGame] = useState<GameStart>();
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
    const tempBoard: React.ReactNodeArray = [];
    for (let r = 0; r < game.rows; r++) {
      for (let c = 0; c < game.cols; c++) {
        tempBoard.push(
          <GameCell
            key={`${difficulty}-${r}-${c}`}
            onClick={() => {
              sock.sendMsg({
                type: SocketMessageType.UNCOVER_CELL,
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
