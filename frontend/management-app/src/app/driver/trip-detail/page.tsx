import { TripDetailContainer } from '../../../features/driver/trip-detail.container';

export default function TripDetailPage() {
    const mockTripId = 'trip-123';
      return <TripDetailContainer tripId={mockTripId} />;
}



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
