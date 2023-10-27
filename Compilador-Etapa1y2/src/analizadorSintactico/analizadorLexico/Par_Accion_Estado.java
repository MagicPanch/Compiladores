package analizadorSintactico.analizadorLexico;

import analizadorSintactico.analizadorLexico.AccionesSemanticas.AccionSemantica;

public class Par_Accion_Estado {
    private Integer estado;
    private AccionSemantica accion;

    public Par_Accion_Estado(Integer estado, AccionSemantica accion) {
        this.estado = estado;
        this.accion = accion;
    }

    public Integer getEstado() {
        return estado;
    }

    public AccionSemantica getAccion() {
        return accion;
    }
}
