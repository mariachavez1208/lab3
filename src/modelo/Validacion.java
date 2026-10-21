package modelo;

import java.util.Locale;

// Ayuda interna del modelo. No necesita crear objetos.
class Validacion {
    private Validacion() {
    }

    static String texto(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(campo + " no puede estar vacío.");
        }
        return valor.trim();
    }

    static String codigo(String valor) {
        return texto(valor, "El código").toUpperCase(Locale.ROOT);
    }

    static void positivo(double valor, String campo) {
        if (!Double.isFinite(valor) || valor <= 0) {
            throw new IllegalArgumentException(campo + " debe ser un número finito mayor que cero.");
        }
    }

    static double importe(double valor) {
        positivo(valor, "El importe calculado");
        return valor;
    }
}
