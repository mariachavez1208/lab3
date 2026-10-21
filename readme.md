TecniHogar - Lab 3 de Herencia

Programa de consola en Java para cotizar servicios, registrar órdenes y cuadrillas, organizar jornadas y consultar reportes.

El código está dividido en:
- src/modelo: clases y reglas del programa.
- src/vista: entrada y salida de datos.
- src/controlador: manejo del menú.
- src/main: inicio del programa.

Para ejecutarlo se necesita JDK 8 o superior. Abrir la terminal en la carpeta donde está src y compilar con:

```bash
mkdir -p bin
javac -encoding UTF-8 -d bin src/modelo/*.java src/vista/*.java src/controlador/*.java src/main/*.java
```

Después ejecutar:

```bash
java -cp bin main.Main
```

Al iniciar aparece el menú con las opciones disponibles. Para salir se utiliza la opción 0.

El programa incluye 8 órdenes pendientes y 3 cuadrillas para probarlo. Las órdenes iniciales no tienen visitas ni ingresos registrados.

Las confirmaciones se responden con S o N. Los decimales pueden escribirse con punto o coma, sin separadores de miles.

Los datos se conservan solamente durante la ejecución. Al cerrar el programa se pierden los cambios.