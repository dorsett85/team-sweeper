import React, { useEffect, useRef, useState } from 'react';
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

    sock.addOnStartGame(handleOnStartGame);
    sock.addOnEndGame(handleOnEndGame);

    return () => {
      sock.removeOnStartGame(handleOnStartGame);
      sock.removeOnEndGame(handleOnEndGame);
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
        <span>Points:</span> <span className={styles.pillText}>0</span>
      </div>
    </div>
  );
};

export default GameScore;
