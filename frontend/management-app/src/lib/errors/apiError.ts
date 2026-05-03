/**
 * Standard API error response structure from backend.
 * Mirrors the backend's ErrorResponse format.
 */
export interface ApiErrorResponse {
  code: string;
  message: string;
  timestamp: number;
}

/**
 * Custom error class for API responses.
 * Thrown by httpClient when backend returns 4xx or 5xx.
 * 
 * @example
 * throw new ApiErrorClass('BUS_NOT_FOUND', 'Bus no encontrado', 404, Date.now())
 */
export class ApiErrorClass extends Error {
  public readonly code: string;
  public readonly userMessage: string;
  public readonly status: number;
  public readonly timestamp: number;

  constructor(code: string, userMessage: string, status: number, timestamp: number) {
    super(userMessage);
    this.name = 'ApiErrorClass';
    this.code = code;
    this.userMessage = userMessage;
    this.status = status;
    this.timestamp = timestamp;
  }
}
