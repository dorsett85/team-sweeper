import React, { FormEventHandler, memo } from 'react';
import styles from './GameControl.module.less';
import buttonStyles from '../../styles/button.module.less';
import {
  SinglePlayerState,
  setDifficulty,
  setIsLoading
} from '../../pages/single-player/singlePlayerSlice';
import { useAppDispatch, useAppSelector } from '../../pages/single-player/singlePlayerStore';
import { DIFFICULTY } from '../../pages/single-player/constants/difficulty';
import { GameStart } from '../../types/game';

const difficultyMap: Record<GameStart['difficulty'], string> = {
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
  const isLoading = useAppSelector((state) => state.isLoading);
  const dispatch = useAppDispatch();

  const handleOnSelect: FormEventHandler<HTMLSelectElement> = ({ currentTarget }) => {
    if (!isLoading) {
      // Save the difficulty setting for when the user returns to the page
      localStorage.setItem(DIFFICULTY, currentTarget.value);

      dispatch(setIsLoading(true));
      dispatch(setDifficulty(currentTarget.value as SinglePlayerState['difficulty']));
    }
  };

  const handleOnResetClick = () => {
    if (!isLoading) {
      dispatch(setIsLoading(true));
    }
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
      <button onClick={handleOnResetClick} className={buttonStyles.baseButton}>
        Reset Game
      </button>
    </div>
  );
};

export default memo(GameControl);
