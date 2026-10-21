# TecniHogar — Laboratorio 3 de Herencia

Programa de consola en Java para administrar los servicios técnicos de TecniHogar.

Permite cotizar y registrar órdenes, registrar cuadrillas, organizar jornadas, controlar visitas y consultar reportes. Las categorías disponibles son diagnóstico, instalación, instalación eléctrica certificada y mantenimiento de aire acondicionado.

## Requisitos para ejecutar

- Tener instalado JDK 8 o una versión posterior.
- Tener disponibles los comandos `javac` y `java`.
- Abrir una terminal dentro de la carpeta principal `TecniHogar`.
- Utilizar codificación UTF-8 para mostrar correctamente las tildes.

El programa no necesita bibliotecas externas ni una base de datos. Los datos se conservan en memoria y se reinician al cerrar.

## Organización del proyecto

- `src/modelo`: clases de los servicios, cuadrillas, jornadas, estados y validaciones.
- `src/vista`: lectura de datos y presentación en consola.
- `src/controlador`: manejo del menú y coordinación con el modelo.
- `src/main`: clase principal que inicia el programa.
- `pruebas`: pruebas del modelo.
- `evidencia`: entradas, salidas y resultados de las pruebas.
- `UML.puml`: descripción del diagrama de clases en PlantUML.

## Compilar

Los siguientes comandos deben ejecutarse desde la carpeta principal `TecniHogar`, no desde `src`.

Primero, crear la carpeta para los archivos compilados:

```bash
mkdir bin
```

Si la carpeta `bin` ya existe, se omite ese paso.

Después, compilar:

```bash
javac -encoding UTF-8 -d bin src/modelo/*.java src/vista/*.java src/controlador/*.java src/main/*.java
```

## Ejecutar

Después de compilar:

```bash
java -cp bin main.Main
```

## Opciones del menú

1. Cotizar un servicio.
2. Registrar y cobrar una orden.
3. Registrar una cuadrilla.
4. Crear una jornada.
5. Asignar una orden.
6. Retirar una orden.
7. Iniciar una jornada.
8. Registrar el resultado de una visita.
9. Consultar órdenes.
10. Consultar cuadrillas.
11. Consultar jornadas.
12. Ver reportes.
0. Salir.

Los números decimales pueden escribirse con punto o coma. No se deben utilizar separadores de miles: por ejemplo, escribir `2000` para dos mil.

Las confirmaciones se responden con `S` o `N`.

Los códigos se convierten a mayúsculas y se eliminan sus espacios exteriores. Por ejemplo, `di-01` y `DI-01` identifican la misma orden.

## Datos iniciales

El programa inicia con ocho órdenes pendientes, dos de cada categoría, todas con cero visitas y sin ingresos registrados.

También incluye estas cuadrillas:

| Código | Capacidad por jornada | Certificada en refrigeración |
| --- | ---: | --- |
| RUTA-01 | 8 horas | No |
| FRIO-02 | 6 horas | Sí |
| MIXTA-03 | 4 horas | Sí |

La orden `IN-01` permite observar el cálculo por puntos: tiene 2.5 horas estimadas y 5 puntos, equivalentes a 3.75 horas por puntos. Se cobran 4 horas y su costo es Q460.00.


## Diseño

La clase abstracta `OrdenServicio` reúne los datos y comportamientos comunes. Las categorías aplican sus reglas mediante herencia y polimorfismo.

`InstalacionCertificada` hereda de `Instalacion` y reutiliza sus cálculos mediante `super`.

Las jornadas utilizan las horas estimadas para comprobar capacidad. Los recargos de Q40.00 se registran únicamente al iniciar una visita adicional.

Los reportes calculan los ingresos a partir de los cobros guardados en cada orden. Cotizar, retirar órdenes o rechazar operaciones no modifica esos ingresos.