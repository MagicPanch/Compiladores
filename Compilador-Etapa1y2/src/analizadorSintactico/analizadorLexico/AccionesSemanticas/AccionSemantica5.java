package analizadorSintactico.analizadorLexico.AccionesSemanticas;

import analizadorSintactico.analizadorLexico.AnalizadorLexico;
import analizadorSintactico.analizadorLexico.AtributosSimbolo;
import analizadorSintactico.analizadorLexico.Generador_Token;
import analizadorSintactico.analizadorLexico.Par_Token_Lexema;

public class AccionSemantica5 extends AccionSemantica {
	
    @Override
    public void ejecutar(Par_Token_Lexema par, char simbolo) {
    	par.setLexema(par.getLexema() + simbolo);
    	AtributosSimbolo token_cantidad = AnalizadorLexico.simbolos.get(par.getLexema());
		if (token_cantidad != null) {
			System.out.print("[" + token_cantidad.getToken() + "]" + "   ");
			par.setToken(token_cantidad.getToken());
			token_cantidad.incrementarCantidad();
		}
		else {
			Generador_Token generador_token = new Generador_Token();
			int token_lexema_nuevo = generador_token.obtenerToken(par.getLexema());
			AnalizadorLexico.simbolos.put(par.getLexema(), new AtributosSimbolo(token_lexema_nuevo, 1));
	    	System.out.print("[" + token_lexema_nuevo + "]" + "   ");
	    	par.setToken(token_lexema_nuevo);
		}
    }
}
