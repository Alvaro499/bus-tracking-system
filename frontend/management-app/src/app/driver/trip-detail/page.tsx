import { TripDetailContainer } from '../../../features/driver/trip-detail.container';

export default function TripDetailPage() {
  const mockTripId = 'b70e8400-e29b-41d4-a716-446655440001';
  return <TripDetailContainer tripId={mockTripId} />;
}

//http://localhost:3000/driver/trips/b70e8400-e29b-41d4-a716-446655440001

/**
 * Future TripDetailPage with dynamic routing:
interface TripDetailPageProps {
  params: Promise<{ tripId: string }>;
}

export default async function TripDetailPage({ params }: TripDetailPageProps) {
  const { tripId } = await params;
  return <TripDetailContainer tripId={tripId} />;
}
 * 
 */
