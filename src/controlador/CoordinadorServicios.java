package controlador;

import java.util.NoSuchElementException;
import modelo.*;
import vista.ConsolaTecniHogar;

public class CoordinadorServicios {
    private final AgendaTecnica agenda;
    private final ConsolaTecniHogar consola;

    public CoordinadorServicios(AgendaTecnica agenda, ConsolaTecniHogar consola) {
        this.agenda = agenda;
        this.consola = consola;
    }

    public void ejecutar() {
        boolean seguir = true;
        while (seguir) {
            try {
                consola.mostrarMenu();
                int opcion = consola.leerEntero("Elija una opción: ");
                switch (opcion) {
                    case 1:
                        OrdenServicio cotizada = leerServicio(false);
                        consola.mostrarCotizacion(cotizada);
                        consola.mostrar("Cotización informativa. No se guardó ni cobró ninguna orden.");
                        break;
                    case 2:
                        OrdenServicio nueva = leerServicio(true);
                        consola.mostrarCotizacion(nueva);
                        if (consola.leerSiNo("¿El cliente acepta registrar y pagar este servicio?")) {
                            agenda.registrarOrden(nueva);
                            consola.mostrar("Orden registrada, cobrada y pendiente de asignación.");
                        } else {
                            consola.mostrar("Registro no confirmado. No se guardó ni cobró la orden.");
                        }
                        break;
                    case 3:
                        registrarCuadrilla();
                        break;
                    case 4:
                        crearJornada();
                        break;
                    case 5:
                        agenda.asignarOrden(consola.leerTexto("Código de jornada: "),
                                consola.leerTexto("Código de orden: "));
                        consola.mostrar("Orden asignada. No se generó un cargo.");
                        break;
                    case 6:
                        agenda.retirarOrden(consola.leerTexto("Código de jornada: "),
                                consola.leerTexto("Código de orden: "));
                        consola.mostrar("Orden retirada y nuevamente pendiente.");
                        break;
                    case 7:
                        agenda.iniciarJornada(consola.leerTexto("Código de jornada: "));
                        consola.mostrar("Jornada iniciada. Se contaron las visitas y se cobraron los recargos aplicables.");
                        break;
                    case 8:
                        registrarResultado();
                        break;
                    case 9:
                        for (OrdenServicio orden : agenda.getOrdenes()) {
                            consola.mostrarOrden(orden);
                        }
                        break;
                    case 10:
                        for (Cuadrilla cuadrilla : agenda.getCuadrillas()) {
                            consola.mostrarCuadrilla(cuadrilla);
                        }
                        break;
                    case 11:
                        if (agenda.getJornadas().isEmpty()) {
                            consola.mostrar("No hay jornadas registradas.");
                        }
                        for (Jornada jornada : agenda.getJornadas()) {
                            consola.mostrarJornada(jornada);
                        }
                        break;
                    case 12:
                        consola.mostrarReportes(agenda);
                        break;
                    case 0:
                        seguir = false;
                        consola.mostrar("Programa finalizado.");
                        break;
                    default:
                        consola.mostrar("Opción inválida. Elija una opción del menú.");
                }
            } catch (IllegalArgumentException | IllegalStateException error) {
                consola.mostrar("Operación rechazada: " + error.getMessage());
            } catch (NoSuchElementException error) {
                consola.mostrar("Entrada finalizada. Se cierra el programa.");
                seguir = false;
            }
        }
    }

    private OrdenServicio leerServicio(boolean paraRegistro) {
        consola.mostrar("1. Diagnóstico\n2. Instalación\n3. Instalación eléctrica certificada"
                + "\n4. Mantenimiento de aire acondicionado");
        int categoria = consola.leerEntero("Categoría: ");
        if (categoria < 1 || categoria > 4) {
            throw new IllegalArgumentException("La categoría debe estar entre 1 y 4.");
        }
        String codigo = "COTIZACION";
        String cliente = "Cliente de cotización";
        String direccion = "Dirección no registrada";
        if (paraRegistro) {
            codigo = consola.leerTexto("Código de la orden: ");
            cliente = consola.leerTexto("Nombre del cliente: ");
            direccion = consola.leerTexto("Dirección: ");
        }
        double horas = consola.leerDecimal("Horas estimadas: ");
        Zona zona = leerZona();
        // Este switch solo crea objetos; las reglas están en cada subclase.
        switch (categoria) {
            case 1:
                return new Diagnostico(codigo, cliente, direccion, horas, zona);
            case 2:
                return new Instalacion(codigo, cliente, direccion, horas, zona,
                        consola.leerEntero("Cantidad de puntos: "));
            case 3:
                int puntos = consola.leerEntero("Cantidad de puntos: ");
                double materiales = consola.leerDecimal("Valor de materiales en quetzales: ");
                return new InstalacionCertificada(codigo, cliente, direccion, horas, zona, puntos, materiales);
            default:
                return new MantenimientoAire(codigo, cliente, direccion, horas, zona,
                        consola.leerEntero("Capacidad del equipo en BTU: "));
        }
    }

    private Zona leerZona() {
        int opcion = consola.leerEntero("Zona (1. Metropolitana / 2. Periférica): ");
        if (opcion == 1) { return Zona.METROPOLITANA; }
        if (opcion == 2) { return Zona.PERIFERICA; }
        throw new IllegalArgumentException("La zona debe ser 1 o 2.");
    }

    private void registrarCuadrilla() {
        String codigo = consola.leerTexto("Código de cuadrilla: ");
        double capacidad = consola.leerDecimal("Capacidad de horas por jornada: ");
        boolean certificada = consola.leerSiNo("¿Está certificada en refrigeración?");
        agenda.registrarCuadrilla(codigo, capacidad, certificada);
        consola.mostrar("Cuadrilla registrada.");
    }

    private void crearJornada() {
        String codigo = consola.leerTexto("Código de la nueva jornada: ");
        String cuadrilla = consola.leerTexto("Código de cuadrilla: ");
        agenda.abrirJornada(codigo, cuadrilla);
        consola.mostrar("Jornada creada en preparación.");
    }

    private void registrarResultado() {
        String jornada = consola.leerTexto("Código de jornada: ");
        String orden = consola.leerTexto("Código de orden: ");
        int resultado = consola.leerEntero("Resultado (1. Completada / 2. Visita fallida): ");
        if (resultado != 1 && resultado != 2) {
            throw new IllegalArgumentException("El resultado debe ser 1 o 2.");
        }
        agenda.registrarResultado(jornada, orden, resultado == 1);
        consola.mostrar("Resultado guardado. Estado de la orden: " + agenda.buscarOrden(orden).getEstado());
        consola.mostrar("Estado de la jornada: " + agenda.buscarJornada(jornada).getEstado());
    }
}
