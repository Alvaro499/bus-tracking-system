
/**
 * usePolling is a custom React hook that sets up a polling mechanism to repeatedly call a specified callback function at defined intervals.
 */

import { useEffect, useRef } from 'react';

export function usePolling(callback: () => void, intervalMs: number) {
  // Persistent object to store the latest callback function
  const savedCallback = useRef(callback);

  // Remember the latest callback if it changes.
  useEffect(() => {
    savedCallback.current = callback;
  }, [callback]);

  //
  useEffect(() => {
    const tick = () => savedCallback.current();
    tick();
    const id = setInterval(tick, intervalMs);
    return () => clearInterval(id);
  }, [intervalMs]);
}