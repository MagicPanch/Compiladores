package Assembler;

public class Nodo {
    private Nodo izq = new Nodo();
    private Nodo der = new Nodo();
    private String lexema = "";

    public Nodo(final Nodo izq, final Nodo der, final String lexema) {
        this.izq = izq;
        this.der = der;
        this.lexema = lexema;
    }

    public Nodo() {

    }

    public Nodo getIzq() {
        return this.izq;
    }

    public void setIzq(final Nodo izq) {
        this.izq = izq;
    }

    public Nodo getDer() {
        return this.der;
    }

    public void setDer(final Nodo der) {
        this.der = der;
    }

    public String getLexema() {
        return this.lexema;
    }

    public void setLexema(final String lexema) {
        this.lexema = lexema;
    }
}
