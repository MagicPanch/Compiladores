package analizadorSintactico.analizadorLexico.AccionesSemanticas;

import analizadorSintactico.analizadorLexico.AnalizadorLexico;
import analizadorSintactico.analizadorLexico.Par_Token_Lexema;

public class AccionSemanticaError extends AccionSemantica {

	@Override
	public void ejecutar(Par_Token_Lexema par, char simbolo) { //dado que son muy similares, las acciones de error se encuentran en la misma clase (solo crean un mensaje de error dependiendo del estado actual)
		if (AnalizadorLexico.estado_actual == 0)
			AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: '" + simbolo + "' no es un caracter valido dentro del lenguaje");
		if (AnalizadorLexico.estado_actual == 2)
			AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: Formato de palabra reservada invalido. Luego de un '_' debe ir al menos una letra mayuscula");
		if (AnalizadorLexico.estado_actual == 4) //existe tambien el posible error por fin de programa en este estado, pero ese se envia directamente desde fuera porque no se debe a una transicion invalida
			AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: Caracter invalido en la cadena multilinea");
		if (AnalizadorLexico.estado_actual == 6)
			AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: Formato de constante numerica invalido. A la secuencia de digitos solo la puede seguir un '_', un '.' u otro digito");
		if (AnalizadorLexico.estado_actual == 7)
			AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: Formato de constante numerica entera invalido. Los unicos sufijos validos para las constantes enteras son '_i' y '_ul'");
		if (AnalizadorLexico.estado_actual == 8)
			AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: Formato de constante numerica unsigned long invalido. El unico sufijo valido para este caso es '_ul'");
		if (AnalizadorLexico.estado_actual == 11)
			AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: Formato de constante numerica flotante invalido. Luego de la letra " + par.getLexema().charAt(par.getLexema().length() - 1) + " debe indicarse el signo del exponente (+ o -)"); //para mas detalle se pregunta por la letra que indica el comienzo del exponente porque puede ser "D" o "d"
		if (AnalizadorLexico.estado_actual == 12)
			AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: Formato de constante numerica flotante invalido. Luego del signo " + par.getLexema().charAt(par.getLexema().length() - 1) + " debe seguir la parte numerica del exponente (al menos un digito)"); //para mas detalle se pregunta por el signo del exponente porque puede ser "+" o "-"
		if (AnalizadorLexico.estado_actual == 16)
			AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: Formato de comparador invalido. El comparador de desigualdad correcto es '!!'");
	}
}
