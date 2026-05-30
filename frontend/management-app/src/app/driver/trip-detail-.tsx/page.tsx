import { TripDetailContainer } from '../../../features/driver/trip-detail.container';

export default function TripDetailPage() {
    const mockTripId = 'trip-123';
      return <TripDetailContainer tripId={mockTripId} />;
}
