package Assembler;

import analizadorSintactico.analizadorLexico.AnalizadorLexico;
import analizadorSintactico.analizadorLexico.AtributosSimbolo;
import analizadorSintactico.generadorCodigoIntermedio.Nodo;
import analizadorSintactico.generadorCodigoIntermedio.ParVariableAtributo;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class GeneradorAssembler {
    private ArrayList<Boolean> tiene_else = new ArrayList<>();
    private boolean es_condicion_for = false;
    private int contador_print = 0;
    private String ultimo_signo_comparacion;
    private int contador_Label = 1; // Utilizado para que no se repitan los label dentro del programa
    private ArrayList<String> pila = new ArrayList<>(); //Utilizado para apilar los label de las sentencias de control
    private int contador_Variable_Auxiliar = 1; // Utilizado para que no haya variables auxiliares con el mismo nombre
    private ArrayList<String> codigoFinal = new ArrayList<>();
    private ArrayList<String> codigoFinalFunciones = new ArrayList<>();

    private String encabezado;

    public GeneradorAssembler() {}

    public Nodo recorrer_y_Reemplazar(Nodo nodo){
        if (nodo != null){
        	if (nodo.getSimbolo().equals("FOR"))
        		es_condicion_for = true;
            else if (nodo.getSimbolo().equals("CUERPO_IF") && nodo.getNodoHijoDerecho() != null)
            	tiene_else.add(true);
            else if (nodo.getSimbolo().equals("CUERPO_IF"))
                tiene_else.add(false);
            nodo.setNodoHijoIzquierdo(recorrer_y_Reemplazar(nodo.getNodoHijoIzquierdo()));;
            nodo.setNodoHijoDerecho(recorrer_y_Reemplazar(nodo.getNodoHijoDerecho()));
            nodo.setNodoHijoUnidireccional(recorrer_y_Reemplazar(nodo.getNodoHijoUnidireccional()));
            System.out.println(nodo.getSimbolo());
            if (nodo.getSimbolo().equals("PRINT") || nodo.getSimbolo().equals("ELSE") || nodo.getSimbolo().equals("CUERPO_FOR") || nodo.getSimbolo().equals("RETURN") || nodo.getSimbolo().equals("THEN") || nodo.getSimbolo().equals("PARAMETRO_REAL") || (nodo.getNodoHijoIzquierdo() != null || nodo.getNodoHijoDerecho() != null || nodo.getNodoHijoUnidireccional() != null)){
                return generar_Codigo(nodo);
            } //ya que para la sentencia ejecutable del PRINT y para los nodos de control THEN, ELSE y CUERPO_FOR hay que efectuar ciertas acciones mas alla de que sus hijos sean todos null   
            else if (nodo.getSimbolo().equals("BLOQUE") || nodo.getSimbolo().equals("IF") || nodo.getSimbolo().equals("FOR") || nodo.getSimbolo().equals("CUERPO_IF") || nodo.getSimbolo().equals("BUCLE") || nodo.getSimbolo().equals("CONDICION") || nodo.getSimbolo().equals("CONDICION_FOR") || nodo.getSimbolo().equals("CUERPO_FUNCION") || nodo.getSimbolo().equals("PROGRAMA") || nodo.getSimbolo().equals("PROGRAMA_VACIO") || nodo.getSimbolo().equals("PARAMETRO_FORMAL"))
            	return null;
            return nodo; //este es el caso de que sea una constante o un identificador, que no tienen que eliminase aun sus nodos (ya que deben ser usados por el padre, por ejemplo una expresion aritmetica)
        }
        return null;
    }

    public void recorrer_y_Generar_Codigo(Nodo nodo,ArrayList<Nodo> funciones){
        for(Nodo nodo_funcion : funciones){
                    String nombre_funcion = nodo_funcion.getSimbolo().replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
                    codigoFinal.add(nombre_funcion+":");
                    recorrer_y_Reemplazar(nodo_funcion);
                    codigoFinal.add("\n");
        }
        for(String codigo : codigoFinal){
            codigoFinalFunciones.add(codigo);
        }
        codigoFinal.clear();
        recorrer_y_Reemplazar(nodo);
        AtributosSimbolo atributos_mensaje_error_int = new AtributosSimbolo("PRINT", "ERROR: OVERFLOW EN SUMA DE ENTEROS CON SIGNO (INT)");
        AnalizadorLexico.simbolos.put("msj_error_overflow_suma_int", atributos_mensaje_error_int);
        AtributosSimbolo atributos_mensaje_error_ulong = new AtributosSimbolo("PRINT", "ERROR: RESULTADO NEGATIVO EN RESTA DE DOS ENTEROS SIN SIGNO (ULONG)");
        AnalizadorLexico.simbolos.put("msj_error_rdo_negativo_resta_ulong", atributos_mensaje_error_ulong);
        AtributosSimbolo atributos_mensaje_error_double = new AtributosSimbolo("PRINT", "ERROR: OVERFLOW EN MULTIPLICACION DE NUMEROS DE PUNTO FLOTANTE (DOUBLE)");
        AnalizadorLexico.simbolos.put("msj_error_overflow_multiplicacion_double", atributos_mensaje_error_double);
        try (FileWriter archivo = new FileWriter("./codigo_Assembler.asm")) {
            encabezado = ".386\n" +
                    ".model flat, stdcall\n" +
                    "option casemap :none\n" +
                    "include \\masm32\\include\\masm32rt.inc\n" +
                    "includelib \\masm32\\lib\\msvcrtprintf.lib\n" +
                    ".data\n";
            archivo.write(encabezado);
            //RECORRIDO TABLA DE SIMBOLOS
            //dw 16 bits
            //dd 32 bits
            //dq 64 bits
            //db Strings
            for(String key : AnalizadorLexico.simbolos.keySet()){
                if (AnalizadorLexico.simbolos.get(key).getTipo() != null && AnalizadorLexico.simbolos.get(key).getTipo().equals("ULONG") && AnalizadorLexico.simbolos.get(key).getUso() != null){
                    String nombre_variable = key;
                    key = key.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
                    archivo.write(key + " dd ?"+ System.lineSeparator());
                    if (AnalizadorLexico.simbolos.get(nombre_variable).getUso().equals("nombre_variable") || AnalizadorLexico.simbolos.get(nombre_variable).getUso().equals("auxiliar") || AnalizadorLexico.simbolos.get(nombre_variable).getUso().equals("nombre_parametro_formal"))
                        archivo.write("_PRINT_" + key + " db \""+ nombre_variable + ": " + "\", 0"+ System.lineSeparator());
                }
                else if (AnalizadorLexico.simbolos.get(key).getTipo() != null && AnalizadorLexico.simbolos.get(key).getTipo().equals("DOUBLE") && AnalizadorLexico.simbolos.get(key).getUso() != null){
                    if (AnalizadorLexico.simbolos.get(key).getUso().equals("auxiliar_constante")) {
                        String valor_constante_double = AnalizadorLexico.simbolos.get(key).getValorConstanteDouble();
                        key = key.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
                        archivo.write(key + " dq " + valor_constante_double + System.lineSeparator());
                    }
                    else {
                        String nombre_variable = key;
                        key = key.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
                        archivo.write(key + " dq ?"+ System.lineSeparator());
                        if (AnalizadorLexico.simbolos.get(nombre_variable).getUso().equals("nombre_variable") || AnalizadorLexico.simbolos.get(nombre_variable).getUso().equals("auxiliar") || AnalizadorLexico.simbolos.get(nombre_variable).getUso().equals("nombre_parametro_formal"))
                            archivo.write("_PRINT_" + key + " db \""+ nombre_variable + ":" + "\", 0"+ System.lineSeparator());
                    }
                }
                else if(AnalizadorLexico.simbolos.get(key).getTipo() != null && AnalizadorLexico.simbolos.get(key).getTipo().equals("INT") && AnalizadorLexico.simbolos.get(key).getUso() != null){
                    String nombre_variable = key;
                    key = key.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
                    archivo.write(key + " dw ?"+ System.lineSeparator());
                    if (AnalizadorLexico.simbolos.get(nombre_variable).getUso().equals("nombre_variable") || AnalizadorLexico.simbolos.get(nombre_variable).getUso().equals("auxiliar") || AnalizadorLexico.simbolos.get(nombre_variable).getUso().equals("nombre_parametro_formal"))
                        archivo.write("_PRINT_" + key + " db \""+ nombre_variable + ":" + "\", 0"+ System.lineSeparator());
                }
                else if(AnalizadorLexico.simbolos.get(key).getTipo() != null && AnalizadorLexico.simbolos.get(key).getTipo().equals("PRINT")){
                    archivo.write(key + " db \""+ AnalizadorLexico.simbolos.get(key).getCadenaCaracteresPrint() + "\", 0"+ System.lineSeparator());
                }
            }
            archivo.write("_AVISO_PRINT_" + " db \""+ "------------------------VALORES RESULTANTES DE CADA VARIABLE------------------------" + "\", 0" + System.lineSeparator());
            archivo.write("newline db 13, 10, 0 ; CRLF" + System.lineSeparator());
            archivo.write("formatoDouble db \"%.20Lf\", 0" + System.lineSeparator());
            archivo.write("formatoInt db \"%d\", 0" + System.lineSeparator());
            archivo.write("formatoUlong db \"%u\", 0" + System.lineSeparator());
            archivo.write("printf PROTO C :VARARG" + System.lineSeparator());
            
            archivo.write("limite_positivo_superior_double dq 1.7976931348623157E+308" + System.lineSeparator());
            archivo.write("limite_positivo_inferior_double dq 2.2250738585072014E-308" + System.lineSeparator());
            archivo.write("limite_negativo_superior_double dq -2.2250738585072014E-308" + System.lineSeparator());
            archivo.write("limite_negativo_inferior_double dq -1.7976931348623157E+308" + System.lineSeparator());
            archivo.write(".code" + System.lineSeparator());
            
                     for (String linea : codigoFinalFunciones) {
                        archivo.write(linea + System.lineSeparator());
                    }
            archivo.write("start:" + System.lineSeparator());
            for (String linea : codigoFinal) {
                archivo.write(linea + System.lineSeparator());
            }
            archivo.write("JMP fin" + System.lineSeparator());
            //LOGICA CONTROL ERRORES
            String error_overflow_suma_int = "error_overflow_suma_int:\ninvoke printf, ADDR msj_error_overflow_suma_int\ninvoke printf, ADDR newline\nJMP fin\n";
            String error_overflow_multiplicacion_double = "error_overflow_multiplicacion_double:\ninvoke printf, ADDR msj_error_overflow_multiplicacion_double\ninvoke printf, ADDR newline\nJMP fin\n";
            String error_rdo_negativo_resta_ulong = "error_rdo_negativo_resta_ulong:\ninvoke printf, ADDR msj_error_rdo_negativo_resta_ulong\ninvoke printf, ADDR newline\nJMP fin\n";
            archivo.write(error_rdo_negativo_resta_ulong);
            archivo.write(error_overflow_multiplicacion_double);
            archivo.write(error_overflow_suma_int);
            archivo.write("fin:\n");
            archivo.write("invoke printf, ADDR newline\n");
            archivo.write("invoke printf, ADDR _AVISO_PRINT_\n");
            archivo.write("invoke printf, ADDR newline\n");
            for(String key : AnalizadorLexico.simbolos.keySet()){
                if (AnalizadorLexico.simbolos.get(key).getTipo() != null && AnalizadorLexico.simbolos.get(key).getTipo().equals("ULONG") && AnalizadorLexico.simbolos.get(key).getUso() != null){
                    if (AnalizadorLexico.simbolos.get(key).getUso().equals("nombre_variable") || AnalizadorLexico.simbolos.get(key).getUso().equals("auxiliar") || AnalizadorLexico.simbolos.get(key).getUso().equals("nombre_parametro_formal")) {
                        key = key.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
                        archivo.write("invoke printf,ADDR _PRINT_" + key + "\n" + "invoke printf,ADDR formatoUlong, " + key + "\n" + "invoke printf, ADDR newline\n");
                    }
                }
                else if (AnalizadorLexico.simbolos.get(key).getTipo() != null && AnalizadorLexico.simbolos.get(key).getTipo().equals("DOUBLE") && AnalizadorLexico.simbolos.get(key).getUso() != null){
                    if (AnalizadorLexico.simbolos.get(key).getUso().equals("nombre_variable") || AnalizadorLexico.simbolos.get(key).getUso().equals("auxiliar") || AnalizadorLexico.simbolos.get(key).getUso().equals("nombre_parametro_formal")) {
                        key = key.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
                        archivo.write("invoke printf,ADDR _PRINT_" + key + "\n" + "invoke printf,ADDR formatoDouble, " + key + "\n" + "invoke printf, ADDR newline\n");
                    }
                }
                else if(AnalizadorLexico.simbolos.get(key).getTipo() != null && AnalizadorLexico.simbolos.get(key).getTipo().equals("INT") && AnalizadorLexico.simbolos.get(key).getUso() != null){
                     if (AnalizadorLexico.simbolos.get(key).getUso().equals("nombre_variable") || AnalizadorLexico.simbolos.get(key).getUso().equals("auxiliar") || AnalizadorLexico.simbolos.get(key).getUso().equals("nombre_parametro_formal")) {
                        key = key.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
                        archivo.write("invoke printf,ADDR _PRINT_" + key + "\n" + "invoke printf,ADDR formatoInt, " + key + "\n" + "invoke printf, ADDR newline\n");
                    }
                }
            }
            archivo.write("invoke ExitProcess, 0\nend start");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public Nodo generar_Codigo(Nodo padre_subarbol){ //Cambiar el tipo nodo por el tipo que tenga el nodo del arbol sintactico
        String resultado;
        switch(padre_subarbol.getSimbolo()){
            case "=":
                 asignacion(padre_subarbol);
                 return null;
            case "+":
                String tipo = padre_subarbol.getTipo();
                if (tipo.equals("DOUBLE")) {
                    resultado = suma_Pto_Flotante(padre_subarbol);
                    Nodo resultado2 = new Nodo(null, null, null, resultado,"DOUBLE");
                    contador_Variable_Auxiliar++;
                    AnalizadorLexico.simbolos.put(resultado,new AtributosSimbolo("DOUBLE","auxiliar",true));
                    return resultado2;
                }
                else if ((tipo.equals("ULONG"))) {
                    resultado = suma_Enteros_Ulong(padre_subarbol);
                    Nodo resultado3 = new Nodo(null, null, null, resultado,"ULONG");
                    contador_Variable_Auxiliar++;
                    AnalizadorLexico.simbolos.put(resultado,new AtributosSimbolo("ULONG","auxiliar",true));
                    return resultado3;
                }
                else {
                    resultado = suma_Enteros_Int(padre_subarbol);
                    Nodo resultado4 = new Nodo(null, null, null, resultado,"INT");
                    contador_Variable_Auxiliar++;
                    AnalizadorLexico.simbolos.put(resultado,new AtributosSimbolo("INT","auxiliar",true));
                    return resultado4;
                }
            case "-":
                String tipo1= padre_subarbol.getTipo();
                if (tipo1.equals("DOUBLE")) {
                    resultado = resta_Pto_Flotante(padre_subarbol);
                    Nodo resultado5 = new Nodo(null, null, null, resultado,"DOUBLE");
                    contador_Variable_Auxiliar++;
                    AnalizadorLexico.simbolos.put(resultado,new AtributosSimbolo("DOUBLE","auxiliar",true));
                    return resultado5;
                }
                else if ((tipo1.equals("ULONG"))) {
                    resultado = resta_Enteros_Ulong(padre_subarbol);
                    Nodo resultado6 = new Nodo(null, null, null, resultado,"ULONG");
                    contador_Variable_Auxiliar++;
                    AnalizadorLexico.simbolos.put(resultado,new AtributosSimbolo("ULONG","auxiliar",true));
                    return resultado6;
                }
                else {
                    resultado = resta_Enteros_INT(padre_subarbol);
                    Nodo resultado7 = new Nodo(null, null, null, resultado,"INT");
                    contador_Variable_Auxiliar++;
                    AnalizadorLexico.simbolos.put(resultado,new AtributosSimbolo("INT","auxiliar",true));
                    return resultado7;
                }
            case "*":
                String tipo3 = padre_subarbol.getTipo();
                if (tipo3.equals("DOUBLE")) {
                    resultado = multiplicacion_Pto_Flotante(padre_subarbol);
                    Nodo resultado8 = new Nodo(null, null, null, resultado,"DOUBLE");
                    contador_Variable_Auxiliar++;
                    AnalizadorLexico.simbolos.put(resultado,new AtributosSimbolo("DOUBLE","auxiliar",true));
                    return resultado8;
                }
                else if ((tipo3.equals("ULONG"))) {
                    resultado = multiplicacion_Enteros_Ulong(padre_subarbol);
                    Nodo resultado9 = new Nodo(null, null, null, resultado,"ULONG");
                    contador_Variable_Auxiliar++;
                    AnalizadorLexico.simbolos.put(resultado,new AtributosSimbolo("ULONG","auxiliar",true));
                    return resultado9;
                }
                else {
                    resultado = multiplicacion_Enteros_Int(padre_subarbol);
                    Nodo resultado10 = new Nodo(null, null, null, resultado,"INT");
                    contador_Variable_Auxiliar++;
                    AnalizadorLexico.simbolos.put(resultado,new AtributosSimbolo("INT","auxiliar",true));
                    return resultado10;
                }
            case "/":
                String tipo4 = padre_subarbol.getTipo();
                if (tipo4.equals("DOUBLE")) {
                    resultado = division_Pto_Flotante(padre_subarbol);
                    Nodo resultado11 = new Nodo(null, null, null, resultado,"DOUBLE");
                    contador_Variable_Auxiliar++;
                    AnalizadorLexico.simbolos.put(resultado,new AtributosSimbolo("DOUBLE","auxiliar",true));
                    return resultado11;
                }
                else if ((tipo4.equals("ULONG"))) {
                    resultado = division_Enteros_Ulong(padre_subarbol);
                    Nodo resultado12 = new Nodo(null, null, null, resultado,"ULONG");
                    contador_Variable_Auxiliar++;
                    AnalizadorLexico.simbolos.put(resultado,new AtributosSimbolo("ULONG","auxiliar",true));
                    return resultado12;
                }
                else {
                    resultado = division_Enteros_Int(padre_subarbol);
                    Nodo resultado13 = new Nodo(null, null, null, resultado,"INT");
                    contador_Variable_Auxiliar++;
                    AnalizadorLexico.simbolos.put(resultado,new AtributosSimbolo("INT","auxiliar",true));
                    return resultado13;
                }
            case "PRINT":
                AtributosSimbolo atributos = new AtributosSimbolo("PRINT", padre_subarbol.getCadenaImpresion());//le deberia pasar del nodo padre el lexema y como tipo PRINT
                AnalizadorLexico.simbolos.put("msj_"+contador_print, atributos);
                String codigo = "invoke printf, ADDR msj_"+contador_print +"\n" 
                                + "invoke printf, ADDR newline";
                contador_print++;
                codigoFinal.add(codigo);
                return null;
            case "CALL": //LLAMADO FUNCIONES
                ArrayList<ParVariableAtributo> pares_variable_atributo = padre_subarbol.getParesVariableAtributo();
                for (ParVariableAtributo par:pares_variable_atributo)
                	intercambiar_Valor_Atributo(par.getVariable(), par.getAtributo(), AnalizadorLexico.simbolos.get((par.getAtributo())).getTipo()); //si se trata de una invocacion a un metodo de clase, se copia el valor de la variable de la instancia en el atributo de la clase correspondiente (ida)
                String nombre_funcion = padre_subarbol.getNodoHijoIzquierdo().getSimbolo().replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
                String codigo22 = "CALL " + nombre_funcion;
                codigoFinal.add(codigo22);
                for (ParVariableAtributo par:pares_variable_atributo)
                	intercambiar_Valor_Atributo(par.getAtributo(), par.getVariable(), AnalizadorLexico.simbolos.get((par.getAtributo())).getTipo()); //si se trata de una invocacion a un metodo de clase, se copia el valor del atributo de la clase en la variable de la instancia correspondiente (vuelta)
                return null;
            case "PARAMETRO_REAL":
            	if (padre_subarbol.getParametroFormalAsociado() != null && padre_subarbol.getNodoHijoUnidireccional() != null) {
                    if (padre_subarbol.getNodoHijoUnidireccional().getValorConstante() == null)
                        intercambiar_Valor_Atributo(padre_subarbol.getNodoHijoUnidireccional().getSimbolo(), padre_subarbol.getParametroFormalAsociado(), AnalizadorLexico.simbolos.get(padre_subarbol.getParametroFormalAsociado()).getTipo());
                    else
                        intercambiar_Valor_Atributo(padre_subarbol.getNodoHijoUnidireccional().getValorConstante(), padre_subarbol.getParametroFormalAsociado(), AnalizadorLexico.simbolos.get(padre_subarbol.getParametroFormalAsociado()).getTipo());
                }
            	return null;
            case "THEN":
                then_if_();
                return null;
            case "ELSE":
                else_if_();
                return null; 
            case "CUERPO_FOR":
                fin_For();
                return null;
            case "PARAMETRO_FORMAL":
                return null;
            case "RETURN":
            	codigoFinal.add("ret");
            	return null;
            default: //Comparaciones
                    comparacion(padre_subarbol);
                    return null;
        }
    }
    
    private void intercambiar_Valor_Atributo(String variable1, String variable2, String tipo_variables) {
    	String codigo = "";
    	if (tipo_variables.equals("INT")) {
    		codigo = "MOV AX," + variable1 + "\n"
    		+ "MOV " + variable2 + ",AX";
    	}
    	else if (tipo_variables.equals("ULONG")) {
    		codigo = "MOV EAX," + variable1 + "\n"
    	    		+ "MOV " + variable2 + ",EAX";
    	}
    	else if (tipo_variables.equals("DOUBLE")) {
    		codigo = "FLD " + variable1 + "\n"
    		+ "FSTP " + variable2;
    	}
        codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
    	codigoFinal.add(codigo);
    }
     private String suma_Enteros_Int(Nodo nodo){
        if (nodo.getNodoHijoIzquierdo().getValorConstante() == null && nodo.getNodoHijoDerecho().getValorConstante() == null) {
            String codigo = "MOV AX," + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "ADD AX," + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "JO error_overflow_suma_int\n"
                    + "MOV @aux" + contador_Variable_Auxiliar +",AX";
            codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo);
        }
        else if(nodo.getNodoHijoIzquierdo().getValorConstante() != null && nodo.getNodoHijoDerecho().getValorConstante() == null){
            String codigo = "MOV AX," + nodo.getNodoHijoIzquierdo().getValorConstante() + "\n"
                    + "ADD AX," + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "JO error_overflow_suma_int\n"
                    + "MOV @aux" + contador_Variable_Auxiliar +",AX";
            codigo =codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo);
        }
        else if(nodo.getNodoHijoDerecho().getValorConstante() != null && nodo.getNodoHijoIzquierdo().getValorConstante() == null){
            String codigo = "MOV AX," + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "ADD AX," + nodo.getNodoHijoDerecho().getValorConstante() + "\n"
                    + "JO error_overflow_suma_int\n"
                    + "MOV @aux" + contador_Variable_Auxiliar +",AX";
            codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo);
        }
        else{
             String codigo = "MOV AX," + nodo.getNodoHijoIzquierdo().getValorConstante() + "\n"
                    + "ADD AX," + nodo.getNodoHijoDerecho().getValorConstante() + "\n"
                    + "JO error_overflow_suma_int\n"
                    + "MOV @aux" + contador_Variable_Auxiliar +",AX";
            codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo);
        }
        return "@aux"+contador_Variable_Auxiliar;
    }

    private String suma_Pto_Flotante(Nodo nodo){
       if (nodo.getNodoHijoIzquierdo().getValorConstante() == null && nodo.getNodoHijoDerecho().getValorConstante() == null) {
            String codigo = "FLD " + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "FLD " + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "FADD \n"
                    + "FSTP @aux" + contador_Variable_Auxiliar;
            codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo);
        }
        else if(nodo.getNodoHijoIzquierdo().getValorConstante() != null && nodo.getNodoHijoDerecho().getValorConstante() == null){
            AnalizadorLexico.simbolos.put("@aux"+contador_Variable_Auxiliar,new AtributosSimbolo("DOUBLE","auxiliar_constante",true,nodo.getNodoHijoIzquierdo().getValorConstante()));
            String codigo = "FLD " + "@aux"+contador_Variable_Auxiliar + "\n";
            codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo);
            contador_Variable_Auxiliar++;
            codigo = "FLD " + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "FADD \n"
                    + "FSTP @aux" + contador_Variable_Auxiliar;
            codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo);
        }
        else if(nodo.getNodoHijoDerecho().getValorConstante() != null && nodo.getNodoHijoIzquierdo().getValorConstante() == null){
            AnalizadorLexico.simbolos.put("@aux"+contador_Variable_Auxiliar,new AtributosSimbolo("DOUBLE","auxiliar_constante",true,nodo.getNodoHijoDerecho().getValorConstante()));
            String codigo = "FLD " + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "FLD " + "@aux"+contador_Variable_Auxiliar;
            codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo);
            contador_Variable_Auxiliar++;        
            codigo = "FADD \n"
                    + "FSTP @aux" + contador_Variable_Auxiliar;
            codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo);
        }
        else{
            AnalizadorLexico.simbolos.put("@aux"+contador_Variable_Auxiliar,new AtributosSimbolo("DOUBLE","auxiliar_constante",true,nodo.getNodoHijoIzquierdo().getValorConstante()));
            String codigo = "FLD " + "@aux"+contador_Variable_Auxiliar + "\n";
            codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo);
            contador_Variable_Auxiliar++;
            AnalizadorLexico.simbolos.put("@aux"+contador_Variable_Auxiliar,new AtributosSimbolo("DOUBLE","auxiliar_constante",true,nodo.getNodoHijoDerecho().getValorConstante()));
            codigo ="FLD " + "@aux"+contador_Variable_Auxiliar + "\n"
                    + "FADD \n";
            codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo);
            contador_Variable_Auxiliar++;
            codigo = "FSTP @aux" + contador_Variable_Auxiliar;
            codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo);
        }
        return "@aux" + contador_Variable_Auxiliar;
    }
    
    private String suma_Enteros_Ulong(Nodo nodo){
        if (nodo.getNodoHijoIzquierdo().getValorConstante() == null && nodo.getNodoHijoDerecho().getValorConstante() == null) {
            String codigo = "MOV EAX," + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "ADD EAX," + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "MOV @aux" + contador_Variable_Auxiliar + ",EAX";
            codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo);
        }
        else if (nodo.getNodoHijoIzquierdo().getValorConstante() != null && nodo.getNodoHijoDerecho().getValorConstante() == null){
            String codigo = "MOV EAX," + nodo.getNodoHijoIzquierdo().getValorConstante() + "\n"
                    + "ADD EAX," + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "MOV @aux" + contador_Variable_Auxiliar + ",EAX";
            codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo);
        }
        else if (nodo.getNodoHijoDerecho().getValorConstante() != null && nodo.getNodoHijoIzquierdo().getValorConstante() == null){
            String codigo = "MOV EAX," + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "ADD EAX," + nodo.getNodoHijoDerecho().getValorConstante() + "\n"
                    + "MOV @aux" + contador_Variable_Auxiliar + ",EAX";
            codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo);
        }
        else {
            String codigo = "MOV EAX," + nodo.getNodoHijoIzquierdo().getValorConstante() + "\n"
                    + "ADD EAX," + nodo.getNodoHijoDerecho().getValorConstante() + "\n"
                    + "MOV @aux" + contador_Variable_Auxiliar + ",EAX";
            codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo);
        }
        return "@aux"+contador_Variable_Auxiliar;
    }

    private String multiplicacion_Enteros_Int(Nodo nodo){
        if (nodo.getNodoHijoIzquierdo().getValorConstante() == null && nodo.getNodoHijoDerecho().getValorConstante() == null) {
            String codigo1 = "MOV AX," + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "IMUL AX," + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "MOV @aux" + contador_Variable_Auxiliar + ",AX";
            codigo1 = codigo1.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo1);
        }
        else if(nodo.getNodoHijoIzquierdo().getValorConstante() != null && nodo.getNodoHijoDerecho().getValorConstante() == null){
            String codigo1 = "MOV AX," + nodo.getNodoHijoIzquierdo().getValorConstante() + "\n"
                    + "IMUL AX," + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "MOV @aux" + contador_Variable_Auxiliar + ",AX";
            codigo1 = codigo1.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo1);
        }
        else if(nodo.getNodoHijoDerecho().getValorConstante() != null && nodo.getNodoHijoIzquierdo().getValorConstante() == null)
        {
            String codigo1 = "MOV AX," + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "IMUL AX," + nodo.getNodoHijoDerecho().getValorConstante() + "\n"
                    + "MOV @aux" + contador_Variable_Auxiliar + ",AX";
            codigo1 = codigo1.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo1);
        }
        else{
            String codigo1 = "MOV AX," + nodo.getNodoHijoIzquierdo().getValorConstante() + "\n"
                    + "IMUL AX," + nodo.getNodoHijoDerecho().getValorConstante() + "\n"
                    + "MOV @aux" + contador_Variable_Auxiliar + ",AX";
            codigo1 = codigo1.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo1);
        }
        return "@aux"+contador_Variable_Auxiliar;
    }

    private String multiplicacion_Enteros_Ulong(Nodo nodo){
        if (nodo.getNodoHijoIzquierdo().getValorConstante() == null && nodo.getNodoHijoDerecho().getValorConstante() == null) {
            String codigo1 = "MOV EAX," + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "IMUL EAX," + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "MOV @aux" + contador_Variable_Auxiliar + ",EAX";
            codigo1 = codigo1.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo1);
        }
        else if(nodo.getNodoHijoIzquierdo().getValorConstante() != null && nodo.getNodoHijoDerecho().getValorConstante() == null){
            String codigo1 = "MOV EAX," + nodo.getNodoHijoIzquierdo().getValorConstante() + "\n"
                    + "IMUL EAX," + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "MOV @aux" + contador_Variable_Auxiliar + ",EAX";
            codigo1 = codigo1.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo1);
        }
        else if(nodo.getNodoHijoDerecho().getValorConstante() != null && nodo.getNodoHijoIzquierdo().getValorConstante() == null) {
            String codigo1 = "MOV EAX," + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "IMUL EAX," + nodo.getNodoHijoDerecho().getValorConstante() + "\n"
                    + "MOV @aux" + contador_Variable_Auxiliar + ",EAX";
            codigo1 = codigo1.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");        
            codigoFinal.add(codigo1);
        }
        else {
            String codigo1 = "MOV EAX," + nodo.getNodoHijoIzquierdo().getValorConstante() + "\n"
                    + "IMUL EAX," + nodo.getNodoHijoDerecho().getValorConstante() + "\n"
                    + "MOV @aux" + contador_Variable_Auxiliar + ",EAX";
            codigo1 = codigo1.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");                    
            codigoFinal.add(codigo1);
        }
        return "@aux"+contador_Variable_Auxiliar;
    }

    private String multiplicacion_Pto_Flotante(Nodo nodo){
        String codigo = "";
        if (nodo.getNodoHijoIzquierdo().getValorConstante() == null && nodo.getNodoHijoDerecho().getValorConstante() == null) {
             codigo = "FNINIT" + "\n"
                    + "FLD " + nodo.getNodoHijoIzquierdo().getSimbolo().replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$") + "\n"
                    + "FLD " + nodo.getNodoHijoDerecho().getSimbolo().replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$") + "\n"
                    + "FMUL \n"
                    + "FSTP @aux" + contador_Variable_Auxiliar + "\n"
                    + "FLD @aux" + contador_Variable_Auxiliar +"\n"
                    + "FLD limite_positivo_superior_double\n"
                    + "FCOM\n" // comparo con limite positivo superior positivo 1.7976931348623157+308
                    + "FSTSW AX\n"
                    + "SAHF\n"
                    + "JBE error_overflow_multiplicacion_double\n"  //salto si es mayor 
                    + "FLD @aux" + contador_Variable_Auxiliar +"\n"
                    + "FLD limite_positivo_inferior_double\n"
                    + "FCOM\n" // comparo con limite positivo inferior positivo 2.2250738585072014D-308
                    + "FSTSW AX\n"
                    + "SAHF\n"
                    + "JAE chequeo_cero" + contador_Variable_Auxiliar + "\n" //salto si es menor
                    + "JMP continuacion_multiplicacion" + contador_Variable_Auxiliar +"\n"
                    + "chequeo_cero" + contador_Variable_Auxiliar + ":\n"
                    + "FLD @aux" + contador_Variable_Auxiliar +"\n"
                    + "FLDZ\n"
                    + "FCOM\n" // comparo con 0
                    + "FSTSW AX\n"
                    + "SAHF\n"
                    + "JNE chequeo_negativo" + contador_Variable_Auxiliar + "\n"
                    + "JMP continuacion_multiplicacion" + contador_Variable_Auxiliar +"\n"
                    + "chequeo_negativo" + contador_Variable_Auxiliar + ":\n"
                    + "FLD @aux" + contador_Variable_Auxiliar +"\n"
                    + "FLD limite_negativo_superior_double\n"
                    + "FCOM\n" // comparo con limite inferior superior negativo  -2.2250738585072014D-308
                    + "FSTSW AX\n"
                    + "SAHF\n"
                    + "JBE error_overflow_multiplicacion_double \n" //salto si es mayor
                    + "JMP chequeo_negativo_inferior" + contador_Variable_Auxiliar + "\n" 
                    + "chequeo_negativo_inferior" + contador_Variable_Auxiliar + ":\n"
                    + "FLD @aux" + contador_Variable_Auxiliar +"\n"
                    + "FLD limite_negativo_inferior_double\n"
                    + "FCOM\n"  //comparo con limite inferior negativo -1.7976931348623157D+308
                    + "FSTSW AX\n"
                    + "SAHF\n"
                    + "JAE error_overflow_multiplicacion_double \n" //salto si es menor
                    + "JMP continuacion_multiplicacion" + contador_Variable_Auxiliar +"\n"
                    + "continuacion_multiplicacion" + contador_Variable_Auxiliar +":";
        }
        else if(nodo.getNodoHijoIzquierdo().getValorConstante() != null && nodo.getNodoHijoDerecho().getValorConstante() == null){
            AnalizadorLexico.simbolos.put("@aux"+contador_Variable_Auxiliar,new AtributosSimbolo("DOUBLE","auxiliar_constante",true,nodo.getNodoHijoIzquierdo().getValorConstante()));
             codigo ="FNINIT" + "\n"
                +"FLD " + "@aux"+contador_Variable_Auxiliar;
             codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
             codigoFinal.add(codigo);
             contador_Variable_Auxiliar++;
             codigo = "FLD " + nodo.getNodoHijoDerecho().getSimbolo().replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$") + "\n"
                   + "FMUL \n"
                    + "FSTP @aux" + contador_Variable_Auxiliar + "\n"
                    + "FLD @aux" + contador_Variable_Auxiliar +"\n"
                    + "FLD limite_positivo_superior_double\n"
                    + "FCOM\n" // comparo con limite positivo superior positivo 1.7976931348623157+308
                    + "FSTSW AX\n"
                    + "SAHF\n"
                    + "JBE error_overflow_multiplicacion_double\n"  //salto si es mayor 
                    + "FLD @aux" + contador_Variable_Auxiliar +"\n"
                    + "FLD limite_positivo_inferior_double\n"
                    + "FCOM\n" // comparo con limite positivo inferior positivo 2.2250738585072014D-308
                    + "FSTSW AX\n"
                    + "SAHF\n"
                    + "JAE chequeo_cero" + contador_Variable_Auxiliar + "\n" //salto si es menor
                    + "JMP continuacion_multiplicacion" + contador_Variable_Auxiliar +"\n"
                    + "chequeo_cero" + contador_Variable_Auxiliar + ":\n"
                    + "FLD @aux" + contador_Variable_Auxiliar +"\n"
                    + "FLDZ\n"
                    + "FCOM\n" // comparo con 0
                    + "FSTSW AX\n"
                    + "SAHF\n"
                    + "JNE chequeo_negativo" + contador_Variable_Auxiliar + "\n"
                    + "JMP continuacion_multiplicacion" + contador_Variable_Auxiliar +"\n"
                    + "chequeo_negativo" + contador_Variable_Auxiliar + ":\n"
                    + "FLD @aux" + contador_Variable_Auxiliar +"\n"
                    + "FLD limite_negativo_superior_double\n"
                    + "FCOM\n" // comparo con limite inferior superior negativo  -2.2250738585072014D-308
                    + "FSTSW AX\n"
                    + "SAHF\n"
                    + "JBE error_overflow_multiplicacion_double \n" //salto si es mayor
                    + "JMP chequeo_negativo_inferior" + contador_Variable_Auxiliar + "\n" 
                    + "chequeo_negativo_inferior" + contador_Variable_Auxiliar + ":\n"
                    + "FLD @aux" + contador_Variable_Auxiliar +"\n"
                    + "FLD limite_negativo_inferior_double\n"
                    + "FCOM\n"  //comparo con limite inferior negativo -1.7976931348623157D+308
                    + "FSTSW AX\n"
                    + "SAHF\n"
                    + "JAE error_overflow_multiplicacion_double \n" //salto si es menor
                    + "JMP continuacion_multiplicacion" + contador_Variable_Auxiliar +"\n"
                    + "continuacion_multiplicacion" + contador_Variable_Auxiliar +":";
        }
        else if(nodo.getNodoHijoDerecho().getValorConstante() != null && nodo.getNodoHijoIzquierdo().getValorConstante() == null) {
            AnalizadorLexico.simbolos.put("@aux"+contador_Variable_Auxiliar,new AtributosSimbolo("DOUBLE","auxiliar_constante",true,nodo.getNodoHijoDerecho().getValorConstante()));
             codigo = "FNINIT" + "\n"
                    +"FLD " + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "FLD " + "@aux"+contador_Variable_Auxiliar;
             codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
             codigoFinal.add(codigo);
             contador_Variable_Auxiliar++;
             codigo = "FMUL \n"
                    + "FSTP @aux" + contador_Variable_Auxiliar + "\n"
                    + "FLD @aux" + contador_Variable_Auxiliar +"\n"
                    + "FLD limite_positivo_superior_double\n"
                    + "FCOM\n" // comparo con limite positivo superior positivo 1.7976931348623157+308
                    + "FSTSW AX\n"
                    + "SAHF\n"
                    + "JBE error_overflow_multiplicacion_double\n"  //salto si es mayor 
                    + "FLD @aux" + contador_Variable_Auxiliar +"\n"
                    + "FLD limite_positivo_inferior_double\n"
                    + "FCOM\n" // comparo con limite positivo inferior positivo 2.2250738585072014D-308
                    + "FSTSW AX\n"
                    + "SAHF\n"
                    + "JAE chequeo_cero" + contador_Variable_Auxiliar + "\n" //salto si es menor
                    + "JMP continuacion_multiplicacion" + contador_Variable_Auxiliar +"\n"
                    + "chequeo_cero" + contador_Variable_Auxiliar + ":\n"
                    + "FLD @aux" + contador_Variable_Auxiliar +"\n"
                    + "FLDZ\n"
                    + "FCOM\n" // comparo con 0
                    + "FSTSW AX\n"
                    + "SAHF\n"
                    + "JNE chequeo_negativo" + contador_Variable_Auxiliar + "\n"
                    + "JMP continuacion_multiplicacion" + contador_Variable_Auxiliar +"\n"
                    + "chequeo_negativo" + contador_Variable_Auxiliar + ":\n"
                    + "FLD @aux" + contador_Variable_Auxiliar +"\n"
                    + "FLD limite_negativo_superior_double\n"
                    + "FCOM\n" // comparo con limite inferior superior negativo  -2.2250738585072014D-308
                    + "FSTSW AX\n"
                    + "SAHF\n"
                    + "JBE error_overflow_multiplicacion_double \n" //salto si es mayor
                    + "JMP chequeo_negativo_inferior" + contador_Variable_Auxiliar + "\n" 
                    + "chequeo_negativo_inferior" + contador_Variable_Auxiliar + ":\n"
                    + "FLD @aux" + contador_Variable_Auxiliar +"\n"
                    + "FLD limite_negativo_inferior_double\n"
                    + "FCOM\n"  //comparo con limite inferior negativo -1.7976931348623157D+308
                    + "FSTSW AX\n"
                    + "SAHF\n"
                    + "JAE error_overflow_multiplicacion_double \n" //salto si es menor
                    + "JMP continuacion_multiplicacion" + contador_Variable_Auxiliar +"\n"
                    + "continuacion_multiplicacion" + contador_Variable_Auxiliar +":";
        }
        else {
            AnalizadorLexico.simbolos.put("@aux"+contador_Variable_Auxiliar,new AtributosSimbolo("DOUBLE","auxiliar_constante",true,nodo.getNodoHijoIzquierdo().getValorConstante()));
             codigo ="FNINIT" + "\n" 
                    +"FLD " + "@aux"+contador_Variable_Auxiliar;
             codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
             codigoFinal.add(codigo);
             contador_Variable_Auxiliar++;
             AnalizadorLexico.simbolos.put("@aux"+contador_Variable_Auxiliar,new AtributosSimbolo("DOUBLE","auxiliar_constante",true,nodo.getNodoHijoDerecho().getValorConstante()));
             codigo ="FLD " + "@aux"+contador_Variable_Auxiliar;
             codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
             codigoFinal.add(codigo);
             contador_Variable_Auxiliar++;
             codigo = "FMUL \n"
                    + "FSTP @aux" + contador_Variable_Auxiliar + "\n"
                    + "FLD @aux" + contador_Variable_Auxiliar +"\n"
                    + "FLD limite_positivo_superior_double\n"
                    + "FCOM\n" // comparo con limite positivo superior positivo 1.7976931348623157+308
                    + "FSTSW AX\n"
                    + "SAHF\n"
                    + "JBE error_overflow_multiplicacion_double\n"  //salto si es mayor 
                    + "FLD @aux" + contador_Variable_Auxiliar +"\n"
                    + "FLD limite_positivo_inferior_double\n"
                    + "FCOM\n" // comparo con limite positivo inferior positivo 2.2250738585072014D-308
                    + "FSTSW AX\n"
                    + "SAHF\n"
                    + "JAE chequeo_cero" + contador_Variable_Auxiliar + "\n" //salto si es menor
                    + "JMP continuacion_multiplicacion" + contador_Variable_Auxiliar +"\n"
                    + "chequeo_cero" + contador_Variable_Auxiliar + ":\n"
                    + "FLD @aux" + contador_Variable_Auxiliar +"\n"
                    + "FLDZ\n"
                    + "FCOM\n" // comparo con 0
                    + "FSTSW AX\n"
                    + "SAHF\n"
                    + "JNE chequeo_negativo" + contador_Variable_Auxiliar + "\n"
                    + "JMP continuacion_multiplicacion" + contador_Variable_Auxiliar +"\n"
                    + "chequeo_negativo" + contador_Variable_Auxiliar + ":\n"
                    + "FLD @aux" + contador_Variable_Auxiliar +"\n"
                    + "FLD limite_negativo_superior_double\n"
                    + "FCOM\n" // comparo con limite inferior superior negativo  -2.2250738585072014D-308
                    + "FSTSW AX\n"
                    + "SAHF\n"
                    + "JBE error_overflow_multiplicacion_double \n" //salto si es mayor
                    + "JMP chequeo_negativo_inferior" + contador_Variable_Auxiliar + "\n" 
                    + "chequeo_negativo_inferior" + contador_Variable_Auxiliar + ":\n"
                    + "FLD @aux" + contador_Variable_Auxiliar +"\n"
                    + "FLD limite_negativo_inferior_double\n"
                    + "FCOM\n"  //comparo con limite inferior negativo -1.7976931348623157D+308
                    + "FSTSW AX\n"
                    + "SAHF\n"
                    + "JAE error_overflow_multiplicacion_double \n" //salto si es menor
                    + "JMP continuacion_multiplicacion" + contador_Variable_Auxiliar +"\n"
                    + "continuacion_multiplicacion" + contador_Variable_Auxiliar +":";
        }
        // agrego la variable auxiliar a la tabla de simbolos
        codigoFinal.add(codigo); // CHEQUEO QUE NO HAYA OVERFLOW EN LA MULTIPLICACION
        return "@aux"+contador_Variable_Auxiliar;
    }

    private String division_Enteros_Int(Nodo nodo){
        if (nodo.getNodoHijoIzquierdo().getValorConstante() == null && nodo.getNodoHijoDerecho().getValorConstante() == null) {
            String codigo1 = "MOV AX," + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "CWD \n"
                    + "IDIV " + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "MOV @aux" + contador_Variable_Auxiliar + ",AX";
            codigo1 = codigo1.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo1);
        }
        else if(nodo.getNodoHijoIzquierdo().getValorConstante() != null && nodo.getNodoHijoDerecho().getValorConstante() == null){
            String codigo1 = "MOV AX," + nodo.getNodoHijoIzquierdo().getValorConstante() + "\n"
                    + "CWD \n"
                    + "IDIV " + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "MOV @aux" + contador_Variable_Auxiliar + ",AX";
            codigo1 = codigo1.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo1);
        }
        else if(nodo.getNodoHijoDerecho().getValorConstante() != null && nodo.getNodoHijoIzquierdo().getValorConstante() == null){
            String codigo1 = "MOV AX," + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "CWD \n"
                    + "MOV BX," + nodo.getNodoHijoDerecho().getValorConstante() + "\n"
                    + "IDIV BX" + "\n"
                    + "MOV @aux" + contador_Variable_Auxiliar + ",AX";
            codigo1 = codigo1.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo1);
        }
        else{
             String codigo1 = "MOV AX," + nodo.getNodoHijoIzquierdo().getValorConstante() + "\n"
                    + "CWD \n"
                    + "MOV BX," + nodo.getNodoHijoDerecho().getValorConstante() + "\n"
                    + "IDIV BX" + "\n"
                    + "MOV @aux" + contador_Variable_Auxiliar + ",AX";
            codigo1 = codigo1.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo1);
        }
        return "@aux"+contador_Variable_Auxiliar;
    }
    private String division_Enteros_Ulong(Nodo nodo){
        if (nodo.getNodoHijoIzquierdo().getValorConstante() == null && nodo.getNodoHijoDerecho().getValorConstante() == null) {
            String codigo1 = "MOV EAX," + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "CDQ \n"
                    + "IDIV " + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "MOV @aux" + contador_Variable_Auxiliar + ",EAX";
            codigo1 = codigo1.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo1);
        }
        else if(nodo.getNodoHijoIzquierdo().getValorConstante() != null && nodo.getNodoHijoDerecho().getValorConstante() == null){
            String codigo1 = "MOV EAX," + nodo.getNodoHijoIzquierdo().getValorConstante() + "\n"
                    + "CDQ \n"
                    + "IDIV " + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "MOV @aux" + contador_Variable_Auxiliar + ",EAX";
            codigo1 = codigo1.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo1);
        }
        else if(nodo.getNodoHijoDerecho().getValorConstante() != null && nodo.getNodoHijoIzquierdo().getValorConstante() == null){
            String codigo1 = "MOV EAX," + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "CDQ \n"
                    + "MOV EBX," + nodo.getNodoHijoDerecho().getValorConstante() + "\n"
                    + "IDIV EBX" + "\n"
                    + "MOV @aux" + contador_Variable_Auxiliar + ",EAX";
            codigo1 = codigo1.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo1);
        }
        else{
             String codigo1 = "MOV EAX," + nodo.getNodoHijoIzquierdo().getValorConstante() + "\n"
                    + "CDQ \n"
                    + "MOV EBX," + nodo.getNodoHijoDerecho().getValorConstante() + "\n"
                    + "IDIV EBX" + "\n"
                    + "MOV @aux" + contador_Variable_Auxiliar + ",EAX";
            codigo1 =codigo1.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo1);
        }
        return "@aux"+contador_Variable_Auxiliar;
    }
    private String division_Pto_Flotante(Nodo nodo){
        if (nodo.getNodoHijoIzquierdo().getValorConstante() == null && nodo.getNodoHijoDerecho().getValorConstante() == null) {
            String codigo = "FLD " + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "FLD " + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "FDIV \n"
                    + "FSTP @aux" + contador_Variable_Auxiliar;
            codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo);
        }
        else if(nodo.getNodoHijoIzquierdo().getValorConstante() != null && nodo.getNodoHijoDerecho().getValorConstante() == null){
            AnalizadorLexico.simbolos.put("@aux"+contador_Variable_Auxiliar,new AtributosSimbolo("DOUBLE","auxiliar_constante",true,nodo.getNodoHijoIzquierdo().getValorConstante()));
            String codigo = "FLD " + "@aux"+contador_Variable_Auxiliar + "\n";
            codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo);
            contador_Variable_Auxiliar++;
            codigo = "FLD " + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "FDIV \n"
                    + "FSTP @aux" + contador_Variable_Auxiliar;
            codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo);
        }
        else if(nodo.getNodoHijoDerecho().getValorConstante() != null && nodo.getNodoHijoIzquierdo().getValorConstante() == null){
            AnalizadorLexico.simbolos.put("@aux"+contador_Variable_Auxiliar,new AtributosSimbolo("DOUBLE","auxiliar_constante",true,nodo.getNodoHijoDerecho().getValorConstante()));
            String codigo = "FLD " + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "FLD " + "@aux"+contador_Variable_Auxiliar + "\n"
                    + "FDIV";
            codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo);
            contador_Variable_Auxiliar++;    
            codigo = "FSTP @aux" + contador_Variable_Auxiliar;
            codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo);
        }
        else{
            AnalizadorLexico.simbolos.put("@aux"+contador_Variable_Auxiliar,new AtributosSimbolo("DOUBLE","auxiliar_constante",true,nodo.getNodoHijoIzquierdo().getValorConstante()));
            String codigo = "FLD " + "@aux"+contador_Variable_Auxiliar + "\n";
            codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo);
            contador_Variable_Auxiliar++;
            AnalizadorLexico.simbolos.put("@aux"+contador_Variable_Auxiliar,new AtributosSimbolo("DOUBLE","auxiliar_constante",true,nodo.getNodoHijoDerecho().getValorConstante()));
            codigo ="FLD " + "@aux"+contador_Variable_Auxiliar + "\n"
                    + "FDIV \n";
            codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo);
            contador_Variable_Auxiliar++;
            codigo = "FSTP @aux" + contador_Variable_Auxiliar;
            codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo);
        }
        return "@aux" + contador_Variable_Auxiliar;
    }

    private String resta_Enteros_INT(Nodo nodo){
        if (nodo.getNodoHijoIzquierdo().getValorConstante() == null && nodo.getNodoHijoDerecho().getValorConstante() == null) {
            String codigo = "MOV AX," + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "SUB AX," + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "MOV @aux" + contador_Variable_Auxiliar + ",AX";
            codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo);
        }
        else if(nodo.getNodoHijoIzquierdo().getValorConstante() != null && nodo.getNodoHijoDerecho().getValorConstante() == null){
            String codigo = "MOV AX," + nodo.getNodoHijoIzquierdo().getValorConstante() + "\n"
                    + "SUB AX," + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "MOV @aux" + contador_Variable_Auxiliar + ",AX";
            codigo =codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo);
        }
        else if(nodo.getNodoHijoDerecho().getValorConstante() != null && nodo.getNodoHijoIzquierdo().getValorConstante() == null){
            String codigo = "MOV AX," + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "SUB AX," + nodo.getNodoHijoDerecho().getValorConstante() + "\n"
                    + "MOV @aux" + contador_Variable_Auxiliar + ",AX";
            codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo);
        }
        else{
             String codigo = "MOV AX," + nodo.getNodoHijoIzquierdo().getValorConstante() + "\n"
                    + "SUB AX," + nodo.getNodoHijoDerecho().getValorConstante() + "\n"
                    + "MOV @aux" + contador_Variable_Auxiliar +",AX";
            codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo);
        }
        return "@aux"+contador_Variable_Auxiliar;
    }
    private String resta_Enteros_Ulong(Nodo nodo){
        if (nodo.getNodoHijoIzquierdo().getValorConstante() == null && nodo.getNodoHijoDerecho().getValorConstante() == null) {
            String codigo = "MOV EAX," + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "SUB EAX," + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "JS error_rdo_negativo_resta_ulong \n" //CONTROLA QUE NO HAYA DADO NEGATIVO
                    + "MOV @aux" + contador_Variable_Auxiliar + ",EAX";
            codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo);
        }
        else if(nodo.getNodoHijoIzquierdo().getValorConstante() != null && nodo.getNodoHijoDerecho().getValorConstante() == null){
            String codigo = "MOV EAX," + nodo.getNodoHijoIzquierdo().getValorConstante() + "\n"
                    + "SUB EAX," + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "JS error_rdo_negativo_resta_ulong \n" //CONTROLA QUE NO HAYA DADO NEGATIVO
                    + "MOV @aux" + contador_Variable_Auxiliar + ",EAX";
            codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo);
        }
        else if(nodo.getNodoHijoDerecho().getValorConstante() != null && nodo.getNodoHijoIzquierdo().getValorConstante() == null){
            String codigo = "MOV EAX," + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "SUB EAX," + nodo.getNodoHijoDerecho().getValorConstante() + "\n"
                    + "JS error_rdo_negativo_resta_ulong \n" //CONTROLA QUE NO HAYA DADO NEGATIVO
                    + "MOV @aux" + contador_Variable_Auxiliar + ",EAX";
            codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo);
        }
        else {
             String codigo = "MOV EAX," + nodo.getNodoHijoIzquierdo().getValorConstante() + "\n"
                    + "SUB EAX," + nodo.getNodoHijoDerecho().getValorConstante() + "\n"
                    + "JS error_rdo_negativo_resta_ulong \n" //CONTROLA QUE NO HAYA DADO NEGATIVO
                    + "MOV @aux" + contador_Variable_Auxiliar + ",EAX";
            codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo);
        }
        return "@aux"+contador_Variable_Auxiliar;
    }

    private String resta_Pto_Flotante(Nodo nodo) {
        if (nodo.getNodoHijoIzquierdo().getValorConstante() == null && nodo.getNodoHijoDerecho().getValorConstante() == null) {
            String codigo = "FLD " + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "FLD " + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "FSUB \n"
                    + "FSTP @aux" + contador_Variable_Auxiliar;
            codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo);
        }
        else if(nodo.getNodoHijoIzquierdo().getValorConstante() != null && nodo.getNodoHijoDerecho().getValorConstante() == null){
            AnalizadorLexico.simbolos.put("@aux"+contador_Variable_Auxiliar,new AtributosSimbolo("DOUBLE","auxiliar_constante",true,nodo.getNodoHijoIzquierdo().getValorConstante()));
            String codigo = "FLD " + "@aux"+contador_Variable_Auxiliar + "\n";
            codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo);
            contador_Variable_Auxiliar++;
            codigo = "FLD " + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "FSUB \n"
                    + "FSTP @aux" + contador_Variable_Auxiliar;
            codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo);
        }
        else if(nodo.getNodoHijoDerecho().getValorConstante() != null && nodo.getNodoHijoIzquierdo().getValorConstante() == null){
            AnalizadorLexico.simbolos.put("@aux"+contador_Variable_Auxiliar,new AtributosSimbolo("DOUBLE","auxiliar_constante",true,nodo.getNodoHijoDerecho().getValorConstante()));
            String codigo = "FLD " + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "FLD " + "@aux"+contador_Variable_Auxiliar + "\n"
                    + "FSUB";
            codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo);
            contador_Variable_Auxiliar++;
            codigo = "FSTP @aux" + contador_Variable_Auxiliar;
            codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo);
        }
        else{
            AnalizadorLexico.simbolos.put("@aux"+contador_Variable_Auxiliar,new AtributosSimbolo("DOUBLE","auxiliar_constante",true,nodo.getNodoHijoIzquierdo().getValorConstante()));
            String codigo = "FLD " + "@aux"+contador_Variable_Auxiliar + "\n";
            codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo);
            contador_Variable_Auxiliar++;
            AnalizadorLexico.simbolos.put("@aux"+contador_Variable_Auxiliar,new AtributosSimbolo("DOUBLE","auxiliar_constante",true,nodo.getNodoHijoDerecho().getValorConstante()));
            codigo ="FLD " + "@aux"+contador_Variable_Auxiliar + "\n"
                    + "FSUB \n";
            codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo);
            contador_Variable_Auxiliar++;
            codigo = "FSTP @aux" + contador_Variable_Auxiliar;
            codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
            codigoFinal.add(codigo);
        }
        return "@aux" + contador_Variable_Auxiliar;
    }
    private String salto(){
        String codigo = "JMP label" + contador_Label;
        codigoFinal.add(codigo);
        pila.add("label"+contador_Label+":"); //apilo en el tope de la pila
        contador_Label++;
        return "";
    }

    // SALTOS CONDICIONALES ARITMETICOS SIGNADO (NUMEROS POSITVOS , NEGATIVOS Y CERO)
    private String salto_Menor_Aritmetico_Signado(int nrolabel){
        String codigo = "JLE label" + nrolabel;
        codigoFinal.add(codigo);
        pila.add("label"+nrolabel+":"); //apilo en el tope de la pila
        contador_Label++;
        return "";
    }

    private String salto_Menor_Igual_Aritmetico_Signado(int nrolabel){
        String codigo = "JLE label" + nrolabel;
        codigoFinal.add(codigo);
        pila.add("label"+nrolabel+":"); //apilo en el tope de la pila
        contador_Label++;
        return "";
    }
    private String salto_Igual_Aritmetico_Signado(int nrolabel){
        String codigo = "JE label" + nrolabel;
        codigoFinal.add(codigo);
        pila.add("label"+nrolabel+":"); //apilo en el tope de la pila
        contador_Label++;
        return "";
    }
    private String salto_Distinto_Aritmetico_Signado(int nrolabel){
        String codigo = "JNE label" + nrolabel;
        codigoFinal.add(codigo);
        pila.add("label"+nrolabel+":"); //apilo en el tope de la pila
        contador_Label++;
        return "";
    }
    private String salto_Mayor_Igual_Aritmetico_Signado(int nrolabel){
        String codigo = "JGE label" + nrolabel;
        codigoFinal.add(codigo);
        pila.add("label"+nrolabel+":"); //apilo en el tope de la pila
        contador_Label++;
        return "";
    }
    private String salto_Mayor_Aritmetico_Signado(int nrolabel){
        String codigo = "JG label" + nrolabel;
        codigoFinal.add(codigo);
        pila.add("label"+nrolabel+":"); //apilo en el tope de la pila
        contador_Label++;
        return "";
    }
    // SALTOS CONDICIONALES ARITMETICOS SIN SIGNO (NUMEROS POSITIVOS Y CERO)
    private String salto_Menor_Aritmetico_Sin_Signo(int nrolabel){
        String codigo = "JB label" + nrolabel;
        codigoFinal.add(codigo);
        pila.add("label"+nrolabel+":"); //apilo en el tope de la pila
        contador_Label++;
        return "";
    }
    private String salto_Menor_Igual_Aritmetico_Sin_Signo(int nrolabel){
        String codigo = "JBE label" + nrolabel;
        codigoFinal.add(codigo);
        pila.add("label"+nrolabel+":"); //apilo en el tope de la pila
        contador_Label++;
        return "";
    }
    private String salto_Igual_Aritmetico_Sin_Signo(int nrolabel){
        String codigo = "JE label" + nrolabel;
        codigoFinal.add(codigo);
        pila.add("label"+nrolabel+":"); //apilo en el tope de la pila
        contador_Label++;
        return "";
    }
    private String salto_Distinto_Aritmetico_Sin_Signo(int nrolabel){
        String codigo = "JNE label" + nrolabel;
        codigoFinal.add(codigo);
        pila.add("label"+nrolabel+":"); //apilo en el tope de la pila
        contador_Label++;
        return "";
    }
    private String salto_Mayor_Aritmetico_Sin_Signo(int nrolabel){
        String codigo = "JA label" + nrolabel;
        codigoFinal.add(codigo);
        pila.add("label"+nrolabel+":"); //apilo en el tope de la pila
        contador_Label++;
        return "";
    }
    private String salto_Mayor_Igual_Aritmetico_Sin_Signo(int nrolabel){
        String codigo = "JAE label" + nrolabel;
        codigoFinal.add(codigo);
        pila.add("label"+nrolabel+":"); //apilo en el tope de la pila
        contador_Label++;
        return "";
    }

    private String asignacion(Nodo nodo){
        String codigo;
        if(nodo.getNodoHijoDerecho().getValorConstante() == null) { //chequea que no sea una constante
            if (nodo.getNodoHijoDerecho().getTipo().equals("INT")) {
                codigo = "MOV AX," + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                        + "MOV " + nodo.getNodoHijoIzquierdo().getSimbolo() + ",AX";
                codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
                codigoFinal.add(codigo);
            }
            else if (nodo.getNodoHijoDerecho().getTipo().equals("ULONG")) {
                codigo = "MOV EAX," + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                        + "MOV " + nodo.getNodoHijoIzquierdo().getSimbolo() + ",EAX";
                codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
                codigoFinal.add(codigo);
            }
            else {
                codigo = "FNINIT\n"
                    + "FLD " + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    +"FSTP " + nodo.getNodoHijoIzquierdo().getSimbolo();
                codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
                codigoFinal.add(codigo);
            }
        }
        else {
            if(nodo.getNodoHijoDerecho().getTipo() != null && !nodo.getNodoHijoDerecho().getTipo().equals("DOUBLE")){
                codigo = "MOV " + nodo.getNodoHijoIzquierdo().getSimbolo() + "," + nodo.getNodoHijoDerecho().getValorConstante();
                codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
                codigoFinal.add(codigo);
            }
            else if (nodo.getNodoHijoDerecho().getTipo() != null){
                AnalizadorLexico.simbolos.put("@aux"+contador_Variable_Auxiliar,new AtributosSimbolo("DOUBLE","auxiliar_constante",true,nodo.getNodoHijoDerecho().getValorConstante()));
                codigo = "FNINIT\n"
                    + "FLD " + "@aux"+contador_Variable_Auxiliar + "\n"
                    +"FSTP " + nodo.getNodoHijoIzquierdo().getSimbolo();
                codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
                codigoFinal.add(codigo);
                contador_Variable_Auxiliar++;
            } 
        }
        return "";
    }

    private String comparacion(Nodo nodo){
        String codigo;
        if (es_condicion_for) {
            codigo = "label" + contador_Label + ":";
            codigoFinal.add(codigo);
            pila.add(codigo);
            contador_Label++;
            es_condicion_for = false;
        }
        ultimo_signo_comparacion = nodo.getSimbolo();
        if (nodo.getNodoHijoIzquierdo().getValorConstante() == null && nodo.getNodoHijoDerecho().getValorConstante() == null) {
            if (nodo.getTipo().equals("INT")) {
                codigo = "MOV AX," + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                        + "CMP AX," + nodo.getNodoHijoDerecho().getSimbolo();
                codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
                codigoFinal.add(codigo);
                condicion_Sentencia_Control_Signado();
            } else if (nodo.getTipo().equals("ULONG")) {
                codigo = "MOV EAX," + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                        + "CMP EAX," + nodo.getNodoHijoDerecho().getSimbolo();
                codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
                codigoFinal.add(codigo);
                condicion_Sentencia_Control_Sin_Signo();
            } else {
                codigo = "FNINIT \n" 
                        +"FLD " + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                        + "FCOM " + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                        + "FSTSW AX" + "\n" ///preguntar si el label del for deberia estar en la linea de abajo
                        + "SAHF";
                codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
                codigoFinal.add(codigo);
                condicion_Sentencia_Control_Sin_Signo();
            }
        }
        else if(nodo.getNodoHijoIzquierdo().getValorConstante() != null && nodo.getNodoHijoDerecho().getValorConstante() == null){
            if (nodo.getTipo().equals("INT")) {
                codigo = "MOV AX," + nodo.getNodoHijoIzquierdo().getValorConstante() + "\n"
                        + "CMP AX," + nodo.getNodoHijoDerecho().getSimbolo();
                codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
                codigoFinal.add(codigo);
                condicion_Sentencia_Control_Signado();
            } else if (nodo.getTipo().equals("ULONG")) {
                codigo = "MOV EAX," + nodo.getNodoHijoIzquierdo().getValorConstante() + "\n"
                        + "CMP EAX," + nodo.getNodoHijoDerecho().getSimbolo();
                codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
                codigoFinal.add(codigo);
                condicion_Sentencia_Control_Sin_Signo();
            } else {
                AnalizadorLexico.simbolos.put("@aux"+contador_Variable_Auxiliar,new AtributosSimbolo("DOUBLE","auxiliar_constante",true,nodo.getNodoHijoIzquierdo().getValorConstante()));
                codigo = "FNINIT \n" 
                        +"FLD " + "@aux"+contador_Variable_Auxiliar + "\n"
                        + "FCOM " + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                        + "FSTSW AX" + "\n" ///preguntar si el label del for deberia estar en la linea de abajo
                        + "SAHF";
                codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
                codigoFinal.add(codigo);
                condicion_Sentencia_Control_Sin_Signo();
                contador_Variable_Auxiliar++;
            }
        }
        else if(nodo.getNodoHijoDerecho().getValorConstante() != null && nodo.getNodoHijoIzquierdo().getValorConstante() == null) {
            if (nodo.getTipo().equals("INT")) {
                codigo = "MOV AX," + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                        + "CMP AX," + nodo.getNodoHijoDerecho().getValorConstante();
                codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
                codigoFinal.add(codigo);
                condicion_Sentencia_Control_Signado();
            } else if (nodo.getTipo().equals("ULONG")) {
                codigo = "MOV EAX," + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                        + "CMP EAX," + nodo.getNodoHijoDerecho().getValorConstante();
                codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
                codigoFinal.add(codigo);
                condicion_Sentencia_Control_Sin_Signo();
            } else {
                AnalizadorLexico.simbolos.put("@aux"+contador_Variable_Auxiliar,new AtributosSimbolo("DOUBLE","auxiliar_constante",true,nodo.getNodoHijoDerecho().getValorConstante()));
                codigo = "FNINIT \n" 
                        +"FLD " + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                        + "FCOM " + "@aux"+contador_Variable_Auxiliar + "\n"
                        + "FSTSW AX" + "\n" ///preguntar si el label del for deberia estar en la linea de abajo
                        + "SAHF";
                codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
                codigoFinal.add(codigo);
                condicion_Sentencia_Control_Sin_Signo();
                contador_Variable_Auxiliar++;
            }
        }
        else {
              if (nodo.getTipo().equals("INT")) {
                codigo = "MOV AX," + nodo.getNodoHijoIzquierdo().getValorConstante() + "\n"
                        + "CMP AX," + nodo.getNodoHijoDerecho().getValorConstante();
                codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
                codigoFinal.add(codigo);
                condicion_Sentencia_Control_Signado();
            } else if (nodo.getTipo().equals("ULONG")) {
                codigo = "MOV EAX," + nodo.getNodoHijoIzquierdo().getValorConstante() + "\n"
                        + "CMP EAX," + nodo.getNodoHijoDerecho().getValorConstante();
                codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
                codigoFinal.add(codigo);
                condicion_Sentencia_Control_Sin_Signo();
            } else {
                AnalizadorLexico.simbolos.put("@aux"+contador_Variable_Auxiliar,new AtributosSimbolo("DOUBLE","auxiliar_constante",true,nodo.getNodoHijoIzquierdo().getValorConstante()));
                codigo ="FNINIT \n" 
                    + "FLD " + "@aux"+contador_Variable_Auxiliar;
                codigoFinal.add(codigo);
                contador_Variable_Auxiliar++;
                AnalizadorLexico.simbolos.put("@aux"+contador_Variable_Auxiliar,new AtributosSimbolo("DOUBLE","auxiliar_constante",true,nodo.getNodoHijoDerecho().getValorConstante()));
                codigo = "FCOM " + "@aux"+contador_Variable_Auxiliar + "\n"
                        + "FSTSW AX" + "\n" ///preguntar si el label del for deberia estar en la linea de abajo
                        + "SAHF";
                codigo = codigo.replace(":","_").replace("(", "C").replace(")", "C").replace("[", "I").replace("]", "I").replace("<", "Z").replace(">", "Z").replace(".", "$");
                codigoFinal.add(codigo);
                condicion_Sentencia_Control_Sin_Signo();
                contador_Variable_Auxiliar++;
            }
        }
        
        return "";
    }

    private String condicion_Sentencia_Control_Signado (){
        switch(ultimo_signo_comparacion){
            case "<":
                salto_Mayor_Igual_Aritmetico_Signado(contador_Label);
                break;
            case "<=":
                salto_Mayor_Aritmetico_Signado(contador_Label);
                break;
            case ">":
                salto_Menor_Igual_Aritmetico_Signado(contador_Label);
                break;
            case ">=":
                salto_Menor_Aritmetico_Signado(contador_Label);
                break;
            case "!!":
                salto_Igual_Aritmetico_Signado(contador_Label);
                break;
            case "==":
                salto_Distinto_Aritmetico_Signado(contador_Label);
        }
        return "";
    }

    private String condicion_Sentencia_Control_Sin_Signo (){
        switch(ultimo_signo_comparacion){
            case "<":
                salto_Mayor_Igual_Aritmetico_Sin_Signo(contador_Label);
                break;
            case "<=":
                salto_Mayor_Aritmetico_Sin_Signo(contador_Label);
                break;
            case ">":
                salto_Menor_Igual_Aritmetico_Sin_Signo(contador_Label);
                break;
            case ">=":
                salto_Menor_Aritmetico_Sin_Signo(contador_Label);
                break;
            case "==":
                salto_Distinto_Aritmetico_Sin_Signo(contador_Label);
                break;
            case "!!":
                salto_Igual_Aritmetico_Sin_Signo(contador_Label);
        }
        return "";
    }


    private void then_if_(){
        String codigo;
        int cantidad_else_conectados = tiene_else.size();
        if(cantidad_else_conectados == 0 || !tiene_else.get(cantidad_else_conectados-1)) {
            codigo = pila.get(pila.size() - 1);
            codigoFinal.add(codigo);
            pila.remove(pila.size() - 1);
        }
        else {
            codigo = salto();
            codigoFinal.add(codigo);
            codigo = pila.get(pila.size() - 2);
            codigoFinal.add(codigo);
            pila.remove(pila.size() - 2);
        }
        tiene_else.remove(cantidad_else_conectados-1);
    }

    private void else_if_(){
        String codigo = pila.get(pila.size()-1);
        codigoFinal.add(codigo);
        pila.remove(pila.size()-1);
    }
    private void fin_For(){
        String label_fin_for = pila.get(pila.size()-2).replace(":","");
        String codigo = "JMP " + label_fin_for;
        codigoFinal.add(codigo);
        codigo = pila.get(pila.size()-1);
        codigoFinal.add(codigo);
        pila.remove(pila.size()-1);
        pila.remove(pila.size()-1);
    }

}





