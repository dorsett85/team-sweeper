import React, { FormEventHandler } from 'react';
import styles from './GameControl.module.less';
import {
  SinglePlayerState,
  setDifficulty,
  setIsLoading
} from '../../pages/single-player/singlePlayerSlice';
import { useAppDispatch, useAppSelector } from '../../pages/single-player/singlePlayerStore';
import { DIFFICULTY } from '../../pages/single-player/constants/difficulty';

const difficultyMap = {
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
    // Save the difficulty setting for when the user returns to the page
    localStorage.setItem(DIFFICULTY, currentTarget.value);

    dispatch(setIsLoading(true));
    dispatch(setDifficulty(currentTarget.value as SinglePlayerState['difficulty']));
  };

  const handleOnResetClick = () => {
    dispatch(setIsLoading(true));
  };

  return (
    <div className={styles.gameControlContainer}>
      <div className={styles.gameControlSelectWrapper}>
        <select
          onInput={handleOnSelect}
          className={styles.gameControlDifficultySelect}
          value={difficulty}
          aria-label='select difficulty'
        >
          {difficultyOptions}
        </select>
        <span className={styles.dropdownIcon}>▼</span>
      </div>
      <button onClick={handleOnResetClick} className={styles.gameControlResetBtn}>
        Reset Game
      </button>
    </div>
  );
};

export default GameControl;
