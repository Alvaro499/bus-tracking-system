import { httpClient } from '@/lib/httpClient';
import type { Trip } from '@/domain/models/Trip';
import { TripDetail } from '@/domain/models/TripDetail';


export const tripService = {
  async getTodayPlannedTrips(): Promise<Trip[]> {
    return httpClient.get<Trip[]>('/tracking/trips/today');
  },

  async getTripDetail(tripId: string): Promise<TripDetail> {
    // TODO: reemplazar por httpClient.get cuando el backend esté listo
    // httpClient.get<TripDetail>(`/tracking/trips/${tripId}/detail`);
    return MOCK_TRIP_DETAIL;
  },
};


const MOCK_TRIP_DETAIL: TripDetail = {
  trip: {
    id: 'trip-123',
    routeName: '300-C',
    origin: 'Terminal SJ',
    destination: 'Cartago',
    departureTime: '05:45:00',
    status: 'IN_PROGRESS',
  },
  stops: [
    {
      routeStop: { id: 'rs1', stopId: 's1', orderIndex: 0, estimatedTimeOffset: 0 },
      stop: { id: 's1', name: 'Terminal San José', latitude: 0, longitude: 0, reference: '' },
      completed: true,
    },
    // ... más paradas
  ],
};