import { Cell, GameEnd, GameStart } from '../types/game';
import { GameDifficulty } from '../types/gameDifficulty';

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

type CellIndexes = Pick<Cell, 'rowIdx' | 'colIdx'>;

/**
 * Key/value pairs of socket message send types and corresponding payload
 */
type MessageSendPayloadMap = {
  NEW_GAME: { difficulty: GameDifficulty };
  UNCOVER_CELL: CellIndexes & { gameId: GameStart['id'] };
};

/**
 * Key/value pairs of socket message receive types and corresponding payload
 */
type MessageReceivePayloadMap = {
  NEW_GAME: GameStart;
  START_GAME: boolean;
  UNCOVER_CELL: Pick<Cell, keyof CellIndexes | 'value'> & { points: number };
  ADJUST_POINTS: { points: number };
  END_GAME: GameEnd;
};

/**
 * Values for the type property when sending socket messages
 */
export type SocketMessageSendType = keyof MessageSendPayloadMap;

/**
 * Values for the type property when receiving socket messages
 */
export type SocketMessageReceiveType = keyof MessageReceivePayloadMap;

type SocketMessageType = SocketMessageSendType | SocketMessageReceiveType;

/**
 * Object for sending and receiving socket messages. The server will know which
 * action to dispatch based on the type as well as know what the corresponding
 * payload is.
 */
interface SocketMessage<TType extends SocketMessageType, TPayload> {
  /**
   * Type of the message which will be used to dispatch actions
   */
  type: TType;
  /**
   * Data associated with the type
   */
  payload: TPayload;
}

export type SocketMessageSend<TType extends SocketMessageSendType> = SocketMessage<
  TType,
  MessageSendPayloadMap[TType]
>;

type DispatchMap = {
  [K in keyof MessageReceivePayloadMap]: (payload: MessageReceivePayloadMap[K]) => void;
};

type NewGameCallback = DispatchMap['NEW_GAME'];
type StartGameCallback = DispatchMap['START_GAME'];
type UncoverCellCallback = (value: Cell['value']) => void;
type AdjustPointsCallback = (points: number) => void;
type EndGameCallback = DispatchMap['END_GAME'];
type UncoverCellParam = MessageReceivePayloadMap['UNCOVER_CELL'];
type AdjustPointsParam = MessageReceivePayloadMap['ADJUST_POINTS'];

export class GameSocket {
  private readonly sock: WebSocket;
  /**
   * Set of callbacks to fire when the game starts
   */
  private readonly onNewGameSet: Set<NewGameCallback> = new Set();
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
  private readonly onAdjustPointsSet: Set<AdjustPointsCallback> = new Set();
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
      NEW_GAME: (payload) => this.handleOnNewGame(payload),
      START_GAME: (payload) => this.handleOnStartGame(payload),
      UNCOVER_CELL: (payload) => this.handleOnUncoverCell(payload),
      ADJUST_POINTS: (payload) => this.handleOnAdjustPoints(payload),
      END_GAME: (status) => this.handleOnEndGame(status)
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
  public sendMsg<TType extends SocketMessageSendType>(
    type: TType,
    payload: SocketMessageSend<TType>['payload']
  ): void {
    this.sock.send(JSON.stringify({ type, payload }));
  }

  /**
   * Add a callback to the onNewGame set
   */
  public addOnNewGame(callback: NewGameCallback): void {
    this.onNewGameSet.add(callback);
  }

  /**
   * Remove a callback from the onNewGame set
   */
  public removeOnNewGame(callback: NewGameCallback): boolean {
    return this.onNewGameSet.delete(callback);
  }

  /**
   * Handler for when a new game is received
   */
  public handleOnNewGame(gameStart: GameStart): void {
    this.onNewGameSet.forEach((callback) => callback(gameStart));
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
   * Handler for when the server says to uncover a cell
   */
  private handleOnUncoverCell({ rowIdx, colIdx, value }: UncoverCellParam): void {
    this.onUncoverCellMap[`${rowIdx}-${colIdx}`]?.(value);
  }

  /**
   * Add a callback to the onAdjustPoints set
   */
  public addOnAdjustPoints(callback: AdjustPointsCallback): void {
    this.onAdjustPointsSet.add(callback);
  }

  /**
   * Remove a callback from the onAdjustPoints set
   */
  public removeOnAdjustPoints(callback: AdjustPointsCallback): boolean {
    return this.onAdjustPointsSet.delete(callback);
  }

  /**
   * Handler for when the server adjusts the user's points
   */
  private handleOnAdjustPoints({ points }: AdjustPointsParam): void {
    this.onAdjustPointsSet.forEach((callback) => callback(points));
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
