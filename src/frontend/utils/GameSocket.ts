import { Cell, Game } from '../types/Game';

interface ReadyStateHandlers {
  /**
   * Called from websocket onopen event
   */
  onOpen: (e: Event) => void;
  /**
   * Called from websocket onerror event
   */
  onError: (e: Event) => void;
  /**
   * Called from websocket onclose event
   */
  onClose: (e: CloseEvent) => void;
}

/**
 * Allowed values for the type property when sending/receiving socket messages.
 * If we start having different values between sending and receiving then we can
 * have separate types.
 */
export enum SocketMessageType {
  UNCOVER_CELL = 'UNCOVER_CELL'
}

/**
 * Object for sending and receiving socket messages. The server will know which
 * action to dispatch based on the type as well as know what the corresponding
 * payload is.
 */
interface SocketMessage<TType extends SocketMessageType, TPayload> {
  /**
   * Type of the message which will be used to dispatch actions on the server
   */
  type: TType;
  /**
   * Data associated with the type
   */
  payload: TPayload;
}

type SocketSend = SocketMessage<
  SocketMessageType.UNCOVER_CELL,
  CellIndexes & { gameId: Game['id'] }
>;

type UncoverCellParam = Pick<Cell, 'rowIdx' | 'colIdx' | 'value'>;
type CellIndexes = Pick<Cell, 'rowIdx' | 'colIdx'>;

type UncoverCellCallback = (value: Cell['value']) => void;

// eslint-disable-next-line @typescript-eslint/no-explicit-any
interface DispatchMap {
  [SocketMessageType.UNCOVER_CELL]: (payload: UncoverCellParam) => void;
}

export class GameSocket {
  private sock: WebSocket;
  /**
   * A map where the keys represent the row and column index of a given cell in
   * the format "<ROW_IDX>-<COLUMN_IDX>", and the value is a callback function
   * that is called when the cell is uncovered.
   */
  private onUncoverCellMap: Record<string, UncoverCellCallback> = {};
  /**
   * This map will lookup the correct dispatch method to call with the payload
   * property as the argument. The lookup is based on the "type" property sent
   * from the server.
   */
  private readonly dispatchMap: DispatchMap;

  public constructor(url: string, readyStateHandlers: ReadyStateHandlers) {
    this.sock = new WebSocket(url);
    this.addSockHandlers(readyStateHandlers);

    // Instantiate all of our message handlers
    this.dispatchMap = {
      [SocketMessageType.UNCOVER_CELL]: (payload) => this.handleOnUncoverCell(payload)
    };
  }

  private addSockHandlers(readyStateHandlers: ReadyStateHandlers): void {
    // Successfully opened socket connection
    this.sock.onopen = (e) => {
      readyStateHandlers.onOpen(e);
    };

    // Failed socket connection
    this.sock.onerror = (e) => {
      readyStateHandlers.onError(e);
    };

    // Successfully closed socket connection
    this.sock.onclose = (e) => {
      readyStateHandlers.onClose(e);
    };

    // Handle published messages
    this.sock.onmessage = (e) => {
      try {
        // Parse the type and payload properties from the server. The type
        // property will lookup the correct dispatch method to call with the
        // payload property as the argument.
        const { type, payload } = JSON.parse(e.data);
        this.dispatchMap[type as SocketMessageType](payload);
      } catch (e) {
        console.error('Unable to handle the server message: ', e);
      }
    };
  }

  /**
   * Same as Websocket send method, but serializes to json first
   */
  public sendMsg(data: SocketSend): void {
    this.sock.send(JSON.stringify(data));
  }

  /**
   * Add a callback function to the onUncoverCellMap. This is the uncover cell
   * event "subscription".
   */
  public addOnUncoverCell({ rowIdx, colIdx }: CellIndexes, callback: UncoverCellCallback): void {
    this.onUncoverCellMap[`${rowIdx}-${colIdx}`] = callback;
  }

  /**
   * Remove the callback function to the onUncoverCellMap
   */
  public removeOnUncoverCell({ rowIdx, colIdx }: CellIndexes): void {
    delete this.onUncoverCellMap[`${rowIdx}-${colIdx}`];
  }

  /**
   * Fire the uncover cell callback for the given row/column index
   */
  public handleOnUncoverCell({ rowIdx, colIdx, value }: UncoverCellParam): void {
    this.onUncoverCellMap[`${rowIdx}-${colIdx}`]?.(value);
  }
}
