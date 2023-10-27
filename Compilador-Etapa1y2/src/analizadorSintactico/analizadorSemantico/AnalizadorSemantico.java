package analizadorSintactico.analizadorSemantico;

public class AnalizadorSemantico {
	public static String ambito_actual = "main";
	
	public AnalizadorSemantico() {}
	
	public void actualizarAmbitoActual(String ambito_nuevo) {
		if (ambito_nuevo.equals("-")) {
			int posicion_ultimo_ambito = ambito_actual.lastIndexOf(":");
			if (posicion_ultimo_ambito == -1)
				ambito_actual = "";
			else
				ambito_actual = ambito_actual.substring(0, posicion_ultimo_ambito);	
		}
		else
			ambito_actual = ambito_actual + ":" + ambito_nuevo;
	}
}