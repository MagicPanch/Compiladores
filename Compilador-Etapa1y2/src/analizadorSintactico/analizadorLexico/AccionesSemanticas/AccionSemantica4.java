package analizadorSintactico.analizadorLexico.AccionesSemanticas;

import analizadorSintactico.analizadorLexico.Par_Token_Lexema;

public class AccionSemantica4 extends AccionSemantica {
	
    @Override
    public void ejecutar(Par_Token_Lexema par, char simbolo) {
        if (simbolo != '\n')
        	par.setLexema(par.getLexema() + simbolo);
    }
}
