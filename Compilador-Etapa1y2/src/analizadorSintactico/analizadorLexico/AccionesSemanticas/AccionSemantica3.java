package analizadorSintactico.analizadorLexico.AccionesSemanticas;

import analizadorSintactico.analizadorLexico.AnalizadorLexico;
import analizadorSintactico.analizadorLexico.Generador_Token;
import analizadorSintactico.analizadorLexico.Par_Token_Lexema;

public class AccionSemantica3 extends AccionSemantica {
	
    @Override
    public void ejecutar(Par_Token_Lexema par, char simbolo) {
    	AnalizadorLexico.indice_caracter_leer--;
    	Generador_Token generador_token = new Generador_Token();
		int token_palabra_reservada = generador_token.obtenerToken(par.getLexema());
        if (token_palabra_reservada != -1) {
        	System.out.print("[" + token_palabra_reservada + "]" + "   ");
        	par.setToken(token_palabra_reservada);
        }
        else
        	AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - par.getLexema().length()) + " - ERROR: '" + par.getLexema() + "' no se reconoce como una palabra reservada valida en el lenguaje");
    }
}
