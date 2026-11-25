# Planificación de Equipo e Integración — Gestión de Juegos de Mesa

Este documento define el reparto de responsabilidades entre los **cinco integrantes del equipo (A, B, C, D y E)**, los contratos compartidos, el flujo de trabajo con Git y GitHub para la fase de desarrollo colaborativo, y las pautas de integración.

> **Nota importante:** En esta fase inicial del proyecto todavía **no se han ejecutado operaciones Git** (ni ramas, ni commits, ni subidas a GitHub). La arquitectura y el reparto documentados aquí dejan el código listo para iniciar el trabajo en ramas independientes.

---

## 1. Contratos compartidos (base común en `main`)

Antes de que cada integrante trabaje en su rama, los siguientes elementos forman el **contrato compartido** común del proyecto y no deben modificarse unilateralmente sin acuerdo del equipo:

- **Dominio:** `Juego`, `Partida`, `EstadoJuego`, `TipoGanador`.
- **Excepciones:** `DominioException`, `ValidacionException`, `JuegoNoEncontradoException`, `PersistenciaException`.
- **Repositorios (interfaces):** `JuegoRepositorio`, `PartidaRepositorio`.
- **Utilidades CSV y modelos de tabla compartidos:** `CsvUtil`, `JuegoTableModel`, `PartidaTableModel`.
- **Formatos CSV:**
  - Recurso inicial (`src/main/resources/juegos_de_mesa.csv`): 6 columnas separadas por `;`.
  - Persistencia juegos (`data/juegos_de_mesa.csv`): 10 columnas (`ID;Título;Editorial;Año lanzamiento;Mín. jugadores;Máx. jugadores;Duración mín. (min);Duración máx. (min);Estado;Valoración`).
  - Persistencia partidas (`data/partidas.csv`): 7 columnas (`ID;JuegoID;Participantes;TieneGanador;Ganador;TipoGanador;Fecha`).

---

## 2. Tabla resumen de responsabilidades

| Integrante | Requisito | Rama Git prevista | Servicio principal | Panel Swing | Pruebas principales |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **A** | RF-01: Catálogo y carga inicial | `feature/registro` | `CatalogoService` | `CatalogoPanel` | `CatalogoServiceTest`, `CsvPersistenciaTest` (carga), `JuegoTest` |
| **B** | RF-02: Gestión de estados | `feature/estados` | `EstadoJuegoService` | `EstadosPanel` | `EstadoJuegoServiceTest` |
| **C** | RF-03: Valoraciones | `feature/valoracion` | `ValoracionService` | `ValoracionesPanel` | `ValoracionServiceTest` |
| **D** | RF-04: Filtros | `feature/filtros` | `FiltroJuegosService` | `FiltrosPanel` | `FiltroJuegosServiceTest` |
| **E** | RF-05: Partidas e historial | `feature/partidas-historial` | `PartidaService` | `PartidasPanel` | `PartidaServiceTest`, `PartidaTest`, `CsvPersistenciaTest` (partidas) |

---

## 3. Planificación detallada por integrante

### Integrante A — RF-01: Catálogo de juegos y carga inicial

1. **Requisito funcional:** RF-01 (Catálogo de juegos).
2. **Objetivo:** Garantizar la carga inicial de los 50 juegos desde el recurso CSV hacia `data/` sin sobrescribir cambios previos, exponer la consulta del catálogo completo y permitir búsquedas por nombre exacto.
3. **Tareas principales:**
   - Inicialización de datos persistentes y persistencia base de juegos.
   - Servicio de consulta de catálogo y búsqueda exacta.
   - Interfaz Swing de visualización del catálogo y detalle del juego seleccionado.
4. **Subtareas concretas:**
   - Incorporar `juegos_de_mesa.csv` en `src/main/resources/`.
   - Implementar la lectura del CSV de 6 columnas y el parseo de duración única (`60`) o intervalo (`30-60`).
   - Implementar la creación no destructiva de `data/juegos_de_mesa.csv` con IDs únicos `1..50`, estado `Disponible` y valoración nula.
   - Implementar `listarCatalogo()`, `obtenerPorId(int)` y `buscarPorNombreExacto(String)`.
   - Construir `CatalogoPanel` con `JTable`, buscador exacto y barra de detalle.
5. **Clases y archivos que crea o modifica:**
   - `src/main/resources/juegos_de_mesa.csv`
   - `es.uv.is2.juegos.persistencia.InicializadorDatos`
   - `es.uv.is2.juegos.persistencia.CsvJuegoRepositorio`
   - `es.uv.is2.juegos.aplicacion.CatalogoService`
   - `es.uv.is2.juegos.presentacion.CatalogoPanel`
   - `es.uv.is2.juegos.presentacion.modelo.JuegoTableModel`
6. **Contratos compartidos que debe respetar:** `Juego`, `EstadoJuego`, `JuegoRepositorio`, `CsvUtil`, `ValidacionException`, `JuegoNoEncontradoException`.
7. **Pruebas unitarias a desarrollar:**
   - `CatalogoServiceTest`: verifica los 50 juegos, búsqueda exacta insensible a mayúsculas, búsqueda sin resultados, nombre vacío e ID inexistente.
   - `CsvPersistenciaTest` (sección juegos): verifica inicialización sin sobrescritura, parseo de duraciones, detección de IDs duplicados y rechazo de títulos fuera del catálogo.
8. **Comprobaciones antes de dar por terminada la tarea:**
   - `./mvnw test` en verde.
   - Al arrancar dos veces la app, los cambios hechos en `data/juegos_de_mesa.csv` se conservan.
   - Los 50 juegos se muestran completos con tildes y caracteres especiales correctos (UTF-8).
9. **Archivos a incluir en su Pull Request (`feature/registro`):** Las clases listadas en el punto 5 y sus pruebas unitarias correspondientes.
10. **Entrega al resto del equipo:** Catálogo inicializado en `data/juegos_de_mesa.csv`, `CatalogoService` operativo y `JuegoTableModel` listo para ser reutilizado por B, C, D y E.

---

### Integrante B — RF-02: Gestión de estados

1. **Requisito funcional:** RF-02 (Gestión de estados).
2. **Objetivo:** Permitir consultar el estado de cualquier juego y cambiarlo libremente entre los cinco estados permitidos (`Disponible`, `Dañado`, `Jugado`, `Completo`, `Prestado`), mostrando claramente el resultado.
3. **Tareas principales:**
   - Servicio de consulta y cambio de estado.
   - Panel Swing de gestión de estados con confirmación visual.
4. **Subtareas concretas:**
   - Implementar `consultarEstado(int juegoId)`.
   - Implementar `cambiarEstado(int juegoId, EstadoJuego nuevoEstado)` con persistencia inmediata.
   - Validar juego inexistente y estado nulo.
   - Construir `EstadosPanel` con tabla de selección, botón de consulta, selector `JComboBox<EstadoJuego>`, botón de cambio y mensajes claros de resultado.
   - Invocar el callback `onDatosActualizados` tras cada cambio.
5. **Clases y archivos que crea o modifica:**
   - `es.uv.is2.juegos.aplicacion.EstadoJuegoService`
   - `es.uv.is2.juegos.presentacion.EstadosPanel`
   - `es.uv.is2.juegos.aplicacion.EstadoJuegoServiceTest`
6. **Contratos compartidos que debe respetar:** `Juego`, `EstadoJuego`, `JuegoRepositorio`, `CatalogoService`, `JuegoTableModel`, `ValidacionException`, `JuegoNoEncontradoException`.
7. **Pruebas unitarias a desarrollar:**
   - `EstadoJuegoServiceTest`: consulta de estado inicial, transición libre entre los 5 estados con comprobación de persistencia, rechazo de estado nulo y de ID inexistente.
8. **Comprobaciones antes de dar por terminada la tarea:**
   - `./mvnw test` en verde.
   - Cambiar un estado desde la interfaz, cerrar la aplicación, volver a abrirla y verificar que el nuevo estado persiste.
9. **Archivos a incluir en su Pull Request (`feature/estados`):** `EstadoJuegoService.java`, `EstadosPanel.java`, `EstadoJuegoServiceTest.java` y el registro de la pestaña en `VentanaPrincipal.java` (coordinado con el integrador).
10. **Entrega al resto del equipo:** Operación completa de consulta y modificación de estados con refresco sincronizado en el resto de pestañas.

---

### Integrante C — RF-03: Valoraciones

1. **Requisito funcional:** RF-03 (Valoraciones).
2. **Objetivo:** Permitir asignar una valoración de 1 a 5, modificar una valoración existente, eliminarla y admitir juegos sin valoración.
3. **Tareas principales:**
   - Servicio de gestión de valoraciones.
   - Panel Swing de valoraciones con las tres operaciones diferenciadas.
4. **Subtareas concretas:**
   - Implementar `asignarValoracion(int juegoId, int valoracion)` validando rango `[1, 5]` y ausencia de valoración previa.
   - Implementar `modificarValoracion(int juegoId, int nuevaValoracion)` validando rango `[1, 5]` y existencia de valoración previa.
   - Implementar `eliminarValoracion(int juegoId)` dejando `valoracion = null`.
   - Construir `ValoracionesPanel` con tabla, etiqueta de valoración actual, selector `1..5` y botones *Asignar*, *Modificar* y *Eliminar*.
5. **Clases y archivos que crea o modifica:**
   - `es.uv.is2.juegos.aplicacion.ValoracionService`
   - `es.uv.is2.juegos.presentacion.ValoracionesPanel`
   - `es.uv.is2.juegos.aplicacion.ValoracionServiceTest`
6. **Contratos compartidos que debe respetar:** `Juego`, `JuegoRepositorio`, `CatalogoService`, `JuegoTableModel`, `ValidacionException`, `JuegoNoEncontradoException`.
7. **Pruebas unitarias a desarrollar:**
   - `ValoracionServiceTest`: asignación válida, modificación válida, eliminación a `null`, rechazo de valores `< 1` o `> 5`, rechazo de asignar si ya existe valoración, rechazo de modificar si no existe valoración e ID inexistente.
8. **Comprobaciones antes de dar por terminada la tarea:**
   - `./mvnw test` en verde.
   - Probar en Swing el ciclo completo: asignar -> modificar -> eliminar -> reiniciar aplicación y comprobar persistencia.
9. **Archivos a incluir en su Pull Request (`feature/valoracion`):** `ValoracionService.java`, `ValoracionesPanel.java`, `ValoracionServiceTest.java` y conexión en `VentanaPrincipal.java`.
10. **Entrega al resto del equipo:** Gestión completa de valoraciones persistentes, habilitando la prueba de filtrado por valoración del Integrante D.

---

### Integrante D — RF-04: Filtros

1. **Requisito funcional:** RF-04 (Filtros).
2. **Objetivo:** Ofrecer filtros independientes por nombre, valoración, número mínimo de jugadores y número máximo de jugadores.
3. **Tareas principales:**
   - Servicio de filtrado con cuatro operaciones independientes.
   - Panel Swing de filtros con controles separados y tabla de resultados.
4. **Subtareas concretas:**
   - Implementar `filtrarPorNombre(String texto)` (insensible a mayúsculas/minúsculas, rechazando texto vacío).
   - Implementar `filtrarPorValoracion(Integer valoracion)` (soporta `1..5` y `null` para juegos sin valoración).
   - Implementar `filtrarPorMinJugadores(int minJugadores)`, `filtrarPorMaxJugadores(int maxJugadores)` y `validarNumeroJugadoresPositivo(int, String)` validando estrictamente `> 0` (rechazando `0` y negativos).
   - Construir `FiltrosPanel` con selector `JComboBox` de criterio y `CardLayout` para mostrar únicamente los campos del filtro seleccionado, con validación al escribir y al aplicar.
5. **Clases y archivos que crea o modifica:**
   - `es.uv.is2.juegos.aplicacion.FiltroJuegosService`
   - `es.uv.is2.juegos.presentacion.FiltrosPanel`
   - `es.uv.is2.juegos.aplicacion.FiltroJuegosServiceTest`
   - `es.uv.is2.juegos.presentacion.FiltrosPanelTest`
6. **Contratos compartidos que debe respetar:** `Juego`, `JuegoRepositorio`, `CatalogoService`, `JuegoTableModel`, `ValidacionException`.
7. **Pruebas unitarias a desarrollar:**
   - `FiltroJuegosServiceTest`: filtrado por nombre, por valoración y por mínimo/máximo de jugadores (rechazando `0` y negativos, aceptando positivos).
   - `FiltrosPanelTest`: visibilidad exclusiva de campos del criterio seleccionado, validación en tiempo real y actualización de la interfaz al cambiar de criterio.
8. **Comprobaciones antes de dar por terminada la tarea:**
   - `./mvnw test` en verde.
   - Verificar en Swing que al cambiar el criterio solo se muestran los campos correspondientes y que se rechazan `0` y negativos con mensajes claros.
9. **Archivos a incluir en su Pull Request (`feature/filtros`):** `FiltroJuegosService.java`, `FiltrosPanel.java`, `FiltroJuegosServiceTest.java`, `FiltrosPanelTest.java` y conexión en `VentanaPrincipal.java`.
10. **Entrega al resto del equipo:** Pestaña de filtros dinámica, validada y sincronizada con los cambios de valoración y estado.
11. **Impacto en Jira:** Actualizar la tarea de RF-04 (validación estricta `> 0`) y añadir subtarea/historia nueva para **RF-04.5** (selector dinámico de criterio en `FiltrosPanel`).

---

### Integrante E — RF-05: Partidas e historial

1. **Requisito funcional:** RF-05 (Gestión de partidas).
2. **Objetivo:** Permitir registrar partidas (con ganador individual, ganador por equipo o expresamente sin ganador), consultar el historial por juego y consultar los juegos jugados durante el último año.
3. **Tareas principales:**
   - Persistencia CSV de partidas (`CsvPartidaRepositorio`).
   - Servicio de partidas e historial (`PartidaService`).
   - Panel Swing de registro, historial y consulta del último año (`PartidasPanel` y `PartidaTableModel`).
4. **Subtareas concretas:**
   - Implementar `CsvPartidaRepositorio` con lectura, escritura segura y generación de IDs secuenciales.
   - Implementar `registrarPartida(...)`, `registrarPartidaSinGanador(...)` (validando los participantes contra `[juego.minJugadores, juego.maxJugadores]` mediante `Juego.validarNumeroParticipantes`), `consultarHistorialPorJuego(int)`, `listarTodasLasPartidas()` y `consultarJuegosJugadosUltimoAnio(...)`.
   - Construir `PartidasPanel` con panel informativo dinámico al seleccionar un juego en `JComboBox` (nombre, mínimo y máximo de jugadores), formulario de registro, tabla de historial filtrable por juego y pestaña de juegos jugados en el último año.
5. **Clases y archivos que crea o modifica:**
   - `es.uv.is2.juegos.persistencia.CsvPartidaRepositorio`
   - `es.uv.is2.juegos.aplicacion.PartidaService`
   - `es.uv.is2.juegos.presentacion.PartidasPanel`
   - `es.uv.is2.juegos.presentacion.modelo.PartidaTableModel`
   - `es.uv.is2.juegos.aplicacion.PartidaServiceTest`
   - `es.uv.is2.juegos.dominio.PartidaTest`
   - `es.uv.is2.juegos.presentacion.PartidasPanelTest`
6. **Contratos compartidos que debe respetar:** `Juego` (incluyendo `validarNumeroParticipantes`), `Partida`, `TipoGanador`, `JuegoRepositorio`, `PartidaRepositorio`, `CsvUtil`, `ValidacionException`, `JuegoNoEncontradoException`.
7. **Pruebas unitarias a desarrollar:**
   - `PartidaTest`: validaciones de participantes, fecha futura, coherencia de ganador/tipo y partidas sin ganador.
   - `PartidaServiceTest`: registro con y sin ganador, casos límite de participantes (exactamente mínimo, exactamente máximo, `< mínimo`, `> máximo`), orden del historial por juego, filtro de último año y juego inexistente.
   - `PartidasPanelTest`: actualización automática de la información del juego al cambiar la selección del `JComboBox`.
   - `CsvPersistenciaTest` (sección partidas): escritura y lectura de partidas con y sin ganador.
8. **Comprobaciones antes de dar por terminada la tarea:**
   - `./mvnw test` en verde.
   - Registrar en Swing partidas con ganador individual, por equipo y sin ganador; verificar que la info del juego cambia al seleccionar otro elemento y que se rechazan participantes fuera de rango.
9. **Archivos a incluir en su Pull Request (`feature/partidas-historial`):** Las clases listadas en el punto 5 y sus pruebas unitarias.
10. **Entrega al resto del equipo:** Gestión completa de `data/partidas.csv`, validación de rango de jugadores, historial y consulta anual integrados en la ventana principal.
11. **Impacto en Jira:** Actualizar la tarea de RF-05 (validación de participantes contra el rango real del juego en `RF-05.1`) y añadir una subtarea/historia nueva para **RF-05.5** (mostrar información automática del juego seleccionado en `PartidasPanel`).

---

## 4. Flujo de trabajo Git y GitHub (para la fase posterior)

La rama principal será `main`. Cada integrante trabajará en su rama local siguiendo exactamente este procedimiento:

1. **Actualizar `main` y crear rama local:**
   ```bash
   git checkout main
   git pull origin main
   git checkout -b feature/<nombre-rama>
   ```
   Ramas previstas:
   - Integrante A: `feature/registro`
   - Integrante B: `feature/estados`
   - Integrante C: `feature/valoracion`
   - Integrante D: `feature/filtros`
   - Integrante E: `feature/partidas-historial`
2. **Implementar las tareas y subtareas** asignadas sin modificar clases de otros integrantes.
3. **Ejecutar pruebas y comprobar compilación:**
   ```bash
   ./mvnw clean test
   ```
4. **Realizar commits descriptivos** (convención sugerida *Conventional Commits*):
   - Ejemplo: `feat(estados): implementar EstadoJuegoService y EstadosPanel`
   - Ejemplo: `test(estados): añadir pruebas unitarias para transiciones de estado`
5. **Subir la rama a GitHub:**
   ```bash
   git push -u origin feature/<nombre-rama>
   ```
6. **Crear un Pull Request hacia `main`** indicando: requisito funcional cubierto, resumen de cambios, capturas o evidencia de pruebas y checklist de verificación.
7. **Revisar y resolver conflictos:** Si `main` avanzó mientras se trabajaba, actualizar la rama antes del merge (`git fetch origin && git rebase origin/main` o `git merge origin/main`), resolver conflictos de forma coordinada (especialmente en `VentanaPrincipal.java` y `Main.java`) y volver a ejecutar `./mvnw clean test`.

---

## 5. Coordinación e integración

### Rol del integrador
Una persona del equipo (puede rotar o asignarse al Integrante A) actuará como **integrador**, con las siguientes responsabilidades:
- Verificar que la base común (`dominio`, `pom.xml`, `mvnw`, `CsvUtil`) está estable en `main` antes de abrir las ramas.
- Revisar todos los Pull Requests comprobando el respeto a la arquitectura por capas.
- Coordinar los cambios en archivos compartidos (`VentanaPrincipal.java` y `Main.java`).
- Ejecutar la verificación final de compilación, pruebas, JaCoCo y análisis de SonarQube tras cada integración en `main`.

### Orden recomendado de integración en `main`
1. **Base común** (modelo de dominio, excepciones, interfaces de repositorio, `pom.xml` y `CsvUtil`).
2. **PR 1 — Integrante A (`feature/registro`)**: establece `InicializadorDatos`, `CsvJuegoRepositorio`, `CatalogoService` y `JuegoTableModel`.
3. **PR 2 — Integrante B (`feature/estados`)**: depende de `CsvJuegoRepositorio` y `CatalogoService`.
4. **PR 3 — Integrante C (`feature/valoracion`)**: depende de `CsvJuegoRepositorio` y `CatalogoService`.
5. **PR 4 — Integrante D (`feature/filtros`)**: se beneficia de que C ya haya integrado valoraciones para pruebas de extremo a extremo.
6. **PR 5 — Integrante E (`feature/partidas-historial`)**: añade `CsvPartidaRepositorio`, `PartidaService` y `PartidasPanel`.

### Dependencias entre tareas
- B, C, D y E dependen de los contratos de dominio y del `CsvJuegoRepositorio` / `CatalogoService` de A.
- D depende funcionalmente de C solo para probar el filtro por valoración en integración (en pruebas unitarias puede preparar sus propios datos).
- E es independiente de B, C y D.

### Archivos compartidos que requieren coordinación
- `src/main/java/es/uv/is2/juegos/Main.java` (inyección de servicios).
- `src/main/java/es/uv/is2/juegos/presentacion/VentanaPrincipal.java` (registro de pestañas y refresco global).
- `pom.xml` (no añadir dependencias sin consenso del equipo).

### Criterios para aceptar un Pull Request
- Compila sin errores ni advertencias críticas con `./mvnw clean test`.
- Todas las pruebas unitarias nuevas y existentes pasan al 100 %.
- No se incumple la separación de capas (sin lógica de negocio en Swing ni acceso directo a CSV desde presentación).
- El código no introduce duplicación innecesaria ni rompe el formato CSV acordado.
- Revisión aprobada al menos por otro integrante y por el integrador.

### Comprobaciones finales de integración
1. Ejecutar `./mvnw clean test package` en `main` tras integrar las cinco ramas.
2. Revisar el informe de cobertura JaCoCo en `target/site/jacoco/index.html`.
3. Ejecutar el análisis de calidad con SonarQube (`./mvnw sonar:sonar` con el servidor SonarQube configurado).
4. Arrancar la aplicación de escritorio, probar los flujos RF-01 a RF-05 de extremo a extremo, reiniciar la aplicación y comprobar que `data/juegos_de_mesa.csv` y `data/partidas.csv` conservan todos los cambios.
