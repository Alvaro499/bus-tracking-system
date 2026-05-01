
import {useEffect, useRef} from 'react';

export function usePolling(callback: () => void, intervalMs: number){

    // At the second render of the component which uses this hook, useRef returns the same object as in the first render
    const savedCallBack = useRef(callback);

    // This useEffect updates the current value of savedCallBack with the new version of the callback function every time it changes,
    // because savedCallBack is a ref
    useEffect(() => {
        savedCallBack.current = callback;
    }, [callback]);

    // It executes only once when the component is mounted and sets up an interval 
    // to execute every new version of the callback by accessing the current value of 
    // savedCallBack obj (which is in memory thanks to useRef) and clears the interval 
    // when the component is unmounted
    useEffect(() => {
        const executeCallback = () => savedCallBack.current();
        executeCallback();
        const id = setInterval(executeCallback, intervalMs);
        return () => clearInterval(id);
    }, [intervalMs]);   
}