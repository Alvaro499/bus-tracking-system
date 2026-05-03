import { httpClient } from '@/lib/httpClient';

/**
 * Service for managing bus location updates.
 * Delegates all HTTP handling to httpClient.
 * Errors flow up to GlobalErrorBoundary without try-catch.
 */
export const busLocationService = {
  /**
   * Updates the location of a bus.
   * Throws ApiErrorClass if backend returns error.
   */
  async updateBusLocation(busId: string, lat: number, lng: number): Promise<void> {
    await httpClient.post(`/tracking/buses/${busId}/location`, { lat, lng });
  },
};


/*

 Older Version:
 export async function updateBusLocation(busId: string, lat: number, lng: number): Promise<void> {
  await fetch(`${BASE_URL}/tracking/buses/${busId}/location`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ lat, lng }),
  });
}
*/