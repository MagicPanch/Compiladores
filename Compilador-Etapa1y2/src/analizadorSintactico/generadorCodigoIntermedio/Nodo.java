package analizadorSintactico.generadorCodigoIntermedio;

import java.util.ArrayList;

public class Nodo {
	private String simbolo = null;
	private String cadena_impresion = null;
	private String valor_constante = null;
	private String tipo = null;
	private ArrayList<ParVariableAtributo> pares_variable_atributo = new ArrayList<ParVariableAtributo>(); //se usa para el caso de los CALL, en caso de que la funcion invocada sea un metodo, para poder asignar el valor de las variables a los atributos y viceversa al finalizar el metodo
	private String parametro_formal_asociado = null; //se usa para guardar en un nodo de PARAMETRO_REAL, el lexema nm del parametro formal al que esta asociado
	private Nodo nodo_hijo_derecho = null;
	private Nodo nodo_hijo_izquierdo = null;
	private Nodo nodo_hijo_unidireccional = null;
	
	public Nodo(String simbolo, Nodo nodo_hijo_izquierdo, Nodo nodo_hijo_derecho) {
		this.simbolo = simbolo;
		this.nodo_hijo_izquierdo = nodo_hijo_izquierdo;
		this.nodo_hijo_derecho = nodo_hijo_derecho;
	}
	
	public Nodo(String simbolo, Nodo nodo_hijo_unidireccional) {
		this.simbolo = simbolo;
		this.nodo_hijo_unidireccional = nodo_hijo_unidireccional;
	}
	
	public Nodo(Nodo nodo_hijo_izquierdo, Nodo nodo_hijo_derecho, Nodo nodo_hijo_unidireccional, String simbolo, String tipo) {
		this.simbolo = simbolo;
		this.nodo_hijo_izquierdo = nodo_hijo_izquierdo;
		this.nodo_hijo_derecho = nodo_hijo_derecho;
		this.nodo_hijo_unidireccional = nodo_hijo_unidireccional;
		this.tipo = tipo;
	}

	public String getSimbolo() {
		return simbolo;
	}

	public void setSimbolo(String simbolo) {
		this.simbolo = simbolo;
	}
	
	public String getCadenaImpresion() {
		return cadena_impresion;
	}

	public void setCadenaImpresion(String cadena_impresion) {
		this.cadena_impresion = cadena_impresion;
	}

	public String getValorConstante() {
		return valor_constante;
	}

	public void setValorConstante(String valor_constante) {
		this.valor_constante = valor_constante;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo_expresion) {
		this.tipo = tipo_expresion;
	}

	public ArrayList<ParVariableAtributo> getParesVariableAtributo() {
		return pares_variable_atributo;
	}

	public void setParesVariableAtributo(ArrayList<ParVariableAtributo> pares_variable_atributo) {
		this.pares_variable_atributo = pares_variable_atributo;
	}
	
	public String getParametroFormalAsociado() {
		return parametro_formal_asociado;
	}

	public void setParametroFormalAsociado(String parametro_formal_asociado) {
		this.parametro_formal_asociado = parametro_formal_asociado;
	}

	public Nodo getNodoHijoDerecho() {
		return nodo_hijo_derecho;
	}

	public void setNodoHijoDerecho(Nodo nodo_hijo_derecho) {
		this.nodo_hijo_derecho = nodo_hijo_derecho;
	}

	public Nodo getNodoHijoIzquierdo() {
		return nodo_hijo_izquierdo;
	}

	public void setNodoHijoIzquierdo(Nodo nodo_hijo_izquierdo) {
		this.nodo_hijo_izquierdo = nodo_hijo_izquierdo;
	}

	public Nodo getNodoHijoUnidireccional() {
		return nodo_hijo_unidireccional;
	}

	public void setNodoHijoUnidireccional(Nodo nodo_hijo_unidireccional) {
		this.nodo_hijo_unidireccional = nodo_hijo_unidireccional;
	}
	
}
