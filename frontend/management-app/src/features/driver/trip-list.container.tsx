'use client';

import { useState, useEffect, useCallback } from 'react';
import { tripService } from '@/infrastructure/services/tripService';
import { TripCard } from './components/TripCard';
import { ApiErrorClass } from '@/lib/errors/apiError';
import type { Trip } from '@/domain/models/Trip';

export function TripListContainer() {
  const [trips, setTrips] = useState<Trip[]>([]);
  const [loading, setLoading] = useState(true);
  const [apiError, setApiError] = useState<ApiErrorClass | null>(null);

  const fetchTrips = useCallback(async () => {
    try {
      setLoading(true);
      const data = await tripService.getTodayPlannedTrips();
      setTrips(data);
      setApiError(null);
    } catch (err) {
      if (err instanceof ApiErrorClass) {
        setApiError(err);
      }
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchTrips();
  }, [fetchTrips]);

  const handleTakeTrip = (tripId: string) => {
    // TODO: Implementar cuando el backend tenga el endpoint para iniciar un viaje
    console.log('Tomar viaje:', tripId);
  };

  if (loading) {
    return <p className="p-4 text-muted-foreground">Cargando viajes...</p>;
  }

  if (apiError) {
    return (
      <div className="p-4">
        <p className="text-destructive font-medium">Error al cargar viajes</p>
        <p className="text-sm text-muted-foreground">{apiError.userMessage}</p>
        <button 
          onClick={fetchTrips}
          className="mt-2 text-sm underline"
        >
          Reintentar
        </button>
      </div>
    );
  }

  if (trips.length === 0) {
    return (
      <div className="p-4 text-center">
        <p className="text-muted-foreground">No tienes viajes planificados para hoy.</p>
      </div>
    );
  }

  return (
    <div className="p-4 space-y-4 max-w-md mx-auto">
      <h1 className="text-xl font-bold mb-4">Viajes disponibles — Hoy</h1>
      {trips.map((trip) => (
        <TripCard key={trip.id} trip={trip} onTakeTrip={handleTakeTrip} />
      ))}
    </div>
  );
}