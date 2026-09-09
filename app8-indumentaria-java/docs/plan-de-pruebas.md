# Plan de pruebas manual — Indumentaria v1.0.0

Este plan se ejecuta manualmente antes de cada release. No hay tests automatizados en este nivel de calidad.

| # | Caso | Pasos | Resultado esperado | OK/Falla |
|---|---|---|---|---|
| 1 | Registro de usuario nuevo | Abrir la app, click en "Registrarse", completar usuario/contraseña/rol nuevos y confirmar | Se muestra "Usuario registrado correctamente" y el usuario puede loguearse | |
| 2 | Registro con usuario duplicado | Registrar dos veces el mismo nombre de usuario | El segundo intento muestra "Ese nombre de usuario ya existe" | |
| 3 | Login correcto | Ingresar usuario y contraseña válidos (por ejemplo admin / admin123) | Se abre el panel principal mostrando el usuario y el rol conectado | |
| 4 | Login incorrecto | Ingresar una contraseña incorrecta para un usuario existente | Mensaje "Usuario o contraseña incorrectos", no se abre el panel principal | |
| 5 | Alta de producto | En la solapa Productos, click en "Nuevo producto", completar nombre/precio/stock/categoría y guardar | El producto aparece en el listado con los datos cargados | |
| 6 | Alta de producto sin nombre | Intentar guardar un producto nuevo dejando el nombre vacío | Mensaje "El nombre es obligatorio", no se crea el producto | |
| 7 | Modificar producto | Seleccionar un producto existente, click en "Editar producto", cambiar el precio y guardar | El nuevo precio se refleja en el listado al refrescar | |
| 8 | Eliminar producto (rol vendedor) | Loguearse con un usuario con rol vendedor e intentar eliminar un producto | Mensaje "No tiene permisos para esta acción", el producto no se elimina | |
| 9 | Búsqueda de productos | Escribir parte de un nombre en el buscador de la solapa Productos y presionar "Buscar" | Solo se listan los productos cuyo nombre contiene el texto buscado | |
| 10 | Registrar venta con stock suficiente | En la solapa Registrar venta, agregar 2 unidades de un producto con stock disponible y confirmar | Venta registrada, el stock del producto se descuenta y el total incluye IVA (21%) | |
| 11 | Registrar venta con stock insuficiente | Intentar agregar al carrito más unidades de un producto que las disponibles en stock | Mensaje "Stock insuficiente", el ítem no se agrega al carrito | |
| 12 | Descuento mayorista | Agregar 10 o más unidades de un mismo producto (o la suma de varios productos) al carrito y confirmar la venta | El total aplica el 10% de descuento mayorista antes de sumar el IVA | |
| 13 | Historial de ventas | Ir a la solapa "Historial de ventas" y seleccionar una venta de la lista | Se muestran los datos generales de la venta y, al seleccionarla, el detalle de productos vendidos | |

## Registro de resultados

Completar la columna "OK/Falla" en cada ejecución del plan, indicando versión probada y fecha.
