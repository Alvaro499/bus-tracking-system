import { RouteStop } from "./RouteStop";
import { Stop } from "./Stop";
import { Trip } from "./Trip";

// DTO that represents what the bckend should return
export interface TripDetail {
  trip: Trip;
  stops: Array<{
    routeStop: RouteStop;
    stop: Stop;
    completedAt: string | null;
  }>;
}