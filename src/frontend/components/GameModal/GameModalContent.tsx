import React, { useState } from 'react';
import { GameEnd } from '../../types/game';
import styles from './GameModal.module.less';
import SessionStatsSummary from '../SessionSummary/SessionStatsSummary';
import { dateToMinutesString, dateToSecondsString } from '../../utils/dateToUnitString';

interface GameModalContentProps {
  gameEnd: GameEnd;
}

const GameModalContent: React.FC<GameModalContentProps> = ({ gameEnd }) => {
  const [isFastestGame, setIsFastestGame] = useState(false);

  const gameDate = new Date(gameEnd.duration);

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
      <SessionStatsSummary gameEnd={gameEnd} onProcessedStats={setIsFastestGame} />
    </>
  );
};

export default GameModalContent;
