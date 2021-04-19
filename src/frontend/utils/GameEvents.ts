import { TsCell } from '../types/TsBoardResponse';

type UncoverCellParam = Pick<TsCell, 'rowIdx' | 'colIdx'>;

export class GameEvents {
  /**
   * A map where the keys represent the row and column index of a given cell in the format
   * "<ROW_IDX>-<COLUMN_IDX>", and the value is a callback function that is called when the cell is
   * uncovered.
   */
  private onUncoverCellMap: Record<string, () => void> = {};

  /**
   * Add a callback function to the onUncoverCellMap. This is the uncover cell event "subscription"
   */
  public addOnUncoverCell({ rowIdx, colIdx }: UncoverCellParam, callback: () => void): void {
    this.onUncoverCellMap[`${rowIdx}-${colIdx}`] = callback;
  }

  /**
   * Remove the callback function to the onUncoverCellMap
   */
  public removeOnUncoverCell({ rowIdx, colIdx }: UncoverCellParam): void {
    delete this.onUncoverCellMap[`${rowIdx}-${colIdx}`];
  }

  /**
   * Fire the uncover cell callback for the given row/column index
   */
  public handleOnUncoverCell({ rowIdx, colIdx }: UncoverCellParam): void {
    this.onUncoverCellMap[`${rowIdx}-${colIdx}`]?.();
  }

  /**
   * TODO *** DELETE once the web socket connection is setup ***
   */
  public uncoverNearbyCells({ rowIdx, colIdx }: UncoverCellParam): void {
    const surroundingCells = [
      [-1, -1],
      [-1, 0],
      [-1, 1],
      [0, 1],
      [1, 1],
      [1, 0],
      [1, -1],
      [0, -1]
    ];

    surroundingCells.forEach(([rIdx, cIdx]) => {
      this.handleOnUncoverCell({
        rowIdx: rowIdx + rIdx,
        colIdx: colIdx + cIdx
      });
    });
  }

  /**
   * TODO *** DELETE once the web socket connection is setup ***
   */
  public uncoverAllCells(): void {
    Object.values(this.onUncoverCellMap).forEach((callback) => callback());
  }
}

const gameEvents = new GameEvents();
export default gameEvents;
