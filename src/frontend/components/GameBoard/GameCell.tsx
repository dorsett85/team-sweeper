import React, { memo, useEffect, useRef, useState } from 'react';
import styles from './GameCell.module.less';
import { Cell, GameStart } from '../../types/game';
import { useGameSocket } from '../GameSocketProvider/GameSocketProvider';
import { SocketMessageSendType } from '../../utils/GameSocket';
import GameCellOuter from './GameCellOuter';

interface GameCellProps extends Pick<Cell, 'rowIdx' | 'colIdx'> {
  difficulty: GameStart['difficulty'];
  gameId: GameStart['id'];
}

const GameCell: React.FC<GameCellProps> = ({ difficulty, gameId, rowIdx, colIdx }) => {
  const [value, setValue] = useState<Cell['value']>();
  const { sock } = useGameSocket();
  const coveredCellRef = useRef<HTMLButtonElement | null>(null);
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

    // Initialize listeners to remove the covered cell button after all of the
    // css transitions have finished.
    let transitionRunListener: (e: TransitionEvent) => void = () => undefined;
    let transitionEndListener: (e: TransitionEvent) => void = () => undefined;

    if (cell) {
      // We want to REMOVE the button from the DOM, but after all of the
      // transitions have happened. We'll count all of the transition starts so
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

  const handleOnCoveredCellClick = () => {
    // Leaving this as a separate handler for now, may add more behavior here
    isCellClicked.current = true;
    sock.sendMsg({
      type: SocketMessageSendType.UNCOVER_CELL,
      payload: { gameId, rowIdx, colIdx }
    });
  };

  const isMine = value === 'x';
  const isClicked = isCellClicked.current;

  return (
    <GameCellOuter difficulty={difficulty}>
      <button
        ref={coveredCellRef}
        className={`${styles.coveredCell} ${value ? styles.coveredCellRemoved : ''}`}
        onClick={handleOnCoveredCellClick}
        disabled={!!value}
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
