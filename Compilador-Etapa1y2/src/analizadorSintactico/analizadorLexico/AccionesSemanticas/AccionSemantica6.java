package analizadorSintactico.analizadorLexico.AccionesSemanticas;

import analizadorSintactico.analizadorLexico.AnalizadorLexico;
import analizadorSintactico.analizadorLexico.Generador_Token;
import analizadorSintactico.analizadorLexico.Par_Token_Lexema;

public class AccionSemantica6 extends AccionSemantica {
	
	@Override
    public void ejecutar(Par_Token_Lexema par, char simbolo) {
		AnalizadorLexico.indice_caracter_leer--;
		Generador_Token generador_token = new Generador_Token();
		int token_lexema_nuevo = generador_token.obtenerToken(par.getLexema());
    	System.out.print("[" + token_lexema_nuevo + "]" + "   ");
    	par.setToken(token_lexema_nuevo);
    }
}
