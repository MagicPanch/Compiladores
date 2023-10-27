package analizadorSintactico.analizadorLexico.ConjuntosSimbolos;

public class ConjuntoAsterisco extends ConjuntoSimbolos {

	@Override
	public boolean contieneSimbolo(char simbolo) {
		return (simbolo == '*');
	}

}
