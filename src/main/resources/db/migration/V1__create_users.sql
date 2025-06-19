-- ================================================
-- Script PostgreSQL validado para migraciones con Flyway (versión 2)
-- ================================================

-- 1. Crear el esquema si no existe
CREATE SCHEMA IF NOT EXISTS micro;

-- ================================================
-- Tabla micro.users
-- ================================================
DROP TABLE IF EXISTS micro.users CASCADE;

CREATE TABLE micro.users (
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
-- Tabla micro.address
-- ================================================
DROP TABLE IF EXISTS micro.address CASCADE;

CREATE TABLE micro.address (
    id SERIAL PRIMARY KEY,
    address VARCHAR(250),
    city VARCHAR(250),
    country VARCHAR(250),
    street VARCHAR(250),
    zip_code VARCHAR(250)
);

-- ================================================
-- Tabla micro.shipping_address
-- ================================================
DROP TABLE IF EXISTS micro.shipping_address CASCADE;

CREATE TABLE micro.shipping_address (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    address_id INT NOT NULL,
    CONSTRAINT fk_shipping_user FOREIGN KEY (user_id)
        REFERENCES micro.users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_shipping_address FOREIGN KEY (address_id)
        REFERENCES micro.address(id) ON DELETE CASCADE ON UPDATE CASCADE
);
