import React from 'react';
import Modal from '../Modal/Modal';
import { ReadyState } from '../GameSocketProvider/GameSocketProvider';
import { useAppDispatch } from '../../pages/single-player/singlePlayerStore';
import { setIsLoading } from '../../pages/single-player/singlePlayerSlice';
import ModalTitle from '../Modal/ModalTitle';
import styles from './GameSocketNotOpen.module.less';
import buttonStyles from '../../styles/button.module.less';

interface GameSocketNotOpenProps extends Pick<CloseEvent, 'reason'> {
  readyState: ReadyState;
  /**
   * Callback when an attempt to reestablish a websocket connection is made
   */
  onReconnectClick: () => void;
}

const MODAL_HEADING_ID = 'websocket-modal-heading';

const GameSocketNotOpen: React.FC<GameSocketNotOpenProps> = ({
  readyState,
  onReconnectClick,
  reason
}) => {
  const dispatch = useAppDispatch();

  const handleOnReconnectClick = () => {
    dispatch(setIsLoading(true));
    onReconnectClick();
  };

  const title = readyState === 'CONNECTING' ? 'Connecting' : 'Connection lost!';
  const body =
    readyState === 'CONNECTING' ? (
      <div className={styles.loader} />
    ) : (
      <section className={styles.modalContentSection}>
        <p>{reason || 'You may have been idle for too long or the server disconnected'}</p>
        <div className='text-center'>
          <button className={buttonStyles.baseButton} onClick={handleOnReconnectClick}>
            Restart connection
          </button>
        </div>
      </section>
    );

  return (
    <Modal
      open={readyState !== 'OPEN'}
      onClose={() => undefined}
      showCloseButton={false}
      aria-labelledby={MODAL_HEADING_ID}
    >
      <ModalTitle id={MODAL_HEADING_ID} title={title} />
      {body}
    </Modal>
  );
};

export default GameSocketNotOpen;
