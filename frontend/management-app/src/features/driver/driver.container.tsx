'use client';

import { useState } from 'react';
import { useGeolocation } from '../../shared/hooks/useGeolocation';
import { usePolling } from '../../shared/hooks/usePolling';
import { busLocationService } from '../../infrastructure/services/busLocationService';

const BUS_ID = '650e8400-e29b-41d4-a716-446655440001';
const POLLING_INTERVAL = 5000;

/**
 * Driver location tracking container.
 * Calls busLocationService which uses httpClient.
 * Errors flow to GlobalErrorBoundary - no try-catch needed.
 */
export function DriverContainer() {
  const { coords, error } = useGeolocation();
  const [status, setStatus] = useState<string>('Obteniendo ubicación...');

  usePolling(async () => {
    if (!coords) return;
    await busLocationService.updateBusLocation(BUS_ID, coords.lat, coords.lng);
    setStatus(`Última actualización: ${new Date().toLocaleTimeString()}`);
  }, POLLING_INTERVAL);

  if (error) return <p>{error}</p>;
  return <p>{status}</p>;
}