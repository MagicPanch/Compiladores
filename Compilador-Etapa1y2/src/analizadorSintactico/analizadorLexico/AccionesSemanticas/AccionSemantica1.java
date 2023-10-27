package analizadorSintactico.analizadorLexico.AccionesSemanticas;

import analizadorSintactico.analizadorLexico.Par_Token_Lexema;

public class AccionSemantica1 extends AccionSemantica {

    @Override
    public void ejecutar(Par_Token_Lexema par, char simbolo) {
        par.setLexema(String.valueOf(simbolo));
    }
}
