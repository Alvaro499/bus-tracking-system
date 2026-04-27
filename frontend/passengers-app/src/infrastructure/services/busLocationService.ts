import type { BusLocation } from "../../domain/models/BusLocation";

// Service responsible for managing bus locations

const BASE_URL = import.meta.env.VITE_API_API_URL;

export async function getBusLocation(busId: string): Promise<BusLocation>{
    
    
    const response = await fetch(`${BASE_URL}/tracking/buses/${busId}/location`);
    
    if (!response.ok){
        const error = await response.json();
        throw new Error(error.errorCode);
    }
    return response.json()
}


//export async function updateBusLocation(busId: string, lat: number, lng: number): Promise<BusLocation>{}
