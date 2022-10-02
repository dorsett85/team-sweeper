import React from 'react';
import styles from './ModalTitle.module.less';

interface GameModalTitleProps {
  /**
   * Passed to the id attribute of the heading element
   */
  id: string;
  /**
   * TextContent of heading element
   */
  title: string;
  /**
   * Passed to the className attribute of the heading element
   */
  className?: string;
}

const ModalTitle: React.FC<GameModalTitleProps> = ({ id, title, className }) => {
  return (
    <>
      <h2 id={id} className={`${styles.modalHeading} ${className || ''}`}>
        {title}
      </h2>
      <hr />
    </>
  );
};

export default ModalTitle;
