import React, { memo } from 'react';
import styles from './GameControl.module.less';
import buttonStyles from '../../styles/button.module.less';
import { setDifficulty, setIsLoading } from '../../pages/single-player/singlePlayerSlice';
import { useAppDispatch } from '../../pages/single-player/singlePlayerStore';
import { DIFFICULTY } from '../../pages/single-player/constants/difficulty';
import { GameDifficulty } from '../../types/gameDifficulty';
import { SelectDifficulty } from '../SelectDifficulty/SelectDifficulty';

interface GameControlProps {
  difficulty: GameDifficulty;
  isLoading: boolean;
}

const GameControl: React.FC<GameControlProps> = ({ difficulty, isLoading }) => {
  const dispatch = useAppDispatch();

  const handleOnSelect = (newDifficulty: GameDifficulty) => {
    if (!isLoading) {
      // Save the difficulty setting for when the user returns to the page
      localStorage.setItem(DIFFICULTY, newDifficulty);

      dispatch(setIsLoading(true));
      dispatch(setDifficulty(newDifficulty));
    }
  };

  const handleOnResetClick = () => {
    if (!isLoading) {
      dispatch(setIsLoading(true));
    }
  };

  return (
    <div className={styles.gameControlContainer}>
      <SelectDifficulty difficulty={difficulty} onSelect={handleOnSelect} />
      <button onClick={handleOnResetClick} className={buttonStyles.baseButton}>
        Reset Game
      </button>
    </div>
  );
};

export default memo(GameControl);
