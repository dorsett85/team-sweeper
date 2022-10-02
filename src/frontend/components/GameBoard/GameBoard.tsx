import React, { useEffect, useState } from 'react';
import styles from './GameBoard.module.less';
import cellStyles from './GameCell.module.less';
import { GameStart } from '../../types/game';
import GameCell from './GameCell';
import { useAppDispatch, useAppSelector } from '../../pages/single-player/singlePlayerStore';
import { setIsLoading } from '../../pages/single-player/singlePlayerSlice';
import { fetchJson } from '../../utils/fetchJson';
import GameCellOuter from './GameCellOuter';
import GameCellList from './GameCellList';
import { boardConfigMap } from '../../utils/constants/boardConfigMap';

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
    const { rows, cols } = boardConfigMap[difficulty];
    gameBoard = (
      <>
        {loadingError && (
          <div role='alert' className={styles.errorMessage}>
            <span>Failed to load the game, try resetting</span>
          </div>
        )}
        <GameCellList
          rows={rows}
          cols={cols}
          renderCell={({ rIdx, cIdx }) => {
            return (
              <GameCellOuter
                key={`${difficulty}-${rIdx}-${cIdx}`}
                difficulty={difficulty}
                className={cellStyles.loadingShimmer}
              >
                <div className={cellStyles.cellLoading} />
              </GameCellOuter>
            );
          }}
        />
      </>
    );
  } else if (game) {
    gameBoard = (
      <GameCellList
        rows={game.rows}
        cols={game.cols}
        renderCell={({ rIdx, cIdx }) => {
          return (
            <GameCell
              key={`${game.difficulty}-${rIdx}-${cIdx}`}
              gameId={game.id}
              difficulty={game.difficulty}
              rowIdx={rIdx}
              colIdx={cIdx}
            />
          );
        }}
      />
    );
  }

  return <div className={`${styles[`board-${difficulty}`]} ${className}`}>{gameBoard}</div>;
};

export default GameBoard;
