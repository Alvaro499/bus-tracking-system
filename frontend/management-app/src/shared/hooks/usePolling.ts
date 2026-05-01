
import {useEffect, useRef} from 'react';

export function usePolling(callback: () => void, intervalMs: number){

    const savedCallBack = useRef(callback);

    useEffect(() => {
        savedCallBack.current = callback;
    }, [callback]);

    useEffect(() => {
        const executeCallback = () => savedCallBack.current();
        executeCallback();
        const id = setInterval(executeCallback, intervalMs);
        return () => clearInterval(id);
    }, [intervalMs]);   
}