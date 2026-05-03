import type { BusLocation } from "../../domain/models/BusLocation";
import { httpClient } from "../../lib/httpClient";

/**
 * Service for managing bus location updates.
 * Delegates all HTTP handling to httpClient.
 * Errors flow up to GlobalErrorBoundary without try-catch.
 */
export const busLocationService = {
  /**
   * Retrieves the current location of a bus.
   * Throws ApiErrorClass if backend returns error.
   */
  async getBusLocation(busId: string): Promise<BusLocation> {
    return httpClient.get<BusLocation>(`/tracking/buses/${busId}/location`);
  },

  /**
   * Updates the location of a bus.
   * Throws ApiErrorClass if backend returns error.
   */
  async updateBusLocation(busId: string, lat: number, lng: number): Promise<void> {
    await httpClient.post(`/tracking/buses/${busId}/location`, { lat, lng });
  },
};



/**
 * Equivalence with older version:
    export async function getBusLocation(busId: string) { ... }
    export async function updateBusLocation(busId: string) { ... }

    // We used to call them like this:
    await getBusLocation(busId);
    await updateBusLocation(busId, lat, lng);
 */