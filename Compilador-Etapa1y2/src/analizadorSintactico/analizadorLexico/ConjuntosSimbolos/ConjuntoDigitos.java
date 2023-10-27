package analizadorSintactico.analizadorLexico.ConjuntosSimbolos;

public class ConjuntoDigitos extends ConjuntoSimbolos {
	
	@Override
	public boolean contieneSimbolo(char simbolo) {
		return Character.isDigit(simbolo);
	}
	
}
