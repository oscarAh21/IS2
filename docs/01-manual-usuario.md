# Manual de Usuario — Gestión de Juegos de Mesa

Este manual describe el uso completo de la aplicación de escritorio **Gestión de Juegos de Mesa**, pensada para organizar el catálogo doméstico de juegos, consultar y actualizar su estado, gestionar valoraciones y registrar partidas.

---

## 1. Requisitos previos

- **Java 21** instalado y disponible en el sistema (`java -version`).
- Sistema operativo Windows, macOS o Linux con entorno gráfico (Java Swing).
- No es necesario instalar Maven manualmente: el proyecto incluye **Maven Wrapper** (`mvnw.cmd` en Windows y `mvnw` en Linux/macOS).

---

## 2. Cómo iniciar la aplicación

Desde la carpeta raíz del proyecto:

### En Windows (PowerShell o Símbolo del sistema)
```powershell
.\mvnw.cmd compile exec:java
```

### En Linux o macOS
```bash
./mvnw compile exec:java
```

### Alternativa mediante JAR compilado
```powershell
.\mvnw.cmd package
java -jar target\gestion-juegos-mesa-1.0.0.jar
```

La primera vez que inicie la aplicación, se creará automáticamente la carpeta `data/` con los archivos `juegos_de_mesa.csv` (con los 50 juegos iniciales en estado `Disponible` y sin valoración) y `partidas.csv` (vacío con cabecera). En arranques posteriores, los datos existentes en `data/` **nunca se sobrescriben**.

---

## 3. Descripción de la ventana principal

Al abrir la aplicación se muestra la ventana principal **Gestión de Juegos de Mesa** con:

- **Barra de menú superior:**
  - `Archivo -> Recargar datos desde CSV`: vuelve a leer los archivos de `data/` y actualiza todas las pestañas.
  - `Archivo -> Salir`: cierra la aplicación.
  - `Ayuda -> Acerca de`: muestra información básica de la aplicación.
- **Cinco pestañas principales:**
  1. **Catálogo**: listado completo de los 50 juegos y búsqueda por nombre exacto.
  2. **Filtros**: filtros independientes por nombre, valoración, mínimo de jugadores y máximo de jugadores.
  3. **Estados**: consulta y cambio de estado de cualquier juego.
  4. **Valoraciones**: asignación, modificación y eliminación de valoraciones (1 a 5).
  5. **Partidas e Historial**: registro de partidas, consulta del historial por juego y consulta de juegos jugados en el último año.

Todas las tablas permiten ordenar las columnas haciendo clic en su cabecera. Cuando se modifica un estado o una valoración, el resto de pestañas se actualiza automáticamente.

---

## 4. Cómo consultar el catálogo (Pestaña «Catálogo»)

1. Al entrar en la pestaña **Catálogo** se muestran automáticamente los 50 juegos con sus columnas: `ID`, `Título`, `Editorial`, `Año`, `Mín. jug.`, `Máx. jug.`, `Duración`, `Estado` y `Valoración`.
2. Haga clic sobre cualquier fila para ver el resumen detallado del juego en la parte inferior.

### Buscar por nombre exacto
1. Escriba el título exacto en el campo **Nombre exacto** (no distingue mayúsculas ni minúsculas, por ejemplo `Catan` o `catan`).
2. Pulse **Buscar por nombre exacto** (o la tecla `Enter`).
3. Si el título coincide exactamente con uno o más juegos, se mostrarán en la tabla. Si no existe coincidencia exacta, aparecerá un aviso informativo.
4. Para volver a ver todos los juegos, pulse **Ver catálogo completo**.

---

## 5. Cómo filtrar juegos (Pestaña «Filtros»)

La pestaña **Filtros** permite elegir qué criterio desea utilizar mediante el desplegable **Criterio de filtrado**. Al seleccionar un criterio, se muestran **únicamente los campos necesarios** para ese filtro y se ocultan los demás; puede cambiar de criterio en cualquier momento sin abandonar la pantalla.

- **Nombre:** escriba un texto en `Nombre contiene:` (por ejemplo, `Ticket`) y pulse **Aplicar filtro**.
- **Valoración:** elija `Sin valoración` o un número del `1` al `5` y pulse **Aplicar filtro**.
- **Número mínimo de jugadores:** introduzca un número entero estrictamente mayor que `0` (no se permite `0` ni valores negativos) y pulse **Aplicar filtro**. Muestra los juegos cuyo mínimo de jugadores sea mayor o igual al valor indicado.
- **Número máximo de jugadores:** introduzca un número entero estrictamente mayor que `0` (no se permite `0` ni valores negativos) y pulse **Aplicar filtro**. Muestra los juegos cuyo máximo de jugadores sea menor o igual al valor indicado.
- **Validación en tiempo real:** si introduce `0`, un número negativo o un texto no numérico, aparecerá un aviso inmediato bajo el filtro y al pulsar **Aplicar filtro** se impedirá la operación con un mensaje claro.
- **Restablecer vista:** pulse **Mostrar todos los juegos** para volver a listar los 50 juegos.

---

## 6. Cómo consultar y cambiar estados (Pestaña «Estados»)

Los cinco estados permitidos son: **Disponible**, **Dañado**, **Jugado**, **Completo** y **Prestado**. Se puede cambiar libremente desde cualquier estado a cualquier otro.

1. Seleccione un juego en la tabla. En la parte inferior aparecerá el juego elegido y su estado actual.
2. Para consultar explícitamente el estado guardado, pulse **Consultar estado**: se mostrará un cuadro informativo con el estado vigente.
3. Para cambiarlo, seleccione el nuevo valor en el desplegable **Nuevo estado** y pulse **Cambiar estado**.
4. La aplicación guardará el cambio en `data/juegos_de_mesa.csv`, actualizará la tabla y mostrará un mensaje de confirmación.

---

## 7. Cómo gestionar valoraciones (Pestaña «Valoraciones»)

Cada juego puede tener una valoración entera entre **1 y 5**, o bien **no tener valoración** (`Sin valoración`).

1. Seleccione un juego en la tabla.
2. **Asignar valoración (juego sin valoración previa):** elija un valor entre `1` y `5` en el desplegable y pulse **Asignar valoración**.
3. **Modificar valoración (juego que ya tiene valoración):** elija el nuevo valor entre `1` y `5` y pulse **Modificar valoración**.
4. **Eliminar valoración:** pulse **Eliminar valoración** para dejar el juego sin valoración.

Si intenta pulsar *Asignar* sobre un juego que ya tiene valoración (o *Modificar* sobre uno que todavía no la tiene), la aplicación le avisará con un mensaje claro indicando la operación correcta.

---

## 8. Cómo registrar partidas y consultar historiales (Pestaña «Partidas e Historial»)

### Registrar una partida con ganador
1. Seleccione el juego en el desplegable **Juego**. Automáticamente se mostrará debajo la información del juego seleccionado (**nombre**, **mínimo de jugadores** y **máximo de jugadores** permitidos), actualizándose cada vez que elija otro juego.
2. Introduzca el número entero de **Participantes**, que debe estar dentro del rango permitido por el juego seleccionado (se aceptan exactamente el mínimo y el máximo; si está fuera de rango, la aplicación impedirá el registro e indicará el rango permitido).
3. Verifique o modifique la **Fecha** en formato `AAAA-MM-DD` (por defecto aparece la fecha de hoy; no se admiten fechas futuras).
4. Deje desmarcada la casilla **Partida sin ganador**.
5. Escriba el nombre en el campo **Ganador** y seleccione **Tipo ganador** (`Individual` o `Equipo`).
6. Pulse **Registrar partida**. Se guardará en `data/partidas.csv` y se mostrará confirmación con el ID asignado.

### Registrar una partida sin ganador
1. Seleccione el juego, el número de participantes y la fecha.
2. Marque la casilla **Partida sin ganador** (los campos de ganador y tipo se desactivarán automáticamente).
3. Pulse **Registrar partida**. En el historial aparecerá explícitamente `¿Ganador?: No` y `Sin ganador`.

### Consultar el historial de partidas de un juego
1. En la subpestaña **Historial de partidas**, elija el juego en el desplegable **Juego**.
2. Pulse **Consultar historial del juego** para ver únicamente sus partidas ordenadas de más reciente a más antigua.
3. Pulse **Ver todas las partidas** para mostrar el historial completo.

### Consultar los juegos jugados durante el último año
1. Abra la subpestaña **Juegos jugados en el último año**.
2. Pulse **Consultar juegos jugados en el último año**.
3. La tabla mostrará los juegos distintos que tengan al menos una partida registrada entre hace exactamente un año y la fecha actual.

---

## 9. Interpretación de mensajes de error y confirmación

- **Cuadros de confirmación (icono de información):** indican que la operación se realizó correctamente y que los cambios ya están persistidos en CSV.
- **Cuadros de error (icono de error):** aparecen cuando algún dato incumple las reglas de validación (por ejemplo, texto vacío, número no entero, fecha con formato incorrecto o futura, juego no seleccionado, o problema de lectura/escritura del archivo CSV). En ningún caso un error corrompe los archivos guardados.

---

## 10. Dónde se guardan los datos

Los datos persistentes se almacenan en la carpeta `data/` dentro del directorio del proyecto, en formato CSV (UTF-8, separador `;`):

- `data/juegos_de_mesa.csv`: los 50 juegos del catálogo con sus identificadores, estado actual y valoración.
- `data/partidas.csv`: todas las partidas registradas.

El catálogo base de solo lectura se conserva en `src/main/resources/juegos_de_mesa.csv`.

---

## 11. Ejemplos de uso paso a paso

### Ejemplo 1: Prestar «Catan» y valorarlo con un 5
1. Abra la pestaña **Estados**, seleccione **Catan** (ID #1), elija `Prestado` y pulse **Cambiar estado**. Confirme el aviso.
2. Abra la pestaña **Valoraciones**, seleccione **Catan**, elija `5` y pulse **Asignar valoración**.
3. Vuelva a la pestaña **Catálogo**: comprobará que Catan muestra estado `Prestado` y valoración `5 / 5`.

### Ejemplo 2: Registrar una partida cooperativa sin ganador de «Pandemic»
1. Abra la pestaña **Partidas e Historial**.
2. En **Juego**, seleccione `#3 - Pandemic (2008)`.
3. En **Participantes**, escriba `4`. Mantenga la fecha de hoy.
4. Marque la casilla **Partida sin ganador**.
5. Pulse **Registrar partida**.
6. Compruebe que aparece en **Historial de partidas** con `¿Ganador?: No` y que **Pandemic** aparece en la subpestaña **Juegos jugados en el último año**.
