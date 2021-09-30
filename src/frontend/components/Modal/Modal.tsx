import React, { useEffect, useRef } from 'react';
import { createPortal } from 'react-dom';
import styles from './Modal.module.less';

interface ModalProps
  extends Pick<React.HTMLAttributes<HTMLDivElement>, 'aria-labelledby' | 'aria-describedby'> {
  /**
   * Whether or not the modal is open
   */
  open: boolean;
  /**
   * Callback when the modal requests to close
   */
  onClose: () => void;
}

const Modal: React.FC<ModalProps> = ({ open, onClose, children, ...rest }) => {
  const backdropRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    // Map of key down listeners
    const keyDownListenerMap: Record<'Escape' | 'Tab', (e: KeyboardEvent) => void> = {
      // Request to close modal on an escape key press
      Escape() {
        onClose();
      },
      // Only allow tabbing through items inside of the modal
      Tab(e) {
        const focusableNodeList = backdropRef.current?.querySelectorAll<HTMLElement>(
          'a[href], button, textarea, input[type="text"], input[type="radio"], input[type="checkbox"], select'
        );

        if (focusableNodeList) {
          // Convert the node list to an array
          const focusableElements = [...focusableNodeList];
          const [firstElement] = focusableElements;
          const lastElement = focusableElements[focusableElements.length - 1];
          const { activeElement } = document;
          // Check if the modal already contains a focused element
          // TODO give focus to a modal element automatically when it opens?
          const modalContainsFocus = backdropRef.current?.contains(activeElement);

          if (e.shiftKey && (activeElement === firstElement || !modalContainsFocus)) {
            lastElement.focus();
            e.preventDefault();
          } else if (!e.shiftKey && (activeElement === lastElement || !modalContainsFocus)) {
            firstElement.focus();
            e.preventDefault();
          }
        }
      }
    };

    const handleOnKeydown = (e: KeyboardEvent) => {
      if (open) {
        keyDownListenerMap[e.key as keyof typeof keyDownListenerMap]?.(e);
      }
    };

    document.addEventListener('keydown', handleOnKeydown);
    document.body.style.overflowY = open ? 'hidden' : '';
    return () => {
      document.removeEventListener('keydown', handleOnKeydown);
      document.body.style.overflow = '';
    };
  }, [onClose, open]);

  const handleOnBackdropClick: React.MouseEventHandler<HTMLDivElement> = (e) => {
    // Only close the modal if the backdrop element is clicked on and not any of
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
      <div
        role='dialog'
        className={styles.modal}
        aria-labelledby={rest['aria-labelledby']}
        aria-describedby={rest['aria-describedby']}
        aria-modal
      >
        {children}
      </div>
    </div>,
    document.body
  );
};

export default Modal;
