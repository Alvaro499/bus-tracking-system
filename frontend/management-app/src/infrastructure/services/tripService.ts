import { httpClient } from '@/lib/httpClient';
import type { Trip } from '@/domain/models/Trip';


export const tripService = {
  async getTodayPlannedTrips(): Promise<Trip[]> {
    return httpClient.get<Trip[]>('/tracking/trips/today');
  },
};