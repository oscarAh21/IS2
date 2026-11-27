# Contribuciones por Integrante — Rama `feature/proyecto-completo-5-integrantes`

Este documento es externo al repositorio de trabajo y resume la organización del proyecto **Gestión de Juegos de Mesa** en **exactamente cinco commits incrementales** sobre la rama `feature/proyecto-completo-5-integrantes`, excluyendo completamente archivos de pruebas (`src/test/`) y documentación Markdown (`docs/`, `*.md`).

---

## 1. Tabla resumen de los cinco commits

| Commit | Hash | Integrante | ID | Mensaje del commit |
| :---: | :--- | :--- | :---: | :--- |
| **1** | `8eabcd0` | Oscar | A | `feat: implementación de estructura inicial, configuración Maven y modelo base de dominio (integrante A)` |
| **2** | `33d414a` | Lobna | B | `feat: implementación de persistencia CSV de juegos, catálogo y gestión de estados (integrante B)` |
| **3** | `6e5d261` | Nasir | C | `feat: implementación de gestión de valoraciones y filtros de búsqueda (integrante C)` |
| **4** | `90146a8` | Raghad | D | `feat: implementación de gestión de partidas, historial y validación de participantes (integrante D)` |
| **5** | `80605db` | Younes | E | `feat: implementación e integración final de la interfaz Java Swing y punto de entrada (integrante E)` |

---

## 2. Detalle por integrante

### Commit 1 — Integrante A: Oscar

- **Hash:** `8eabcd0`
- **Mensaje:** `feat: implementación de estructura inicial, configuración Maven y modelo base de dominio (integrante A)`
- **Archivos y componentes principales incorporados (17 archivos):**
  - Configuración Maven y calidad: `pom.xml`, `mvnw`, `mvnw.cmd`, `.mvn/wrapper/maven-wrapper.properties`, `sonar-project.properties`, `.gitignore`.
  - Catálogo inicial y archivos de datos CSV: `src/main/resources/juegos_de_mesa.csv`, `data/juegos_de_mesa.csv`, `data/partidas.csv`.
  - Capa de dominio base: `Juego.java`, `EstadoJuego.java`, `TipoGanador.java`, `JuegoRepositorio.java`, `DominioException.java`, `ValidacionException.java`, `JuegoNoEncontradoException.java`, `PersistenciaException.java`.
- **Funcionalidades implementadas:**
  - Estructura completa de proyecto Java 21 + Maven Wrapper.
  - Modelo de entidad `Juego` con invariantes de negocio: identificador único positivo, título y editorial obligatorios, año válido no futuro, `minJugadores > 0` y `maxJugadores > 0` con `maxJugadores >= minJugadores` (`Juego.validarNumeroJugadoresPositivo`), duración positiva, estado obligatorio y valoración opcional (`1..5`), además de `validarNumeroParticipantes(int)`.
  - Enumeraciones cerradas `EstadoJuego` (`Disponible`, `Dañado`, `Jugado`, `Completo`, `Prestado`) y `TipoGanador` (`Individual`, `Equipo`).
  - Jerarquía de excepciones de dominio/persistencia e interfaz `JuegoRepositorio`.
- **Responsabilidad dentro del proyecto:** Establecer los cimientos técnicos, el catálogo inicial y el contrato de dominio sobre el que trabajan todos los demás integrantes.
- **Dependencias con commits anteriores:** Ninguna (commit raíz de la rama).
- **Resultado funcional que aporta:** Proyecto Maven compilable desde el primer commit con el modelo de dominio y las reglas de validación centrales operativas.

---

### Commit 2 — Integrante B: Lobna

- **Hash:** `33d414a`
- **Mensaje:** `feat: implementación de persistencia CSV de juegos, catálogo y gestión de estados (integrante B)`
- **Archivos y componentes principales incorporados (5 archivos):**
  - `src/main/java/es/uv/is2/juegos/persistencia/CsvUtil.java`
  - `src/main/java/es/uv/is2/juegos/persistencia/InicializadorDatos.java`
  - `src/main/java/es/uv/is2/juegos/persistencia/CsvJuegoRepositorio.java`
  - `src/main/java/es/uv/is2/juegos/aplicacion/CatalogoService.java`
  - `src/main/java/es/uv/is2/juegos/aplicacion/EstadoJuegoService.java`
- **Funcionalidades implementadas:**
  - Utilidades CSV (`;`, soporte de comillas escapadas, parseo de duración única `60` o intervalo `30-60`, escritura atómica mediante archivo temporal).
  - Inicialización idempotente de `data/juegos_de_mesa.csv` y `data/partidas.csv` desde recursos sin sobrescribir datos existentes.
  - Repositorio `CsvJuegoRepositorio` con validación de IDs únicos e integridad de catálogo cerrado (50 juegos).
  - **RF-01 (`CatalogoService`):** listado completo, consulta por ID y búsqueda por nombre exacto insensible a mayúsculas/minúsculas.
  - **RF-02 (`EstadoJuegoService`):** consulta de estado y transición libre entre los cinco estados permitidos con persistencia inmediata.
- **Responsabilidad dentro del proyecto:** Persistir de forma segura el catálogo de juegos y habilitar las operaciones centrales de consulta y gestión de estados.
- **Dependencias con commits anteriores:** Depende del Commit 1 (`Juego`, `EstadoJuego`, `JuegoRepositorio`, excepciones y recurso CSV).
- **Resultado funcional que aporta:** Catálogo persistente en disco y casos de uso RF-01 y RF-02 completamente funcionales.

---

### Commit 3 — Integrante C: Nasir

- **Hash:** `6e5d261`
- **Mensaje:** `feat: implementación de gestión de valoraciones y filtros de búsqueda (integrante C)`
- **Archivos y componentes principales incorporados (2 archivos):**
  - `src/main/java/es/uv/is2/juegos/aplicacion/ValoracionService.java`
  - `src/main/java/es/uv/is2/juegos/aplicacion/FiltroJuegosService.java`
- **Funcionalidades implementadas:**
  - **RF-03 (`ValoracionService`):** asignar valoración (`1..5`), modificar valoración existente y eliminar valoración dejándola en `null`.
  - **RF-04 (`FiltroJuegosService`):** cuatro criterios independientes de filtrado:
    - Por nombre mediante coincidencia parcial insensible a mayúsculas/minúsculas.
    - Por valoración exacta (`1..5`) o sin valoración (`null`).
    - Por número mínimo de jugadores (`minJugadores >= valor`), delegando la validación `> 0` en `Juego.validarNumeroJugadoresPositivo`.
    - Por número máximo de jugadores (`maxJugadores <= valor`), delegando la validación `> 0` en `Juego.validarNumeroJugadoresPositivo`.
- **Responsabilidad dentro del proyecto:** Completar los casos de uso de valoración y consulta filtrada sobre el catálogo.
- **Dependencias con commits anteriores:** Depende del Commit 1 (`Juego`, `JuegoRepositorio`, `ValidacionException`) y del Commit 2 (persistencia del catálogo).
- **Resultado funcional que aporta:** Servicios completos y validados para RF-03 y RF-04 listos para ser consumidos por la interfaz gráfica.

---

### Commit 4 — Integrante D: Raghad

- **Hash:** `90146a8`
- **Mensaje:** `feat: implementación de gestión de partidas, historial y validación de participantes (integrante D)`
- **Archivos y componentes principales incorporados (4 archivos):**
  - `src/main/java/es/uv/is2/juegos/dominio/Partida.java`
  - `src/main/java/es/uv/is2/juegos/dominio/repositorio/PartidaRepositorio.java`
  - `src/main/java/es/uv/is2/juegos/persistencia/CsvPartidaRepositorio.java`
  - `src/main/java/es/uv/is2/juegos/aplicacion/PartidaService.java`
- **Funcionalidades implementadas:**
  - Entidad de dominio `Partida` (soporta ganador individual, ganador por equipo o partida expresamente sin ganador, fecha válida no futura y participantes positivos).
  - Contrato `PartidaRepositorio` e implementación `CsvPartidaRepositorio` con escritura atómica y generación secuencial de IDs.
  - **RF-05 (`PartidaService`):**
    - Registro de partidas con o sin ganador validando el número de participantes contra el rango real `[minJugadores, maxJugadores]` del juego seleccionado mediante `Juego.validarNumeroParticipantes`.
    - Consulta del historial de partidas de un juego ordenado de más reciente a más antigua.
    - Consulta de juegos distintos jugados durante el último año.
- **Responsabilidad dentro del proyecto:** Incorporar el dominio, persistencia y lógica completa de partidas e historial.
- **Dependencias con commits anteriores:** Depende del Commit 1 (`Juego`, `TipoGanador`, `JuegoRepositorio`, excepciones) y del Commit 2 (`CsvUtil`, `CsvJuegoRepositorio`).
- **Resultado funcional que aporta:** Toda la lógica de negocio y persistencia de la aplicación (RF-01 a RF-05) queda completa.

---

### Commit 5 — Integrante E: Younes

- **Hash:** `80605db`
- **Mensaje:** `feat: implementación e integración final de la interfaz Java Swing y punto de entrada (integrante E)`
- **Archivos y componentes principales incorporados (9 archivos):**
  - Modelos de tabla: `JuegoTableModel.java`, `PartidaTableModel.java`.
  - Paneles Swing: `CatalogoPanel.java`, `EstadosPanel.java`, `ValoracionesPanel.java`, `FiltrosPanel.java`, `PartidasPanel.java`.
  - Ventana principal y arranque: `VentanaPrincipal.java`, `Main.java`.
- **Funcionalidades implementadas:**
  - Interfaz de escritorio completa exclusivamente en Java Swing con barra de menú (`Archivo` y `Ayuda`) y cinco pestañas sincronizadas.
  - `CatalogoPanel` (listado completo, detalle del juego y búsqueda por nombre exacto).
  - `FiltrosPanel` (selector dinámico `JComboBox<CriterioFiltro>` con `CardLayout` mostrando solo los campos del criterio elegido y validación en tiempo real).
  - `EstadosPanel` (consulta y cambio de estado con actualización global).
  - `ValoracionesPanel` (asignar, modificar y eliminar valoraciones con actualización global).
  - `PartidasPanel` (visualización automática del nombre, mínimo y máximo de jugadores al seleccionar un juego en el `JComboBox`, formulario de registro con/sin ganador, historial y consulta del último año).
  - `Main` como raíz de composición que inicializa `data/`, inyecta repositorios y servicios, configura el Look & Feel y lanza `VentanaPrincipal`.
- **Responsabilidad dentro del proyecto:** Construir e integrar la interfaz Java Swing completa y dejar la aplicación lista para compilarse y ejecutarse de extremo a extremo.
- **Dependencias con commits anteriores:** Depende de los Commits 1, 2, 3 y 4 (consume el dominio, la inicialización de datos y los cinco servicios de aplicación).
- **Resultado funcional que aporta:** Aplicación de escritorio Java 21 + Swing completamente integrada, compilable y ejecutable.

---

## 3. Resumen del desarrollo incremental

1. **Commit 1 (Oscar — Integrante A):** Inicia el proyecto desde cero creando la estructura Maven, los recursos CSV, las enumeraciones, excepciones, `JuegoRepositorio` y la entidad `Juego` con las reglas fundamentales del modelo (incluyendo `minJugadores > 0` y `maxJugadores > 0`).
2. **Commit 2 (Lobna — Integrante B):** Incorpora la lectura/escritura segura de CSV, la inicialización no destructiva del catálogo y los servicios `CatalogoService` (RF-01) y `EstadoJuegoService` (RF-02).
3. **Commit 3 (Nasir — Integrante C):** Añade la gestión completa de valoraciones `ValoracionService` (RF-03) y los cuatro filtros independientes `FiltroJuegosService` (RF-04) reutilizando la validación del modelo `Juego`.
4. **Commit 4 (Raghad — Integrante D):** Incorpora la entidad `Partida`, su repositorio CSV y `PartidaService` (RF-05), incluyendo el historial, la consulta del último año y la validación de participantes según el rango real del juego.
5. **Commit 5 (Younes — Integrante E):** Completa e integra la aplicación añadiendo todos los paneles Java Swing, modelos de tabla, la ventana principal con sincronización entre pestañas y el punto de entrada `Main`.
