# Manual de instalación — Indumentaria (Linux)

Probado en Ubuntu 22.04 LTS con OpenJDK 17 y Maven.

## 1. Requisitos previos

```bash
sudo apt update
sudo apt install -y openjdk-17-jdk maven sqlite3
```

Verificar la versión de Java instalada:

```bash
java -version
```

## 2. Obtener el código

```bash
cd app8-indumentaria-java
```

(Copiar la carpeta del proyecto, entregada junto con el resto del TP, al servidor/VM donde se va a instalar.)

## 3. Compilar

```bash
mvn package
```

Esto genera el JAR ejecutable con todas las dependencias incluidas (el driver de SQLite) en:

```
target/indumentaria-1.0.0.jar
```

## 4. Cargar datos de ejemplo

La base `indumentaria.db` se crea automáticamente la primera vez que se ejecuta la aplicación, en el directorio desde donde se corre el jar. Para cargarla además con usuarios, productos y ventas de ejemplo, ejecutar:

```bash
java -jar target/indumentaria-1.0.0.jar --seed
```

Esto crea el esquema (si no existía) y carga los datos de ejemplo. Si ya se habían cargado antes, no los duplica.

Usuarios de prueba:

| Usuario | Contraseña | Rol |
|---|---|---|
| admin | admin123 | admin |
| mrodriguez | vend2024 | vendedor |
| jgonzalez | gonzalez99 | vendedor |

También se puede cargar el mismo set de datos desde el botón "Cargar datos de ejemplo" en la pantalla de login, una vez que la aplicación ya está abierta.

## 5. Ejecutar la aplicación

```bash
java -jar target/indumentaria-1.0.0.jar
```

**Nota:** es una aplicación de escritorio con interfaz gráfica (Swing). Requiere un entorno gráfico disponible. Si se va a correr en una VM/servidor sin escritorio, hace falta instalar un entorno gráfico (por ejemplo `sudo apt install -y xfce4` y un visor VNC) o usar X11 forwarding (`ssh -X`) desde una máquina que sí tenga entorno gráfico.

## 6. Inspeccionar la base de datos directamente

```bash
sqlite3 indumentaria.db
```

```sql
.tables
.schema productos
SELECT * FROM usuarios;
SELECT * FROM productos;
SELECT * FROM ventas;
SELECT * FROM venta_detalle;
```

Salir con `.quit`.

## 7. Desinstalación / limpieza

```bash
rm target/indumentaria-1.0.0.jar
rm indumentaria.db
```

Y borrar la carpeta del proyecto si ya no se necesita el código fuente.
