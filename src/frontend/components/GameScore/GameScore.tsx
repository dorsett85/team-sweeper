import React, { memo, useEffect, useRef, useState } from 'react';
import styles from './GameScore.module.less';
import { useGameSocket } from '../GameSocketProvider/GameSocketProvider';
import { useAppSelector } from '../../pages/single-player/singlePlayerStore';
import { dateToMinutesString, dateToSecondsString } from '../../utils/dateToUnitString';

interface GameScoreProps {
  className?: string;
}

const GameScore: React.FC<GameScoreProps> = ({ className = '' }) => {
  const gameIsLoading = useAppSelector((state) => state.isLoading);
  const [time, setTime] = useState(new Date(0));
  const [uncovers, setUncovers] = useState(0);
  const { sock } = useGameSocket();
  const timerIntervalRef = useRef<NodeJS.Timer>();

  // Reset the time when a new game is being loaded
  useEffect(() => {
    if (gameIsLoading) {
      setTime(new Date(0));
      if (timerIntervalRef.current) {
        clearInterval(timerIntervalRef.current);
      }
    }
  }, [gameIsLoading]);

  useEffect(() => {
    const handleOnNewGame = () => {
      setUncovers(0);
    };
    // When the game starts, increase the timer every second
    const handleOnStartGame = () => {
      timerIntervalRef.current = setInterval(() => {
        setTime((prevTime) => new Date(prevTime.getTime() + 1000));
      }, 1000);
    };
    const handleOnEndGame = () => {
      if (timerIntervalRef.current) {
        clearInterval(timerIntervalRef.current);
      }
    };
    const handleOnAdjustPoints = (newUncovers: number) => {
      setUncovers((oldUncovers) => oldUncovers + newUncovers);
    };

    sock.addOnNewGame(handleOnNewGame);
    sock.addOnStartGame(handleOnStartGame);
    sock.addOnEndGame(handleOnEndGame);
    sock.addOnAdjustPoints(handleOnAdjustPoints);

    return () => {
      sock.removeOnNewGame(handleOnNewGame);
      sock.removeOnStartGame(handleOnStartGame);
      sock.removeOnEndGame(handleOnEndGame);
      sock.removeOnAdjustPoints(handleOnAdjustPoints);
      if (timerIntervalRef.current) {
        clearInterval(timerIntervalRef.current);
      }
    };
  }, [sock]);

  return (
    <div className={`${styles.gameScoreContainer} ${className}`}>
      <div>
        <span>Time:</span>{' '}
        <span className={styles.pillText}>
          {dateToMinutesString(time)}:{dateToSecondsString(time)}
        </span>
      </div>
      <div>
        <span>Uncovers:</span> <span className={styles.pillText}>{uncovers}</span>
      </div>
    </div>
  );
};

export default memo(GameScore);
