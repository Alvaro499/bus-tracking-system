import { Card, CardHeader, CardTitle, CardDescription, CardContent, CardFooter } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Clock, MapPin } from 'lucide-react';
import type { Trip } from '@/domain/models/Trip';

interface TripCardProps {
  trip: Trip;
  onTakeTrip: (tripId: string) => void;
}

function formatTime(isoTime: string): string {
  const [hours, minutes] = isoTime.split(':');
  const h = parseInt(hours, 10);
  const ampm = h >= 12 ? 'p.m.' : 'a.m.';
  const hour12 = h % 12 || 12;
  return `${hour12}:${minutes} ${ampm}`;
}

export function TripCard({ trip, onTakeTrip }: TripCardProps) {
  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-lg font-semibold">
          {trip.routeName}
        </CardTitle>
        <CardDescription className="flex items-center gap-1 text-muted-foreground">
          <MapPin size={14} />
          {trip.origin} → {trip.destination}
        </CardDescription>
      </CardHeader>
      <CardContent>
        <div className="flex items-center gap-2 text-sm">
          <Clock size={14} className="text-muted-foreground" />
          <span className="font-medium">Salida:</span>
          <span>{formatTime(trip.departureTime)}</span>
        </div>
      </CardContent>
      <CardFooter>
        <Button 
          className="w-full" 
          onClick={() => onTakeTrip(trip.id)}
          disabled={trip.status !== 'PLANNED'}
        >
          Tomar
        </Button>
      </CardFooter>
    </Card>
  );
}