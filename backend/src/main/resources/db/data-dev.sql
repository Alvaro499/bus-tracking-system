
-- ============================================================================
-- DEVELOPMENT SEEDS ORCHESTRATOR
-- ============================================================================
-- Carga todos los seeds de test en orden de dependencias.
-- IMPORTANTE: Mantener orden (base primero, luego features por HU)
-- ============================================================================

-- Base seeds (required for all features)
\i seeds/00-base.sql

-- Feature seeds by UserStory
\i seeds/hu-17-trips.sql

-- Future HUs can be added below:
-- \i seeds/hu-18-auth.sql
-- \i seeds/hu-19-notifications.sql