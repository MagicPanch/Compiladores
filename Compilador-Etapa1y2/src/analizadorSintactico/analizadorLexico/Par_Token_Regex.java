package analizadorSintactico.analizadorLexico;

public class Par_Token_Regex {
    private int token;
    private String regex;

    public Par_Token_Regex(int token, String regex) {
        this.token = token;
        this.regex = regex;
    }

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }


}
