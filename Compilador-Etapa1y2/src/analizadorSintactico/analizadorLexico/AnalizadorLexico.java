package analizadorSintactico.analizadorLexico;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import analizadorSintactico.analizadorLexico.AccionesSemanticas.AccionSemantica;
import analizadorSintactico.analizadorLexico.AccionesSemanticas.AccionSemantica1;
import analizadorSintactico.analizadorLexico.AccionesSemanticas.AccionSemantica10;
import analizadorSintactico.analizadorLexico.AccionesSemanticas.AccionSemantica11;
import analizadorSintactico.analizadorLexico.AccionesSemanticas.AccionSemantica2;
import analizadorSintactico.analizadorLexico.AccionesSemanticas.AccionSemantica3;
import analizadorSintactico.analizadorLexico.AccionesSemanticas.AccionSemantica4;
import analizadorSintactico.analizadorLexico.AccionesSemanticas.AccionSemantica5;
import analizadorSintactico.analizadorLexico.AccionesSemanticas.AccionSemantica6;
import analizadorSintactico.analizadorLexico.AccionesSemanticas.AccionSemantica7;
import analizadorSintactico.analizadorLexico.AccionesSemanticas.AccionSemantica8;
import analizadorSintactico.analizadorLexico.AccionesSemanticas.AccionSemantica9;
import analizadorSintactico.analizadorLexico.AccionesSemanticas.AccionSemanticaError;
import analizadorSintactico.analizadorLexico.ConjuntosSimbolos.ConjuntoAsterisco;
import analizadorSintactico.analizadorLexico.ConjuntosSimbolos.ConjuntoCaracteresSimilares;
import analizadorSintactico.analizadorLexico.ConjuntosSimbolos.ConjuntoDMayuscula;
import analizadorSintactico.analizadorLexico.ConjuntosSimbolos.ConjuntoDigitos;
import analizadorSintactico.analizadorLexico.ConjuntosSimbolos.ConjuntoGuionBajo;
import analizadorSintactico.analizadorLexico.ConjuntosSimbolos.ConjuntoIgual;
import analizadorSintactico.analizadorLexico.ConjuntosSimbolos.ConjuntoLMsinD;
import analizadorSintactico.analizadorLexico.ConjuntosSimbolos.ConjuntoLetrad;
import analizadorSintactico.analizadorLexico.ConjuntosSimbolos.ConjuntoLetrai;
import analizadorSintactico.analizadorLexico.ConjuntosSimbolos.ConjuntoLetral;
import analizadorSintactico.analizadorLexico.ConjuntosSimbolos.ConjuntoLetrau;
import analizadorSintactico.analizadorLexico.ConjuntosSimbolos.ConjuntoLlaveAbre;
import analizadorSintactico.analizadorLexico.ConjuntosSimbolos.ConjuntoLlaveCierra;
import analizadorSintactico.analizadorLexico.ConjuntosSimbolos.ConjuntoOperadoresComparacion;
import analizadorSintactico.analizadorLexico.ConjuntosSimbolos.ConjuntoPorcentaje;
import analizadorSintactico.analizadorLexico.ConjuntosSimbolos.ConjuntoPunto;
import analizadorSintactico.analizadorLexico.ConjuntosSimbolos.ConjuntoSignoDeAdmiracion;
import analizadorSintactico.analizadorLexico.ConjuntosSimbolos.ConjuntoSignoMas;
import analizadorSintactico.analizadorLexico.ConjuntosSimbolos.ConjuntoSignoMenos;
import analizadorSintactico.analizadorLexico.ConjuntosSimbolos.ConjuntoSimbolos;
import analizadorSintactico.analizadorLexico.ConjuntosSimbolos.ConjuntoTabBlNl;
import analizadorSintactico.analizadorLexico.ConjuntosSimbolos.ConjuntolmSINiuld;

public class AnalizadorLexico {
    private Par_Accion_Estado[][] matriz_transicion_estados = new Par_Accion_Estado[20][22];
    private ArrayList<ConjuntoSimbolos> mapeo_columnas_mte = new ArrayList<>();
    private String linea;
    private BufferedReader br;
    public static int estado_actual = 0;
    public static int indice_caracter_leer = 0;
    public static int numero_linea = 1;
    public static HashMap<String,AtributosSimbolo> simbolos = new HashMap<String,AtributosSimbolo>();
	public static String clave_tabla_simbolos = null;
    public static ArrayList<String> errores_y_warnings = new ArrayList<>();

    public AnalizadorLexico(String nombre_archivo) {
    	try {
    		br = new BufferedReader(new FileReader(nombre_archivo));
    		linea = br.readLine();
    		if (linea != null) {
    			linea = linea + '\n';
        		numero_linea = 1;
        		mapeo_columnas_mte.add(new ConjuntoDigitos());
        		mapeo_columnas_mte.add(new ConjuntoLMsinD());
        		mapeo_columnas_mte.add(new ConjuntolmSINiuld());
        		mapeo_columnas_mte.add(new ConjuntoSignoMas());
        		mapeo_columnas_mte.add(new ConjuntoSignoMenos());
        		mapeo_columnas_mte.add(new ConjuntoAsterisco());
        		mapeo_columnas_mte.add(new ConjuntoCaracteresSimilares());
        		mapeo_columnas_mte.add(new ConjuntoLetrai());
        		mapeo_columnas_mte.add(new ConjuntoLetrau());
        		mapeo_columnas_mte.add(new ConjuntoLetral());
        		mapeo_columnas_mte.add(new ConjuntoLetrad());
        		mapeo_columnas_mte.add(new ConjuntoDMayuscula());
        		mapeo_columnas_mte.add(new ConjuntoLlaveAbre());
        		mapeo_columnas_mte.add(new ConjuntoLlaveCierra());
        		mapeo_columnas_mte.add(new ConjuntoPorcentaje());
        		mapeo_columnas_mte.add(new ConjuntoPunto());
        		mapeo_columnas_mte.add(new ConjuntoIgual());
        		mapeo_columnas_mte.add(new ConjuntoOperadoresComparacion());
        		mapeo_columnas_mte.add(new ConjuntoGuionBajo());
        		mapeo_columnas_mte.add(new ConjuntoSignoDeAdmiracion());
        		mapeo_columnas_mte.add(new ConjuntoTabBlNl()); //se agregan al ArrayList de mapeo los conjuntos de simbolos en el mismo orden que figuran en las columnas de la matriz de transicion de estados
        		cargarMatrizTransicionEstados();
    		}
    	} catch (IOException e) {
            e.printStackTrace();
        }
	}
    
    public int obtenerSiguienteToken() {
    	try {
    		if (numero_linea == 1 && indice_caracter_leer == 0) //para poder hacer el primer print de linea
    			System.out.print("Linea " + numero_linea + ":	");
            estado_actual = 0; //ya que siempre se comienza a buscar un nuevo token desde el estado 0 (inicial)
            Par_Token_Lexema par_token_lexema = new Par_Token_Lexema(-1, "");
            while (linea != null) {
                while (indice_caracter_leer < linea.length()) {
                	if (estado_actual != -2) { //ya que puede que se esten descartando caracteres para el caso especial de las cadenas (ya que este descarte puede ser de mas de una linea)
                		char caracter = linea.charAt(indice_caracter_leer);
                        boolean columna_correcta = false;
                        int columna = 0;
                        while (!columna_correcta && columna < mapeo_columnas_mte.size()) { //si este bucle termina porque el numero de columna se paso del limite del arreglo, es porque contiene el valor de la columna asociada a caracteres no pertenecientes al lenguaje
                        	if (mapeo_columnas_mte.get(columna).contieneSimbolo(caracter))
                        		columna_correcta = true;
                        	else
                        		columna++;
                        }
                        indice_caracter_leer++;
                        AccionSemantica accion_semantica = matriz_transicion_estados[estado_actual][columna].getAccion();
                        if (accion_semantica != null)
                        	matriz_transicion_estados[estado_actual][columna].getAccion().ejecutar(par_token_lexema, caracter);
                        int token = par_token_lexema.getToken();
                        estado_actual = matriz_transicion_estados[estado_actual][columna].getEstado();
                        if (token != -1) {
							if (token == 273 || token == 274 || token == 275 || token == 276 || token == 277) //tokens asociados a los 3 tipos de constantes, a las cadenas multilinea y a los identificadores, cuyos tokens y lexemas se guardan en la tabla de simbolos
								clave_tabla_simbolos = par_token_lexema.getLexema();
							else
								clave_tabla_simbolos = null;
							return token;
						}
                        if (estado_actual == -1) { //este es el caso de que el caracter leido haya generado un error que genere tipo de descarte -1 (que es para los casos en los que el error no es en una cadena de caracteres)
                        	indice_caracter_leer--; //ya que puede que el caracter que genero el problema sea un caracter de sincronizacion, por eso hay que analizarlo tambien
                        	descartarCaracteres(-1);
                        }
                	}
                	if (estado_actual == -2) //no va el else ya que puede que inicialmente el estado no sea -2 pero que luego del analisis del nuevo caracter si
                		descartarCaracteres(-2);
                }
                linea = br.readLine();
                if (linea != null) {
                	numero_linea++;
                	linea = linea + '\n'; //se incluye el salto de linea final, sino no se toma como parte del String
                	indice_caracter_leer = 0;
                	System.out.println();
                    System.out.print("Linea " + numero_linea + ":	");
                }
            }
            if (estado_actual == -2 || estado_actual == 4) //porque puede pasar que una cadena de caracteres con errores (-2) o sin errores (4) no haya sido cerrada
            	errores_y_warnings.add("Linea " + numero_linea + " / Posicion " + (indice_caracter_leer -1) + " - ERROR: Fin de programa con cadena de caracteres nunca cerrada");
            if (estado_actual == 18 || estado_actual == 19)
            	errores_y_warnings.add("Linea " + numero_linea + " / Posicion " + (indice_caracter_leer -1) + " - WARNING: Fin de programa con comentario nunca cerrado"); //porque puede pasar que un comentario nunca se cierre, pero en este caso es un warning y no un error lo que se muestra	
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    	return 0; //en este caso ya se llego al fin del archivo por lo que se retorna el 0
	}

	public void imprimirTsYErrores() {
		System.out.println();
		System.out.println();
		System.out.println("------------------------ERRORES Y WARNINGS------------------------");
		System.out.println();
		for (String error_o_warning: errores_y_warnings)
			System.out.println(error_o_warning);
		System.out.println();
		System.out.println("------------------------TABLA DE SIMBOLOS------------------------");
		System.out.println();
		for (String key: simbolos.keySet()) {
			System.out.println("Lexema: " + key + " - Token: " + simbolos.get(key).getToken());
		}
		System.out.println();	
	}

	public void eliminarConstanteTS(String constante_fuera_rango) { //esta funcion se encarga de eliminar una constante de la tabla de simbolos, y es usada por el parser cuando detecta que una constante entera simple se encuentra fuera de rango
		simbolos.remove(constante_fuera_rango);
	}

	public void constanteNegativaDetectada(String modulo_constante) { //esta funcion actualiza la tabla de simbolos cuando se detecta una nueva constante negativa desde el parser
		String constante_negativa = "-" + modulo_constante;
		AtributosSimbolo token_cantidad_constante_negativa = simbolos.get(constante_negativa);
		if (token_cantidad_constante_negativa == null) {
			Generador_Token generador_token = new Generador_Token();
			int token = generador_token.obtenerToken(modulo_constante);
			simbolos.put(constante_negativa, new AtributosSimbolo(token, 1, simbolos.get(modulo_constante).getTipo()));
		}
		else
			token_cantidad_constante_negativa.incrementarCantidad(); //si ya el parser habia detectado una constante negativa con ese mismo modulo, entonces ya esta en la tabla de simbolos y simplemente se incrementa la cantidad de ocurrencias de la misma
		AtributosSimbolo token_cantidad_constante_positiva = simbolos.get(modulo_constante);
		token_cantidad_constante_positiva.decrementarCantidad(); //se decrementa la cantidad de ocurrencias detectadas de la constante positiva en la tabla de simbolos, ya que en realidad una es negativa
        if (token_cantidad_constante_positiva.getCantidad() == 0) //si en realidad no hay ninguna constante entera con el modulo de la constante negativa hasta el momento, se elimina de la tabla de simbolos la constante positiva
            simbolos.remove(modulo_constante);
	}
    
    private void descartarCaracteres(int tipo_descarte) { //este metodo se encarga de descartar caracteres luego de un error, hasta alcanzar un caracter de sincronizacion. Si el tipo de descarte es -1, es el descarte normal. Si es -2, es el descarte para el caso de las cadenas de caracteres
    	boolean caracter_sincronizacion_encontrado = false;
    	while (indice_caracter_leer < linea.length() && !caracter_sincronizacion_encontrado) {
    		char caracter = linea.charAt(indice_caracter_leer);
    		if (tipo_descarte == -1) {
    			if (caracter == ' ' || caracter == ',' || caracter == '\t' || caracter == '\n') //estos son los caracteres de sincronizacion a partir de los cuales se sigue compilando y detectando tokens luego de un error para el caso del tipo de descarte -1
	    			caracter_sincronizacion_encontrado = true;
    			else
    				indice_caracter_leer++;
    		}
    		if (tipo_descarte == -2) {
    			if (caracter == '%') //en el caso de que el error sea por un caracter invalido dentro de una cadena de caracteres, el unico caracter de sincronizacion es el "%"
    				caracter_sincronizacion_encontrado = true;
    			indice_caracter_leer++; //no hay else ya que el caracter de sincronizacion no debe ser considerado desde otra iteracion para buscar un token, porque es parte de la cadena
    		}
    	}
    	if (caracter_sincronizacion_encontrado) //si bien para el tipo de descarte -1 siempre se va a encontrar un token de sincronizacion (que a lo sumo es el fin de linea), para el otro caso no necesariamente
    		estado_actual = 0;
    }
    
    private AccionSemantica retornarAccionSemantica(String accion_semantica) {
    	if (accion_semantica.equals("AS1"))
    		return (new AccionSemantica1());
    	if (accion_semantica.equals("AS2"))
    		return (new AccionSemantica2());
    	if (accion_semantica.equals("AS3"))
    		return (new AccionSemantica3());
    	if (accion_semantica.equals("AS4"))
    		return (new AccionSemantica4());
    	if (accion_semantica.equals("AS5"))
    		return (new AccionSemantica5());
    	if (accion_semantica.equals("AS6"))
    		return (new AccionSemantica6());
    	if (accion_semantica.equals("AS7"))
    		return (new AccionSemantica7());
    	if (accion_semantica.equals("AS8"))
    		return (new AccionSemantica8());
    	if (accion_semantica.equals("AS9"))
    		return (new AccionSemantica9());
		if (accion_semantica.equals("AS10"))
    		return (new AccionSemantica10());
		if (accion_semantica.equals("AS11"))
    		return (new AccionSemantica11());
    	return null;
    }

	private void cargarMatrizTransicionEstados () {
		try {
            BufferedReader br = new BufferedReader(new FileReader(".\\src\\analizadorSintactico\\analizadorLexico\\MatrizTransicionEstados.txt"));
            String linea;
            int fila = 0;
            int columna = 0;
            while ((linea = br.readLine()) != null) {
                String[] celdas = linea.split(" ");
                if (celdas.length > 1) { //para evitar problemas por la ultima linea que es vacia
                	for (String celda : celdas) {
                        if (celda.equals("ERROR")) {
                        	if (fila != 4)
                        		matriz_transicion_estados[fila][columna] = new Par_Accion_Estado(-1, new AccionSemanticaError());
                        	else
                        		matriz_transicion_estados[fila][columna] = new Par_Accion_Estado(-2, new AccionSemanticaError()); //porque esta es la casilla asociada al caso de que dentro de una cadena de caracteres haya un caracter no permitido por el lenguaje, que es un caso distinto
                        }
                        else {
                        	int posicion_barra = celda.indexOf('/');
                        	String prox_estado = celda.substring(0, posicion_barra);
                        	if (prox_estado.equals("F"))
                        		matriz_transicion_estados[fila][columna] = new Par_Accion_Estado(0, retornarAccionSemantica(celda.substring(posicion_barra+1, celda.length())));
                        	else
                        		matriz_transicion_estados[fila][columna] = new Par_Accion_Estado(Integer.valueOf(prox_estado), retornarAccionSemantica(celda.substring(posicion_barra+1, celda.length())));
                        }
                        columna++;
                    }
                    columna = 0;
                    fila++;
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}
