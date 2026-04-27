
// This custom React hook allows us to set up a polling mechanism that repeatedly calls a specified callback function at defined intervals. It uses the useEffect and useRef hooks to manage the polling behavior and ensure that the latest version of the callback is always used.

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