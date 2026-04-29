
/**
 * usePolling is a custom React hook that sets up a polling mechanism to repeatedly call a specified callback function at defined intervals.
 */

import { useEffect, useRef } from 'react';

export function usePolling(callback: () => void, intervalMs: number) {
  const savedCallback = useRef(callback);

  useEffect(() => {
    savedCallback.current = callback;
  }, [callback]);

  useEffect(() => {
    const tick = () => savedCallback.current();
    tick();
    const id = setInterval(tick, intervalMs);
    return () => clearInterval(id);
  }, [intervalMs]);
}