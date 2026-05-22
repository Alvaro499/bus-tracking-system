import { Component, ReactNode } from 'react';
import { ApiErrorClass } from './apiError';

interface Props {
  children: ReactNode;
}

interface State {
  error: ApiErrorClass | null;
}

/**
 * Global Error Boundary - equivalent to @ExceptionHandler in backend.
 * Captures any ApiErrorClass that bubbles up from child components.
 * Decides what to render based on error.code, not error.userMessage.
 * 
 * This is the only place where UI decisions are made based on error codes.
 */
export class GlobalErrorBoundary extends Component<Props, State> {
    constructor(props: Props) {
        super(props);
        this.state = { error: null };
    }

  /**
   * React calls this method when a child component throws an error.
   * This is the Error Boundary lifecycle method.
   */
  static getDerivedStateFromError(error: unknown): State {
    // If it's already an ApiErrorClass, use it directly
    if (error instanceof ApiErrorClass) {
      return { error };
    }

    // If it's any other error (bug, null pointer, etc), wrap it
    return {
      error: new ApiErrorClass(
        'UNEXPECTED_ERROR',
        'Ocurrió un error inesperado',
        500,
        Date.now()
      ),
    };
  }

  /**
   * Optional: Log the error to a service for monitoring.
   * Backend equivalent: log.error() in @ExceptionHandler
   */
  componentDidCatch(error: unknown, errorInfo: any) {
    console.error('Error captured by GlobalErrorBoundary:', error, errorInfo);
  }

  render() {
    const error = this.state.error;

    if (error) {
      // Decide what to show based on error.code (the contract with backend)
      // NOT based on error.userMessage (that's only for users)
      switch (error.code) {
        case 'BUS_NOT_FOUND':
          return (
            <div style={{ padding: '20px', color: 'red' }}>
              <h2>Bus no encontrado</h2>
              <p>El bus solicitado no existe en el sistema.</p>
            </div>
          );

        case 'INVALID_COORDINATES':
          return (
            <div style={{ padding: '20px', color: 'red' }}>
              <h2>Coordenadas inválidas</h2>
              <p>Las coordenadas enviadas no son válidas.</p>
            </div>
          );

        case 'UNAUTHORIZED':
          return (
            <div style={{ padding: '20px', color: 'red' }}>
              <h2>No autorizado</h2>
              <p>Debes iniciar sesión para acceder.</p>
            </div>
          );

        case 'UNEXPECTED_ERROR':
        default:
          return (
            <div style={{ padding: '20px', color: 'red' }}>
              <h2>Error inesperado</h2>
              <p>{error.userMessage}</p>
              <details style={{ marginTop: '10px', fontSize: '12px' }}>
                <summary>Detalles técnicos (dev only)</summary>
                <pre>{JSON.stringify({ code: error.code, status: error.status }, null, 2)}</pre>
              </details>
            </div>
          );
      }
    }

    // No error, render children normally
    return this.props.children;
  }
}
