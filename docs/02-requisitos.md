# Especificación de Requisitos — Gestión de Juegos de Mesa

## 0. Acuerdos
A partir de los acuerdos con el cliente, se desarrollan los requisitos del sistema.
| ID | Pregunta o incertidumbre | Respuesta y fecha | Criterio de aceptación |
| --- | --- | --- | --- |
| A-01 | ¿Qué representa cada registro? | Cada juego es una entidad independiente. Pueden repetirse nombres. 24/11/2026 | Cada juego tiene un identificador único. |
| A-02 | ¿Qué estados existen? | Disponible, Dañado, Jugado, Completo y Prestado. 24/11/2026 | Se aceptan los cinco estados. |
| A-03 | ¿Hay transiciones prohibidas? | No existen transiciones prohibidas. 24/11/2026 | Se permite cambiar a cualquiera de los cinco estados. |
| A-04 | ¿Qué contiene el catálogo? | 50 juegos cargados desde CSV. 24/11/2026 | Se cargan los registros iniciales. |
| A-05 | ¿Cómo se representa la duración? | Mediante duración mínima y máxima. 24/11/2026 | 60 se representa como 60-60 y 30-60 como 30-60. |
| A-06 | ¿Cómo se representa la valoración? | Entero de 1 a 5 o sin valorar. 24/11/2026 | Se acepta ausencia de valoración o un entero válido. |
| A-07 | ¿Qué validaciones se aplican? | Campos obligatorios, año válido, jugadores coherentes, valoración y duración válidas. 24/11/2026 | Se rechazan entradas incorrectas. |
| A-08 | ¿Dónde se guardan los juegos? | En CSV, conservando los 50 registros iniciales. Con catálogo fijo. 24/11/2026 | Los cambios permanecen después de reiniciar. |
| A-09 | ¿Qué información contiene una partida? | Juego, participantes, ganador opcional y fecha. 24/11/2026 | Se registra juego, participantes, ganador opcional y fecha. |
| A-10 | ¿Cómo se consulta un juego? | Por nombre y coincidencia exacta. 24/11/2026. 24/11/2026 | Se devuelve el resultado correspondiente o se informa de que no existe. |
| A-11 | ¿Qué debe ocurrir ante una consulta inexistente? | Informar de que no se ha encontrado. 24/11/2026 | Se muestra el mensaje correspondiente. |
| A-12 | ¿Puede haber copias de un juego? | Sí. 24/11/2026 | Para manejar como entidades distintas, los identificadores deben ser únicos. Se rechaza un identificador duplicado. 24/11/2026 |
| A-13 | ¿Cómo se gestionan los campos vacíos y formatos incorrectos? | No se permiten campos vacíos y se informa de formatos incorrectos. 24/11/2026 | La operación se rechaza con un mensaje de error. |
| A-14 | ¿Qué resultado se muestra al usuario? | Mensaje de éxito o error. 24/11/2026 | Cada operación informa de su resultado. |
| A-15 | ¿Qué información debe incluir el historial? | Partidas jugadas, juegos jugados durante el último año y partidas por juego. 24/11/2026 | Deben comprobarse las consultas de partidas por juego y juegos jugados durante el último año. |
| A-16 | ¿Se registra el estado anterior y nuevo? | No. 24/11/2026 | El historial solicitado es de partidas, no un historial de cambios de estado. |
| A-17 | ¿Se registra fecha y hora? | Se registra la fecha. | Cada partida almacena su fecha. |
| A-18 | Estado inicial de los juegos | Todos los juegos comenzarán con el estado Disponible. | Al registrar o cargar un juego en el sistema, su estado inicial será automáticamente Disponible, sin que el usuario tenga que seleccionarlo manualmente. |

## 1. Descripción del sistema

La aplicación **Gestión de Juegos de Mesa** es un sistema de escritorio desarrollado en Java 21 con interfaz gráfica Java Swing y persistencia local en archivos CSV. Permite gestionar una colección doméstica fija de 50 juegos de mesa: consultar el catálogo, buscar y filtrar juegos, controlar su estado, asignar valoraciones personales y llevar un registro histórico de partidas.

---

## 2. Alcance

### Incluido en el alcance
- Inicialización automática de la persistencia local en `data/` a partir del catálogo inicial de 50 juegos sin sobrescribir datos existentes.
- Consulta del catálogo completo y búsqueda por nombre exacto.
- Consulta y modificación del estado de cualquier juego entre los cinco estados permitidos.
- Asignación, modificación y eliminación de valoraciones opcionales (1 a 5).
- Filtrado independiente por nombre, valoración, número mínimo de jugadores y número máximo de jugadores.
- Registro de partidas (con ganador individual, ganador por equipo o sin ganador), consulta de historial por juego y consulta de juegos jugados durante el último año.
- Validación de datos de entrada y protección frente a corrupción de archivos CSV.

### Fuera del alcance
- Alta o eliminación de nuevos títulos fuera del catálogo inicial de 50 juegos.
- Arquitectura cliente-servidor, acceso remoto, base de datos relacional o interfaz web.
- Combinación simultánea de múltiples criterios de filtro (no solicitada; cada criterio se implementa como operación independiente).

---

## 3. Actores

| Actor | Descripción |
| :--- | :--- |
| **Usuario propietario** | Persona que gestiona su colección doméstica de juegos de mesa desde la interfaz de escritorio Java Swing. |

---

## 4. Requisitos funcionales

### RF-01. Catálogo de juegos
- **RF-01.1:** Cargar los 50 juegos iniciales desde el recurso CSV del proyecto e inicializar `data/juegos_de_mesa.csv` si no existe.
- **RF-01.2:** Mostrar el listado completo de juegos con toda su información (ID, título, editorial, año, mínimo y máximo de jugadores, duración mínima y máxima, estado y valoración).
- **RF-01.3:** Consultar juegos por nombre exacto (sin distinguir mayúsculas/minúsculas).
- **RF-01.4:** Tratar dos juegos con el mismo título como entidades independientes si poseen identificadores distintos.

### RF-02. Gestión de estados
- **RF-02.1:** Consultar el estado actual de un juego.
- **RF-02.2:** Modificar el estado de un juego a cualquiera de los cinco valores permitidos (`Disponible`, `Dañado`, `Jugado`, `Completo`, `Prestado`), sin restricciones de transición.
- **RF-02.3:** Mostrar de forma clara el resultado de la operación y persistir el cambio en `data/juegos_de_mesa.csv`.

### RF-03. Valoraciones
- **RF-03.1:** Asignar una valoración entera entre 1 y 5 a un juego sin valoración.
- **RF-03.2:** Modificar una valoración existente dentro del rango 1 a 5.
- **RF-03.3:** Eliminar la valoración de un juego.
- **RF-03.4:** Permitir que un juego no tenga valoración (`null` / `Sin valoración`).

### RF-04. Filtros
Permitir filtrar los juegos mediante criterios independientes seleccionables:
- **RF-04.1:** Filtrar por nombre (coincidencia parcial insensible a mayúsculas/minúsculas).
- **RF-04.2:** Filtrar por valoración exacta (1 a 5) o por ausencia de valoración (`Sin valoración`).
- **RF-04.3:** Filtrar por número mínimo de jugadores (`juego.minJugadores >= valor`), exigiendo un entero estrictamente mayor que 0 (se rechazan 0 y negativos), validado tanto al introducir el valor como al ejecutar el filtrado.
- **RF-04.4:** Filtrar por número máximo de jugadores (`juego.maxJugadores <= valor`), exigiendo un entero estrictamente mayor que 0 (se rechazan 0 y negativos), validado tanto al introducir el valor como al ejecutar el filtrado.
- **RF-04.5:** Selección dinámica del criterio de filtrado: el usuario elige qué filtro desea aplicar y la interfaz muestra únicamente los campos de ese criterio, ocultando los demás y permitiendo cambiar de criterio sin abandonar la pantalla.

### RF-05. Gestión de partidas
- **RF-05.1:** Registrar una partida asociada a un juego del catálogo con número de participantes y fecha, validando que el número de participantes se encuentre dentro del rango real permitido por el juego seleccionado (`minJugadores <= participantes <= maxJugadores`, ambos inclusive).
- **RF-05.2:** Permitir registrar ganador con su tipo (`Individual` o `Equipo`), o registrar expresamente que la partida no tuvo ganador.
- **RF-05.3:** Consultar el historial de partidas de un juego concreto.
- **RF-05.4:** Consultar los juegos distintos que se han jugado durante el último año.
- **RF-05.5:** Mostrar automáticamente la información del juego seleccionado en el `JComboBox` del formulario de registro de partidas (como mínimo nombre del juego, número mínimo de jugadores y número máximo de jugadores), actualizándose automáticamente al cambiar la selección.

### Criterios de aceptación (requisitos modificados y nuevos)

| ID | Criterio de aceptación |
| :--- | :--- |
| **RF-04.3** | Al introducir `0` o un número negativo en el filtro de mínimo de jugadores, se muestra un aviso inmediato y al pulsar aplicar se impide el filtrado con mensaje claro; cualquier entero `>= 1` devuelve los juegos con `minJugadores >= valor`. |
| **RF-04.4** | Al introducir `0` o un número negativo en el filtro de máximo de jugadores, se muestra un aviso inmediato y al pulsar aplicar se impide el filtrado con mensaje claro; cualquier entero `>= 1` devuelve los juegos con `maxJugadores <= valor`. |
| **RF-04.5** | Al seleccionar un criterio en el desplegable de filtros, solo son visibles los campos de ese criterio y permanecen ocultos los del resto; cambiar de criterio actualiza la vista sin abandonar la pestaña. |
| **RF-05.1** | Al registrar una partida, se aceptan exactamente `minJugadores` y `maxJugadores` del juego seleccionado; si el valor es inferior al mínimo o superior al máximo del juego, se impide el registro y se muestra un mensaje con el rango permitido y el motivo. |
| **RF-05.5** | Al cargar la pestaña de partidas o cambiar el juego en el `JComboBox`, el panel informativo muestra inmediatamente el nombre del juego, el mínimo de jugadores y el máximo de jugadores sin necesidad de consultar el catálogo. |

---

## 5. Requisitos no funcionales

| ID | Categoría | Descripción |
| :--- | :--- | :--- |
| **RNF-01** | Tecnología | Implementación obligatoria en Java 21 con compilación mediante Maven y Maven Wrapper (`mvnw` / `mvnw.cmd`). |
| **RNF-02** | Interfaz | Interfaz de escritorio exclusivamente en Java Swing, sin tecnologías web. |
| **RNF-03** | Arquitectura | Arquitectura en 4 capas (Presentación, Aplicación, Dominio, Persistencia) con separación estricta de responsabilidades. |
| **RNF-04** | Persistencia | Almacenamiento en archivos CSV UTF-8 con escritura segura (archivo temporal + reemplazo atómico) para evitar pérdida o corrupción de datos. |
| **RNF-05** | Calidad y pruebas | Pruebas unitarias automatizadas con JUnit 5, cobertura con JaCoCo y configuración preparada para SonarQube. |
| **RNF-06** | Mantenibilidad | Organización modular de servicios y paneles orientada al trabajo paralelo de 5 integrantes en ramas independientes. |

---

## 6. Reglas de negocio y validaciones

1. **Catálogo cerrado:** Solo existen los 50 juegos del catálogo inicial; no se permite añadir títulos nuevos.
2. **Identificadores únicos:** Tanto juegos como partidas tienen identificadores enteros positivos únicos (`> 0`).
3. **Estado inicial:** Todos los juegos del catálogo inicial comienzan en estado `Disponible` y sin valoración.
4. **Transiciones de estado:** Cualquier estado puede cambiar a cualquiera de los cinco estados permitidos.
5. **Campos obligatorios:** Título, editorial, estado y fecha de partida no pueden estar vacíos ni ser nulos.
6. **Año de lanzamiento:** Entero positivo y no posterior al año actual.
7. **Número de jugadores:** Los valores mínimo y máximo de jugadores deben ser enteros estrictamente mayores que cero (`> 0`); no se permite `0` ni valores negativos.
8. **Duración:** Mínima y máxima enteras `> 0`, con `duracionMaxima >= duracionMinima`. Si el CSV original contiene un valor único (p. ej. `60`), se almacena `min = 60, max = 60`; si es un intervalo (p. ej. `30-60`), se almacena `min = 30, max = 60`.
9. **Valoración:** Nula (sin valoración) o entero en el intervalo `[1, 5]`.
10. **Participantes en partida:** Número entero positivo comprendido dentro del rango real del juego seleccionado (`juego.minJugadores <= participantes <= juego.maxJugadores`), aceptando exactamente los límites mínimo y máximo.
11. **Coherencia de ganador:** Si hay ganador (texto no vacío), el tipo de ganador (`Individual` o `Equipo`) es obligatorio. Si no hay ganador, tanto el ganador como el tipo deben quedar vacíos (`null`).
12. **Fecha de partida:** Fecha válida en formato ISO `AAAA-MM-DD`, no posterior a la fecha actual.
13. **Integridad CSV:** Se valida el número de columnas, el cierre de comillas y el formato de cada campo al leer CSV; cualquier error lanza `ValidacionException` o `PersistenciaException` sin corromper los archivos.

---

## 7. Matriz de trazabilidad

| Requisito | Servicio de Aplicación | Interfaz Swing | Persistencia | Pruebas Unitarias |
| :--- | :--- | :--- | :--- | :--- |
| **RF-01** | `CatalogoService` | `CatalogoPanel` | `InicializadorDatos`, `CsvJuegoRepositorio` | `CatalogoServiceTest`, `CsvPersistenciaTest`, `JuegoTest` |
| **RF-02** | `EstadoJuegoService` | `EstadosPanel` | `CsvJuegoRepositorio` | `EstadoJuegoServiceTest`, `JuegoTest` |
| **RF-03** | `ValoracionService` | `ValoracionesPanel` | `CsvJuegoRepositorio` | `ValoracionServiceTest`, `JuegoTest` |
| **RF-04 (RF-04.1–04.5)** | `FiltroJuegosService` | `FiltrosPanel` | `CsvJuegoRepositorio` | `FiltroJuegosServiceTest`, `FiltrosPanelTest` |
| **RF-05 (RF-05.1–05.5)** | `PartidaService` | `PartidasPanel` | `CsvPartidaRepositorio`, `CsvJuegoRepositorio` | `PartidaServiceTest`, `PartidaTest`, `PartidasPanelTest`, `CsvPersistenciaTest` |
