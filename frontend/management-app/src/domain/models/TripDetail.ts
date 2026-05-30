import { RouteStop } from "./RouteStop";
import { Stop } from "./Stop";
import { Trip } from "./Trip";

export interface TripDetail {
  trip: Trip;
  stops: Array<{
    routeStop: RouteStop;
    stop: Stop;
    completed: boolean;
  }>;
}