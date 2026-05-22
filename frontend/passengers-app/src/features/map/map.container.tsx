import { useState } from 'react';
import type { BusLocation } from '../../domain/models/BusLocation';
import { busLocationService } from '../../infrastructure/services/busLocationService';
import { usePolling } from '../../shared/hooks/usePolling';
import { BusMap } from '../../components/BusMap';
import { ApiErrorClass } from '../../lib/errors/apiError';

const BUS_ID = '650e8400-e29b-41d4-a716-446655440001'; // temporal, hardcodeado por ahora
const POLLING_INTERVAL = 5000; // 5 segundos

export function MapContainer() {
  const [busLocation, setBusLocation] = useState<BusLocation | null>(null);
  const [apiError, setApiError] = useState<ApiErrorClass | null>(null);

  usePolling(async () => {
    try {
      const location = await busLocationService.getBusLocation(BUS_ID);
      setBusLocation(location);
      setApiError(null);
    } catch (err) {
      if (err instanceof ApiErrorClass) {
        setApiError(err);
      }
    }
  }, POLLING_INTERVAL);

  if (apiError) return <p>{apiError.userMessage}</p>;
  if (!busLocation) return <p>Cargando ubicación...</p>;

  return (
    <>
      <BusMap location={busLocation} />
      <p>
        Bus: {busLocation.busId} — lat: {busLocation.lat}, lng: {busLocation.lng}
      </p>
    </>
  );
}