import { httpClient } from '@/lib/httpClient';
import type { Trip } from '@/domain/models/Trip';
import { TripDetail } from '@/domain/models/TripDetail';

export const realTripService = {
  async getTodayPlannedTrips(): Promise<Trip[]> {
    return httpClient.get<Trip[]>('/tracking/trips/today');
  },

  async getTripDetail(tripId: string): Promise<TripDetail> {
    return httpClient.get<TripDetail>(`/tracking/trips/${tripId}/detail`);
  },

  async confirmStop(tripId: string, stopId: string): Promise<void> {
    await httpClient.post(
      `/tracking/trips/${tripId}/stops/${stopId}/confirm`,
      { completedAt: new Date().toISOString() }
    );
  },

  async finishTrip(tripId: string, delayMinutes: number): Promise<void> {
    await httpClient.post(
      `/tracking/trips/${tripId}/finish`,
      { delayMinutes: delayMinutes }
    );
  },

  async cancelTrip(tripId: string, cancelationReason: string): Promise<void> {
    await httpClient.post(
      `/tracking/trips/${tripId}/cancel`,
      { cancelationReason: cancelationReason }
    );
  },
};