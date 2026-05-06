# ============================================================================
# PASSENGERS-APP: QUICK COMMANDS
# ============================================================================

# ============================================================================
# DESARROLLO
# ============================================================================

# Instalar dependencias (primera vez)
cd frontend/passengers-app
npm install

# Correr en desarrollo (Vite dev server)
npm run dev

# Correr en puerto específico
npm run dev -- --port 3000

# Acceder a la app
# http://localhost:5173 (puerto default de Vite)
# O el que especifiques con --port


# ============================================================================
# BUILD & PREVIEW
# ============================================================================

# Compilar para producción
npm run build

# Ver preview de build
npm run preview


# ============================================================================
# LINTING
# ============================================================================

# Verificar código
npm run lint

# Arreglar errores automáticos
npm run lint -- --fix
