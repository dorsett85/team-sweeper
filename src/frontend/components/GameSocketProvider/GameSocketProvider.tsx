import React, { createContext, useState } from 'react';
import { GameSocket } from '../../utils/GameSocket';

export type ReadyState = 'CONNECTING' | 'OPEN' | 'CLOSED';

interface GameSocketState {
  sock: GameSocket;
}

interface GameSocketProviderProps {
  /**
   * Url for the websocket connection
   */
  url: string;

  /**
   * Returns a jsx element to be rendered when the websocket is not open. This
   * has a function parameter that will attempt to establish a new connection.
   */
  renderGameSocketNotOpen(
    readyState: ReadyState,
    onConnectToGameSocket: () => void
  ): React.ReactElement;
}

const GameSocketContext = createContext<GameSocketState | undefined>(undefined);

export const useGameSocket = (): GameSocketState => {
  const context = React.useContext(GameSocketContext);
  if (context === undefined) {
    throw new Error('useGameSocket must be used within a GameSocketProvider');
  }
  return context;
};

const GameSocketProvider: React.FC<GameSocketProviderProps> = ({
  url,
  renderGameSocketNotOpen,
  children
}) => {
  const [readyState, setReadyState] = useState<ReadyState>('CONNECTING');
  const connectToGameSocket = () =>
    new GameSocket(url, {
      onOpen: () => setReadyState('OPEN'),
      onError: (e) => {
        console.error(e);
        setReadyState('CLOSED');
      },
      onClose: (e) => {
        console.warn('websocket closed:', e);
        setReadyState('CLOSED');
      }
    });
  const [sock, setSock] = useState<GameSocket>(() => connectToGameSocket());

  const handleOnConnectToGameSocket = () => {
    setSock(connectToGameSocket());
  };

  return (
    <GameSocketContext.Provider value={{ sock }}>
      {readyState === 'OPEN'
        ? children
        : renderGameSocketNotOpen(readyState, handleOnConnectToGameSocket)}
    </GameSocketContext.Provider>
  );
};

export default GameSocketProvider;
