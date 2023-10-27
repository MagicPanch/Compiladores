package analizadorSintactico.analizadorLexico;

public class Par_Token_Lexema {
	private int token;
	private String lexema;
	
	public Par_Token_Lexema(int token, String lexema) {
		this.token = token;
		this.lexema = lexema;
	}

	public int getToken() {
		return token;
	}

	public void setToken(int token) {
		this.token = token;
	}

	public String getLexema() {
		return lexema;
	}

	public void setLexema(String lexema) {
		this.lexema = lexema;
	}
}
