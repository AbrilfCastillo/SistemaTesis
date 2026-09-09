# Sistema de gestión Indumentaria — v1.0.0

Aplicación de escritorio de punto de venta e inventario para una tienda de indumentaria / electrónica.

- **Rubro:** indumentaria / electrónica
- **Lenguaje:** Java 17 (Swing)
- **Medio de ejecución:** aplicación de escritorio (`java -jar indumentaria.jar`)
- **Base de datos:** SQLite (`indumentaria.db`), acceso JDBC directo (sin ORM)
- **Estado:** versión estable, aprobada para producción

## Funcionalidad

- Registro y login de usuarios, con roles `admin` y `vendedor`.
- ABM de productos (alta, baja, modificación, listado y búsqueda por nombre); la baja está restringida al rol `admin`.
- Registro de ventas con carrito de varios productos, descuento de stock, cálculo de total con IVA (21%) y descuento mayorista (10% a partir de 10 unidades en la misma venta), asociada al usuario logueado y fecha/hora.
- Historial de ventas, con detalle de los productos vendidos en cada una.
- Carga de datos de ejemplo desde el botón "Cargar datos de ejemplo" en el login, o con `java -jar indumentaria.jar --seed`.

Ver [`CHANGELOG.md`](CHANGELOG.md) para el historial de versiones e [`INSTALL.md`](INSTALL.md) para la instalación en Linux.
