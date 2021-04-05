import { CaseReducer, createSlice, PayloadAction, SliceCaseReducers } from '@reduxjs/toolkit';

interface SinglePlayerState {
  value: number;
}

type SinglePlayerCaseReducer<TPayload = void> = CaseReducer<
  SinglePlayerState,
  PayloadAction<TPayload>
>;

interface SinglePlayerReducers extends SliceCaseReducers<SinglePlayerState> {
  resetSinglePlayerState: SinglePlayerCaseReducer;
}

const initialState: SinglePlayerState = {
  value: 0
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
