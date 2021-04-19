import React, { FormEventHandler } from 'react';
import styles from './GameControl.module.less';
import {
  SinglePlayerState,
  setDifficulty,
  setIsLoading
} from '../../pages/single-player/singlePlayerSlice';
import { useAppDispatch, useAppSelector } from '../../pages/single-player/singlePlayerStore';

const difficultyMap: Record<SinglePlayerState['difficulty'], 'Easy' | 'Medium' | 'Hard'> = {
  e: 'Easy',
  m: 'Medium',
  h: 'Hard'
} as const;

const difficultyOptions = Object.entries(difficultyMap).map(([key, value]) => (
  <option key={key} value={key}>
    {value}
  </option>
));

const GameControl: React.FC = () => {
  const difficulty = useAppSelector((state) => state.difficulty);
  const dispatch = useAppDispatch();

  const handleOnSelect: FormEventHandler<HTMLSelectElement> = ({ currentTarget }) => {
    dispatch(setIsLoading(true));
    dispatch(setDifficulty(currentTarget.value as SinglePlayerState['difficulty']));
  };

  const handleOnResetClick = () => {
    dispatch(setIsLoading(true));
  };

  return (
    <div className={styles.gameControlContainer}>
      <select
        onInput={handleOnSelect}
        className={styles.gameControlDifficultySelect}
        value={difficulty}
      >
        {difficultyOptions}
      </select>
      <button onClick={handleOnResetClick} className={styles.gameControlResetBtn}>
        Reset Game
      </button>
    </div>
  );
};

export default GameControl;
