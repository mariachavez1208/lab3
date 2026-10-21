package modelo;

public class Instalacion extends OrdenServicio {
    private final int puntos;

    public Instalacion(String codigo, String cliente, String direccion,
                       double horasEstimadas, Zona zona, int puntos) {
        super(codigo, cliente, direccion, horasEstimadas, zona);
        if (puntos <= 0) {
            throw new IllegalArgumentException("La cantidad de puntos debe ser un entero positivo.");
        }
        this.puntos = puntos;
    }

    public double horasFacturables() {
        // Math.ceil redondea hacia arriba: una fracción también se cobra.
        return Math.ceil(Math.max(getHorasEstimadas(), puntos * 0.75));
    }

    @Override
    public double calcularCosto() {
        return Validacion.importe(getZona().getTarifaBase() + 90 * horasFacturables());
    }

    @Override
    public int diasDeEspera() {
        return getZona() == Zona.METROPOLITANA ? 3 : 5;
    }

    @Override
    public String nombreCategoria() { return "Instalación"; }

    public int getPuntos() { return puntos; }
}
