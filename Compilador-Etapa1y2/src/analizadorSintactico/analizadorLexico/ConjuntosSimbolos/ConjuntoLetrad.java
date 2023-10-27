package analizadorSintactico.analizadorLexico.ConjuntosSimbolos;

public class ConjuntoLetrad extends ConjuntoSimbolos {

	@Override
	public boolean contieneSimbolo(char simbolo) {
		return (simbolo == 'd');
	}

}
