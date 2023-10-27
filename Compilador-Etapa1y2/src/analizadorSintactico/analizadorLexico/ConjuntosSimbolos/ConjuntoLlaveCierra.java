package analizadorSintactico.analizadorLexico.ConjuntosSimbolos;

public class ConjuntoLlaveCierra extends ConjuntoSimbolos {

	@Override
	public boolean contieneSimbolo(char simbolo) {
		return (simbolo == '}');
	}

}
