import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import type { BusLocation } from '../domain/models/BusLocation';
import 'leaflet/dist/leaflet.css';
import L from 'leaflet';
import iconUrl from 'leaflet/dist/images/marker-icon.png';
import iconShadow from 'leaflet/dist/images/marker-shadow.png';

// Fix icono por defecto de Leaflet con Vite
const defaultIcon = L.icon({
  iconUrl,
  shadowUrl: iconShadow,
  iconAnchor: [12, 41],
});
L.Marker.prototype.options.icon = defaultIcon;

interface Props {
  location: BusLocation;
}

export function BusMap({ location }: Props) {
  return (
    <MapContainer
      center={[location.lat, location.lng]}
      zoom={15}
      style={{ height: '100vh', width: '100%' }}
    >
      <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" />
      <Marker position={[location.lat, location.lng]}>
        <Popup>Bus {location.busId}</Popup>
      </Marker>
    </MapContainer>
  );
}