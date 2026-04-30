
/**
 * This
 */

import { useEffect, useState } from 'react';

interface Coords {
  lat: number;
  lng: number;
}

// Pure function to extract coordinates from GeolocationPosition object 
function extractCoords(position: GeolocationPosition): Coords {
  return {
    lat: position.coords.latitude,
    lng: position.coords.longitude,
  };
}

export function useGeolocation() {
  const [coords, setCoords] = useState<Coords | null>(null);
  const [error, setError] = useState<string | null>(null);

  function onSuccess(position: GeolocationPosition) {
    setCoords(extractCoords(position));
  }

  function onError() {
    setError('Error obteniendo ubicación');
  }

  const options: PositionOptions = {
    enableHighAccuracy: true,
  };

  useEffect(() => {
    const watchId = navigator.geolocation.watchPosition(onSuccess, onError, options);
    return () => navigator.geolocation.clearWatch(watchId);
  }, []);

  return { coords, error };
}