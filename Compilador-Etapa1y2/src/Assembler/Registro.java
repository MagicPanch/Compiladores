package Assembler;

public class Registro {
    private Registro parte_Alta;
    private Registro parte_Baja;
    private int bits;
    private boolean en_Uso = false;
    private int carry_Flag,parity_Flag,zero_Flag,sign_Flag,overflow_Flag;
    private String valor;

    public Registro(final Registro parte_Alta, final Registro parte_Baja, final int bits) {
        this.parte_Alta = parte_Alta;
        this.parte_Baja = parte_Baja;
        this.bits = bits;
    }

    public boolean isEn_Uso() {
        return this.en_Uso;
    }

    public void setEn_Uso(final boolean en_Uso) {
        this.en_Uso = en_Uso;
    }

    public String getValor() {
        return this.valor;
    }

    public void setValor(final String valor) {
        this.valor = valor;
    }

    public Registro getParte_Alta() {
        return this.parte_Alta;
    }

    public void setParte_Alta(final Registro parte_Alta) {
        this.parte_Alta = parte_Alta;
    }

    public Registro getParte_Baja() {
        return this.parte_Baja;
    }

    public void setParte_Baja(final Registro parte_Baja) {
        this.parte_Baja = parte_Baja;
    }

    public int getBits() {
        return this.bits;
    }

    public void setBits(final int bits) {
        this.bits = bits;
    }

    public int getCarry_Flag() {
        return this.carry_Flag;
    }

    public void setCarry_Flag(final int carry_Flag) {
        this.carry_Flag = carry_Flag;
    }

    public int getParity_Flag() {
        return this.parity_Flag;
    }

    public void setParity_Flag(final int parity_Flag) {
        this.parity_Flag = parity_Flag;
    }

    public int getZero_Flag() {
        return this.zero_Flag;
    }

    public void setZero_Flag(final int zero_Flag) {
        this.zero_Flag = zero_Flag;
    }

    public int getSign_Flag() {
        return this.sign_Flag;
    }

    public void setSign_Flag(final int sign_Flag) {
        this.sign_Flag = sign_Flag;
    }

    public int getOverflow_Flag() {
        return this.overflow_Flag;
    }

    public void setOverflow_Flag(final int overflow_Flag) {
        this.overflow_Flag = overflow_Flag;
    }
}
