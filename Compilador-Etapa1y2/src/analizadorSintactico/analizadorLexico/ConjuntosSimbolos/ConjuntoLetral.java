package analizadorSintactico.analizadorLexico.ConjuntosSimbolos;

public class ConjuntoLetral extends ConjuntoSimbolos {

	@Override
	public boolean contieneSimbolo(char simbolo) {
		return (simbolo == 'l');
	}

}
