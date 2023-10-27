package analizadorSintactico.analizadorLexico.ConjuntosSimbolos;

public class ConjuntoGuionBajo extends ConjuntoSimbolos {

	@Override
	public boolean contieneSimbolo(char simbolo) {
		return (simbolo == '_');
	}

}
