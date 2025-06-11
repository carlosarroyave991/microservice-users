-- ================================================
-- Script PostgreSQL
-- ================================================
-- Crear el esquema (si no existe)
CREATE SCHEMA IF NOT EXISTS micro;
-- ================================================
-- Tabla micro.storage
-- ================================================
CREATE TABLE IF NOT EXISTS micro.storage (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL
);

-- ================================================
-- Tabla micro.supply
-- ================================================
CREATE TABLE IF NOT EXISTS micro.supply (
    id SERIAL PRIMARY KEY,
    product_id INT NOT NULL,
    quantity_supply INT NOT NULL,
    storage_id INT NOT NULL,
    supply_date TIMESTAMP NOT NULL,
    CONSTRAINT fk_products_supply FOREIGN KEY (product_id) REFERENCES micro.products(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_storage_supply FOREIGN KEY (storage_id) REFERENCES micro.storage(id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- ================================================
-- Tabla micro.warehouse_address
-- ================================================
CREATE TABLE IF NOT EXISTS micro.warehouse_address (
    id SERIAL PRIMARY KEY,
    storage_id INT NOT NULL,
    address_id INT NOT NULL,
    CONSTRAINT fk_storage_warehouse FOREIGN KEY (storage_id) REFERENCES micro.storage(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_address_warehouse FOREIGN KEY (address_id) REFERENCES micro.address(id) ON DELETE CASCADE ON UPDATE CASCADE
);