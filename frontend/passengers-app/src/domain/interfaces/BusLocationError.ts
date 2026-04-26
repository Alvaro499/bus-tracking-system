// This file defines the structure of an error response from the API when fetching bus location data fails.
export interface ApiError {
    errorCode: string;
    message: string;
    details: string;
}