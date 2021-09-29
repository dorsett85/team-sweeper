import React, { useRef } from 'react';
import { createPortal } from 'react-dom';
import styles from './Modal.module.less';

interface ModalProps {
  /**
   * Whether or not the modal is open
   */
  open: boolean;
  /**
   * Callback when the modal closes
   */
  onClose: () => void;
}

const Modal: React.FC<ModalProps> = ({ open, onClose, children }) => {
  const backdropRef = useRef<HTMLDivElement>(null);

  const handleOnBackdropClick: React.MouseEventHandler<HTMLDivElement> = (e) => {
    // only close the modal if the backdrop element is clicked on and not any of
    // the child elements.
    if (backdropRef.current === e.target) {
      onClose();
    }
  };

  if (!open) {
    return null;
  }

  return createPortal(
    // eslint-disable-next-line jsx-a11y/click-events-have-key-events,jsx-a11y/no-static-element-interactions
    <div ref={backdropRef} onClick={handleOnBackdropClick} className={styles.modalBackdrop}>
      <div role='dialog' aria-modal className={styles.modal}>
        {children}
      </div>
    </div>,
    document.body
  );
};

export default Modal;
