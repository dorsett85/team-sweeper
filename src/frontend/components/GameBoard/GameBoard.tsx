import React, { useEffect, useState } from 'react';
import styles from './GameBoard.module.less';
import cellStyles from './GameCell.module.less';
import { GameStart } from '../../types/game';
import GameCell from './GameCell';
import { useAppDispatch } from '../../pages/single-player/singlePlayerStore';
import { setIsLoading } from '../../pages/single-player/singlePlayerSlice';
import GameCellOuter from './GameCellOuter';
import GameCellList from './GameCellList';
import { boardConfigMap } from '../../utils/constants/boardConfigMap';
import { useGameSocket } from '../GameSocketProvider/GameSocketProvider';
import { GameDifficulty } from '../../types/gameDifficulty';

interface GameBoardProps {
  difficulty: GameDifficulty;
  isLoading: boolean;
  className?: string;
}

const LoadingBoard: React.FC<{ difficulty: GameDifficulty }> = ({ difficulty }) => {
  const { rows, cols } = boardConfigMap[difficulty];
  return (
    <GameCellList
      rows={rows}
      cols={cols}
      renderCell={({ rIdx, cIdx }) => {
        return (
          <GameCellOuter key={`${difficulty}-${rIdx}-${cIdx}`} difficulty={difficulty}>
            <div className={cellStyles.cellLoading} />
          </GameCellOuter>
        );
      }}
    />
  );
};

const ReadyBoard: React.FC<{ game: GameStart }> = ({ game }) => {
  return (
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
};

const GameBoard: React.FC<GameBoardProps> = ({ difficulty, isLoading, className = '' }) => {
  const [game, setGame] = useState<GameStart>();
  const dispatch = useAppDispatch();
  const { sock } = useGameSocket();

  // We'll request a new game whenever the difficulty or loading state changes,
  // which will occur on any page refresh or game control change.
  useEffect(() => {
    if (isLoading) {
      sock.sendMsg('NEW_GAME', { difficulty });
    }
  }, [difficulty, isLoading, sock]);

  // Subscribes to new game socket event
  useEffect(() => {
    const handleOnNewGame = (gameStart: GameStart) => {
      setGame(gameStart);
      dispatch(setIsLoading(false));
    };

    sock.addOnNewGame(handleOnNewGame);

    return () => {
      sock.removeOnNewGame(handleOnNewGame);
    };
  }, [dispatch, sock]);

  const gameBoard = isLoading ? (
    <LoadingBoard difficulty={difficulty} />
  ) : game ? (
    <ReadyBoard game={game} />
  ) : null;

  return <div className={`${styles[`board-${difficulty}`]} ${className}`}>{gameBoard}</div>;
};

export default GameBoard;
