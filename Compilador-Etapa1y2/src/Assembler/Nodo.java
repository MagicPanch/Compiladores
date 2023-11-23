package Assembler;

public class Nodo {
    private Nodo izq = new Nodo();
    private Nodo der = new Nodo();
    private Nodo uni = new Nodo();
    private String lexema = "";
    private String tipo;

    private String valor_constante;

    public Nodo(final Nodo izq, final Nodo der,final Nodo uni, final String lexema,final String tipo,final String valor_constante) {
        this.izq = izq;
        this.der = der;
        this.lexema = lexema;
        this.tipo = tipo;
        this.uni = uni;
        this.valor_constante = valor_constante;
    }
    public Nodo(final Nodo izq, final Nodo der,final Nodo uni, final String lexema) {
        this.izq = izq;
        this.der = der;
        this.lexema = lexema;
        this.uni = uni;
    }

    public Nodo() {

    }

    public String getValor_constante() {
        return this.valor_constante;
    }

    public void setValor_constante(final String valor_constante) {
        this.valor_constante = valor_constante;
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

    public String getTipo() {
        return this.tipo;
    }

    public void setTipo(final String tipo) {
        this.tipo = tipo;
    }

    public Nodo getUni() {
        return this.uni;
    }

    public void setUni(final Nodo uni) {
        this.uni = uni;
    }
}
