package analizadorSintactico.analizadorLexico.ConjuntosSimbolos;

public class ConjuntoLMsinD extends ConjuntoSimbolos {

	@Override
	public boolean contieneSimbolo(char simbolo) {
		return (Character.isUpperCase(simbolo) && simbolo != 'D');
	}
	
}
