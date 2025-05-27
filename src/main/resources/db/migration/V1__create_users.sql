-- ================================================
-- Script PostgreSQL corregido y mejorado (versión 2)
-- ================================================

-- 1. Crear el esquema (si no existe)
CREATE SCHEMA IF NOT EXISTS micro_user;

-- ================================================
-- Tabla micro_user_user.users
-- ================================================
DROP TABLE IF EXISTS micro_user.users CASCADE;

CREATE TABLE micro_user.users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(250),
    user_type VARCHAR(50),
    email VARCHAR(250),
    phone VARCHAR(20),
    dni VARCHAR(20),
    username VARCHAR(250),
    password VARCHAR(250)
);

-- ================================================
-- Tabla micro_user.address
-- ================================================
DROP TABLE IF EXISTS micro_user.address CASCADE;

CREATE TABLE micro_user.address(
    id SERIAL PRIMARY KEY,
    address VARCHAR(250),
    city VARCHAR(250),
    country VARCHAR(250),
    street VARCHAR(250),
    zip_code VARCHAR(250)
);

-- ================================================
-- Tabla micro_user.shipping_address
-- ================================================
DROP TABLE IF EXISTS micro_user.shipping_address CASCADE;

CREATE TABLE micro_user.shipping_address(
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    address_id INT NOT NULL,
    CONSTRAINT fk_shipping_user FOREIGN KEY (user_id) REFERENCES micro_user.users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_shipping_address FOREIGN KEY (address_id) REFERENCES micro_user.address(id) ON DELETE CASCADE ON UPDATE CASCADE
);


