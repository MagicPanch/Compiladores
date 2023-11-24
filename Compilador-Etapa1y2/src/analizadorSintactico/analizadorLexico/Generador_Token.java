package analizadorSintactico.analizadorLexico;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.HashMap;

public class Generador_Token {
    private ArrayList<Par_Token_Regex> pares_token_regex = new ArrayList<Par_Token_Regex>();
    private HashMap<String,Integer> palabras_reservadas = new HashMap<String,Integer>();

    public Generador_Token() {
        pares_token_regex.add(new Par_Token_Regex(-1,"^[{}(),;.:]$")); //Caracteres especiales, este token no se usa realmente, sino que se devuelve el correspondiente al valor ASCII del caracter
        pares_token_regex.add(new Par_Token_Regex(-1,"^[+\\-*/]$")); //Operadores arimeticos simples, este token no se usa realmente, sino que se devuelve el correspondiente al valor ASCII del caracter
        pares_token_regex.add(new Par_Token_Regex(-1,"^=$")); //Operador de asignacion, este token no se usa realmente, sino que se devuelve el correspondiente al valor ASCII del caracter
        pares_token_regex.add(new Par_Token_Regex(-1,"^>$")); //Comparador de mayor, este token no se usa realmente, sino que se devuelve el correspondiente al valor ASCII del caracter
        pares_token_regex.add(new Par_Token_Regex(-1,"^<$")); //Comparador de menor, este token no se usa realmente, sino que se devuelve el correspondiente al valor ASCII del caracter
        pares_token_regex.add(new Par_Token_Regex(-2,"^[A-Z]+(_[A-Z]+)?$")); //Palabras reservadas, este token no se usa realmente, sino que se busca el especifico en la tabla de palabras reservadas
        palabras_reservadas.put("IF", 257);
		palabras_reservadas.put("ELSE", 258);
		palabras_reservadas.put("END_IF", 259);
		palabras_reservadas.put("PRINT", 260);
		palabras_reservadas.put("CLASS", 261);
		palabras_reservadas.put("VOID", 262);
		palabras_reservadas.put("INT", 263);
		palabras_reservadas.put("ULONG", 264);
		palabras_reservadas.put("DOUBLE", 265);
		palabras_reservadas.put("FOR", 266);
		palabras_reservadas.put("IN", 267);
		palabras_reservadas.put("RANGE", 268);
		palabras_reservadas.put("IMPL", 269);
		palabras_reservadas.put("INTERFACE", 270);
		palabras_reservadas.put("IMPLEMENT", 271);
		palabras_reservadas.put("RETURN", 272);
		pares_token_regex.add(new Par_Token_Regex(273,"^(?:(\\d+\\.\\d*([dD][-+]\\d+)?)|(\\.\\d+|(\\.\\d+[dD][-+]\\d+)))$")); //Punto Flotante
        pares_token_regex.add(new Par_Token_Regex(274,"^\\d+_i$")); //Integer
        pares_token_regex.add(new Par_Token_Regex(275,"^\\d+_ul$")); //Unsigned long 
        pares_token_regex.add(new Par_Token_Regex(276,"^%[^%]*%$"));//Cadenas multilinea
        pares_token_regex.add(new Par_Token_Regex(277,"^[a-z_]+[a-z0-9_]*$")); //Identificadores
        pares_token_regex.add(new Par_Token_Regex(278,"^-=$")); //Asignador de -=
        pares_token_regex.add(new Par_Token_Regex(279,"^>=$")); //Comparador de mayor o igual
        pares_token_regex.add(new Par_Token_Regex(280,"^<=$")); //Comparador de menor o igual
        pares_token_regex.add(new Par_Token_Regex(281,"^==$")); //Comparador de igual
        pares_token_regex.add(new Par_Token_Regex(282,"^!!$")); //Comparador de distinto
    }

    public int obtenerToken(String lexema) {
        for (int i = 0; i < this.pares_token_regex.size(); i++) {
            Pattern pattern = Pattern.compile(this.pares_token_regex.get(i).getRegex());
            Matcher matcher = pattern.matcher(lexema);
            if (matcher.matches()) {
            	int token = this.pares_token_regex.get(i).getToken();
            	if (token != -2) {
            		if (token == -1) { //en este caso (token = -1), se trata de un caracter, por lo que se retorna el verdadero token igual al valor ASCII del mismo
            			char caracter_individual = lexema.charAt(0);
            			return (int) caracter_individual;
            		}
            		return token;
            	}
            	else { //en este caso (token igual a -2), se trata de una palabra reservada, por lo que se retorna el verdadero token que corresponda, extrayendolo del ArrayList de palabras reservadas
            		Integer token_palabra_reservada = palabras_reservadas.get(lexema);
            		if (token_palabra_reservada != null)
            			return token_palabra_reservada;
            	}
            }
        }
        return -1;
    }
}
