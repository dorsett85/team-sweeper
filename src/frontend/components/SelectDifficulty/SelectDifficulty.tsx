import React from 'react';
import styles from './SelectDifficulty.module.less';
import { GameStart } from '../../types/game';
import { GameDifficulty } from '../../types/gameDifficulty';

interface SelectDifficultyProps {
  difficulty: GameDifficulty;

  /**
   * Called when an option is selected from the select element
   */
  onSelect(difficulty: GameDifficulty): void;

  /**
   * Passed to the outer element
   */
  className?: string;
}

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

export const SelectDifficulty: React.FC<SelectDifficultyProps> = ({
  difficulty,
  onSelect,
  className = ''
}) => {
  const handleOnInput: React.ChangeEventHandler<HTMLSelectElement> = (e) => {
    onSelect(e.currentTarget.value as GameDifficulty);
  };

  return (
    <div className={`${styles.selectWrapper} ${className}`}>
      <select
        onInput={handleOnInput}
        className={styles.selectDifficulty}
        value={difficulty}
        aria-label='select difficulty'
      >
        {difficultyOptions}
      </select>
      <span className={styles.dropdownIcon}>▼</span>
    </div>
  );
};
