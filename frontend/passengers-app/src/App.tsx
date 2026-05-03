import { useState } from 'react'
import { MapContainer } from "./features/map/map.container";
import { GlobalErrorBoundary } from "./lib/errors/GlobalErrorBoundary";
import './App.css'

function App() {
  const [count, setCount] = useState(0)

  return (
    <GlobalErrorBoundary>
      <div>
        <MapContainer/>
      </div>
    </GlobalErrorBoundary>
  )
}

export default App
