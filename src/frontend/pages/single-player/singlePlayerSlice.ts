import { CaseReducer, createSlice, PayloadAction, SliceCaseReducers } from '@reduxjs/toolkit';
import { GameStart } from '../../types/game';

export interface SinglePlayerState {
  /**
   * Difficulty setting of the game
   */
  difficulty: GameStart['difficulty'];
  /**
   * Whether or not the game board is loading
   */
  isLoading: boolean;
}

type SinglePlayerCaseReducer<TPayload = void> = CaseReducer<
  SinglePlayerState,
  PayloadAction<TPayload>
>;

interface SinglePlayerReducers extends SliceCaseReducers<SinglePlayerState> {
  setDifficulty: SinglePlayerCaseReducer<SinglePlayerState['difficulty']>;
  setIsLoading: SinglePlayerCaseReducer<SinglePlayerState['isLoading']>;
}

export const initialState: SinglePlayerState = {
  difficulty: 'h',
  isLoading: true
};

export const counterSlice = createSlice<SinglePlayerState, SinglePlayerReducers>({
  name: 'singlePlayer',
  initialState,
  reducers: {
    setDifficulty: (state, { payload }) => {
      state.difficulty = payload;
    },
    setIsLoading: (state, { payload }) => {
      state.isLoading = payload;
    }
  }
});

export const { setDifficulty, setIsLoading } = counterSlice.actions;

export const singlePlayerReducer = counterSlice.reducer;
