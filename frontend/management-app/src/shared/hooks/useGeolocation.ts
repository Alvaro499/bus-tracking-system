
/**
 
* This  

@PositionOptions is an interface that defines the config options for the geolcation API.

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

//Custom hook to manage geolocation state and logic
export function useGeolocation() {
  const [coords, setCoords] = useState<Coords | null>(null);
  const [error, setError] = useState<string | null>(null);

  // Function used by watchPosition to update state with new coordinates
  function onSuccess(position: GeolocationPosition) {
    setCoords(extractCoords(position));
  }

  // Function used by watchPosition to update state with error message
  function onError() {
    setError('Error obteniendo ubicación');
  }

  // Object with contract (interface) used by watchPosition to configure geolocation options
  const options: PositionOptions = {
    enableHighAccuracy: true,
  };

  useEffect(() => {
    // This function keeps watching the user's position on secondary thread and calls onSuccess or onError when position changes or an error occurs
    const watchId = navigator.geolocation.watchPosition(onSuccess, onError, options);
    //When the component is unmounted, clear the geolocation watch to prevent memory leaks
    return () => navigator.geolocation.clearWatch(watchId);
  }, []);

  return { coords, error };
}