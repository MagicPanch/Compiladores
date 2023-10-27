package analizadorSintactico.analizadorLexico.AccionesSemanticas;

import analizadorSintactico.analizadorLexico.Par_Token_Lexema;

public abstract class AccionSemantica {

    public abstract void ejecutar(Par_Token_Lexema par, char simbolo);
}
