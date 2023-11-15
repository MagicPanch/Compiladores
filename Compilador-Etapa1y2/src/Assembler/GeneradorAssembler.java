package Assembler;

import java.awt.*;
import java.util.ArrayList;

public class GeneradorAssembler {
    private String ultimo_signo_comparacion;
    private int contador_Label = 1; // Utilizado para que no se repitan los label dentro del programa
    private ArrayList<String> pila = new ArrayList<>(); //Utilizado para apilar los label de las sentencias de control
    private int contador_Variable_Auxiliar = 1; // Utilizado para que no haya variables auxiliares con el mismo nombre
    private Registro AH = new Registro(null,null,8);
    private Registro AL = new Registro(null,null,8);
    private Registro BH = new Registro(null,null,8);
    private Registro BL = new Registro(null,null,8);
    private Registro CH = new Registro(null,null,8);
    private Registro CL = new Registro(null,null,8);
    private Registro DH = new Registro(null,null,8);
    private Registro DL = new Registro(null,null,8);

    private Registro AX = new Registro(AH,AL,16);
    private Registro BX = new Registro(BH,BL,16);
    private Registro CX = new Registro(CH,CL,16);
    private Registro DX = new Registro(DH,DL,16);

    private Registro EAX = new Registro(null,AX,32);
    private Registro EBX = new Registro(null,BX,32);
    private Registro ECX = new Registro(null,CX,32);
    private Registro EDX = new Registro(null,DX,32);

    public Nodo generar_Codigo(Nodo padre_subarbol){ //Cambiar el tipo nodo por el tipo que tenga el nodo del arbol sintactico
        //CREAR NODO A MANDAR CON EL STRING OBTENIDO
        //AUMENTAR EN 1 contador_Variable_Auxiliar
        return null; //DEVOLVER EL NODO CREADO ANTERIORMENTE
    }
    
    private String suma_Enteros(Nodo nodo){
        String codigo = "MOV EAX," + nodo.getIzq() + "/n" + "ADD EAX ," + nodo.getDer() + "/n" + "MOV @aux" + contador_Variable_Auxiliar +",EAX";
        return "@aux"+contador_Variable_Auxiliar;
    }

    private String multiplicacion_Enteros(Nodo nodo){ //USO EL IMUL PORQ EL MUL SOLO PERMITE REG/MEM Y EL OTRO REG/MEM/INMED
        //CHEQUEAR CANTIDAD DE BYTES
        //SI AMBOS OPERANDOS SON DE 32 BITS
        String codigo = "MOV EAX," + nodo.getIzq() + "/n" + "IMUL EAX ," + nodo.getDer() + "/n" + "MOV @aux" + contador_Variable_Auxiliar +",EAX";
        //SI AMBOS OPERANDOS SON DE 16 BITS
        String codigo1 = "MOV AX," + nodo.getIzq() + "/n" + "IMUL AX ," + nodo.getDer() + "/n" + "MOV @aux" + contador_Variable_Auxiliar +",AX";
        //SI AMBOS OPERANDOS SON DE 8 BITS
        String codigo2 = "MOV AL," + nodo.getIzq() + "/n" + "IMUL AL ," + nodo.getDer() + "/n" + "MOV @aux" + contador_Variable_Auxiliar +",AL";
        return "@aux"+contador_Variable_Auxiliar;
    }

    private String division_Enteros(Nodo nodo){
        //CHEQUEAR CANTIDAD DE BYTES
        //SI AMBOS OPERANDOS SON DE 32 BITS
        String codigo = "MOV EAX," + nodo.getIzq() + "/n" + "DIV EAX ," + nodo.getDer() + "/n" + "MOV @aux" + contador_Variable_Auxiliar +",EAX";
        //SI AMBOS OPERANDOS SON DE 16 BITS
        String codigo1 = "MOV AX," + nodo.getIzq() + "/n" + "DIV AX ," + nodo.getDer() + "/n" + "MOV @aux" + contador_Variable_Auxiliar +",AX";
        //SI AMBOS OPERANDOS SON DE 8 BITS
        String codigo2 = "MOV AH," + nodo.getIzq() + "/n" + "DIV AH ," + nodo.getDer() + "/n" + "MOV @aux" + contador_Variable_Auxiliar +",AL";
        return "@aux"+contador_Variable_Auxiliar;
    }

    private String resta_Enteros(Nodo nodo){
        String codigo = "MOV EAX," + nodo.getIzq() + "/n" + "SUB EAX ," + nodo.getDer() + "/n" + "MOV @aux" + contador_Variable_Auxiliar +",EAX";
        return "@aux"+contador_Variable_Auxiliar;
    }

    private String salto(int nrolabel){
        String codigo = "JMP " + nrolabel ;
        pila.add(codigo); //apilo en el tope de la pila
        contador_Label++;
        return "";
    }

    // SALTOS CONDICIONALES ARITMETICOS SIGNADO (NUMEROS POSITVOS , NEGATIVOS Y CERO)
    private String salto_Menor_Aritmetico_Signado(int nrolabel){
        String codigo = "JLE label" + nrolabel;
        pila.add(codigo); //apilo en el tope de la pila
        contador_Label++;
        return "";
    }

    private String salto_Menor_Igual_Aritmetico_Signado(int nrolabel){
        String codigo = "JLE label" + nrolabel;
        pila.add(codigo); //apilo en el tope de la pila
        contador_Label++;
        return "";
    }
    private String salto_Igual_Aritmetico_Signado(int nrolabel){
        String codigo = "JE label" + nrolabel;
        pila.add(codigo); //apilo en el tope de la pila
        contador_Label++;
        return "";
    }
    private String salto_Distinto_Aritmetico_Signado(int nrolabel){
        String codigo = "JNE label" + nrolabel;
        pila.add(codigo); //apilo en el tope de la pila
        contador_Label++;
        return "";
    }
    private String salto_Mayor_Igual_Aritmetico_Signado(int nrolabel){
        String codigo = "JGE label" + nrolabel;
        pila.add(codigo); //apilo en el tope de la pila
        contador_Label++;
        return "";
    }
    private String salto_Mayor_Aritmetico_Signado(int nrolabel){
        String codigo = "JG label" + nrolabel;
        pila.add(codigo); //apilo en el tope de la pila
        contador_Label++;
        return "";
    }
    // SALTOS CONDICIONALES ARITMETICOS SIN SIGNO (NUMEROS POSITIVOS Y CERO)
    private String salto_Menor_Aritmetico_Sin_Signo(int nrolabel){
        String codigo = "JB label" + nrolabel;
        pila.add(codigo); //apilo en el tope de la pila
        contador_Label++;
        return "";
    }
    private String salto_Menor_Igual_Aritmetico_Sin_Signo(int nrolabel){
        String codigo = "JBE label" + nrolabel;
        pila.add(codigo); //apilo en el tope de la pila
        contador_Label++;
        return "";
    }
    private String salto_Igual_Aritmetico_Sin_Signo(int nrolabel){
        String codigo = "JE label" + nrolabel;
        pila.add(codigo); //apilo en el tope de la pila
        contador_Label++;
        return "";
    }
    private String salto_Distinto_Aritmetico_Sin_Signo(int nrolabel){
        String codigo = "JNE label" + nrolabel;
        pila.add(codigo); //apilo en el tope de la pila
        contador_Label++;
        return "";
    }
    private String salto_Mayor_Aritmetico_Sin_Signo(int nrolabel){
        String codigo = "JA label" + nrolabel;
        pila.add(codigo); //apilo en el tope de la pila
        contador_Label++;
        return "";
    }
    private String salto_Mayor_Igual_Aritmetico_Sin_Signo(int nrolabel){
        String codigo = "JAE label" + nrolabel;
        pila.add(codigo); //apilo en el tope de la pila
        contador_Label++;
        return "";
    }
    private String asignacion(Nodo nodo){
        String codigo = "MOV EAX," + nodo.getDer()+ "/n" + "MOV " + nodo.getIzq() + ",EAX";
        //Destildar registros en uso
        return "";
    }

    private String comparacion(Nodo nodo){
        String codigo = "CMP " + nodo.getIzq() + "," + nodo.getDer();
        ultimo_signo_comparacion = nodo.getLexema(); //SUPONGO QUE SE GUARDA EL SIGNO SIN NAME MANGLIN.
        //Destildar registros en uso
        return "";
    }

    private String condicion_if_Signado (Nodo nodo){
        String codigo;
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

    private String condicion_if_Sin_Signo (Nodo nodo){
        String codigo;
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

    private String then_if (Nodo nodo){
        //si no tiene else
        String codigo = pila.get(pila.size()-1);
        pila.remove(pila.size()-1);

        //si tiene else
        codigo = salto(contador_Label);
        codigo = pila.get(pila.size()-2);
        pila.remove(pila.size()-2);

        return "";
    }

    private String else_if (Nodo nodo){
        String codigo = pila.get(pila.size()-1);
        pila.remove(pila.size()-1);
        return "";
    }

    

}

