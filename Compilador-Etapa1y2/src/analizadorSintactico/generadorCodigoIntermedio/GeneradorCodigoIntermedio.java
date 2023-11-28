package analizadorSintactico.generadorCodigoIntermedio;

import java.util.ArrayList;

import analizadorSintactico.analizadorLexico.AnalizadorLexico;

public class GeneradorCodigoIntermedio {
	public static Nodo nodo_programa = null;
	public static ArrayList<Nodo> funciones = new ArrayList<Nodo>();

	public GeneradorCodigoIntermedio() {}
	
	public Nodo generarNodo(String simbolo, Nodo nodo_hijo_derecho, Nodo nodo_hijo_izquierdo) {
		if (!AnalizadorLexico.hayErrores())
			return new Nodo(simbolo, nodo_hijo_derecho, nodo_hijo_izquierdo);
		return null;
	}
	
	public Nodo generarNodoUnidireccional(String simbolo, Nodo nodo_hijo_unidireccional) {
		if (!AnalizadorLexico.hayErrores())
			return new Nodo(simbolo, nodo_hijo_unidireccional);
		return null;
	}
	
	public static Nodo getNodoPrograma() {
		return nodo_programa;
	}

	public static void setNodoPrograma(Nodo nodo_programa) {
		GeneradorCodigoIntermedio.nodo_programa = nodo_programa;
	}

	public static ArrayList<Nodo> getFunciones() {
		return funciones;
	}

	public static void setFunciones(ArrayList<Nodo> funciones) {
		GeneradorCodigoIntermedio.funciones = funciones;
	}

	public void imprimirArbol() {
		System.out.println();
		System.out.println("------------------------PROGRAMA------------------------");
		System.out.println();
		imprimirNodos(nodo_programa, "");
		System.out.println();
		System.out.println("------------------------FUNCIONES Y METODOS------------------------");
		System.out.println();
		for (Nodo nodo_funcion: funciones) {
			imprimirNodos(nodo_funcion, "");
			System.out.println();
		}
	}
	
	public void imprimirNodos(Nodo nodo, String espacio_identacion) {
        if (nodo != null) {
            System.out.print(espacio_identacion);
            System.out.print(nodo.getSimbolo());
            //if (nodo.getParametroFormalAsociado() != null)
            	//System.out.print("------" + nodo.getParametroFormalAsociado() + "------");
            ArrayList<ParVariableAtributo> pares = nodo.getParesVariableAtributo();
            if (pares != null)
            	for (ParVariableAtributo par: pares)
            		System.out.print(" " + par.getVariable() + "/" + par.getAtributo());
            //if (nodo.getTipo() != null)
            	//System.out.print("------" + nodo.getTipo() + "------");
            //if (nodo.getValorConstante() != null)
            	//System.out.print("------" + nodo.getValorConstante() + "------");
            System.out.println();
            if (nodo.getNodoHijoUnidireccional() != null)
            	imprimirNodos(nodo.getNodoHijoUnidireccional(), espacio_identacion + "   ");
            else {
            	imprimirNodos(nodo.getNodoHijoIzquierdo(), espacio_identacion + "   ");
                imprimirNodos(nodo.getNodoHijoDerecho(), espacio_identacion + "   ");
            }
        }
	}
	
}
