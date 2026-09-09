-- Esquema de la base de datos de Indumentaria (SQLite).
-- Nivel de calidad objetivo: REGULAR — ver detalle en README

CREATE TABLE IF NOT EXISTS usuarios (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    rol TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS productos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    precio TEXT NOT NULL,
    stock INTEGER NOT NULL DEFAULT 0,
    categoria TEXT
);

CREATE TABLE IF NOT EXISTS ventas (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    usuario_id INTEGER,
    fecha TEXT NOT NULL,
    total TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS venta_detalle (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    venta_id INTEGER,
    producto_id INTEGER,
    cantidad INTEGER NOT NULL,
    precio_unitario TEXT NOT NULL,
    subtotal TEXT NOT NULL
);
