import { configureStore } from '@reduxjs/toolkit';
import { TypedUseSelectorHook, useDispatch, useSelector } from 'react-redux';
import { singlePlayerReducer } from './singlePlayerSlice';

const singlePlayerStore = configureStore({
  reducer: singlePlayerReducer
});

// Infer the `RootState` and `AppDispatch` types from the singlePlayerStore itself
export type AppState = ReturnType<typeof singlePlayerStore.getState>;
export type AppDispatch = typeof singlePlayerStore.dispatch;

// Use throughout your app instead of plain `useDispatch` and `useSelector`
export const useAppDispatch = (): AppDispatch => useDispatch<AppDispatch>();
export const useAppSelector: TypedUseSelectorHook<AppState> = useSelector;

export default singlePlayerStore;
