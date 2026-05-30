import { Button } from '@/components/ui/button';
import { TripDetail } from '@/domain/models/TripDetail';
import { cn } from '@/lib/utils';

interface TripStopsListProps {
  stops: TripDetail['stops'];
}

export function TripStopsList({ stops }: TripStopsListProps) {
  // Encontramos el índice de la primera parada no completada
  const nextStopIndex = stops.findIndex(s => !s.completed);

  return (
    <ul className="max-h-64 overflow-y-auto space-y-1">
      {stops.map((stop, index) => {
        const isCompleted = stop.completed;
        const isNext = index === nextStopIndex;

        return (
          <li key={stop.routeStop.id} className="flex items-center justify-between py-1">
            <span className={cn(
              'text-sm',
              isCompleted && 'line-through text-muted-foreground',
              isNext && 'font-semibold'
            )}>
              {isCompleted ? '✓' : '→'} {stop.stop.name}
            </span>
            {isNext && (
              <Button size="sm" variant="outline" disabled>
                Confirmar
              </Button>
            )}
          </li>
        );
      })}
    </ul>
  );
}