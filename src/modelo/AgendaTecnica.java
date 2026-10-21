package modelo;

import java.util.ArrayList;

public class AgendaTecnica {
    private final ArrayList<OrdenServicio> ordenes;
    private final ArrayList<Cuadrilla> cuadrillas;
    private final ArrayList<Jornada> jornadas;

    public AgendaTecnica(boolean incluirEjemplos) {
        ordenes = new ArrayList<>();
        cuadrillas = new ArrayList<>();
        jornadas = new ArrayList<>();
        if (incluirEjemplos) {
            cargarBase();
        }
    }

    private void cargarBase() {
        // Estos ejemplos se incorporan sin llamar a anotarRegistro: ingreso Q0.00.
        ordenes.add(new Diagnostico("DI-01", "Elena Paredes", "Zona 7", 1, Zona.METROPOLITANA));
        ordenes.add(new Diagnostico("DI-02", "Mateo Chajón", "San Lucas", 1.5, Zona.PERIFERICA));
        ordenes.add(new Instalacion("IN-01", "Lidia Orellana", "Zona 11", 2.5, Zona.METROPOLITANA, 5));
        ordenes.add(new Instalacion("IN-02", "Tomás Ajú", "Villa Canales", 1.25, Zona.PERIFERICA, 1));
        ordenes.add(new InstalacionCertificada("EC-01", "Irene Valdés", "Zona 12", 2.5,
                Zona.METROPOLITANA, 5, 2000));
        ordenes.add(new InstalacionCertificada("EC-02", "Saúl Cifuentes", "Amatitlán", 1.25,
                Zona.PERIFERICA, 1, 1500));
        ordenes.add(new MantenimientoAire("AA-01", "Nora Galicia", "Zona 10", 1.5,
                Zona.METROPOLITANA, 9000));
        ordenes.add(new MantenimientoAire("AA-02", "Rubén Tzul", "Zona 14", 2.25,
                Zona.METROPOLITANA, 60000));
        registrarCuadrilla("RUTA-01", 8, false);
        registrarCuadrilla("FRIO-02", 6, true);
        registrarCuadrilla("MIXTA-03", 4, true);
    }

    public void registrarOrden(OrdenServicio nueva) {
        if (nueva == null) {
            throw new IllegalArgumentException("Debe proporcionar una orden.");
        }
        for (OrdenServicio orden : ordenes) {
            if (orden.getCodigo().equals(nueva.getCodigo())) {
                throw new IllegalArgumentException("Ya existe una orden con ese código.");
            }
        }
        // Se comprueba el importe antes de modificar la orden o la colección.
        Validacion.importe(ingresosRegistro(null) + ingresosVisitas(null) + nueva.calcularCosto());
        nueva.anotarRegistro();
        ordenes.add(nueva);
    }

    public void registrarCuadrilla(String codigo, double capacidad, boolean certificada) {
        Cuadrilla nueva = new Cuadrilla(codigo, capacidad, certificada);
        for (Cuadrilla cuadrilla : cuadrillas) {
            if (cuadrilla.getCodigo().equals(nueva.getCodigo())) {
                throw new IllegalArgumentException("Ya existe una cuadrilla con ese código.");
            }
        }
        cuadrillas.add(nueva);
    }

    public void abrirJornada(String codigoJornada, String codigoCuadrilla) {
        String codigo = Validacion.codigo(codigoJornada);
        Cuadrilla cuadrilla = buscarCuadrilla(codigoCuadrilla);
        for (Jornada jornada : jornadas) {
            if (jornada.getCodigo().equals(codigo)) {
                throw new IllegalArgumentException("Ya existe una jornada con ese código.");
            }
            if (jornada.getCuadrilla() == cuadrilla && jornada.getEstado() != EstadoJornada.CERRADA) {
                throw new IllegalStateException("La cuadrilla ya tiene una jornada sin cerrar.");
            }
        }
        jornadas.add(new Jornada(codigo, cuadrilla));
    }

    public void asignarOrden(String jornada, String orden) {
        buscarJornada(jornada).asignar(buscarOrden(orden));
    }

    public void retirarOrden(String jornada, String orden) {
        buscarJornada(jornada).retirar(buscarOrden(orden));
    }

    public void iniciarJornada(String jornada) {
        buscarJornada(jornada).iniciar();
    }

    public void registrarResultado(String jornada, String orden, boolean completada) {
        buscarJornada(jornada).registrarResultado(buscarOrden(orden), completada);
    }

    public OrdenServicio buscarOrden(String codigo) {
        String clave = Validacion.codigo(codigo);
        for (OrdenServicio orden : ordenes) {
            if (orden.getCodigo().equals(clave)) {
                return orden;
            }
        }
        throw new IllegalArgumentException("No existe una orden con código " + clave + ".");
    }

    public Cuadrilla buscarCuadrilla(String codigo) {
        String clave = Validacion.codigo(codigo);
        for (Cuadrilla cuadrilla : cuadrillas) {
            if (cuadrilla.getCodigo().equals(clave)) {
                return cuadrilla;
            }
        }
        throw new IllegalArgumentException("No existe una cuadrilla con código " + clave + ".");
    }

    public Jornada buscarJornada(String codigo) {
        String clave = Validacion.codigo(codigo);
        for (Jornada jornada : jornadas) {
            if (jornada.getCodigo().equals(clave)) {
                return jornada;
            }
        }
        throw new IllegalArgumentException("No existe una jornada con código " + clave + ".");
    }

    public ArrayList<String> getCategorias() {
        ArrayList<String> categorias = new ArrayList<>();
        for (OrdenServicio orden : ordenes) {
            String nombre = orden.nombreCategoria();
            if (!categorias.contains(nombre)) {
                categorias.add(nombre);
            }
        }
        return categorias;
    }

    public int contarOrdenes(String categoria, EstadoOrden estado) {
        int cantidad = 0;
        for (OrdenServicio orden : ordenes) {
            if (orden.nombreCategoria().equals(categoria) && orden.getEstado() == estado) {
                cantidad++;
            }
        }
        return cantidad;
    }

    // categoria == null significa sumar todas las categorías.
    public double ingresosRegistro(String categoria) {
        double total = 0;
        for (OrdenServicio orden : ordenes) {
            if (categoria == null || orden.nombreCategoria().equals(categoria)) {
                total += orden.getCobroRegistro();
            }
        }
        return total;
    }

    public double ingresosVisitas(String categoria) {
        double total = 0;
        for (OrdenServicio orden : ordenes) {
            if (categoria == null || orden.nombreCategoria().equals(categoria)) {
                total += orden.getCobrosVisita();
            }
        }
        return total;
    }

    // Las copias impiden que otra capa agregue o elimine elementos internos.
    public ArrayList<OrdenServicio> getOrdenes() { return new ArrayList<>(ordenes); }
    public ArrayList<Cuadrilla> getCuadrillas() { return new ArrayList<>(cuadrillas); }
    public ArrayList<Jornada> getJornadas() { return new ArrayList<>(jornadas); }
}
