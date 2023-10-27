package analizadorSintactico.analizadorLexico.ConjuntosSimbolos;

public class ConjuntoSignoMenos extends ConjuntoSimbolos {

	@Override
	public boolean contieneSimbolo(char simbolo) {
		return (simbolo == '-');
	}
	
}
