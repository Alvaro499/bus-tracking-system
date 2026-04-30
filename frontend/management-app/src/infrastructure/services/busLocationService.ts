const BASE_URL = process.env.NEXT_PUBLIC_API_URL;

/*

Identifica QUÉ recurso  → URL
Son datos del recurso   → Body

*/

export async function updateBusLocation(busId: string, lat: number, lng: number): Promise<void> {
  await fetch(`${BASE_URL}/tracking/buses/${busId}/location`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ lat, lng }),
  });
}