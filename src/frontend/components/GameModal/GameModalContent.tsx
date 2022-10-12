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
  const [mostWinUncovers, setMostWinUncovers] = useState<null | number>();
  const [highestWinScore, sethighestWinScore] = useState<null | number>();

  const gameDate = new Date(gameEnd.duration);

  const isFastestGame =
    fastestWinTime !== undefined &&
    gameEnd.status === 'WON' &&
    (fastestWinTime === null || gameEnd.duration <= fastestWinTime);

  const isMostUncovers =
    mostWinUncovers !== undefined &&
    gameEnd.status === 'WON' &&
    (mostWinUncovers === null || gameEnd.uncovers >= mostWinUncovers);

  const isHighestScore =
    highestWinScore !== undefined &&
    gameEnd.status === 'WON' &&
    (highestWinScore === null || gameEnd.score >= highestWinScore);

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
          <span className={styles.timeText}>{gameEnd.uncovers}</span>
          <span className={styles.timeType}>uncovers</span>
        </div>
      </section>
      {isMostUncovers && (
        <strong className={styles.fastestWin}>Excellent, most points so far!</strong>
      )}
      <section className={styles.summary}>
        <div className={styles.timeBox}>
          <span className={styles.timeText}>{gameEnd.score.toFixed(2)}</span>
          <span className={styles.timeType}>score</span>
        </div>
      </section>
      {isHighestScore && <strong className={styles.fastestWin}>Wow, new high score!</strong>}
      <SessionStatsSummary
        onStatsLoaded={(time, uncovers, score) => {
          setFastestWinTime(time);
          setMostWinUncovers(uncovers);
          sethighestWinScore(score);
        }}
      />
    </>
  );
};

export default GameModalContent;
