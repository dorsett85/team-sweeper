import { Cell } from '../types/Game';

type CellIndexParam = Pick<Cell, 'rowIdx' | 'colIdx'>;
type UncoverCellParam = Pick<Cell, 'rowIdx' | 'colIdx' | 'value'>;

type UncoverCellCallback = (value: Cell['value']) => void;

export class GameSocket {
  /**
   * A map where the keys represent the row and column index of a given cell in the format
   * "<ROW_IDX>-<COLUMN_IDX>", and the value is a callback function that is called when the cell is
   * uncovered.
   */
  private onUncoverCellMap: Record<string, UncoverCellCallback> = {};

  private sock: WebSocket;

  public constructor(url: string) {
    this.sock = new WebSocket(url);
    this.addSockHandlers();
  }

  private addSockHandlers(): void {
    // Successfully opened socket connection
    this.sock.onopen = () => {
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
        const { rowIdx, colIdx, value } = JSON.parse(e.data);
        this.handleOnUncoverCell({ rowIdx, colIdx, value });
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
   * Add a callback function to the onUncoverCellMap. This is the uncover cell
   * event "subscription".
   */
  public addOnUncoverCell({ rowIdx, colIdx }: CellIndexParam, callback: UncoverCellCallback): void {
    this.onUncoverCellMap[`${rowIdx}-${colIdx}`] = callback;
  }

  /**
   * Remove the callback function to the onUncoverCellMap
   */
  public removeOnUncoverCell({ rowIdx, colIdx }: CellIndexParam): void {
    delete this.onUncoverCellMap[`${rowIdx}-${colIdx}`];
  }

  /**
   * Fire the uncover cell callback for the given row/column index
   */
  public handleOnUncoverCell({ rowIdx, colIdx, value }: UncoverCellParam): void {
    this.onUncoverCellMap[`${rowIdx}-${colIdx}`]?.(value);
  }
}

const sock = new GameSocket('ws://localhost:8080/game/publish');
export default sock;
