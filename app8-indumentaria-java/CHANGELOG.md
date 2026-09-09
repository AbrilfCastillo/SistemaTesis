# Changelog — Indumentaria

## v1.0.0 — 2026-08-24

Versión estable, primera entrega en producción.

### Agregado
- Registro y login de usuarios con roles `admin` y `vendedor`.
- ABM de productos con búsqueda por nombre.
- Registro de ventas con carrito multi-producto, descuento de stock, cálculo de IVA (21%) y descuento mayorista (10% a partir de 10 unidades).
- Historial de ventas con detalle por producto.
- Esquema SQLite (`src/main/resources/schema.sql`) y carga de datos de ejemplo (botón en el login o `--seed`).
- Empaquetado como JAR ejecutable con Maven (`indumentaria-1.0.0.jar`).
