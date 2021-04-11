import { CaseReducer, createSlice, PayloadAction, SliceCaseReducers } from '@reduxjs/toolkit';

interface SinglePlayerState {
  /**
   * Difficulty setting of the game
   */
  difficulty: 'e' | 'm' | 'h';
}

type SinglePlayerCaseReducer<TPayload = void> = CaseReducer<
  SinglePlayerState,
  PayloadAction<TPayload>
>;

interface SinglePlayerReducers extends SliceCaseReducers<SinglePlayerState> {
  resetSinglePlayerState: SinglePlayerCaseReducer;
}

const initialState: SinglePlayerState = {
  difficulty: 'h'
};

export const counterSlice = createSlice<SinglePlayerState, SinglePlayerReducers>({
  name: 'singlePlayer',
  initialState,
  reducers: {
    resetSinglePlayerState: (state) => {
      state = initialState;
    }
  }
});

export const { resetSinglePlayerState } = counterSlice.actions;

export const singlePlayerReducer = counterSlice.reducer;
