import { ApiErrorClass, ApiErrorResponse } from './errors/apiError';

const BASE_URL = process.env.NEXT_PUBLIC_API_URL;

/**
 * Core HTTP request handler with centralized error handling.
 * Throws ApiErrorClass when backend returns 4xx or 5xx.
 * This is the only place where try-catch for HTTP errors lives.
 * Classic equivalence:
        {
        method: 'POST',                        // viene de ...options
        body: JSON.stringify({ user: 'juan' }), // viene de ...options
        headers: {
            'Content-Type': 'application/json',  // siempre garantizado
            'Authorization': 'Bearer token123'   // viene de ...options.headers
        }
 */
async function request<T>(path: string, options: RequestInit): Promise<T> {
  const response = await fetch(`${BASE_URL}${path}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
  });

  if (!response.ok) {
    const body: ApiErrorResponse = await response.json();
    throw new ApiErrorClass(body.code, body.message, response.status, body.timestamp);
  }

  // We avoid an parsing error when the response has no content
  if (response.status === 204) {
    return null as unknown as T;
  }
  return response.json();
}

/**
 * HTTP client with GET and POST methods.
 * Delegates all error handling to the request() function.
 * Services and components use this and never deal with try-catch.
 */
export const httpClient = {
  get: function<T>(path: string) {
    return request<T>(path, { method: 'GET' });
  },

  post: function<T>(path: string, data: unknown) {
    return request<T>(path, { method: 'POST', body: JSON.stringify(data) });
  },
};