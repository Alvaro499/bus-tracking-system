'use client';

import { useState, useEffect, useCallback } from 'react';
import { tripService } from '@/infrastructure/services/tripService';
import { TripCard } from './components/TripCard';
import { ApiErrorClass } from '@/lib/errors/apiError';
import type { Trip } from '@/domain/models/Trip';
import type { Route } from '@/domain/models/Route';
import type { RouteStop } from '@/domain/models/RouteStop';
import type { Stop } from '@/domain/models/Stop';
import { Button } from '@/components/ui/button';
import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/card';
import { TripDetail } from '@/domain/models/TripDetail';
import { busLocationService } from '@/infrastructure/services/busLocationService';
import { useGeolocation } from '@/shared/hooks/useGeolocation';
import { usePolling } from '@/shared/hooks/usePolling';
import { TripStopsList } from './components/TripStopsList';


interface TripDetailContainerProps {
  tripId: string;
}

const BUS_ID = '650e8400-e29b-41d4-a716-446655440001'; 

export function TripDetailContainer({ tripId }: TripDetailContainerProps) {
  const [tripDetail, setTripDetail] = useState<TripDetail | null>(null);
  const [loading, setLoading] = useState(true);
  const [apiError, setApiError] = useState<ApiErrorClass | null>(null);
  const [lastTransmissionTime, setLastTransmissionTime] = useState<Date | null>(null);

  const { coords, error: geoError } = useGeolocation();

  // Cargar detalle del viaje (mock)
  const fetchTripDetail = useCallback(async () => {
    try {
      setLoading(true);
      const data = await tripService.getTripDetail(tripId);
      setTripDetail(data);
      setApiError(null);
    } catch (err) {
      if (err instanceof ApiErrorClass) setApiError(err);
    } finally {
      setLoading(false);
    }
  }, [tripId]);

  useEffect(() => {
    fetchTripDetail();
  }, [fetchTripDetail]);

  // Transmisión de ubicación (solo si el viaje está IN_PROGRESS)
  usePolling(async () => {
    if (!coords || !tripDetail || tripDetail.trip.status !== 'IN_PROGRESS') return;
    try {
      await busLocationService.updateBusLocation(BUS_ID, coords.lat, coords.lng);
      setLastTransmissionTime(new Date());
    } catch (err) {
      // Podríamos mostrar un indicador de error local si quisiéramos
    }
  }, 5000);

  if (loading) return <p className="p-4">Cargando viaje...</p>;
  if (apiError) return <p className="p-4 text-destructive">{apiError.userMessage}</p>;
  if (!tripDetail) return <p className="p-4">No se encontró el viaje.</p>;

  const { trip, stops } = tripDetail;
  const isInProgress = trip.status === 'IN_PROGRESS';

  return (
    <div className="p-4 max-w-md mx-auto space-y-4">
      {/* Cabecera del viaje */}
      <Card>
        <CardHeader>
          <CardTitle>Viaje en curso — Ruta {trip.routeName}</CardTitle>
          <p className="text-sm text-muted-foreground">
            {trip.origin} → {trip.destination} — {trip.departureTime}
          </p>
        </CardHeader>
        <CardContent>
          {isInProgress ? (
            <div className="flex items-center gap-2 text-sm text-green-600">
              <span className="h-2 w-2 rounded-full bg-green-600 animate-pulse" />
              Transmitiendo ubicación...
              {lastTransmissionTime && (
                <span className="text-muted-foreground">
                  Última transmisión: hace {Math.round((Date.now() - lastTransmissionTime.getTime()) / 1000)} seg
                </span>
              )}
            </div>
          ) : (
            <p className="text-sm">Viaje {trip.status === 'PLANNED' ? 'por iniciar' : 'finalizado'}</p>
          )}
        </CardContent>
      </Card>

      {/* Lista de paradas */}
      <Card>
        <CardContent>
          <h3 className="font-semibold mb-2">Paradas</h3>
          <TripStopsList stops={stops} />
        </CardContent>
      </Card>

      {/* Botones de acción */}
      {isInProgress && (
        <div className="flex gap-2">
          <Button variant="secondary" className="flex-1">Finalizar viaje</Button>
          <Button variant="destructive" className="flex-1">Cancelar viaje</Button>
        </div>
      )}
    </div>
  );
}