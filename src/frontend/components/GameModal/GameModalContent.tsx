import React, { useState } from 'react';
import { GameEnd } from '../../types/game';
import styles from './GameModal.module.less';
import SessionStatsSummary from '../SessionSummary/SessionStatsSummary';
import { dateToMinutesString, dateToSecondsString } from '../../utils/dateToUnitString';

interface GameModalContentProps {
  gameEnd: GameEnd;
}

type RecordStat = undefined | null | number;

/**
 * Calculates the all-time record stat against the current game stat used in the
 * predicate function.
 */
const calcNewRecord = <TStat extends RecordStat>(
  stat: TStat,
  predicate: (knownStat: Extract<RecordStat, number>) => boolean
): boolean => {
  return stat !== undefined && (stat === null || predicate(stat));
};

const GameModalContent: React.FC<GameModalContentProps> = ({ gameEnd }) => {
  const [fastestWinTime, setFastestWinTime] = useState<RecordStat>();
  const [mostWinUncovers, setMostWinUncovers] = useState<RecordStat>();
  const [highestWinScore, setHighestWinScore] = useState<RecordStat>();

  const gameDate = new Date(gameEnd.duration);

  let isFastestGame = false;
  let isMostUncovers = false;
  let isHighestScore = false;
  if (gameEnd.status === 'WON') {
    isFastestGame = calcNewRecord(fastestWinTime, (record) => gameEnd.duration <= record);
    isMostUncovers = calcNewRecord(mostWinUncovers, (record) => gameEnd.uncovers >= record);
    isHighestScore = calcNewRecord(highestWinScore, (record) => gameEnd.score >= record);
  }

  const recordLiText: string[] = [
    isFastestGame && 'Nice, new fastest time',
    isMostUncovers && 'Excellent, most uncovers so far!',
    isHighestScore && 'Wow, new high score!'
  ].filter((text): text is string => !!text);

  const recordList = !!recordLiText.length && (
    <ul className={styles.recordList}>
      {recordLiText.map((text) => {
        return (
          <li key={text}>
            <strong>{text}</strong>
          </li>
        );
      })}
    </ul>
  );

  return (
    <>
      <section className={styles.boxSection}>
        <div className={styles.boxScore}>
          <div className={styles.timeBox}>
            <div className={styles.timePart}>
              <span className={styles.statText}>{dateToMinutesString(gameDate)}</span>
              <span className={styles.typeText}>Minutes</span>
            </div>
            <div className={styles.timeSeparator}>:</div>
            <div className={styles.timePart}>
              <span className={styles.statText}>{dateToSecondsString(gameDate)}</span>
              <span className={styles.typeText}>Seconds</span>
            </div>
          </div>
          <div className={styles.statBoxOuter}>
            <div className={styles.statBox}>
              <span className={styles.statText}>{gameEnd.uncovers}</span>
              <span className={styles.typeText}>Uncovers</span>
            </div>
            <div className={styles.statBox}>
              <span className={styles.statText}>{gameEnd.score.toFixed(2)}</span>
              <span className={styles.typeText}>SCORE</span>
            </div>
          </div>
        </div>
      </section>
      <section>{recordList}</section>
      <SessionStatsSummary
        onStatsLoaded={(time, uncovers, score) => {
          setFastestWinTime(time);
          setMostWinUncovers(uncovers);
          setHighestWinScore(score);
        }}
      />
    </>
  );
};

export default GameModalContent;
