package analizadorSintactico.analizadorLexico.ConjuntosSimbolos;

public class ConjuntoPunto extends ConjuntoSimbolos {

	@Override
	public boolean contieneSimbolo(char simbolo) {
		return (simbolo == '.');
	}

}
