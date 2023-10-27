package analizadorSintactico.analizadorLexico.AccionesSemanticas;

import java.math.BigInteger;

import analizadorSintactico.analizadorLexico.AnalizadorLexico;
import analizadorSintactico.analizadorLexico.AtributosSimbolo;
import analizadorSintactico.analizadorLexico.Generador_Token;
import analizadorSintactico.analizadorLexico.Par_Token_Lexema;

public class AccionSemantica8 extends AccionSemantica {
	
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
	    	int posicion_guion = par.getLexema().indexOf('_');
	    	BigInteger numero_entero = new BigInteger(par.getLexema().substring(0, posicion_guion));
	    	int token_lexema_nuevo = generador_token.obtenerToken(par.getLexema());
	    	if (token_lexema_nuevo == 274) { //pregunta si el token del lexema nuevo es el asociado a los enteros simples
	    		if (numero_entero.compareTo(BigInteger.valueOf((long) (Math.pow(2, 15)))) > 0)
	    			AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - par.getLexema().length()) + " - ERROR: Constante entera simple fuera de rango");
	    		else {
	    			System.out.print("[" + token_lexema_nuevo + "]" + "   ");
	    			AnalizadorLexico.simbolos.put(par.getLexema(), new AtributosSimbolo(token_lexema_nuevo, 1, "INT"));
	    			par.setToken(token_lexema_nuevo);
	    		}
	    	}
	    	else { //aca va el caso de los enteros unsigned long
	    		if (numero_entero.compareTo(BigInteger.valueOf((long) ((Math.pow(2, 32)-1)))) > 0)
	    			AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - par.getLexema().length()) + " - ERROR: Constante entera unsigned long fuera de rango");
	    		else {
	    			System.out.print("[" + token_lexema_nuevo + "]" + "   ");
	    			AnalizadorLexico.simbolos.put(par.getLexema(), new AtributosSimbolo(token_lexema_nuevo, 1, "ULONG"));
	    			par.setToken(token_lexema_nuevo);
	    		}
	    	}
		}
	}
}
