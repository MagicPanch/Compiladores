package analizadorSintactico.analizadorLexico.ConjuntosSimbolos;

public class ConjuntoCaracteresSimilares extends ConjuntoSimbolos {

	@Override
	public boolean contieneSimbolo(char simbolo) {
		return (simbolo == '/' || simbolo == '(' || simbolo == ')' || simbolo == ';' || simbolo == ',' || simbolo == ':');
	}

}
