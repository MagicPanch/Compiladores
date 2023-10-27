package analizadorSintactico.analizadorLexico;

public class AtributosSimbolo {
	private int token;
	private int cantidad;
	private String tipo = null;
	private String uso = null;
	private String ambito = null;
	
	public AtributosSimbolo(int token, int cantidad) {
		this.token = token;
		this.cantidad = cantidad;
	}

	public AtributosSimbolo(int token, int cantidad, String tipo) { //nunca es necesario desde el analizador lexico pasar al contructor al agregar un simbolo el uso o el ambito, por eso no hay ningun cosntructor con dichos atributos
		this.token = token;
		this.cantidad = cantidad;
		this.tipo = tipo;
	}

	public int getToken() {
		return token;
	}

	public void setToken(int token) {
		this.token = token;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getUso() {
		return uso;
	}

	public void setUso(String uso) {
		this.uso = uso;
	}
	
	public String getAmbito() {
		return ambito;
	}

	public void setAmbito(String ambito) {
		this.ambito = ambito;
	}

	public void incrementarCantidad() {
		cantidad++;
	}

	public void decrementarCantidad() {
		cantidad--;
	}
	
}
