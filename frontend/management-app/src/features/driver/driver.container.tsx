'use client';

import { useState } from 'react';
import { useGeolocation } from '../../shared/hooks/useGeolocation';
import { usePolling } from '../../shared/hooks/usePolling';
import { busLocationService } from '../../infrastructure/services/busLocationService';
import { ApiErrorClass } from '@/lib/errors/apiError';

const BUS_ID = '650e8400-e29b-41d4-a716-446655440001';
const POLLING_INTERVAL = 5000;

/**
 * Driver location tracking container.
 * Calls busLocationService which uses httpClient.
 * Errors flow to GlobalErrorBoundary - no try-catch needed.
 */
export function DriverContainer() {
  const { coords, error: geoError } = useGeolocation();
  const [status, setStatus] = useState<string>('Obteniendo ubicación...');
  const [apiError, setApiError] = useState<ApiErrorClass | null>(null);


  usePolling(async () => {
    if (!coords) return;
    try {
      await busLocationService.updateBusLocation(BUS_ID, coords.lat, coords.lng);
      setStatus(`Última actualización: ${new Date().toLocaleTimeString()}`);
      setApiError(null);
    } catch (err) {
      if (err instanceof ApiErrorClass) {
        setApiError(err);
      }
    }
  }, POLLING_INTERVAL);

  if (geoError) return <p>{geoError}</p>;
  if (apiError) return <p>{apiError.userMessage}</p>;
  return <p>{status}</p>;
}