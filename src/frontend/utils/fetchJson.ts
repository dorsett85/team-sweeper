/**
 * Fetch wrapper that returns json
 */
// eslint-disable-next-line @typescript-eslint/no-explicit-any
export const fetchJson = async <T extends Record<string, any>>(
  input: RequestInfo,
  init?: RequestInit
): Promise<T> => {
  const res = await fetch(input, init);
  return res.json();
};
