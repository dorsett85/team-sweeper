import { CaseReducer, createSlice, PayloadAction, SliceCaseReducers } from '@reduxjs/toolkit';
import { Game } from '../../types/Game';

export interface SinglePlayerState {
  /**
   * Difficulty setting of the game
   */
  difficulty: Game['difficulty'];
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

const initialState: SinglePlayerState = {
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
