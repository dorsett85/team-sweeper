import React, { useState } from 'react';
import { GameEnd } from '../../types/game';
import styles from './GameModal.module.less';
import SessionStatsSummary from '../SessionSummary/SessionStatsSummary';
import { dateToMinutesString, dateToSecondsString } from '../../utils/dateToUnitString';

interface GameModalContentProps {
  gameEnd: GameEnd;
}

const GameModalContent: React.FC<GameModalContentProps> = ({ gameEnd }) => {
  const [fastestWinTime, setFastestWinTime] = useState<null | number>();
  const [mostWinPoints, setMostWinPoints] = useState<null | number>();

  const gameDate = new Date(gameEnd.duration);

  const isFastestGame =
    fastestWinTime !== undefined &&
    gameEnd.status === 'WON' &&
    (fastestWinTime === null || gameEnd.duration <= fastestWinTime);

  const isHighestPoints =
    mostWinPoints !== undefined &&
    gameEnd.status === 'WON' &&
    (mostWinPoints === null || gameEnd.points >= mostWinPoints);

  return (
    <>
      <section className={styles.summary}>
        <div className={styles.timeBox}>
          <span className={styles.timeText}>{dateToMinutesString(gameDate)}</span>
          <span className={styles.timeType}>minutes</span>
        </div>
        <div className={styles.timeSeparator}>:</div>
        <div className={styles.timeBox}>
          <span className={styles.timeText}>{dateToSecondsString(gameDate)}</span>
          <span className={styles.timeType}>seconds</span>
        </div>
      </section>
      {isFastestGame && <strong className={styles.fastestWin}>Nice, new fastest time</strong>}
      <section className={styles.summary}>
        <div className={styles.timeBox}>
          <span className={styles.timeText}>{gameEnd.points}</span>
          <span className={styles.timeType}>points</span>
        </div>
      </section>
      {isHighestPoints && (
        <strong className={styles.fastestWin}>Excellent, most points so far!</strong>
      )}
      <SessionStatsSummary
        onStatsLoaded={(time, points) => {
          setFastestWinTime(time);
          setMostWinPoints(points);
        }}
      />
    </>
  );
};

export default GameModalContent;
