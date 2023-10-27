package analizadorSintactico.analizadorLexico.ConjuntosSimbolos;

public class ConjuntoSignoMas extends ConjuntoSimbolos {

	@Override
	public boolean contieneSimbolo(char simbolo) {
		return (simbolo == '+');
	}

}
