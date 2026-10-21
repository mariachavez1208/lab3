package vista;

import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;
import modelo.*;

public class ConsolaTecniHogar {
    private final Scanner entrada;

    public ConsolaTecniHogar() {
        entrada = new Scanner(System.in, "UTF-8");
    }

    public void mostrarMenu() {
        mostrar("\nTECNIHOGAR - AGENDA DE SERVICIOS");
        mostrar("1. Cotizar servicio\n2. Registrar y cobrar una orden\n3. Registrar cuadrilla");
        mostrar("4. Crear jornada\n5. Asignar orden\n6. Retirar orden\n7. Iniciar jornada");
        mostrar("8. Registrar resultado de visita\n9. Consultar órdenes\n10. Consultar cuadrillas");
        mostrar("11. Consultar jornadas\n12. Ver reportes\n0. Salir");
    }

    public void mostrar(String mensaje) {
        System.out.println(mensaje);
    }

    public String leerTexto(String mensaje) {
        System.out.print(mensaje);
        if (!entrada.hasNextLine()) {
            throw new NoSuchElementException("La entrada terminó.");
        }
        return entrada.nextLine().trim();
    }

    public int leerEntero(String mensaje) {
        while (true) {
            try {
                return Integer.parseInt(leerTexto(mensaje));
            } catch (NumberFormatException error) {
                mostrar("Entrada inválida: escriba un número entero, sin decimales.");
            }
        }
    }

    public double leerDecimal(String mensaje) {
        while (true) {
            try {
                double valor = Double.parseDouble(leerTexto(mensaje).replace(',', '.'));
                if (Double.isFinite(valor)) {
                    return valor;
                }
                mostrar("Entrada inválida: el número debe ser finito.");
            } catch (NumberFormatException error) {
                mostrar("Entrada inválida: escriba un número; puede usar punto o coma decimal.");
            }
        }
    }

    public boolean leerSiNo(String mensaje) {
        while (true) {
            String respuesta = leerTexto(mensaje + " (S/N): ");
            if (respuesta.equalsIgnoreCase("S")) {
                return true;
            }
            if (respuesta.equalsIgnoreCase("N")) {
                return false;
            }
            mostrar("Respuesta inválida: escriba S o N.");
        }
    }

    public void mostrarCotizacion(OrdenServicio orden) {
        mostrar("Categoría: " + orden.nombreCategoria());
        mostrar("Costo: " + dinero(orden.calcularCosto()));
        mostrar("Atención estimada: " + orden.diasDeEspera() + " día(s) hábil(es).");
    }

    public void mostrarOrden(OrdenServicio orden) {
        mostrar("\n" + orden.getCodigo() + " | " + orden.nombreCategoria() + " | " + orden.getEstado());
        mostrar("Cliente: " + orden.getCliente() + " | Dirección: " + orden.getDireccion());
        mostrar("Zona: " + orden.getZona() + " | Horas estimadas: " + orden.getHorasEstimadas());
        mostrar("Visitas: " + orden.getVisitasRealizadas() + "/" + orden.maximoVisitas());
        mostrar("Costo del servicio: " + dinero(orden.calcularCosto())
                + " | Espera: " + orden.diasDeEspera() + " día(s) hábil(es)");
        mostrar("Ingreso por registro: " + dinero(orden.getCobroRegistro())
                + " | Ingreso por visitas: " + dinero(orden.getCobrosVisita()));
    }

    public void mostrarCuadrilla(Cuadrilla cuadrilla) {
        mostrar(cuadrilla.getCodigo() + " | Capacidad: " + cuadrilla.getCapacidadHoras()
                + " h | Certificación de refrigeración: "
                + (cuadrilla.isCertificadaRefrigeracion() ? "Sí" : "No"));
    }

    public void mostrarJornada(Jornada jornada) {
        mostrar("\n" + jornada.getCodigo() + " | " + jornada.getEstado()
                + " | Cuadrilla: " + jornada.getCuadrilla().getCodigo());
        mostrar(String.format(Locale.US, "Horas asignadas: %.2f | Capacidad disponible: %.2f h",
                jornada.getHorasAsignadas(), jornada.getHorasDisponibles()));
        for (OrdenServicio orden : jornada.getOrdenes()) {
            mostrar("  " + orden.getCodigo() + " | Resultado de esta jornada: "
                    + (jornada.tieneResultado(orden) ? "Registrado" : "Pendiente"));
        }
    }

    public void mostrarReportes(AgendaTecnica agenda) {
        mostrar("\nÓRDENES POR CATEGORÍA Y ESTADO");
        for (String categoria : agenda.getCategorias()) {
            int subtotal = 0;
            mostrar(categoria + ":");
            for (EstadoOrden estado : EstadoOrden.values()) {
                int cantidad = agenda.contarOrdenes(categoria, estado);
                subtotal += cantidad;
                mostrar("  " + estado + ": " + cantidad);
            }
            mostrar("  TOTAL: " + subtotal);
        }
        mostrar("Total general de órdenes: " + agenda.getOrdenes().size());
        mostrar("\nINGRESOS");
        double registros = agenda.ingresosRegistro(null);
        double visitas = agenda.ingresosVisitas(null);
        mostrar("Por registro: " + dinero(registros));
        mostrar("Por visitas adicionales: " + dinero(visitas));
        mostrar("Ingreso total: " + dinero(registros + visitas));
        for (String categoria : agenda.getCategorias()) {
            double porRegistro = agenda.ingresosRegistro(categoria);
            double porVisita = agenda.ingresosVisitas(categoria);
            mostrar(categoria + " | Registro: " + dinero(porRegistro)
                    + " | Visitas: " + dinero(porVisita)
                    + " | Total: " + dinero(porRegistro + porVisita));
        }
        mostrar("\nJORNADAS SIN CERRAR");
        boolean hayAbiertas = false;
        for (Jornada jornada : agenda.getJornadas()) {
            if (jornada.getEstado() != EstadoJornada.CERRADA) {
                mostrarJornada(jornada);
                hayAbiertas = true;
            }
        }
        if (!hayAbiertas) {
            mostrar("No hay jornadas sin cerrar.");
        }
    }

    private String dinero(double monto) {
        return String.format(Locale.US, "Q%.2f", monto);
    }
}
