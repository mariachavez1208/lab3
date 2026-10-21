package modelo;

import java.util.ArrayList;
import java.math.BigDecimal;

public class Jornada {
    private final String codigo;
    private final Cuadrilla cuadrilla;
    private EstadoJornada estado;
    private final ArrayList<OrdenServicio> ordenes;
    private final ArrayList<OrdenServicio> resueltas;

    Jornada(String codigo, Cuadrilla cuadrilla) {
        this.codigo = Validacion.codigo(codigo);
        if (cuadrilla == null) {
            throw new IllegalArgumentException("La jornada necesita una cuadrilla.");
        }
        this.cuadrilla = cuadrilla;
        estado = EstadoJornada.EN_PREPARACION;
        ordenes = new ArrayList<>();
        resueltas = new ArrayList<>();
    }

    void asignar(OrdenServicio orden) {
        exigirPreparacion();
        if (orden == null || orden.getEstado() != EstadoOrden.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden asignar órdenes pendientes y libres.");
        }
        if (!orden.esCompatibleCon(cuadrilla)) {
            throw new IllegalArgumentException("La orden no es compatible con la certificación de esta cuadrilla.");
        }
        // Se usan las horas estimadas, nunca las horas facturables.
        BigDecimal nuevasHoras = totalHoras().add(BigDecimal.valueOf(orden.getHorasEstimadas()));
        if (nuevasHoras.compareTo(BigDecimal.valueOf(cuadrilla.getCapacidadHoras())) > 0) {
            throw new IllegalArgumentException("La asignación supera la capacidad de horas de la cuadrilla.");
        }
        orden.reservar();
        ordenes.add(orden);
    }

    void retirar(OrdenServicio orden) {
        exigirPreparacion();
        if (!ordenes.contains(orden)) {
            throw new IllegalArgumentException("La orden no pertenece a esta jornada.");
        }
        orden.liberar();
        ordenes.remove(orden);
    }

    void iniciar() {
        exigirPreparacion();
        if (ordenes.isEmpty()) {
            throw new IllegalStateException("No puede iniciar una jornada vacía.");
        }
        // Se revisan todas antes de cambiar cualquier orden o cobrar.
        for (OrdenServicio orden : ordenes) {
            orden.comprobarInicio();
        }
        for (OrdenServicio orden : ordenes) {
            orden.iniciarVisita();
        }
        estado = EstadoJornada.EN_CURSO;
    }

    void registrarResultado(OrdenServicio orden, boolean completada) {
        if (estado != EstadoJornada.EN_CURSO) {
            throw new IllegalStateException("Solo se registran resultados en una jornada en curso.");
        }
        if (!ordenes.contains(orden)) {
            throw new IllegalArgumentException("La orden no pertenece a esta jornada.");
        }
        if (resueltas.contains(orden)) {
            throw new IllegalStateException("Esta visita ya tiene un resultado registrado.");
        }
        orden.cerrarVisita(completada);
        resueltas.add(orden);
        if (resueltas.size() == ordenes.size()) {
            estado = EstadoJornada.CERRADA;
        }
    }

    private void exigirPreparacion() {
        if (estado != EstadoJornada.EN_PREPARACION) {
            throw new IllegalStateException("La jornada debe estar en preparación.");
        }
    }

    private BigDecimal totalHoras() {
        // Evita que 0.1 + 0.2 se considere mayor que una capacidad de 0.3.
        BigDecimal horas = BigDecimal.ZERO;
        for (OrdenServicio orden : ordenes) {
            horas = horas.add(BigDecimal.valueOf(orden.getHorasEstimadas()));
        }
        return horas;
    }

    public double getHorasAsignadas() { return totalHoras().doubleValue(); }

    public double getHorasDisponibles() {
        return BigDecimal.valueOf(cuadrilla.getCapacidadHoras()).subtract(totalHoras()).doubleValue();
    }

    public boolean tieneResultado(OrdenServicio orden) { return resueltas.contains(orden); }
    public String getCodigo() { return codigo; }
    public Cuadrilla getCuadrilla() { return cuadrilla; }
    public EstadoJornada getEstado() { return estado; }
    public ArrayList<OrdenServicio> getOrdenes() { return new ArrayList<>(ordenes); }
}
