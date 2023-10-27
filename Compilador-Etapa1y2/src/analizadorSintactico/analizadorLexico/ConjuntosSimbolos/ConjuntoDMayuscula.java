package analizadorSintactico.analizadorLexico.ConjuntosSimbolos;

public class ConjuntoDMayuscula extends ConjuntoSimbolos {

	@Override
	public boolean contieneSimbolo(char simbolo) {
		return (simbolo == 'D');
	}

}
