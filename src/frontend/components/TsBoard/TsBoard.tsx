import React, { useEffect, useState } from 'react';
import styles from './TsBoard.module.less';
import { TsBoardResponse } from '../../types/TsBoardResponse';
import TsBoardCell from './TsBoardCell';
import { useAppSelector } from '../../pages/single-player/singlePlayerStore';

const TsBoard: React.FC = () => {
  const [boardResponse, setBoardResponse] = useState<TsBoardResponse>();
  const difficulty = useAppSelector((state) => state.difficulty);

  useEffect(() => {
    fetch(`/single-player/new-game?difficulty=${difficulty}`)
      .then((res) => res.json())
      .then((board) => {
        setBoardResponse(board);
      });
  }, [difficulty]);

  return (
    <div className={styles[`tsBoard-${difficulty}`]}>
      {boardResponse?.board.flatMap((row) =>
        row.map((cell) => <TsBoardCell key={`${cell.rowIdx} ${cell.colIdx}`} {...cell} />)
      )}
    </div>
  );
};

export default TsBoard;
