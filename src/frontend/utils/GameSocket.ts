import { Cell } from '../types/Board';

type UncoverCellParam = Pick<Cell, 'rowIdx' | 'colIdx'>;

export class GameSocket {
  /**
   * A map where the keys represent the row and column index of a given cell in the format
   * "<ROW_IDX>-<COLUMN_IDX>", and the value is a callback function that is called when the cell is
   * uncovered.
   */
  private onUncoverCellMap: Record<string, () => void> = {};

  private sock: WebSocket;

  public constructor(url: string) {
    this.sock = new WebSocket(url);
    this.addSockHandlers();
  }

  private addSockHandlers(): void {
    // Successfully opened socket connection
    this.sock.onopen = (e) => {
      console.log('Socket connection is open:', e);
      // TODO fire on open callbacks
    };

    // Successfully closed socket connection
    this.sock.onclose = (e) => {
      console.log('Socket connection is closed:', e);
      // TODO fire on close callbacks
    };

    // Failed socket connection
    this.sock.onerror = (e) => {
      console.log(e);
    };

    // Handle published messages
    this.sock.onmessage = (e) => {
      console.log('Message data from server:', e.data);
      try {
        const { rowIdx, colIdx } = JSON.parse(e.data);
        this.handleOnUncoverCell({ rowIdx, colIdx });
      } catch (e) {
        console.error('Unable to handle the server message');
      }
    };
  }

  /**
   * Same as Websocket send method, but serializes to json first
   */
  public sendJson<T>(data: T): void {
    this.sock.send(JSON.stringify(data));
  }

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

const sock = new GameSocket('ws://localhost:8080/publish');
export default sock;
