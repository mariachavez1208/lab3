package modelo;

public abstract class OrdenServicio {
    private final String codigo;
    private final String cliente;
    private final String direccion;
    private final double horasEstimadas;
    private final Zona zona;
    private EstadoOrden estado;
    private int visitasRealizadas;
    private double cobroRegistro;
    private double cobrosVisita;

    protected OrdenServicio(String codigo, String cliente, String direccion,
                            double horasEstimadas, Zona zona) {
        this.codigo = Validacion.codigo(codigo);
        this.cliente = Validacion.texto(cliente, "El cliente");
        this.direccion = Validacion.texto(direccion, "La dirección");
        Validacion.positivo(horasEstimadas, "Las horas estimadas");
        if (zona == null) {
            throw new IllegalArgumentException("Debe elegir una zona válida.");
        }
        this.horasEstimadas = horasEstimadas;
        this.zona = zona;
        estado = EstadoOrden.PENDIENTE;
        visitasRealizadas = 0;
        cobroRegistro = 0;
        cobrosVisita = 0;
    }

    public abstract double calcularCosto();
    public abstract int diasDeEspera();
    public abstract String nombreCategoria();

    public int maximoVisitas() {
        return 3;
    }

    public boolean esCompatibleCon(Cuadrilla cuadrilla) {
        return cuadrilla != null;
    }

    // Visibilidad de paquete: la vista no puede cambiar estados ni cobros.
    void anotarRegistro() {
        if (estado != EstadoOrden.PENDIENTE || visitasRealizadas != 0 || cobroRegistro != 0) {
            throw new IllegalStateException("Solo puede registrarse una orden nueva.");
        }
        double importe = Validacion.importe(calcularCosto());
        cobroRegistro = importe;
    }

    void reservar() {
        if (estado != EstadoOrden.PENDIENTE || visitasRealizadas >= maximoVisitas()) {
            throw new IllegalStateException("La orden debe estar pendiente y tener visitas disponibles.");
        }
        estado = EstadoOrden.ASIGNADA;
    }

    void liberar() {
        if (estado != EstadoOrden.ASIGNADA) {
            throw new IllegalStateException("Solo se retira una orden asignada.");
        }
        estado = EstadoOrden.PENDIENTE;
    }

    void comprobarInicio() {
        if (estado != EstadoOrden.ASIGNADA || visitasRealizadas >= maximoVisitas()) {
            throw new IllegalStateException("La orden no está lista para iniciar otra visita.");
        }
    }

    void iniciarVisita() {
        comprobarInicio();
        // Asignar o retirar nunca cobra: el recargo nace al iniciar la visita.
        if (visitasRealizadas > 0) {
            cobrosVisita += 40;
        }
        visitasRealizadas++;
        estado = EstadoOrden.EN_ATENCION;
    }

    void cerrarVisita(boolean completada) {
        if (estado != EstadoOrden.EN_ATENCION) {
            throw new IllegalStateException("La orden debe estar en atención para registrar un resultado.");
        }
        if (completada) {
            estado = EstadoOrden.COMPLETADA;
        } else if (visitasRealizadas >= maximoVisitas()) {
            estado = EstadoOrden.CANCELADA;
        } else {
            estado = EstadoOrden.PENDIENTE;
        }
    }

    public String getCodigo() { return codigo; }
    public String getCliente() { return cliente; }
    public String getDireccion() { return direccion; }
    public double getHorasEstimadas() { return horasEstimadas; }
    public Zona getZona() { return zona; }
    public EstadoOrden getEstado() { return estado; }
    public int getVisitasRealizadas() { return visitasRealizadas; }
    public double getCobroRegistro() { return cobroRegistro; }
    public double getCobrosVisita() { return cobrosVisita; }
}
