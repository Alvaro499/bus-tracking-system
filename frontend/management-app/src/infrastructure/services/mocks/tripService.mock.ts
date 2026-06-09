
import type { Trip } from '@/domain/models/Trip';
import { TripDetail } from '@/domain/models/TripDetail';

const MOCK_TRIP_DETAIL: TripDetail = {
    trip: {
        id: 'b70e8400-e29b-41d4-a716-446655440001',
        routeName: 'Cartago-Orosi',
        origin: 'Cartago',
        destination: 'Orosi',
        departureTime: '08:00:00',
        status: 'IN_PROGRESS',
    },
    stops: [
        {
            routeStop: {
                id: '960e8400-e29b-41d4-a716-446655440001',
                stopId: '860e8400-e29b-41d4-a716-446655440001',
                orderIndex: 1,
                estimatedTimeOffset: 0,
            },
            stop: {
                id: '860e8400-e29b-41d4-a716-446655440001',
                name: 'Cartago',
                latitude: 9.8612,
                longitude: -83.9180,
                reference: 'Terminal Central Cartago',
            },
            completedAt: new Date().toISOString(),   // completada (inicio del viaje)
        },
        {
            routeStop: {
                id: '960e8400-e29b-41d4-a716-446655440002',
                stopId: '860e8400-e29b-41d4-a716-446655440002',
                orderIndex: 2,
                estimatedTimeOffset: 5,
            },
            stop: {
                id: '860e8400-e29b-41d4-a716-446655440002',
                name: 'Colegio Vicente de Costa Rica',
                latitude: 9.8615,
                longitude: -83.9145,
                reference: 'Frente Colegio Vicente de Costa Rica',
            },
            completedAt: new Date().toISOString(),   // completada
        },
        {
            routeStop: {
                id: '960e8400-e29b-41d4-a716-446655440003',
                stopId: '860e8400-e29b-41d4-a716-446655440003',
                orderIndex: 3,
                estimatedTimeOffset: 15,
            },
            stop: {
                id: '860e8400-e29b-41d4-a716-446655440003',
                name: 'Estadio de Paraíso',
                latitude: 9.8142,
                longitude: -83.8156,
                reference: 'Estadio de Paraíso',
            },
            completedAt: null,                       // pendiente (parada activa)
        },
        {
            routeStop: {
                id: '960e8400-e29b-41d4-a716-446655440004',
                stopId: '860e8400-e29b-41d4-a716-446655440004',
                orderIndex: 4,
                estimatedTimeOffset: 20,
            },
            stop: {
                id: '860e8400-e29b-41d4-a716-446655440004',
                name: 'Paraíso Centro',
                latitude: 9.8098,
                longitude: -83.8042,
                reference: 'Centro de Paraíso',
            },
            completedAt: null,
        },
        {
            routeStop: {
                id: '960e8400-e29b-41d4-a716-446655440005',
                stopId: '860e8400-e29b-41d4-a716-446655440005',
                orderIndex: 5,
                estimatedTimeOffset: 30,
            },
            stop: {
                id: '860e8400-e29b-41d4-a716-446655440005',
                name: 'Recinto UCR Paraíso',
                latitude: 9.7856,
                longitude: -83.7834,
                reference: 'Recinto de la UCR en Paraíso',
            },
            completedAt: null,
        },
        {
            routeStop: {
                id: '960e8400-e29b-41d4-a716-446655440006',
                stopId: '860e8400-e29b-41d4-a716-446655440006',
                orderIndex: 6,
                estimatedTimeOffset: 40,
            },
            stop: {
                id: '860e8400-e29b-41d4-a716-446655440006',
                name: 'Orosi',
                latitude: 9.7798,
                longitude: -83.7345,
                reference: 'Terminal Orosi',
            },
            completedAt: null,
        },
    ],
};

export const mockTripService = {
    async getTodayPlannedTrips(): Promise<Trip[]> {
        return [
            {
                id: 'b70e8400-e29b-41d4-a716-446655440001',
                routeName: 'Cartago-Orosi',
                origin: 'Cartago',
                destination: 'Orosi',
                departureTime: '08:00:00',
                status: 'PLANNED'
            },
            {
                id: 'b70e8400-e29b-41d4-a716-446655440002',
                routeName: 'Cartago-Orosi',
                origin: 'Cartago',
                destination: 'Orosi',
                departureTime: '09:00:00',
                status: 'PLANNED'
            },
            {
                id: 'b70e8400-e29b-41d4-a716-446655440003',
                routeName: 'Cartago-Orosi',
                origin: 'Cartago',
                destination: 'Orosi',
                departureTime: '10:00:00',
                status: 'PLANNED'
            },
            {
                id: 'b70e8400-e29b-41d4-a716-446655440004',
                routeName: 'Cartago-Orosi',
                origin: 'Cartago',
                destination: 'Orosi',
                departureTime: '11:00:00',
                status: 'PLANNED'
            },
            {
                id: 'b70e8400-e29b-41d4-a716-446655440005',
                routeName: 'Cartago-Orosi',
                origin: 'Cartago',
                destination: 'Orosi',
                departureTime: '12:00:00',
                status: 'PLANNED'
            },
            {
                id: 'b70e8400-e29b-41d4-a716-446655440006',
                routeName: 'Cartago-Orosi',
                origin: 'Cartago',
                destination: 'Orosi',
                departureTime: '13:00:00',
                status: 'PLANNED'
            }
        ];
    },

    async getTripDetail(_tripId: string): Promise<TripDetail> {
        return MOCK_TRIP_DETAIL;
    },

    async confirmStop(_tripId: string, _stopId: string): Promise<void> {
        // no hace nada, solo simula éxito
    },

    async finishTrip(_tripId: string, _delayMinutes: number): Promise<void> {
        // no hace nada
    },

    async cancelTrip(_tripId: string, _cancelationReason: string): Promise<void> {
        // no hace nada
    },
};