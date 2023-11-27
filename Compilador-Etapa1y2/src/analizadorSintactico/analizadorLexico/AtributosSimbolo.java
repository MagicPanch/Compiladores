package analizadorSintactico.analizadorLexico;

import java.util.ArrayList;

public class AtributosSimbolo {
	private int token;
	private int cantidad = -1; //ya que solo para las constantes y las cadenas de caracteres tiene sentido almacenar la cantidad de repeticiones (para las cadenas tiene sentido para un chequeo semantico que implica quitarlas de la tabla de simbolos en ciertos casos)
	private String tipo = null;
	private String uso = null;
	private String lexema_implementacion = null; //sirve para almacenar el lexema de la funcion que implementa a un prototipo de metodo de una clase (se usa en el analizador semantico)
	private int cantidad_prototipos = -1; //sirve para almacenar la cantidad de prototipos que contiene una interfaz (sirve para chequeos semanticos)
	private String lexema_interfaz_implementada = null;
	private String nombre_parametro_formal = null;
	private boolean variable_referenciada = false;
	private String lexema_clase_asociada = null;
	private ArrayList<String> lexemas_clases_heredadas = new ArrayList<String>(); //sirve para almacenar el conjunto de clases heredadas por composicion por otra clase (sirve para chequeos semanticos)
	private String cadena_caracteres_print = null; //usado para las variables que contienen las cadenas de caracteres a imprimir en el codigo Assembler
	private String valor_constante_double = null; //usado para guardar el valor de las variables auxiliares para trabajar con valores DOUBLE en la generacion de codigo Assembler
	
	public AtributosSimbolo(int token) {
		this.token = token;
	}
	
	public AtributosSimbolo(int token, int cantidad) {
		this.token = token;
		this.cantidad = cantidad;
	}
	
	public AtributosSimbolo(int token, String uso) {
		this.token = token;
		this.uso = uso;
	}

	public AtributosSimbolo(int token, int cantidad, String tipo) { //nunca es necesario desde el analizador lexico pasar al contructor al agregar un simbolo el uso o el ambito, por eso no hay ningun cosntructor con dichos atributos
		this.token = token;
		this.cantidad = cantidad;
		this.tipo = tipo;
	}

	public AtributosSimbolo(String tipo, String cadena_caracteres_print) {
		this.tipo = tipo;
		this.cadena_caracteres_print = cadena_caracteres_print;
	}
	
	public AtributosSimbolo(String tipo, String uso, boolean variable_auxiliar) { //el boolean no se usa, es para que no interfiera con el constructor de arriba
		this.tipo = tipo;
		this.uso = uso;
	}
	
	public AtributosSimbolo(String tipo, String uso, boolean variable_auxiliar, String valor_constante_double) {
		this.tipo = tipo;
		this.uso = uso;
		this.valor_constante_double = valor_constante_double;
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
	
	public String getLexemaFuncionImplementacion() {
		return lexema_implementacion;
	}

	public void setLexemaFuncionImplementacion(String lexema_implementacion) {
		this.lexema_implementacion = lexema_implementacion;
	}
	
	public int getCantidadPrototipos() {
		return cantidad_prototipos;
	}

	public void setCantidadPrototipos(int cantidad_prototipos) {
		this.cantidad_prototipos = cantidad_prototipos;
	}

	public String getLexemaInterfazImplementada() {
		return lexema_interfaz_implementada;
	}

	public void setLexemaInterfazImplementada(String lexema_interfaz_implementada) {
		this.lexema_interfaz_implementada = lexema_interfaz_implementada;
	}
	

	public String getNombreParametroFormal() {
		return nombre_parametro_formal;
	}

	public void setNombreParametroFormal(String nombre_parametro_formal) {
		this.nombre_parametro_formal = nombre_parametro_formal;
	}

	public boolean fueReferenciada() {
		return this.variable_referenciada;
	}

	public void variableReferenciada() {
		this.variable_referenciada = true;
	}

	public String getLexemaClaseAsociada() {
		return lexema_clase_asociada;
	}

	public void setLexemaClaseAsociada(String lexema_clase_asociada) {
		this.lexema_clase_asociada = lexema_clase_asociada;
	}
	
	public void agregarClaseHeredada(String clase) {
		this.lexemas_clases_heredadas.add(clase);
	}
	
	public ArrayList<String> getClasesHeredadas() {
		return this.lexemas_clases_heredadas;
	}

	public String getCadenaCaracteresPrint() {
		return cadena_caracteres_print;
	}

	public void setCadenaCaracteresPrint(String cadena_caracteres_print) {
		this.cadena_caracteres_print = cadena_caracteres_print;
	}
	
	public String getValorConstanteDouble() {
		return valor_constante_double;
	}

	public void setValorConstanteDouble(String valor_constante_double) {
		this.valor_constante_double = valor_constante_double;
	}

	public void incrementarCantidad() {
		cantidad++;
	}

	public void decrementarCantidad() {
		cantidad--;
	}
	
}
