package analizadorSintactico.analizadorLexico.AccionesSemanticas;

import analizadorSintactico.analizadorLexico.AnalizadorLexico;
import analizadorSintactico.analizadorLexico.AtributosSimbolo;
import analizadorSintactico.analizadorLexico.Generador_Token;
import analizadorSintactico.analizadorLexico.Par_Token_Lexema;

public class AccionSemantica9 extends AccionSemantica {
	
	@Override
    public void ejecutar(Par_Token_Lexema par, char simbolo) {
		AnalizadorLexico.indice_caracter_leer--;
		AtributosSimbolo token_cantidad = AnalizadorLexico.simbolos.get(par.getLexema());
		if (token_cantidad != null) {
			System.out.print("[" + token_cantidad.getToken() + "]" + "   ");
			par.setToken(token_cantidad.getToken());
			token_cantidad.incrementarCantidad();
		}
		else {
			int posicion_D = par.getLexema().indexOf('D');
			if (posicion_D == -1)
				posicion_D = par.getLexema().indexOf('d');
			Double numero_punto_flotante = null;
			if (posicion_D != -1) {
				numero_punto_flotante =  Double.parseDouble(par.getLexema().substring(0, posicion_D));
				Double exponente = Double.parseDouble(par.getLexema().substring(posicion_D + 1, par.getLexema().length()));
				numero_punto_flotante = Math.pow(numero_punto_flotante, exponente);
			}
			else
				numero_punto_flotante = Double.parseDouble(par.getLexema());
			boolean numero_valido = true;
			if (!(numero_punto_flotante > Math.pow(2.2250738585072014, -308) && numero_punto_flotante < Math.pow(1.7976931348623157, 308)) && numero_punto_flotante != 0.0) {
				AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - par.getLexema().length()) + " - ERROR: Constante de punto flotante fuera de rango");
				numero_valido = false;
			}
			if (numero_valido) {
				Generador_Token generador_token = new Generador_Token();
				int token_lexema_nuevo = generador_token.obtenerToken(par.getLexema());
				System.out.print("[" + token_lexema_nuevo + "]" + "   ");
				AnalizadorLexico.simbolos.put(par.getLexema(), new AtributosSimbolo(token_lexema_nuevo, 1, "DOUBLE"));
	        	par.setToken(token_lexema_nuevo);
			}
		}
    }

}
