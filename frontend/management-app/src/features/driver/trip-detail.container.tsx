'use client';

import { useState, useEffect, useCallback } from 'react';
import { tripService } from '@/infrastructure/services/tripService';
import { ApiErrorClass } from '@/lib/errors/apiError';

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
  
  // To show a loading state while fetching the trip detail for the first time
  const [loading, setLoading] = useState(true);
  const [apiError, setApiError] = useState<ApiErrorClass | null>(null);
  const [lastTransmissionTime, setLastTransmissionTime] = useState<Date | null>(null);

  // They both start as null
  const { coords, error: geoError } = useGeolocation();

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

  // Executes after the first render and every time the tripId changes
  useEffect(() => {
    fetchTripDetail();
  }, [fetchTripDetail]);

  // Location is transmitted  (only when trip is IN_PROGRESS)
  usePolling(async () => {
    const hasCoords = coords !== null;
    const hasTripDetail = tripDetail !== null;
    const isTripInProgress = tripDetail !== null && tripDetail.trip.status === 'IN_PROGRESS';
    if (!hasCoords || !hasTripDetail || !isTripInProgress) {
      return;
    }

    try {
      await busLocationService.updateBusLocation(BUS_ID, coords.lat, coords.lng);
      setLastTransmissionTime(new Date());
    } catch (err) {
    }
  }, 5000);

  if (loading) return <p className="p-4">Cargando viaje...</p>;
  if (apiError) return <p className="p-4 text-destructive">{apiError.userMessage}</p>;
  if (!tripDetail) return <p className="p-4">No se encontró el viaje.</p>;


  const { trip, stops } = tripDetail;
  const isInProgress = trip.status === 'IN_PROGRESS';

  const handleConfirmStop = (stopId: string) => {
      if (tripDetail === null) return;

    const updatedStops = [];

    for (let i = 0; i < tripDetail.stops.length; i++) {
      const stop = tripDetail.stops[i];

      if (stop.routeStop.id === stopId) {

        const stopConfirmada = {
          routeStop: stop.routeStop,
          stop: stop.stop,
          completedAt: new Date().toISOString()
        };
        updatedStops.push(stopConfirmada);
      } else {
        updatedStops.push(stop);
      }
    }

    // We update the state
    const newTripDetail = {
      trip: tripDetail.trip,
      stops: updatedStops
    };

    setTripDetail(newTripDetail);
  }

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

      {/* List of stops */}
      <Card>
        <CardContent>
          <h3 className="font-semibold mb-2">Paradas</h3>
          <TripStopsList stops={stops} onConfirmStop={handleConfirmStop} />
        </CardContent>
      </Card>

      {/* Action buttons */}
      {isInProgress && (
        <div className="flex gap-2">
          <Button variant="secondary" className="flex-1">Finalizar viaje</Button>
          <Button variant="destructive" className="flex-1">Cancelar viaje</Button>
        </div>
      )}
    </div>
  );
}