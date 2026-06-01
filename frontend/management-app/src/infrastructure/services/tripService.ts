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

  async confirmStop(tripId: string, stopId: string): Promise<void> {
    await httpClient.post(
      `/tracking/trips/${tripId}/stops/${stopId}/confirm`,
      { completedAt: new Date().toISOString() }
    );
  },

  async finishTrip(tripId: string, routeStopId: string): Promise<void> {
    await httpClient.post(
      `/tracking/trips/${tripId}/finish`,
      { routeStopId }
    );
  }
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
    stop: { id: 's1', name: 'Terminal San José', latitude: 9.9333, longitude: -84.0833, reference: 'Costado norte del parque' },
    completedAt: '2023-10-01T05:45:00Z',
  },
  {
    routeStop: { id: 'rs2', stopId: 's2', orderIndex: 1, estimatedTimeOffset: 300 },
    stop: { id: 's2', name: 'Paseo Colón', latitude: 9.9347, longitude: -84.0789, reference: 'Frente al Hospital San Juan de Dios' },
    completedAt: '2023-10-01T05:52:00Z',
  },
  {
    routeStop: { id: 'rs3', stopId: 's3', orderIndex: 2, estimatedTimeOffset: 600 },
    stop: { id: 's3', name: 'La Sabana', latitude: 9.9358, longitude: -84.0697, reference: 'Entrada principal del parque' },
    completedAt: null,
  },
  {
    routeStop: { id: 'rs4', stopId: 's4', orderIndex: 3, estimatedTimeOffset: 900 },
    stop: { id: 's4', name: 'Rohrmoser', latitude: 9.9421, longitude: -84.0618, reference: 'Plaza Mayor' },
    completedAt: null,
  },
  {
    routeStop: { id: 'rs5', stopId: 's5', orderIndex: 4, estimatedTimeOffset: 1200 },
    stop: { id: 's5', name: 'San Pedro', latitude: 9.9281, longitude: -84.0473, reference: 'Mall San Pedro' },
    completedAt: null,
  },
  {
    routeStop: { id: 'rs6', stopId: 's6', orderIndex: 5, estimatedTimeOffset: 1500 },
    stop: { id: 's6', name: 'Curridabat', latitude: 9.9161, longitude: -84.0336, reference: 'Centro comercial' },
    completedAt: null,
  },
  {
    routeStop: { id: 'rs7', stopId: 's7', orderIndex: 6, estimatedTimeOffset: 1800 },
    stop: { id: 's7', name: 'Cartago', latitude: 9.8644, longitude: -83.9194, reference: 'Ruinas de Cartago' },
    completedAt: null,
  },
],
};