import React, { memo, useEffect, useRef, useState } from 'react';
import styles from './GameCell.module.less';
import { Cell, GameStart } from '../../types/game';
import { useGameSocket } from '../GameSocketProvider/GameSocketProvider';
import GameCellOuter from './GameCellOuter';

interface GameCellProps extends Pick<Cell, 'rowIdx' | 'colIdx'> {
  difficulty: GameStart['difficulty'];
  gameId: GameStart['id'];
}

const GameCell: React.FC<GameCellProps> = ({ difficulty, gameId, rowIdx, colIdx }) => {
  const [value, setValue] = useState<Cell['value']>();
  const [flag, setFlag] = useState(false);
  const { sock } = useGameSocket();
  const coveredCellRef = useRef<HTMLButtonElement | null>(null);
  const isButtonHeldRef = useRef(false);
  const wasFlagged = useRef(false);
  const isCellClicked = useRef(false);

  // Subscribe to the onUncoverCell event
  useEffect(() => {
    sock.addOnUncoverCell({ rowIdx, colIdx }, setValue);

    return () => {
      sock.removeOnUncoverCell({ rowIdx, colIdx });
    };
  }, [colIdx, rowIdx, sock]);

  // Create custom transition event where the covered cell is removed from the
  // DOM after the animations end.
  useEffect(() => {
    const cell = coveredCellRef.current;

    // Initialize listeners to remove the covered cell button after all the css
    // transitions have finished.
    let transitionRunListener: (e: TransitionEvent) => void;
    let transitionEndListener: (e: TransitionEvent) => void;

    if (cell) {
      // We want to REMOVE the button from the DOM, but after all the
      // transitions have happened. We'll count all the transition starts so
      // we know when to delete the button element on the last transition end.
      let transitionRunCount = 0;
      let transitionEndCount = 0;
      transitionRunListener = () => transitionRunCount++;
      transitionEndListener = () => {
        transitionEndCount++;
        if (transitionRunCount === transitionEndCount) {
          cell.remove();
        }
      };
      cell.addEventListener('transitionrun', transitionRunListener);
      cell.addEventListener('transitionend', transitionEndListener);
    }

    return () => {
      cell?.removeEventListener('transitionrun', transitionRunListener);
      cell?.removeEventListener('transitionend', transitionEndListener);
    };
  }, []);

  const handleOnCoveredCellMouseDown = (e: React.MouseEvent) => {
    // Don't allow right clicks
    if (e.button === 2) {
      return;
    }

    isButtonHeldRef.current = true;

    // when the mouse is held down the cell will be flagged
    setTimeout(() => {
      if (isButtonHeldRef.current) {
        if (flag) {
          wasFlagged.current = true;
        }
        setFlag((oldState) => !oldState);
      }
    }, 300);
  };

  const handleOnCoveredCellClick = () => {
    isButtonHeldRef.current = false;

    // Don't allow the cell to be uncovered if it's flagged or if the user
    // removed the flag before releasing the hold.
    if (flag || wasFlagged.current) {
      wasFlagged.current = false;
      return;
    }

    // Leaving this as a separate handler for now, may add more behavior here
    isCellClicked.current = true;
    sock.sendMsg('UNCOVER_CELL', { gameId, rowIdx, colIdx });
  };

  const isMine = value === 'x';
  const isClicked = isCellClicked.current;

  return (
    <GameCellOuter difficulty={difficulty}>
      <button
        ref={coveredCellRef}
        className={`${!flag ? styles.coveredCell : styles.coveredCellFlagged} ${
          value ? styles.coveredCellRemoved : ''
        }`}
        onMouseDown={handleOnCoveredCellMouseDown}
        onClick={handleOnCoveredCellClick}
        disabled={!!value}
        aria-label={flag ? 'flagged' : undefined}
      />
      <div className={styles[isMine && isClicked ? 'uncoveredClickedMineCell' : 'uncoveredCell']}>
        {value && (
          <div
            className={
              styles[isMine ? `mineCell${isClicked ? 'Clicked' : ''}` : `nearbyMineCell-${value}`]
            }
          >
            {!['0', 'x'].includes(value) && value}
          </div>
        )}
      </div>
    </GameCellOuter>
  );
};

export default memo(GameCell);
