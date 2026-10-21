package modelo;

public class InstalacionCertificada extends Instalacion {
    private final double valorMateriales;

    public InstalacionCertificada(String codigo, String cliente, String direccion,
                                  double horasEstimadas, Zona zona, int puntos,
                                  double valorMateriales) {
        super(codigo, cliente, direccion, horasEstimadas, zona, puntos);
        Validacion.positivo(valorMateriales, "El valor de los materiales");
        this.valorMateriales = valorMateriales;
    }

    @Override
    public double calcularCosto() {
        return Validacion.importe(super.calcularCosto() + 120 + valorMateriales * 0.05);
    }

    @Override
    public int diasDeEspera() { return super.diasDeEspera() + 2; }

    @Override
    public int maximoVisitas() { return 2; }

    @Override
    public String nombreCategoria() { return "Instalación eléctrica certificada"; }

    public double getValorMateriales() { return valorMateriales; }
}
