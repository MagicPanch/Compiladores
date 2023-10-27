package analizadorSintactico.analizadorSemantico;

public class Terceto {
	private String operador;
	private String operando1;
	private String operando2;
	
	public Terceto(String operador, String operando1, String operando2) {
		this.operador = operador;
		this.operando1 = operando1;
		this.operando2 = operando2;
	}

	public String getOperador() {
		return operador;
	}

	public void setOperador(String operador) {
		this.operador = operador;
	}

	public String getOperando1() {
		return operando1;
	}

	public void setOperando1(String operando1) {
		this.operando1 = operando1;
	}

	public String getOperando2() {
		return operando2;
	}

	public void setOperando2(String operando2) {
		this.operando2 = operando2;
	}
	
	
}
