package modelo;

public class MantenimientoAire extends OrdenServicio {
    private final int capacidadBtu;

    public MantenimientoAire(String codigo, String cliente, String direccion,
                             double horasEstimadas, Zona zona, int capacidadBtu) {
        super(codigo, cliente, direccion, horasEstimadas, zona);
        if (zona != Zona.METROPOLITANA) {
            throw new IllegalArgumentException("El mantenimiento de aire solo se acepta en zona metropolitana.");
        }
        if (capacidadBtu < 9000 || capacidadBtu > 60000) {
            throw new IllegalArgumentException("La capacidad del equipo debe estar entre 9000 y 60000 BTU.");
        }
        this.capacidadBtu = capacidadBtu;
    }

    @Override
    public double calcularCosto() {
        return Validacion.importe(getZona().getTarifaBase() + 110 * getHorasEstimadas() + 80);
    }

    @Override
    public int diasDeEspera() { return 2; }

    @Override
    public int maximoVisitas() { return 1; }

    @Override
    public boolean esCompatibleCon(Cuadrilla cuadrilla) {
        return cuadrilla != null && cuadrilla.isCertificadaRefrigeracion();
    }

    @Override
    public String nombreCategoria() { return "Mantenimiento de aire acondicionado"; }

    public int getCapacidadBtu() { return capacidadBtu; }
}
