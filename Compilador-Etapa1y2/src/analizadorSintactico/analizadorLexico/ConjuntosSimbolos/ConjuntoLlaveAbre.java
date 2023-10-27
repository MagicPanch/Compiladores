package analizadorSintactico.analizadorLexico.ConjuntosSimbolos;

public class ConjuntoLlaveAbre extends ConjuntoSimbolos {

	@Override
	public boolean contieneSimbolo(char simbolo) {
		return (simbolo == '{');
	}

}
