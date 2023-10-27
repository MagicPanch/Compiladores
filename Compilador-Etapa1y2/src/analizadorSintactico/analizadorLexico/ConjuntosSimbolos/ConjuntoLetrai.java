package analizadorSintactico.analizadorLexico.ConjuntosSimbolos;

public class ConjuntoLetrai extends ConjuntoSimbolos {

	@Override
	public boolean contieneSimbolo(char simbolo) {
		return (simbolo == 'i');
	}

}
