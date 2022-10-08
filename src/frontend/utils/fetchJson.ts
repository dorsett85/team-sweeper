/**
 * Non 2xx/3xx fetch responses that contain the original response object.
 */
export class FetchJsonError extends Error {
  public response: Response;

  constructor(res: Response) {
    super(`response status ${res.status}`);
    this.response = res;
    this.name = FetchJsonError.name;
    Object.setPrototypeOf(this, FetchJsonError.prototype);
  }
}

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

  throw new FetchJsonError(res);
};
