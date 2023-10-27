package analizadorSintactico.analizadorLexico.ConjuntosSimbolos;

public class ConjuntoOperadoresComparacion extends ConjuntoSimbolos {

	@Override
	public boolean contieneSimbolo(char simbolo) {
		return (simbolo == '>' || simbolo == '<');
	}

}
