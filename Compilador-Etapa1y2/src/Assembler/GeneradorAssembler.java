package Assembler;

public class GeneradorAssembler {
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

    public int generar_Codigo(){ //Cambiar el tipo int por el tipo que tenga el nodo del arbol sintactico
        
    }
}
