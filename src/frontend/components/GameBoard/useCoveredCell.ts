import { Cell } from '../../types/Game';
import { MutableRefObject, useEffect, useRef, useState } from 'react';
import sock from '../../utils/GameSocket';
import styles from './GameCell.module.less';

/**
 * Props are passed to the covered cell react button element
 */
interface UseCoveredCellReturn {
  ref: MutableRefObject<HTMLButtonElement | null>;
  className: string;
}

/**
 * Return a ref for the covered cell and initialize behavior for when the
 * cell is uncovered.
 *
 * **NOTE** We're using direct DOM manipulation here for better performance.
 */
export const useCoveredCell = ({
  rowIdx,
  colIdx
}: Pick<Cell, 'rowIdx' | 'colIdx'>): UseCoveredCellReturn => {
  const coveredCellRef = useRef<HTMLButtonElement | null>(null);
  const [className, setClassName] = useState(styles.cellCovered);

  // Initial event listener setup
  useEffect(() => {
    const cell = coveredCellRef.current;

    // Initialize a listener to remove the uncovered cell button after all of
    // the css transitions have finished.
    let transitionRunListener: (e: TransitionEvent) => void = () => undefined;
    let transitionEndListener: (e: TransitionEvent) => void = () => undefined;

    if (cell) {
      // Subscribe to the onUncoverCell Event
      sock.addOnUncoverCell({ rowIdx, colIdx }, () => {
        if (!cell.disabled) {
          cell.disabled = true;
          setClassName((currentState) => `${currentState} ${styles.cellCoverRemoved}`);
        }
      });

      // Onclick handler sends a message to the server to uncover it
      cell.onclick = () => sock.sendJson({ rowIdx, colIdx });

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
      sock.removeOnUncoverCell({ rowIdx, colIdx });
    };
  }, [colIdx, rowIdx]);

  return { ref: coveredCellRef, className };
};
