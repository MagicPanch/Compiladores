package analizadorSintactico.analizadorLexico.ConjuntosSimbolos;

public class ConjuntoTabBlNl extends ConjuntoSimbolos {

	@Override
	public boolean contieneSimbolo(char simbolo) {
		return (simbolo == '\t' || simbolo == ' ' || simbolo == '\n');
	}

}
