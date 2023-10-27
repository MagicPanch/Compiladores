package analizadorSintactico.analizadorLexico.ConjuntosSimbolos;

public class ConjuntolmSINiuld extends ConjuntoSimbolos {

	@Override
	public boolean contieneSimbolo(char simbolo) {
		return (Character.isLowerCase(simbolo) && simbolo != 'i' && simbolo != 'u' && simbolo != 'l' && simbolo != 'd');
	}
	
}
