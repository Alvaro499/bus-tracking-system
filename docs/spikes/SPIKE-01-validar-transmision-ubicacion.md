# SPIKE-01 — Validar viabilidad de transmisión de ubicación en tiempo real

## Objetivo

Investigar si la geolocalización del navegador y la transmisión periódica de coordenadas al backend son técnicamente viables antes de comprometer desarrollo en HU-17.

## Pregunta técnica a responder

¿Es posible obtener las coordenadas GPS del dispositivo desde el navegador y enviarlas periódicamente al backend de forma confiable?

## Restricciones

- **Tiempo asignado**: 1 sprint o máximo 2 días
- **Dependencias**: Ninguna
- **Bloqueadores**: HU-17, HU-18, HU-21 esperan resultado positivo

## Tareas de investigación

1. Evaluar `navigator.geolocation.watchPosition` como mecanismo de obtención continua de coordenadas
2. Evaluar `setInterval` como mecanismo de transmisión periódica
3. Implementar un prototipo mínimo que obtenga coordenadas y las envíe al backend
4. Verificar que el backend recibe y persiste las coordenadas correctamente
5. Identificar limitaciones conocidas: HTTPS requerido para GPS en móviles, coordenadas fijas en localhost, comportamiento en pérdida de señal

## Resultado esperado

Un documento de decisión o nota técnica que responda:

- [Sí/No] ¿Funciona `watchPosition` para obtener coordenadas continuas?
- [Sí/No] ¿Llegan las coordenadas al backend correctamente?
- [Sí/No] ¿El mapa del pasajero las muestra en tiempo real?
- Lista de limitaciones y riesgos identificados
- **Decisión**: ¿se puede proceder con HU-17 tal como está planteada?

## Resultado final

**Decisión: VIABLE**

### Hallazgos técnicos

- `useGeolocation` con `watchPosition` funciona correctamente en navegadores modernos
- `usePolling` con `useRef + setInterval` transmite coordenadas de forma estable sin ciclos infinitos de re-render
- El backend recibe y persiste la última ubicación conocida correctamente
- El mapa en `passengers-app` se actualiza cada 5 segundos con la posición del bus

### Limitaciones identificadas

- **HTTPS requerido**: GPS requiere HTTPS en dispositivos móviles — localhost no es suficiente, se necesita ngrok o despliegue real para pruebas en celular
- **Precisión**: En entornos sin señal GNSS clara, la precisión puede variar significativamente

### Decisión de arquitectura

Se puede proceder con HU-17, HU-18 y HU-21 tal como están planteadas.

### Riesgos residuales

- GPS en móviles requiere HTTPS — Impacto en ambiente de desarrollo local (mitigado con ngrok)
- Consumo de batería en transmisión continua — Investigar en siguiente sprint si es necesario

## Artefactos generados

1. **useGeolocation.ts** — Hook reutilizable para obtener coordenadas del GPS
2. **usePolling.ts** — Hook reutilizable para ejecutar callbacks periódicos
3. **DriverContainer** — Prototipo funcional de transmisión (será reemplazado en HU-17 final)
4. **MapContainer** — Prototipo funcional de recepción (será reemplazado en HU-01)

**Ubicación**: `frontend/management-app/src/hooks/` y `frontend/passengers-app/src/components/`

## Siguiente paso

→ Crear HU-17 usando artefactos de este SPIKE como base

---

**Investigador(es)**: [nombre]  
**Fecha de cierre**: 01/05/2026
