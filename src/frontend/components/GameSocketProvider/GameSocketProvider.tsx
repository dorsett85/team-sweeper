import React, { createContext, useEffect, useState } from 'react';
import { GameSocket } from '../../utils/GameSocket';

export type ReadyState = 'CONNECTING' | 'OPEN' | 'CLOSED';

interface GameSocketState {
  sock: GameSocket;
}

interface RenderGameSocketNotOpen extends Pick<CloseEvent, 'reason'> {
  readyState: ReadyState;
  /**
   * Callback when an attempt to reestablish a websocket connection is made
   */
  onSocketConnect: () => void;
}

interface GameSocketProviderProps {
  /**
   * Url for the websocket connection
   */
  url: string;

  /**
   * Returns a jsx element to be rendered when the websocket is not open
   */
  renderGameSocketNotOpen(props: RenderGameSocketNotOpen): React.ReactElement;
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
  const [reason, setReason] = useState('');
  const [sock, setSock] = useState<GameSocket>();

  const connectToGameSocket = () =>
    new GameSocket(url, {
      onOpen: () => {
        // give a delay when setting to open to avoid UI flashes
        setTimeout(() => {
          setReadyState('OPEN');
        }, 1000);
      },
      onError: (e) => {
        console.error(e);
        setReadyState('CLOSED');
      },
      onClose: (e) => {
        console.warn('websocket closed:', e);
        setReadyState('CLOSED');
        setReason(e.reason);
      }
    });

  useEffect(() => {
    setSock(connectToGameSocket());
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  if (!sock) {
    return null;
  }

  const content =
    readyState === 'OPEN'
      ? children
      : renderGameSocketNotOpen({
          readyState,
          onSocketConnect: () => setSock(connectToGameSocket()),
          reason
        });

  return <GameSocketContext.Provider value={{ sock }}>{content}</GameSocketContext.Provider>;
};

export default GameSocketProvider;
