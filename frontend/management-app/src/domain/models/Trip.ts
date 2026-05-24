/**
 * It represents a trip for the driver.
 * It maps directly to the TripResponse from the backend.
 */

export interface Trip {
  id: string;
  routeName: string;
  origin: string;
  destination: string;
  departureTime: string; // Formato "HH:mm:ss"
  status: string;
}