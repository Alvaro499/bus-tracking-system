import { useState } from 'react';
import type { BusLocation } from '../../domain/models/BusLocation';
import { getBusLocation } from '../../infrastructure/services/busLocationService';
import { usePolling } from '../../common/hooks/usePolling';

const BUS_ID = '650e8400-e29b-41d4-a716-446655440001'; // temporal, hardcodeado por ahora
const POLLING_INTERVAL = 5000; // 5 segundos

export function MapContainer() {
  const [busLocation, setBusLocation] = useState<BusLocation | null>(null);
  const [error, setError] = useState<string | null>(null);

  usePolling(async () => {
    try {
      const location = await getBusLocation(BUS_ID);
      setBusLocation(location);
      setError(null);
    } catch (e: any) {
      setError(e.message);
    }
  }, POLLING_INTERVAL);

  if (error) return <p>Error: {error}</p>;
  if (!busLocation) return <p>Cargando ubicación...</p>;

  return (
    <p>
      Bus: {busLocation.busId} — lat: {busLocation.lat}, lng: {busLocation.lng}
    </p>
  );
}