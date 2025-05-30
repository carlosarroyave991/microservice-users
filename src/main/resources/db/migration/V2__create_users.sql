-- ================================================
-- Script para mover objetos desde el esquema micro_user a micro
-- ================================================

-- 1. Crear el esquema de destino "micro" (si no existe)
-- CREATE SCHEMA IF NOT EXISTS micro;

-- 2. Mover las tablas principales del esquema antiguo al nuevo esquema
-- ALTER TABLE micro_user.users SET SCHEMA micro;
-- ALTER TABLE micro_user.address SET SCHEMA micro;
-- ALTER TABLE micro_user.shipping_address SET SCHEMA micro;

-- 3. (Opcional) Verificar que los objetos se hayan movido correctamente
-- Puedes ejecutar: \dt micro.*   -- si usas psql

-- 4. (Opcional) Eliminar el esquema antiguo si ya no lo necesitas
-- DROP SCHEMA micro_user CASCADE;