package Assembler;

public class GeneradorAssembler {
    private int contador_Variable_Auxiliar = 1;
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
        String codigo = "MOV EAX,_" + nodo.getIzq() + "/n" + "ADD EAX ,_" + nodo.getDer() + "/n" + "MOV @aux" + contador_Variable_Auxiliar +",EAX";
        return "@aux"+contador_Variable_Auxiliar;
    }

    private String multiplicacion_Enteros(Nodo nodo){ //USO EL IMUL PORQ EL MUL SOLO PERMITE REG/MEM Y EL OTRO REG/MEM/INMED
        //CHEQUEAR CANTIDAD DE BYTES
        //SI AMBOS OPERANDOS SON DE 32 BITS
        String codigo = "MOV EAX,_" + nodo.getIzq() + "/n" + "IMUL EAX ,_" + nodo.getDer() + "/n" + "MOV @aux" + contador_Variable_Auxiliar +",EAX";
        //SI AMBOS OPERANDOS SON DE 16 BITS
        String codigo1 = "MOV AX,_" + nodo.getIzq() + "/n" + "IMUL AX ,_" + nodo.getDer() + "/n" + "MOV @aux" + contador_Variable_Auxiliar +",AX";
        //SI AMBOS OPERANDOS SON DE 8 BITS
        String codigo2 = "MOV AL,_" + nodo.getIzq() + "/n" + "IMUL AL ,_" + nodo.getDer() + "/n" + "MOV @aux" + contador_Variable_Auxiliar +",AL";
        return "@aux"+contador_Variable_Auxiliar;
    }

    private String division_Enteros(Nodo nodo){
        //CHEQUEAR CANTIDAD DE BYTES
        //SI AMBOS OPERANDOS SON DE 32 BITS
        String codigo = "MOV EAX,_" + nodo.getIzq() + "/n" + "DIV EAX ,_" + nodo.getDer() + "/n" + "MOV @aux" + contador_Variable_Auxiliar +",EAX";
        //SI AMBOS OPERANDOS SON DE 16 BITS
        String codigo1 = "MOV AX,_" + nodo.getIzq() + "/n" + "DIV AX ,_" + nodo.getDer() + "/n" + "MOV @aux" + contador_Variable_Auxiliar +",AX";
        //SI AMBOS OPERANDOS SON DE 8 BITS
        String codigo2 = "MOV AH,_" + nodo.getIzq() + "/n" + "DIV AH ,_" + nodo.getDer() + "/n" + "MOV @aux" + contador_Variable_Auxiliar +",AL";
        return "@aux"+contador_Variable_Auxiliar;
    }
}
