-- ================================================
-- Script PostgreSQL bounded context ventas
-- ================================================
-- ================================================
-- Tabla micro.car
-- ================================================
CREATE TABLE IF NOT EXISTS micro.car (
    id SERIAL PRIMARY KEY,
    created_date TIMESTAMP NOT NULL,
    user_id INT NOT NULL,
    CONSTRAINT fk_car_users FOREIGN KEY (user_id) REFERENCES micro.users(id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- ================================================
-- Tabla micro.order
-- ================================================
CREATE TABLE IF NOT EXISTS micro.order (
    id SERIAL PRIMARY KEY,
    order_date TIMESTAMP NOT NULL,
    reference VARCHAR(255) NOT NULL,
    payment_method VARCHAR(255) NOT NULL,
    amount_value INT NOT NULL,
    sale_price DECIMAL(10,2) NOT NULL,
    order_status VARCHAR(50) NOT NULL,
    car_id INT NOT NULL,
    storage_id INT NOT NULL,
    CONSTRAINT fk_car_order FOREIGN KEY (car_id) REFERENCES micro.car(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_storage_order FOREIGN KEY (storage_id) REFERENCES micro.storage(id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- ================================================
-- Tabla micro.product_car
-- ================================================
CREATE TABLE IF NOT EXISTS micro.product_car (
    id SERIAL PRIMARY KEY,
    quantity INT NOT NULL,
    product_id INT NOT NULL,
    car_id INT NOT NULL,
    CONSTRAINT fk_car_productcar FOREIGN KEY (car_id) REFERENCES micro.car(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_product_productcar FOREIGN KEY (product_id) REFERENCES micro.products(id) ON DELETE CASCADE ON UPDATE CASCADE
);
