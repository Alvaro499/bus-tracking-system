import { Button } from '@/components/ui/button';
import { TripDetail } from '@/domain/models/TripDetail';
import { cn } from '@/lib/utils';

interface TripStopsListProps {
  stops: TripDetail['stops'];
  onConfirmStop: (stopId: string) => void
  disabled: boolean;
}

export function TripStopsList({ stops, onConfirmStop, disabled }: TripStopsListProps) {
  // First, we found the first non-completed stop from the list
  // this is the current stop where the bus is at the moment
  let currentUncompletedStopIndex  = -1;
  for (let i = 0; i < stops.length; i++) {
    if (stops[i].completedAt === null) {
      currentUncompletedStopIndex  = i;
      break;
    }
  }

  return (
    <ul className="max-h-64 overflow-y-auto space-y-1">
      {stops.map(function(currentStop, currentIndex) {
        const isCompleted = currentStop.completedAt !== null;
        const isActiveStop = currentIndex === currentUncompletedStopIndex;

        return (
          <li key={currentStop.routeStop.id} className="flex items-center justify-between py-1">
            <span className={cn(
              'text-sm',
              isCompleted && 'line-through text-muted-foreground',
              isActiveStop && 'font-semibold'
            )}>
              {isCompleted ? '✓' : '→'} {currentStop.stop.name}
            </span>
            {isActiveStop && (
              <Button size="sm" variant="outline" onClick={() => onConfirmStop(currentStop.routeStop.id)} disabled={disabled}>
                Confirmar
              </Button>
            )}
          </li>
        );
      })}
    </ul>
  );
}