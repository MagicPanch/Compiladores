package analizadorSintactico.analizadorLexico.ConjuntosSimbolos;

public class ConjuntoLetrau extends ConjuntoSimbolos {

	@Override
	public boolean contieneSimbolo(char simbolo) {
		return (simbolo == 'u');
	}

}
