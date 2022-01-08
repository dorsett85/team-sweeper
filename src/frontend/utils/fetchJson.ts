/**
 * Fetch wrapper that returns json
 */
// eslint-disable-next-line @typescript-eslint/no-explicit-any
export const fetchJson = async <T extends Record<string, any>>(
  input: RequestInfo,
  init?: RequestInit
): Promise<T> => {
  const res = await fetch(input, init);
  if (res.ok) {
    return res.json();
  }

  // TODO return custom error object with cloned response
  throw new Error(`Request failed with status ${res.status}`);
};
