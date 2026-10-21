package main;

import controlador.CoordinadorServicios;
import modelo.AgendaTecnica;
import vista.ConsolaTecniHogar;

public class Main {
    public static void main(String[] args) {
        AgendaTecnica agendaDelDia = new AgendaTecnica(true);
        ConsolaTecniHogar panelConsola = new ConsolaTecniHogar();
        CoordinadorServicios enlaceServicios = new CoordinadorServicios(agendaDelDia, panelConsola);
        enlaceServicios.ejecutar();
    }
}
