package Assembler;

import analizadorSintactico.analizadorLexico.AnalizadorLexico;
import analizadorSintactico.analizadorLexico.AtributosSimbolo;
import analizadorSintactico.generadorCodigoIntermedio.Nodo;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class GeneradorAssembler {
    private boolean tiene_else = false;
    private boolean es_for = false;
    private boolean es_condicion_for = false;
    private int contador_print = 0;
    private String ultimo_signo_comparacion;
    private int contador_Label = 1; // Utilizado para que no se repitan los label dentro del programa
    private ArrayList<String> pila = new ArrayList<>(); //Utilizado para apilar los label de las sentencias de control
    private int contador_Variable_Auxiliar = 1; // Utilizado para que no haya variables auxiliares con el mismo nombre
    private ArrayList<String> codigoFinal = new ArrayList<>();

    private String encabezado;


    public void recorrer_y_Generar_Codigo(Nodo nodo){
        //
        try (FileWriter archivo = new FileWriter("./codigo_Assembler.asm")) {
            //AGREGAR A TABLA DE SIMBOLOS LOS 3 MESSAGEBOX CON EL MENSAJE DE ERROR CORRESPONDIENTE (OVERFLOW ENTEROS,OVERFLOW PRODUCTO DOUBLE, RESULTADO NEGATIVO RESTA ULONG)
            encabezado = ".386\n" +
                    ".model flat, stdcall\n" +
                    "option casemap :none\n" +
                    "include \\masm32\\include\\windows.inc\n" +
                    "include \\masm32\\include\\kernel32.inc\n" +
                    "include \\masm32\\include\\user32.inc\n" +
                    "includelib \\masm32\\lib\\kernel32.lib\n" +
                    "includelib \\masm32\\lib\\user32.lib\n" +
                    ".data\n";
            archivo.write(encabezado);
            //RECORRIDO TABLA DE SIMBOLOS
            //dw 16 bits
            //dd 32 bits
            //dq 64 bits
            //db Strings
            for(String key : AnalizadorLexico.simbolos.keySet()){
                if (AnalizadorLexico.simbolos.get(key).getTipo() != null && AnalizadorLexico.simbolos.get(key).getTipo().equals("ULONG")){
                    archivo.write(key + " dd ?"+ System.lineSeparator());
                }
                else if (AnalizadorLexico.simbolos.get(key).getTipo() != null && AnalizadorLexico.simbolos.get(key).getTipo().equals("DOUBLE")){
                    archivo.write(key + " dq ?"+ System.lineSeparator());
                }
                else if(AnalizadorLexico.simbolos.get(key).getTipo() != null && AnalizadorLexico.simbolos.get(key).getTipo().equals("INT")){
                    archivo.write(key + " dw ?"+ System.lineSeparator());
                }
                else if(AnalizadorLexico.simbolos.get(key).getTipo() != null && AnalizadorLexico.simbolos.get(key).getTipo().equals("PRINT")){
                    archivo.write(key + " db \""+ AnalizadorLexico.simbolos.get(key).getCadenaCaracteresPrint() + "\", 0"+ System.lineSeparator());
                }
            }
            archivo.write(".code" + System.lineSeparator());
            //RECORRIDO ARBOL DE FUNCIONES
            archivo.write("start:" + System.lineSeparator());
            for (String linea : codigoFinal) {
                archivo.write(linea + System.lineSeparator());
            }
            //LOGICA CONTROL ERRORES
            String error_overflow_suma_int = "error_overflow_suma_int:\ninvoke MessageBox, NULL, addr msj_error_overflow_suma_int, addr msj_error_overflow_suma_int, MB_OK\nJMP fin\n";
            String error_overflow_multiplicacion_double = "error_overflow_multiplicacion_double:\ninvoke MessageBox, NULL, addr msj_error_overflow_multiplicacion_double, addr msj_error_overflow_multiplicacion_double, MB_OK\nJMP fin\n";
            String error_rdo_negativo_resta_ulong = "error_rdo_negativo_resta_ulong:\ninvoke MessageBox, NULL, addr msj_error_rdo_negativo_resta_ulong, addr msj_error_rdo_negativo_resta_ulong, MB_OK\nJMP fin\n";
            archivo.write(error_rdo_negativo_resta_ulong);
            archivo.write(error_overflow_multiplicacion_double);
            archivo.write(error_overflow_suma_int);
            archivo.write("fin:\ninvoke ExitProcess, 0\nendstart");
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
                    Nodo resultado2 = new Nodo(null, null, null, resultado);
                    contador_Variable_Auxiliar++;
                    return resultado2;
                }
                else if ((tipo.equals("ULONG"))) {
                    resultado = suma_Enteros_Ulong(padre_subarbol);
                    Nodo resultado3 = new Nodo(null, null, null, resultado);
                    contador_Variable_Auxiliar++;
                    return resultado3;
                }
                else {
                    resultado = suma_Enteros_Int(padre_subarbol);
                    Nodo resultado4 = new Nodo(null, null, null, resultado);
                    contador_Variable_Auxiliar++;
                    return resultado4;
                }
            case "-":
                String tipo1= padre_subarbol.getTipo();
                if (tipo1.equals("DOUBLE")) {
                    resultado = resta_Pto_Flotante(padre_subarbol);
                    Nodo resultado5 = new Nodo(null, null, null, resultado);
                    contador_Variable_Auxiliar++;
                    return resultado5;
                }
                else if ((tipo1.equals("ULONG"))) {
                    resultado = resta_Enteros_Ulong(padre_subarbol);
                    Nodo resultado6 = new Nodo(null, null, null, resultado);
                    contador_Variable_Auxiliar++;
                    return resultado6;
                }
                else {
                    resultado = resta_Enteros_INT(padre_subarbol);
                    Nodo resultado7 = new Nodo(null, null, null, resultado);
                    contador_Variable_Auxiliar++;
                    return resultado7;
                }
            case "*":
                String tipo3 = padre_subarbol.getTipo();
                if (tipo3.equals("DOUBLE")) {
                    resultado = multiplicacion_Pto_Flotante(padre_subarbol);
                    Nodo resultado8 = new Nodo(null, null, null, resultado);
                    contador_Variable_Auxiliar++;
                    return resultado8;
                }
                else if ((tipo3.equals("ULONG"))) {
                    resultado = multiplicacion_Enteros_Ulong(padre_subarbol);
                    Nodo resultado9 = new Nodo(null, null, null, resultado);
                    contador_Variable_Auxiliar++;
                    return resultado9;
                }
                else {
                    resultado = multiplicacion_Enteros_Int(padre_subarbol);
                    Nodo resultado10 = new Nodo(null, null, null, resultado);
                    contador_Variable_Auxiliar++;
                    return resultado10;
                }
            case "/":
                String tipo4 = padre_subarbol.getTipo();
                if (tipo4.equals("DOUBLE")) {
                    resultado = division_Pto_Flotante(padre_subarbol);
                    Nodo resultado11 = new Nodo(null, null, null, resultado);
                    contador_Variable_Auxiliar++;
                    return resultado11;
                }
                else if ((tipo4.equals("ULONG"))) {
                    resultado = division_Enteros_Ulong(padre_subarbol);
                    Nodo resultado12 = new Nodo(null, null, null, resultado);
                    contador_Variable_Auxiliar++;
                    return resultado12;
                }
                else {
                    resultado = division_Enteros_Int(padre_subarbol);
                    Nodo resultado13 = new Nodo(null, null, null, resultado);
                    contador_Variable_Auxiliar++;
                    return resultado13;
                }
            case "PRINT":
                AtributosSimbolo atributos = new AtributosSimbolo("PRINT", padre_subarbol.getCadenaImpresion());//le deberia pasar del nodo padre el lexema y como tipo PRINT
                AnalizadorLexico.simbolos.put("msj_"+contador_print, atributos);
                String codigo = "invoke MessageBox, NULL, addr msj_"+contador_print + ", addr msj_"+contador_print+", MB_OK\n";
                contador_print++;
                codigoFinal.add(codigo);
                return null;
            case "CALL": //LLAMADO FUNCIONES
                // tal vez deberia copiar las variables aca de ida  !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                String codigo22 = "CALL " + padre_subarbol.getSimbolo();
                codigoFinal.add(codigo22);
                // tal vez deberia copiar las variables aca de vuelta !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
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
            default: //Comparaciones
                    comparacion(padre_subarbol);
                    return null;
        }
    }

    private String suma_Pto_Flotante(Nodo nodo){
        if (nodo.getNodoHijoIzquierdo().getValorConstante() == null && nodo.getNodoHijoDerecho().getValorConstante() == null) {
            String codigo = "FLD " + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "FLD " + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "FADD \n"
                    + "FSTP @aux" + contador_Variable_Auxiliar;
            codigoFinal.add(codigo);
        }
        else if (nodo.getNodoHijoIzquierdo().getValorConstante() != null)
        {
            String codigo4 = "FLD " + nodo.getNodoHijoIzquierdo().getValorConstante() + "\n"
                    + "FLD " + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "FADD \n"
                    + "FSTP @aux" + contador_Variable_Auxiliar;
            codigoFinal.add(codigo4);
        }
        else
        {
            String codigo4 = "FLD " + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "FLD " + nodo.getNodoHijoDerecho().getValorConstante() + "\n"
                    + "FADD \n"
                    + "FSTP @aux" + contador_Variable_Auxiliar;
            codigoFinal.add(codigo4);
        }
        return "@aux"+contador_Variable_Auxiliar;
    }
    private String suma_Enteros_Int(Nodo nodo){
        if (nodo.getNodoHijoIzquierdo().getValorConstante() == null && nodo.getNodoHijoDerecho().getValorConstante() == null) {
            String codigo = "MOV AX," + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "ADD AX ," + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "JO error_overflow_suma_int \n" //CONTROL OVERFLOW
                    + "MOV @aux" + contador_Variable_Auxiliar + ",AX"; //EL JO ES PARA CONTROLAR EL OVERFLOW
            codigoFinal.add(codigo);
        }
        else if(nodo.getNodoHijoIzquierdo().getValorConstante() != null){
            String codigo = "MOV AX," + nodo.getNodoHijoIzquierdo().getValorConstante() + "\n"
                    + "ADD AX ," + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "JO error_overflow_suma_int \n" //CONTROL OVERFLOW
                    + "MOV @aux" + contador_Variable_Auxiliar + ",AX"; //EL JO ES PARA CONTROLAR EL OVERFLOW
            codigoFinal.add(codigo);
        }
        else{
            String codigo = "MOV AX," + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "ADD AX ," + nodo.getNodoHijoDerecho().getValorConstante() + "\n"
                    + "JO error_overflow_suma_int \n" //CONTROL OVERFLOW
                    + "MOV @aux" + contador_Variable_Auxiliar + ",AX"; //EL JO ES PARA CONTROLAR EL OVERFLOW
            codigoFinal.add(codigo);
        }
        return "@aux"+contador_Variable_Auxiliar;
    }
    private String suma_Enteros_Ulong(Nodo nodo){
        if (nodo.getNodoHijoIzquierdo().getValorConstante() == null && nodo.getNodoHijoDerecho().getValorConstante() == null) {
            String codigo = "MOV EAX," + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "ADD EAX ," + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "MOV @aux" + contador_Variable_Auxiliar + ",EAX";
            codigoFinal.add(codigo);
        }
        else if (nodo.getNodoHijoIzquierdo().getValorConstante() != null){
            String codigo = "MOV EAX," + nodo.getNodoHijoIzquierdo().getValorConstante() + "\n"
                    + "ADD EAX ," + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "MOV @aux" + contador_Variable_Auxiliar + ",EAX";
            codigoFinal.add(codigo);
        }
        else{
            String codigo = "MOV EAX," + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "ADD EAX ," + nodo.getNodoHijoDerecho().getValorConstante() + "\n"
                    + "MOV @aux" + contador_Variable_Auxiliar + ",EAX";
            codigoFinal.add(codigo);
        }
        return "@aux"+contador_Variable_Auxiliar;
    }

    private String multiplicacion_Enteros_Int(Nodo nodo){
        if (nodo.getNodoHijoIzquierdo().getValorConstante() == null && nodo.getNodoHijoDerecho().getValorConstante() == null) {
            String codigo1 = "MOV AX," + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "IMUL AX ," + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "MOV @aux" + contador_Variable_Auxiliar + ",AX";
            codigoFinal.add(codigo1);
        }
        else if(nodo.getNodoHijoIzquierdo().getValorConstante() != null){
            String codigo1 = "MOV AX," + nodo.getNodoHijoIzquierdo().getValorConstante() + "\n"
                    + "IMUL AX ," + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "MOV @aux" + contador_Variable_Auxiliar + ",AX";
            codigoFinal.add(codigo1);
        }
        else
        {
            String codigo1 = "MOV AX," + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "IMUL AX ," + nodo.getNodoHijoDerecho().getValorConstante() + "\n"
                    + "MOV @aux" + contador_Variable_Auxiliar + ",AX";
            codigoFinal.add(codigo1);
        }
        return "@aux"+contador_Variable_Auxiliar;
    }

    private String multiplicacion_Enteros_Ulong(Nodo nodo){
        if (nodo.getNodoHijoIzquierdo().getValorConstante() == null && nodo.getNodoHijoDerecho().getValorConstante() == null) {
            String codigo1 = "MOV EAX," + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "MUL EAX ," + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "MOV @aux" + contador_Variable_Auxiliar + ",EAX";
            codigoFinal.add(codigo1);
        }
        else if(nodo.getNodoHijoIzquierdo().getValorConstante() != null){
            String codigo1 = "MOV EAX," + nodo.getNodoHijoIzquierdo().getValorConstante() + "\n"
                    + "MUL EAX ," + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "MOV @aux" + contador_Variable_Auxiliar + ",EAX";
            codigoFinal.add(codigo1);
        }
        else {
            String codigo1 = "MOV EAX," + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "MUL EAX ," + nodo.getNodoHijoDerecho().getValorConstante() + "\n"
                    + "MOV @aux" + contador_Variable_Auxiliar + ",EAX";
            codigoFinal.add(codigo1);
        }
        return "@aux"+contador_Variable_Auxiliar;
    }

    private String multiplicacion_Pto_Flotante(Nodo nodo){
        String codigo;
        if (nodo.getNodoHijoIzquierdo().getValorConstante() == null && nodo.getNodoHijoDerecho().getValorConstante() == null) {
             codigo = "FLD " + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "FLD " + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "FMUL \n"
                    + "FSTSW @aux" + contador_Variable_Auxiliar + "\n"
                    + "MOV AX , " + "@aux" + contador_Variable_Auxiliar + "\n"
                    + "SAHF\n"
                    + "JO error_overflow_multiplicacion_double:\n";
        }
        else if(nodo.getNodoHijoIzquierdo().getValorConstante() != null){
             codigo = "FLD " + nodo.getNodoHijoIzquierdo().getValorConstante() + "\n"
                    + "FLD " + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "FMUL \n"
                    + "FSTSW @aux" + contador_Variable_Auxiliar + "\n"
                    + "MOV AX , " + "@aux" + contador_Variable_Auxiliar + "\n"
                    + "SAHF\n"
                    + "JO error_overflow_multiplicacion_double:\n";
        }
        else {
             codigo = "FLD " + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "FLD " + nodo.getNodoHijoDerecho().getValorConstante()+ "\n"
                    + "FMUL \n"
                    + "FSTSW @aux" + contador_Variable_Auxiliar + "\n"
                    + "MOV AX , " + "@aux" + contador_Variable_Auxiliar + "\n"
                    + "SAHF\n"
                    + "JO error_overflow_multiplicacion_double:\n";
        }
        // agrego la variable auxiliara la tabla de simbolos
        contador_Variable_Auxiliar++;
        codigoFinal.add(codigo);
        codigo = "FSTP @aux" + contador_Variable_Auxiliar;
        codigoFinal.add(codigo);
        return "@aux"+contador_Variable_Auxiliar;
    }

    private String division_Enteros_Int(Nodo nodo){
        if (nodo.getNodoHijoIzquierdo().getValorConstante() == null && nodo.getNodoHijoDerecho().getValorConstante() == null) {
            String codigo1 = "MOV AX," + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "CWD \n"
                    + "IDIV " + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "MOV @aux" + contador_Variable_Auxiliar + ",AX";
            codigoFinal.add(codigo1);
        }
        else if(nodo.getNodoHijoIzquierdo().getValorConstante() != null){
            String codigo1 = "MOV AX," + nodo.getNodoHijoIzquierdo().getValorConstante() + "\n"
                    + "CWD \n"
                    + "IDIV " + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "MOV @aux" + contador_Variable_Auxiliar + ",AX";
            codigoFinal.add(codigo1);
        }
        else{
            String codigo1 = "MOV AX," + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "CWD \n"
                    + "IDIV " + nodo.getNodoHijoDerecho().getValorConstante() + "\n"
                    + "MOV @aux" + contador_Variable_Auxiliar + ",AX";
            codigoFinal.add(codigo1);
        }
        return "@aux"+contador_Variable_Auxiliar;
    }
    private String division_Enteros_Ulong(Nodo nodo){
        if (nodo.getNodoHijoIzquierdo().getValorConstante() == null && nodo.getNodoHijoDerecho().getValorConstante() == null) {
            String codigo1 = "MOV EAX," + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "MOV EDX,0 \n"
                    + "DIV " + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "MOV @aux" + contador_Variable_Auxiliar + ",EAX";
            codigoFinal.add(codigo1);
        }
        else if(nodo.getNodoHijoIzquierdo().getValorConstante() != null){
            String codigo1 = "MOV EAX," + nodo.getNodoHijoIzquierdo().getValorConstante() + "\n"
                    + "MOV EDX,0 \n"
                    + "DIV " + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "MOV @aux" + contador_Variable_Auxiliar + ",EAX";
            codigoFinal.add(codigo1);
        }
        else{
            String codigo1 = "MOV EAX," + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "MOV EDX,0 \n"
                    + "DIV " + nodo.getNodoHijoDerecho().getValorConstante() + "\n"
                    + "MOV @aux" + contador_Variable_Auxiliar + ",EAX";
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
            codigoFinal.add(codigo);
        }
        else if(nodo.getNodoHijoIzquierdo().getValorConstante() != null){
            String codigo = "FLD " + nodo.getNodoHijoIzquierdo().getValorConstante() + "\n"
                    + "FLD " + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "FDIV \n"
                    + "FSTP @aux" + contador_Variable_Auxiliar;
            codigoFinal.add(codigo);
        }
        else{
            String codigo = "FLD " + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "FLD " + nodo.getNodoHijoDerecho().getValorConstante() + "\n"
                    + "FDIV \n"
                    + "FSTP @aux" + contador_Variable_Auxiliar;
            codigoFinal.add(codigo);
        }
        return "@aux"+contador_Variable_Auxiliar;
    }

    private String resta_Enteros_INT(Nodo nodo){
        if (nodo.getNodoHijoIzquierdo().getValorConstante() == null && nodo.getNodoHijoDerecho().getValorConstante() == null) {
            String codigo = "MOV AX," + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "SUB AX ," + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "MOV @aux" + contador_Variable_Auxiliar + ",AX";
            codigoFinal.add(codigo);
        }
        else if(nodo.getNodoHijoIzquierdo().getValorConstante() != null){
            String codigo = "MOV AX," + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "SUB AX ," + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "MOV @aux" + contador_Variable_Auxiliar + ",AX";
            codigoFinal.add(codigo);
        }
        else{
            String codigo = "MOV AX," + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "SUB AX ," + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "MOV @aux" + contador_Variable_Auxiliar + ",AX";
            codigoFinal.add(codigo);
        }
        return "@aux"+contador_Variable_Auxiliar;
    }
    private String resta_Enteros_Ulong(Nodo nodo){
        if (nodo.getNodoHijoIzquierdo().getValorConstante() == null && nodo.getNodoHijoDerecho().getValorConstante() == null) {
            String codigo = "MOV EAX," + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "SUB EAX ," + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "JS error_rdo_negativo_resta_ulong \n" //CONTROLA QUE NO HAYA DADO NEGATIVO
                    + "MOV @aux" + contador_Variable_Auxiliar + ",EAX";
            codigoFinal.add(codigo);
        }
        else if(nodo.getNodoHijoIzquierdo().getValorConstante() != null){
            String codigo = "MOV EAX," + nodo.getNodoHijoIzquierdo().getValorConstante() + "\n"
                    + "SUB EAX ," + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "JS error_rdo_negativo_resta_ulong \n" //CONTROLA QUE NO HAYA DADO NEGATIVO
                    + "MOV @aux" + contador_Variable_Auxiliar + ",EAX";
            codigoFinal.add(codigo);
        }
        else{
            String codigo = "MOV EAX," + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "SUB EAX ," + nodo.getNodoHijoDerecho().getValorConstante() + "\n"
                    + "JS error_rdo_negativo_resta_ulong \n" //CONTROLA QUE NO HAYA DADO NEGATIVO
                    + "MOV @aux" + contador_Variable_Auxiliar + ",EAX";
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
            codigoFinal.add(codigo);
        }
        else if(nodo.getNodoHijoIzquierdo().getValorConstante() != null){
            String codigo = "FLD " + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "FLD " + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "FSUB \n"
                    + "FSTP @aux" + contador_Variable_Auxiliar;
            codigoFinal.add(codigo);
        }
        else{
            String codigo = "FLD " + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                    + "FLD " + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                    + "FSUB \n"
                    + "FSTP @aux" + contador_Variable_Auxiliar;
            codigoFinal.add(codigo);
        }
        return "@aux" + contador_Variable_Auxiliar;
    }
    private String salto(){
        String codigo = "JMP label " + contador_Label;
        codigoFinal.add(codigo);
        pila.add("label"+contador_Label+":"); //apilo en el tope de la pila
        contador_Label++;
        return "";
    }

    // SALTOS CONDICIONALES ARITMETICOS SIGNADO (NUMEROS POSITVOS , NEGATIVOS Y CERO)
    private String salto_Menor_Aritmetico_Signado(int nrolabel){
        String codigo = "JLE label " + nrolabel;
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
        if(!(nodo.getNodoHijoDerecho().getSimbolo().contains("@")) | !(Character.isLetter(nodo.getNodoHijoDerecho().getNodoHijoDerecho().getSimbolo().charAt(0))) | (nodo.getNodoHijoDerecho().getSimbolo().startsWith("_"))) { //chequea que sea una constante
            if (nodo.getNodoHijoDerecho().getTipo().equals("INT")) {
                codigo = "MOV AX," + nodo.getNodoHijoDerecho().getValorConstante() + "\n"
                        + "MOV " + nodo.getNodoHijoIzquierdo().getSimbolo() + ",AX";
                codigoFinal.add(codigo);
            }
            else if (nodo.getNodoHijoDerecho().getTipo().equals("ULONG")) {
                codigo = "MOV EAX," + nodo.getNodoHijoDerecho().getValorConstante() + "\n"
                        + "MOV " + nodo.getNodoHijoIzquierdo().getSimbolo() + ",EAX";
                codigoFinal.add(codigo);
            }
            else {
                codigo = "MOV " + nodo.getNodoHijoIzquierdo().getSimbolo() + "," + nodo.getNodoHijoDerecho().getValorConstante();
                codigoFinal.add(codigo);
            }
        }
        else {
            codigo = "MOV " + nodo.getNodoHijoIzquierdo().getSimbolo() + "," + nodo.getNodoHijoDerecho().getSimbolo();
            codigoFinal.add(codigo);
        }
        return "";
    }

    private String comparacion(Nodo nodo){
        String codigo;
        if (es_for && es_condicion_for) {
            codigo = "label" + contador_Label + ";";
            codigoFinal.add(codigo);
            pila.add(codigo);
            contador_Label++;
            }
        ultimo_signo_comparacion = nodo.getSimbolo();
        if (nodo.getNodoHijoIzquierdo().getValorConstante() == null && nodo.getNodoHijoDerecho().getValorConstante() == null) {
            if (nodo.getTipo().equals("INT")) {
                codigo = "MOV AX," + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                        + "CMP AX," + nodo.getNodoHijoDerecho().getSimbolo();
                codigoFinal.add(codigo);
                condicion_Sentencia_Control_Signado();
            } else if (nodo.getTipo().equals("ULONG")) {
                codigo = "MOV EAX," + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                        + "CMP EAX," + nodo.getNodoHijoDerecho().getSimbolo();
                codigoFinal.add(codigo);
                condicion_Sentencia_Control_Sin_Signo();
            } else {
                codigo = "FLD " + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                        + "FCOM" + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                        + "FSTSW @aux" + contador_Variable_Auxiliar + "\n" ///preguntar si el label del for deberia estar en la linea de abajo
                        + "MOV AX,@aux" + contador_Variable_Auxiliar + "\n"
                        + "SAHF";
                codigoFinal.add(codigo);
                //ACA SE TIENE QUE GUARDAR VARIABLE AUXILIAR EN TABLA DE SIMBOLOS.
                contador_Variable_Auxiliar++;
                condicion_Sentencia_Control_Sin_Signo();
            }
        }
        else if(nodo.getNodoHijoIzquierdo().getValorConstante() != null){
            if (nodo.getTipo().equals("INT")) {
                codigo = "MOV AX," + nodo.getNodoHijoIzquierdo().getValorConstante() + "\n"
                        + "CMP AX," + nodo.getNodoHijoDerecho().getSimbolo();
                codigoFinal.add(codigo);
                condicion_Sentencia_Control_Signado();
            } else if (nodo.getTipo().equals("ULONG")) {
                codigo = "MOV EAX," + nodo.getNodoHijoIzquierdo().getValorConstante() + "\n"
                        + "CMP EAX," + nodo.getNodoHijoDerecho().getSimbolo();
                codigoFinal.add(codigo);
                condicion_Sentencia_Control_Sin_Signo();
            } else {
                codigo = "FLD " + nodo.getNodoHijoIzquierdo().getValorConstante() + "\n"
                        + "FCOM" + nodo.getNodoHijoDerecho().getSimbolo() + "\n"
                        + "FSTSW @aux" + contador_Variable_Auxiliar + "\n" ///preguntar si el label del for deberia estar en la linea de abajo
                        + "MOV AX,@aux" + contador_Variable_Auxiliar + "\n"
                        + "SAHF";
                codigoFinal.add(codigo);
                //ACA SE TIENE QUE GUARDAR VARIABLE AUXILIAR EN TABLA DE SIMBOLOS.
                contador_Variable_Auxiliar++;
                condicion_Sentencia_Control_Sin_Signo();
            }
        }
        else{
            if (nodo.getTipo().equals("INT")) {
                codigo = "MOV AX," + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                        + "CMP AX," + nodo.getNodoHijoDerecho().getValorConstante();
                codigoFinal.add(codigo);
                condicion_Sentencia_Control_Signado();
            } else if (nodo.getTipo().equals("ULONG")) {
                codigo = "MOV EAX," + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                        + "CMP EAX," + nodo.getNodoHijoDerecho().getValorConstante();
                codigoFinal.add(codigo);
                condicion_Sentencia_Control_Sin_Signo();
            } else {
                codigo = "FLD " + nodo.getNodoHijoIzquierdo().getSimbolo() + "\n"
                        + "FCOM" + nodo.getNodoHijoDerecho().getValorConstante() + "\n"
                        + "FSTSW @aux" + contador_Variable_Auxiliar + "\n" ///preguntar si el label del for deberia estar en la linea de abajo
                        + "MOV AX,@aux" + contador_Variable_Auxiliar + "\n"
                        + "SAHF";
                codigoFinal.add(codigo);
                //ACA SE TIENE QUE GUARDAR VARIABLE AUXILIAR EN TABLA DE SIMBOLOS.
                contador_Variable_Auxiliar++;
                condicion_Sentencia_Control_Sin_Signo();
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
            case "==":
                salto_Distinto_Aritmetico_Signado(contador_Label);
                break;
            case "!!":
                salto_Igual_Aritmetico_Signado(contador_Label);
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
        if(tiene_else) {
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
    }

    private void else_if_(){
        String codigo = pila.get(pila.size()-1);
        codigoFinal.add(codigo);
        pila.remove(pila.size()-1);
    }
    private void fin_For(){
        String codigo = "JMP " +pila.get(pila.size()-2);
        codigoFinal.add(codigo);
        codigo = pila.get(pila.size()-1);
        codigoFinal.add(codigo);
        pila.remove(pila.size()-1);
        pila.remove(pila.size()-1);
    }

}





