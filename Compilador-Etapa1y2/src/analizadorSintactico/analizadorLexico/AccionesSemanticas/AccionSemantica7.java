package analizadorSintactico.analizadorLexico.AccionesSemanticas;

import analizadorSintactico.analizadorLexico.AnalizadorLexico;
import analizadorSintactico.analizadorLexico.AtributosSimbolo;
import analizadorSintactico.analizadorLexico.Generador_Token;
import analizadorSintactico.analizadorLexico.Par_Token_Lexema;

public class AccionSemantica7 extends AccionSemantica {
	
	@Override
    public void ejecutar(Par_Token_Lexema par, char simbolo) {
		AnalizadorLexico.indice_caracter_leer--;
		if (par.getLexema().length() > 20) {
			String identificador_cortado = par.getLexema().substring(0, 20);
			AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - par.getLexema().length()) + " - WARNING: Nombre de identificador muy largo, se considerara solo " + identificador_cortado);
			par.setLexema(identificador_cortado);
		}
		AtributosSimbolo token_cantidad = AnalizadorLexico.simbolos.get(par.getLexema());
		if (token_cantidad != null) {
			System.out.print("[" + token_cantidad.getToken() + "]" + "   ");
			par.setToken(token_cantidad.getToken());
		}
		else {
			Generador_Token generador_token = new Generador_Token();
			int token_lexema_nuevo = generador_token.obtenerToken(par.getLexema());
			AnalizadorLexico.simbolos.put(par.getLexema(), new AtributosSimbolo(token_lexema_nuevo));
        	System.out.print("[" + token_lexema_nuevo + "]" + "   ");
        	par.setToken(token_lexema_nuevo);
		}
    }
}
