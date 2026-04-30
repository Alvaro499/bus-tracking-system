
import {useEffect, useRef} from 'react';

export function usePolling(callback: () => void, intervalMs: number){

    const savedCallBack = useRef(callback);

    useEffect(() => {
        savedCallBack.current = callback;
    }, [callback]);

    useEffect(() => {
        const tick = () => savedCallBack.current();
        tick();
        const id = setInterval(tick, intervalMs);
        return () => clearInterval(id);
    }, [intervalMs]);   
}