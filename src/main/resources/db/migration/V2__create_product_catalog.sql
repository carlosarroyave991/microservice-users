-- ================================================
-- Tabla micro.category
-- ================================================
DROP TABLE IF EXISTS micro.category CASCADE;

CREATE TABLE micro.category (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    description VARCHAR(250),
    image VARCHAR(250),
    created_date DATE,
    updated_date DATE
);

-- ================================================
-- Tabla micro.products
-- ================================================
DROP TABLE IF EXISTS micro.products CASCADE;

CREATE TABLE micro.products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    brand VARCHAR(50),
    price DECIMAL(10,2),
    stock INT NOT NULL DEFAULT 0 CHECK (stock >= 0)
);

-- ================================================
-- Tabla micro.product_category
-- ================================================
DROP TABLE IF EXISTS micro.product_category CASCADE;

CREATE TABLE micro.product_category (
    id SERIAL PRIMARY KEY,
    product_id INT NOT NULL,
    category_id INT NOT NULL,
    CONSTRAINT fk_product FOREIGN KEY (product_id)
        REFERENCES micro.products(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_category FOREIGN KEY (category_id)
        REFERENCES micro.category(id) ON DELETE CASCADE ON UPDATE CASCADE
);
