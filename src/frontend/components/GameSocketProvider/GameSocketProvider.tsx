import React, { createContext, useMemo, useState } from 'react';
import { GameSocket } from '../../utils/GameSocket';

interface GameSocketState {
  sock: GameSocket;
  /**
   * State of the websocket connection
   */
  readyState: 'CONNECTING' | 'OPEN' | 'CLOSING' | 'CLOSED';
}

const GameSocketContext = createContext<GameSocketState | undefined>(undefined);

export const useGameSocket = (): GameSocketState => {
  const context = React.useContext(GameSocketContext);
  if (context === undefined) {
    throw new Error('useGameSocket must be used within a GameSocketProvider');
  }
  return context;
};

const GameSocketProvider: React.FC = ({ children }) => {
  const [readyState, setReadyState] = useState<GameSocketState['readyState']>('CONNECTING');
  const sock = useMemo(
    () =>
      new GameSocket('ws://localhost:8080/game/publish', {
        onOpen: () => setReadyState('OPEN'),
        onError: () => setReadyState('CLOSED'),
        onClose: () => setReadyState('CLOSED')
      }),
    []
  );

  return (
    <GameSocketContext.Provider value={{ readyState, sock }}>
      {/* TODO add fallback component if the socket connection closes */}
      {readyState === 'OPEN' ? children : <div>WEBSOCKET CONNECTION IS CLOSED</div>}
    </GameSocketContext.Provider>
  );
};

export default GameSocketProvider;
