import { Cell, GameEnd, GameStart } from '../types/game';

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
 * Values for the type property when sending socket messages.
 */
export enum SocketMessageSendType {
  UNCOVER_CELL = 'UNCOVER_CELL'
}

/**
 * Values for the type property when sending socket messages.
 */
export enum SocketMessageReceiveType {
  START_GAME = 'START_GAME',
  UNCOVER_CELL = 'UNCOVER_CELL',
  END_GAME = 'END_GAME'
}

/**
 * Object for sending and receiving socket messages. The server will know which
 * action to dispatch based on the type as well as know what the corresponding
 * payload is.
 */
interface SocketMessage<TType, TPayload> {
  /**
   * Type of the message which will be used to dispatch actions on the server
   */
  type: TType;
  /**
   * Data associated with the type
   */
  payload: TPayload;
}

type SocketMessageSend = SocketMessage<
  SocketMessageSendType.UNCOVER_CELL,
  CellIndexes & { gameId: GameStart['id'] }
>;

type UncoverCellParam = Pick<Cell, 'rowIdx' | 'colIdx' | 'value'>;
type CellIndexes = Pick<Cell, 'rowIdx' | 'colIdx'>;

type StartGameCallback = (startGame: boolean) => void;

type UncoverCellCallback = (value: Cell['value']) => void;

type EndGameCallback = (gameEnd: GameEnd) => void;

interface DispatchMap {
  [SocketMessageReceiveType.START_GAME]: (payload: boolean) => void;
  [SocketMessageReceiveType.UNCOVER_CELL]: (payload: UncoverCellParam) => void;
  [SocketMessageReceiveType.END_GAME]: EndGameCallback;
}

export class GameSocket {
  private readonly sock: WebSocket;
  /**
   * Set of callbacks to fire when the game starts
   */
  private readonly onStartGameSet: Set<StartGameCallback> = new Set();
  /**
   * A map where the keys represent the row and column index of a given cell in
   * the format "<ROW_IDX>-<COLUMN_IDX>", and the value is a callback function
   * that is called when the cell is uncovered.
   */
  private readonly onUncoverCellMap: Record<string, UncoverCellCallback> = {};
  /**
   * Set of callbacks to fire when the game ends
   */
  private readonly onEndGameSet: Set<EndGameCallback> = new Set();
  /**
   * This map will look up the correct dispatch method to call with the payload
   * property as the argument. The lookup is based on the "type" property sent
   * from the server.
   */
  private readonly dispatchMap: DispatchMap;

  public constructor(url: string, readyStateHandlers: ReadyStateHandlers) {
    this.sock = new WebSocket(url);
    this.addSockHandlers(readyStateHandlers);

    // Instantiate all of our message handlers
    this.dispatchMap = {
      [SocketMessageReceiveType.START_GAME]: (payload) => this.handleOnStartGame(payload),
      [SocketMessageReceiveType.UNCOVER_CELL]: (payload) => this.handleOnUncoverCell(payload),
      [SocketMessageReceiveType.END_GAME]: (status) => this.handleOnEndGame(status)
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
        // property will look up the correct dispatch method to call with the
        // payload property as the argument.
        const { type, payload } = JSON.parse(e.data);
        this.dispatchMap[type as SocketMessageReceiveType](payload);
      } catch (e) {
        console.error('Unable to handle the server message: ', e);
      }
    };
  }

  /**
   * Same as Websocket send method, but serializes to json first
   */
  public sendMsg(data: SocketMessageSend): void {
    this.sock.send(JSON.stringify(data));
  }

  /**
   * Add a callback to the onStartGame set
   */
  public addOnStartGame(callback: StartGameCallback): void {
    this.onStartGameSet.add(callback);
  }

  /**
   * Remove a callback from the onStartGame set
   */
  public removeOnStartGame(callback: StartGameCallback): boolean {
    return this.onStartGameSet.delete(callback);
  }

  /**
   * Handler for when the server says the game starts
   */
  private handleOnStartGame(startGame: boolean) {
    this.onStartGameSet.forEach((callback) => callback(startGame));
  }

  /**
   * Add a callback to the onUncoverCellMap. This is the uncover cell event
   * "subscription".
   */
  public addOnUncoverCell({ rowIdx, colIdx }: CellIndexes, callback: UncoverCellCallback): void {
    this.onUncoverCellMap[`${rowIdx}-${colIdx}`] = callback;
  }

  /**
   * Remove a callback from the onUncoverCellMap
   */
  public removeOnUncoverCell({ rowIdx, colIdx }: CellIndexes): void {
    delete this.onUncoverCellMap[`${rowIdx}-${colIdx}`];
  }

  /**
   * Fire the uncover cell callback for the given row/column index
   */
  private handleOnUncoverCell({ rowIdx, colIdx, value }: UncoverCellParam): void {
    this.onUncoverCellMap[`${rowIdx}-${colIdx}`]?.(value);
  }

  /**
   * Add a callback to the onEndGame set
   */
  public addOnEndGame(callback: EndGameCallback): void {
    this.onEndGameSet.add(callback);
  }

  /**
   * Remove a callback from the onEndGame set
   */
  public removeOnEndGame(callback: EndGameCallback): boolean {
    return this.onEndGameSet.delete(callback);
  }

  /**
   * Handler for when the server says the game has either been won or lost
   */
  private handleOnEndGame(gameEnd: GameEnd): void {
    this.onEndGameSet.forEach((callback) => callback(gameEnd));
  }
}
