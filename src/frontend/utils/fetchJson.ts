/**
 * Fetch wrapper that returns json
 */
export const fetchJson = async <T extends Record<string, any>>(
  input: RequestInfo,
  init?: RequestInit
): Promise<T> => {
  const res = await fetch(input, init);
  return await res.json();
};
