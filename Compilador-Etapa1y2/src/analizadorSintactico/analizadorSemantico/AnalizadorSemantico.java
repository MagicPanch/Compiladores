package analizadorSintactico.analizadorSemantico;
import java.util.ArrayList;

import analizadorSintactico.analizadorLexico.AnalizadorLexico;
import analizadorSintactico.analizadorLexico.AtributosSimbolo;
import analizadorSintactico.generadorCodigoIntermedio.ParVariableAtributo;

public class AnalizadorSemantico {
	public static String ambito_actual = "main";
	public static boolean ambito_invalido_detectado = false;
	public static String ambito_metodos_impl_actual = "";
	public static ArrayList<String> ambitos_metodos_impl = new ArrayList<String>();
	public static String lexema_funcion_clausula_impl_actual = null;
	public static int cantidad_prototipos_interfaz_actual = 0;
	public static int cantidad_prototipos_interfaz_utilizados = 0;
	public static ArrayList<DatosAnidamiento> metodos_actuales_anidamientos = new ArrayList<DatosAnidamiento>();
	public static String lexema_invocacion_metodo_actual = null;
	
	public AnalizadorSemantico() {}
	
	public void actualizarAmbitoActual(String ambito_nuevo) {
		if (!ambito_invalido_detectado) {
			if (ambito_nuevo.equals("-")) {
				int posicion_ultimo_ambito = ambito_actual.lastIndexOf(":");
				if (posicion_ultimo_ambito == -1)
					ambito_actual = "";
				else
					ambito_actual = ambito_actual.substring(0, posicion_ultimo_ambito);	
			}
			else if (ambito_nuevo.equals("()")) { //este caso quiere decir que se cerro una clase
				int posicion_ultimo_ambito_clase = ambito_actual.lastIndexOf("(");
				ambito_actual = ambito_actual.substring(0, posicion_ultimo_ambito_clase);
				cantidad_prototipos_interfaz_utilizados = 0;
			}
			else if (ambito_nuevo.equals("[]")) { //este caso quiere decir que se cerro una interfaz
				int posicion_ultimo_ambito_interfaz = ambito_actual.lastIndexOf("[");
				ambito_actual = ambito_actual.substring(0, posicion_ultimo_ambito_interfaz);
				cantidad_prototipos_interfaz_actual = 0;
			}
			else if (ambito_nuevo.equals("<>")) { //este caso quiere decir que se cerro una clausula IMPL
				int posicion_ultimo_ambito_impl = ambito_actual.lastIndexOf("<");
				ambito_actual = ambito_actual.substring(0, posicion_ultimo_ambito_impl);
				int cantidad_ambitos_impl_actuales = ambitos_metodos_impl.size();
				if (cantidad_ambitos_impl_actuales > 0) { //si llego hasta aca se supone que deberia ser mayor a 0 pero se pregunta igual
					ambitos_metodos_impl.remove(cantidad_ambitos_impl_actuales-1);
					cantidad_ambitos_impl_actuales--;
					if (cantidad_ambitos_impl_actuales > 0) 
						ambito_metodos_impl_actual = ambitos_metodos_impl.get(cantidad_ambitos_impl_actuales-1); //de este modo, si por ejemplo hay una clausula IMPL que contiene una funcion que adentro tiene otra clausula IMPL, al salir de la mas interna se conserva el ambito de la clausula externa para cuando termine la definicion de la funcion
					else
						ambito_metodos_impl_actual = "";
				}
				else
					ambito_metodos_impl_actual = "";
			}
			else if (ambito_nuevo.charAt(0) == '(' || ambito_nuevo.charAt(0) == '[' || ambito_nuevo.charAt(0) == '<') //este caso quiere decir que se entro en una clase si inicia con parentesis, en una interfaz si es con corchetes, o en una clausula IMPL si empieza con un "<" (no se marcan como ambitos nuevos como tales, pero es necesario diferenciarlos del ambiente externo)
				ambito_actual = ambito_actual + ambito_nuevo;
			else
				ambito_actual = ambito_actual + ":" + ambito_nuevo;
			System.out.print("   " + ambito_actual + "   ");
		}
		else { //este es el caso de que se haya entrado en un ambito que es invalido ya que es tras una redeclaracion o por una clausula IMPL que implementa metodos de una clase inexistente
			if (ambito_nuevo.charAt(0) == '(')
				ambito_actual = ambito_actual + "(INVALIDO)";
			else if (ambito_nuevo.charAt(0) == '[')
				ambito_actual = ambito_actual + "[INVALIDO]";
			else if (ambito_nuevo.charAt(0) == '<') {
				ambito_actual = ambito_actual + "<INVALIDO>";
				ambitos_metodos_impl.add("INVALIDO");
			}
			else
				ambito_actual = ambito_actual + ":INVALIDO";
			ambito_invalido_detectado = false; //esto se hace a traves de una variable que aqui se vuelve a setear en false y se usa una marca de "INVALIDO" en el ambito actual. No se podria hacer seteando la variable en false al llegar al final de la estructura sintactica, porque si adentro contiene otras estructuras sintacticas el cierre de estas provocaria que esta variable se setee en false antes de tiempo
		}
	}
	
	public void marcarAmbitoInvalido(String marca_ambito_invalido) { //permite definir un ambito como invalido cuando el problema es causado por una cuestion sintactica que impide registrar el nuevo ambito (por ejemplo que falte el ID de nombre en una funcion)
		ambito_invalido_detectado = true;
		actualizarAmbitoActual(marca_ambito_invalido);
	}
	
	private String obtenerTipoAmbitoContenedor(String ambito) {
		int posicion_ultimo_ambito = ambito.lastIndexOf(":");
		int posicion_ultimo_ambito_clase = ambito.lastIndexOf("(");
		int posicion_ultimo_ambito_impl = ambito.lastIndexOf("<");
		int posicion_ultimo_ambito_interfaz = ambito.lastIndexOf("[");
		if (posicion_ultimo_ambito > posicion_ultimo_ambito_clase && posicion_ultimo_ambito > posicion_ultimo_ambito_impl && posicion_ultimo_ambito > posicion_ultimo_ambito_interfaz)
			return "ambito_simple";
		else if (posicion_ultimo_ambito_clase > posicion_ultimo_ambito_impl && posicion_ultimo_ambito_clase > posicion_ultimo_ambito_interfaz)
			return "ambito_clase";
		else if (posicion_ultimo_ambito_impl > posicion_ultimo_ambito_interfaz)
			return "ambito_clausula_impl";
		else if (posicion_ultimo_ambito_interfaz > posicion_ultimo_ambito_impl)
			return "ambito_interfaz";
		else //si se llega a este else es porque todos valen lo mismo, lo que quiere decir que el ambito es el main, o sea un ambito simple
			return "ambito_simple";
	}
	
	private boolean esDeClase(String ambito) { //este metodo determina si una variable o funcion es propia de una clase (por lo que seria un atributo o un metodo respectivamente)  
		if (obtenerTipoAmbitoContenedor(ambito).equals("ambito_clase"))
			return true;
		return false;
	}
	
	private boolean esDeInterfaz(String ambito) { //este metodo determina si un prototipo es propio de una interfaz  
		if (obtenerTipoAmbitoContenedor(ambito).equals("ambito_interfaz"))
			return true;
		return false;
	}
	
	private boolean esDeClausulaIMPL(String ambito) { //este metodo determina si una funcion es propia de una clausula IMPL
		if (obtenerTipoAmbitoContenedor(ambito).equals("ambito_clausula_impl"))
			return true;
		return false;
	}
	
	private boolean esAmbitoValido() { //no pasa por si la variable ambito_invalido_detectado es false, ya que en realidad esta solo se usa para agregar la marca de INVALIDO al ambito actual, pero no para saber en todo momento si el ambito es valido o no dado que eso traeria inconvenientes
		return !ambito_actual.contains("INVALIDO");
	}
	
	private boolean existePrototipoMetodo(String nombre_prototipo, String ambito_busqueda) {
		String lexema_prototipo_nm = nombre_prototipo + ":" + ambito_busqueda;
		AtributosSimbolo atributos_prototipo_nm = AnalizadorLexico.simbolos.get(lexema_prototipo_nm);
		if (atributos_prototipo_nm != null && atributos_prototipo_nm.getUso().equals("nombre_prototipo_metodo"))
			return true;
		return false;
	}
	
	public void chequearParametroFuncionIMPL(String parametro, String tipo) { //este metodo chequea que el parametro de una funcion de una clausula IMPL coincida con el de un prototipo que esta implementando (que se supone que el nombre ya se chequeo por separado antes)
		if (esDeClausulaIMPL(obtenerLexemaAmbitoSuperior(ambito_actual))) { //se hace el chequeo de parametro separado del de nombre de funcion (no es como para el caso del chequeo de si un metodo implementado en una clase es uno de la interfaz, porque ahi a lo sumo no se considera como la implementacion del prototipo de la interfaz pero si el metodo como valido) 
			if (esAmbitoValido()) {											//entonces, como un error de ambito invalido puede producirse entre medio (por un error sintactico en el parametro), y en tal caso eliminar la entrada del nombre de la funcion impl
				if (lexema_funcion_clausula_impl_actual != null) { //de todas formas no tiene sentido que no se cumpla
					String nombre_prototipo = lexema_funcion_clausula_impl_actual.substring(0, lexema_funcion_clausula_impl_actual.indexOf(":"));
					String lexema_prototipo_nm = nombre_prototipo + ":" + ambito_metodos_impl_actual;
					AtributosSimbolo atributos_prototipo_nm = AnalizadorLexico.simbolos.get(lexema_prototipo_nm);
					if (atributos_prototipo_nm != null) { //de todas formas no tiene sentido que no se cumpla
						String nombre_parametro_en_prototipo = atributos_prototipo_nm.getNombreParametroFormal();
						if (nombre_parametro_en_prototipo != null && parametro != null && (nombre_parametro_en_prototipo.equals(parametro))) {
							String lexema_parametro_en_prot_nm = nombre_parametro_en_prototipo + ":" + ambito_metodos_impl_actual + ":" + nombre_prototipo; //es el lexema del parametro del prototipo de la clase
							AtributosSimbolo atributos_parametro_prototipo_nm = AnalizadorLexico.simbolos.get(lexema_parametro_en_prot_nm);
							if (atributos_parametro_prototipo_nm == null || !atributos_parametro_prototipo_nm.getTipo().equals(tipo)) { //se llaman igual y sus parametros tambien, y se define si es la implementacion de ese metodo en base a si el tipo de los parametros es igual
								int posicion_ultimo_ambito_clase = ambito_metodos_impl_actual.lastIndexOf("(");
								String nombre_clase_impl = ambito_metodos_impl_actual.substring(posicion_ultimo_ambito_clase+1, ambito_metodos_impl_actual.length()-1);
								AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: No existe el prototipo de metodo '" + nombre_prototipo + "' dentro de la clase '" + nombre_clase_impl + "' con la misma estructura de parametro indicada");
								int posicion_primero_ambito_funcion = lexema_funcion_clausula_impl_actual.indexOf(":");
								String lexema_parametro_clausula_nm = parametro + ":" + lexema_funcion_clausula_impl_actual.substring(posicion_primero_ambito_funcion+1, lexema_funcion_clausula_impl_actual.length()) + ":" + lexema_funcion_clausula_impl_actual.substring(0, posicion_primero_ambito_funcion);
								AnalizadorLexico.simbolos.remove(lexema_funcion_clausula_impl_actual);
								AnalizadorLexico.simbolos.remove(lexema_parametro_clausula_nm);
								atributos_prototipo_nm.setLexemaFuncionImplementacion(null); //se elimina la marca que se establecio antes para el prototipo de la funcion que lo implementaba (ya que el nombre coincide pero los parametros que se chequean luego no)
								actualizarAmbitoActual("-"); //se regresa hasta antes de registrar el ambito de la funcion
								marcarAmbitoInvalido(":"); //se lo marca como invalido
							}
						}
						else if (nombre_parametro_en_prototipo != null || parametro != null) { //en este caso uno tiene parametro y el otro no, o tienen partametro ambos pero se llaman distinto
							int posicion_ultimo_ambito_clase = ambito_metodos_impl_actual.lastIndexOf("(");
							String nombre_clase_impl = ambito_metodos_impl_actual.substring(posicion_ultimo_ambito_clase+1, ambito_metodos_impl_actual.length()-1);
							AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: No existe el prototipo de metodo '" + nombre_prototipo + "' dentro de la clase '" + nombre_clase_impl + "' con la misma estructura de parametro indicada");
							int posicion_primero_ambito_funcion = lexema_funcion_clausula_impl_actual.indexOf(":");
							String lexema_parametro_clausula_nm = parametro + ":" + lexema_funcion_clausula_impl_actual.substring(posicion_primero_ambito_funcion+1, lexema_funcion_clausula_impl_actual.length()) + ":" + lexema_funcion_clausula_impl_actual.substring(0, posicion_primero_ambito_funcion);
							AnalizadorLexico.simbolos.remove(lexema_funcion_clausula_impl_actual);
							AnalizadorLexico.simbolos.remove(lexema_parametro_clausula_nm);
							atributos_prototipo_nm.setLexemaFuncionImplementacion(null);
							actualizarAmbitoActual("-"); //se regresa hasta antes de registrar el ambito de la funcion
							marcarAmbitoInvalido(":"); //se lo marca como invalido
						}
					}
				}
			}
			else {
				if (lexema_funcion_clausula_impl_actual != null) { //ya que como para el caso de las funciones el chequeo de parametro se hace separado del de encabezado, puede pasar que el ambito se haya vuelto invalido por un error sintactico del parametro, pero que antes de eso el lexema de la funcion se haya guardado igual
					String nombre_prototipo = lexema_funcion_clausula_impl_actual.substring(0, lexema_funcion_clausula_impl_actual.indexOf(":"));
					String lexema_prototipo_nm = nombre_prototipo + ":" + ambito_metodos_impl_actual;
					AtributosSimbolo atributos_prototipo_nm = AnalizadorLexico.simbolos.get(lexema_prototipo_nm);
					if (atributos_prototipo_nm != null)
						atributos_prototipo_nm.setLexemaFuncionImplementacion(null);
					AnalizadorLexico.simbolos.remove(lexema_funcion_clausula_impl_actual);
				} //para este caso no es necesario marcar que se detecto un ambito invalido como true, ya que el ambito ya es invalido
			}
		}
	}
	
	private void setearLexemaImplementacionPrototipoMetodo(String nombre_prototipo, String lexema_implementacion_nm, String ambito_busqueda) {
		String lexema_prototipo_nm = nombre_prototipo + ":" + ambito_busqueda;
		AtributosSimbolo atributos_prototipo_nm = AnalizadorLexico.simbolos.get(lexema_prototipo_nm);
		if (atributos_prototipo_nm != null && atributos_prototipo_nm.getUso().equals("nombre_prototipo_metodo")) //en teoria como estos chequeos ya deberian haberse hecho, tendria que ser siempre true, pero se pregunta por las dudas
			atributos_prototipo_nm.setLexemaFuncionImplementacion(lexema_implementacion_nm);
	}
	
	private void setearParametroMetodoFuncionPrototipo(String nombre_parametro) { //permite guardar en la tabla de simbolos el nombre del parametro formal de una funcion, metodo o prototipo del ambito que sea
		int posicion_ultimo_ambito = ambito_actual.lastIndexOf(":");
		if (posicion_ultimo_ambito != -1) { //no tendria sentido que esta condicion no se cumpla ya que si el ambito es valido (lo cual se chequea en el metodo anterior) se supone que la funcion, metodo o prototipo fue registrada recientemente, pero se hace el chequeo por las dudas
			String nombre_funcion_metodo_prototipo = ambito_actual.substring(posicion_ultimo_ambito+1, ambito_actual.length());
			String lexema_funcion_metodo_prototipo_nm = nombre_funcion_metodo_prototipo + ":" + ambito_actual.substring(0, posicion_ultimo_ambito);
			AtributosSimbolo atributos_id_nm = AnalizadorLexico.simbolos.get(lexema_funcion_metodo_prototipo_nm);
			if (atributos_id_nm != null  && ((atributos_id_nm.getUso().equals("nombre_funcion") || atributos_id_nm.getUso().equals("nombre_metodo")) || atributos_id_nm.getUso().equals("nombre_funcion_clausula_impl"))) //no tendria sentido que no se cumpla por lo mismo que la anterior condicion. Ademas, se pregunta por las dudas el uso (no se pregunta si es un prototipo ya que en realidad hasta el momento no se sabe si es un prototipo o no)
				atributos_id_nm.setNombreParametroFormal(nombre_parametro);
		}
	}
	
	private boolean verificarNoSobreescribeMetodosHeredados(String lexema_clase_heredada, boolean declaracion_referencia, String nombre_metodo) { //este metodo permite recorrer la cadena de herencia por composicion verificando que nunca se sobreescriba el nombre de un metodo heredado. El parametro booleano es para que ante un eventual error se imprima algo si el problema fue luego de declarar una referencia a clase o un metodo/prototipo de metodo, y el parametro de nombre es usado cuando la declaracion fue la de un metodo o prototipo (ya que de la clase actual solo se querria comparar el nombre de ese)
		int posicion_primer_ambito_clase_heredada = lexema_clase_heredada.indexOf(":");
		String ambito_interno_clase_heredada = lexema_clase_heredada.substring(posicion_primer_ambito_clase_heredada+1, lexema_clase_heredada.length()) + "(" + lexema_clase_heredada.substring(0, posicion_primer_ambito_clase_heredada) + ")";
		ArrayList<String> metodos_clase_heredada = new ArrayList<String>();
		ArrayList<String> metodos_clase_actual = new ArrayList<String>();
		for (String key: AnalizadorLexico.simbolos.keySet()) { //en este for se obtienen todos los nombres de metodos o prototipos de metodos pertenecientes a la clase heredada y a la que hereda
			int posicion_primer_ambito = key.indexOf(":");
			if (posicion_primer_ambito != -1) {
				AtributosSimbolo atributos_elemento_nm = AnalizadorLexico.simbolos.get(key);
				if (atributos_elemento_nm != null && (atributos_elemento_nm.getUso().equals("nombre_metodo") || atributos_elemento_nm.getUso().equals("nombre_prototipo_metodo"))) {
					String ambito_completo = key.substring(posicion_primer_ambito+1, key.length());
					if (ambito_completo.equals(ambito_interno_clase_heredada)) //es el caso de que sea un nombre de metodo o de prototipo de metodo perteneciente a la clase heredada por composicion
						metodos_clase_heredada.add(key.substring(0, posicion_primer_ambito));
					if (declaracion_referencia && ambito_completo.equals(ambito_actual)) //es el caso de que sea un nombre de metodo o de prototipo de metodo perteneciente a la clase que hereda
						metodos_clase_actual.add(key.substring(0, posicion_primer_ambito));
				}
			}
		}
		if (!declaracion_referencia)
			metodos_clase_actual.add(nombre_metodo);
		for (String metodo_clase_actual: metodos_clase_actual)
			for (String metodo_clase_heredada: metodos_clase_heredada)
				if (metodo_clase_actual.equals(metodo_clase_heredada)) {
					String nombre_clase_actual = ambito_actual.substring(ambito_actual.lastIndexOf("(")+1, ambito_actual.length()-1);
					String nombre_clase_heredada = lexema_clase_heredada.substring(0, lexema_clase_heredada.indexOf(":"));
					if (declaracion_referencia)
						AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: La clase '" + nombre_clase_actual + "' ya posee un metodo llamado '" + metodo_clase_actual + "', y no puede sobreescribir a un metodo con el mismo nombre presente en la clase '" + nombre_clase_heredada + "', que se heredaria por la cadena de composicion");
					else
						AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: La clase '" + nombre_clase_actual + "' ya hereda por la cadena de composicion un metodo llamado '" + metodo_clase_heredada + "', de la clase '" + nombre_clase_heredada + "', y no puede sobreescribirlo");
					return false;
				}
		AtributosSimbolo atributos_clase_heredada_nm = AnalizadorLexico.simbolos.get(lexema_clase_heredada);
		if (atributos_clase_heredada_nm != null) { //de todas formas no tendria sentido que sea null
			ArrayList<String> lexemas_clases_heredadas_por_cadena = atributos_clase_heredada_nm.getClasesHeredadas();
			for (String clase: lexemas_clases_heredadas_por_cadena)
				if (!verificarNoSobreescribeMetodosHeredados(clase, declaracion_referencia, nombre_metodo)) //se chequea que tampoco se sobreescriban metodos de clases heredadas por la que se hereda (dado que forman parte de la cadena de la herencia por composicion)
					return false;
		}
		return true;
	}
	
	private boolean referenciaClaseValida(String referencia_clase) { //permite verificar que una referencia a una clase para efectuar herencia por composicion sea valida
		String lexema_clase_heredada_nm = obtenerClaseAsociada(referencia_clase);
		if (lexema_clase_heredada_nm != null) {
			if (esDeClase(ambito_actual)) { //si la referencia a la clase se declaro dentro de una clase, entonces hay que chequear que no se esten sobreescribiendo metodos
				return verificarNoSobreescribeMetodosHeredados(lexema_clase_heredada_nm, true, null);
			}
			else //si se declaro una referencia a una clase fuera de una clase en si, no hay que hacer ningun otro chequeo mas que exista la clase referenciada
				return true;
		}
		AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: No existe una clase llamada '" + referencia_clase + "' al alcance que pueda heredarse por composicion");
		return false;
	}
	
	private void registrarNuevaClaseHeredada(String lexema_clase, String lexema_clase_heredada) {
		AtributosSimbolo atributos_clase_nm = AnalizadorLexico.simbolos.get(lexema_clase);
		if (atributos_clase_nm != null)
			atributos_clase_nm.agregarClaseHeredada(lexema_clase_heredada);
	}
	
	private void agregarVariableAdicional(String primera_parte_nombre, String segunda_parte_nombre, AtributosSimbolo atributos_elemento_nm, boolean es_referencia_clase) { //este metodo agrega una entrada adicional a la tabla de simbolos correspondiente a una instancia de un atributo de una clase o a una instancia de una referencia a una clase
		String lexema_nuevo_id_nm = primera_parte_nombre + "." + segunda_parte_nombre + ":" + ambito_actual;
		AtributosSimbolo atributos_lexema_nuevo = new AtributosSimbolo(atributos_elemento_nm.getToken());
		atributos_lexema_nuevo.setTipo(atributos_elemento_nm.getTipo());
		atributos_lexema_nuevo.setLexemaClaseAsociada(atributos_elemento_nm.getLexemaClaseAsociada());
		if (es_referencia_clase)
			atributos_lexema_nuevo.setUso("referencia_clase");
		else if (esDeClase(ambito_actual)) //porque en este caso es una variable, y la variable nueva generada puede estar en una clase o no (por ejemplo, si el prefijo es una instancia declarada en el main, es una variable, si es una instancia en una clase, es tambien un atributo)
			atributos_lexema_nuevo.setUso("nombre_atributo");
		else
			atributos_lexema_nuevo.setUso("nombre_variable");
		AnalizadorLexico.simbolos.put(lexema_nuevo_id_nm, atributos_lexema_nuevo);
	}
	
	private void generarVariablesAdicionales(String lexema_clase, boolean declaracion_referencia, String nombre_instancia) { //permite generar las variables adicionales surgidas por una herencia por composicion o por la declaracion de una instancia de una clase
		int posicion_primer_ambito_clase = lexema_clase.indexOf(":");
		String nombre_clase = lexema_clase.substring(0, posicion_primer_ambito_clase);
		String ambito_interno_clase = lexema_clase.substring(posicion_primer_ambito_clase+1, lexema_clase.length()) + "(" + nombre_clase + ")";
		ArrayList<String> lexemas_atributos_y_referencias_clase = new ArrayList<String>();
		for (String key: AnalizadorLexico.simbolos.keySet()) { //en este for se obtienen todos los lexemas de atributos o referencias de la clase
			int posicion_primer_ambito = key.indexOf(":");
			if (posicion_primer_ambito != -1) {
				AtributosSimbolo atributos_elemento_nm = AnalizadorLexico.simbolos.get(key);
				if (atributos_elemento_nm != null && (atributos_elemento_nm.getUso().equals("nombre_atributo") || atributos_elemento_nm.getUso().equals("referencia_clase"))) {
					String ambito_completo = key.substring(posicion_primer_ambito+1, key.length());
					if (ambito_completo.equals(ambito_interno_clase)) //es el caso de que sea un nombre de variable a agregar
						lexemas_atributos_y_referencias_clase.add(key);
				}
			}
		}
		for (String lexema_id_nm: lexemas_atributos_y_referencias_clase) { //en este for se generan todos los nuevos lexemas que corresponda
			int posicion_primer_ambito = lexema_id_nm.indexOf(":");
			AtributosSimbolo atributos_elemento_nm = AnalizadorLexico.simbolos.get(lexema_id_nm);
			if (declaracion_referencia) //declaracion_referencia vale true si la declaracion que genero la nueva entrada de la tabla de simbolos fue una referencia a clase y false si fue una instancia de una clase
				agregarVariableAdicional(nombre_clase, lexema_id_nm.substring(0, posicion_primer_ambito), atributos_elemento_nm, atributos_elemento_nm.getUso().equals("referencia_clase")); //el parametro booleano de la comparacion del final indica si el elemento encontrado es una referencia a clase o un atributo (no es lo mismo que el boolean de la declaracion)
			else
				agregarVariableAdicional(nombre_instancia, lexema_id_nm.substring(0, posicion_primer_ambito), atributos_elemento_nm, atributos_elemento_nm.getUso().equals("referencia_clase"));
		}
	}
	
	private boolean metodoValido(String nombre_metodo) { //es para chequear en el caso de la declaracion de un metodo o prototipo de metodo si no sobreescribe ningun metodo heredado por composicion
		if (esDeClase(ambito_actual)) { //igual se supone que si llega aca es porque es un ambito de clase
			String lexema_clase_actual_nm = ambito_actual.substring(ambito_actual.lastIndexOf("(")+1, ambito_actual.length()-1) + ":" + obtenerLexemaAmbitoSuperior(ambito_actual);
			AtributosSimbolo atributos_clase_actual_nm = AnalizadorLexico.simbolos.get(lexema_clase_actual_nm);
			if (atributos_clase_actual_nm != null) {
				ArrayList<String> lexemas_clases_heredadas = atributos_clase_actual_nm.getClasesHeredadas();
				for (String clase: lexemas_clases_heredadas) //el analisis es respecto a los metodos heredados por composicion, no a los propios de la clase ya que de esos no es necesario en este caso
					if (!verificarNoSobreescribeMetodosHeredados(clase, false, nombre_metodo))
						return false;
			}
		}
		return true;
	}
	
	public void registrarAmbitoUsoID(String lexema, String uso) { //se utiliza para registrar el ambito y uso de los ID declarados segun corresponda, y enviar un error en caso de que ya exista un ID con ese nombre en ese ambito. En algunos casos permite registrar otros datos
		if (esAmbitoValido()) {
			if (uso.equals("nombre_variable") && esDeClase(ambito_actual))
				uso = "nombre_atributo";
			if (uso.equals("nombre_funcion")) {
				if (esDeClase(ambito_actual))
					uso = "nombre_metodo";
				else if (esDeClausulaIMPL(ambito_actual))
					uso = "nombre_funcion_clausula_impl";
			}
			if ((!uso.equals("nombre_funcion_clausula_impl") && !uso.equals("referencia_clase")) && !uso.equals("nombre_metodo") && !uso.equals("nombre_prototipo_metodo") || (uso.equals("nombre_funcion_clausula_impl") && existePrototipoMetodo(lexema, ambito_metodos_impl_actual)) || (uso.equals("referencia_clase") && referenciaClaseValida(lexema)) || ((uso.equals("nombre_metodo") || uso.equals("nombre_prototipo_metodo")) && metodoValido(lexema))) {
				String lexema_nm = lexema + ":" + ambito_actual; //nm quiere decir "name mangling", porque se obtiene el lexema completo con el ambito del identificador
				AtributosSimbolo atributos_id_nm = AnalizadorLexico.simbolos.get(lexema_nm);
				if (atributos_id_nm == null) {
					atributos_id_nm = AnalizadorLexico.simbolos.remove(lexema);
					atributos_id_nm.setUso(uso);
					AnalizadorLexico.simbolos.put(lexema_nm, atributos_id_nm);
					if (uso.equals("nombre_funcion_clausula_impl")) {
						setearLexemaImplementacionPrototipoMetodo(lexema, lexema_nm, ambito_metodos_impl_actual); //permite setear el lexema de implementacion de un prototipo de metodo (o sea cual es la funcion que realmente la esta definiendo)
						metodos_actuales_anidamientos.add(new DatosAnidamiento(lexema_nm, 0, 0)); //porque lo que implementa una clausula IMPL es un metodo de una clase, y entonces es necesario hacer el control de la cantidad de anidamientos ahi tambien
						lexema_funcion_clausula_impl_actual = lexema_nm;
					}
					else if (uso.equals("nombre_parametro_formal"))
						setearParametroMetodoFuncionPrototipo(lexema);
					else if (uso.equals("nombre_metodo"))
						metodos_actuales_anidamientos.add(new DatosAnidamiento(lexema_nm, 0, 0));
					else if (uso.equals("nombre_funcion") && !esDeInterfaz(ambito_actual)) { //ya que un prototipo de interfaz que hasta este momento es detectado con uso de funcion (despues se determina que es un prototipo de interfaz), no debe ser considerado nunca como un nuevo anidamiento
						int cantidad_metodos_actuales = metodos_actuales_anidamientos.size();
						if (cantidad_metodos_actuales > 0) {
							DatosAnidamiento anidamientos_ultimo_metodo = metodos_actuales_anidamientos.get(cantidad_metodos_actuales-1);
							anidamientos_ultimo_metodo.aumentarCantidadAnidamientos();
							if (anidamientos_ultimo_metodo.getCantidadActualAnidamientos() > anidamientos_ultimo_metodo.getCantidadMaximaAnidamientos())
								anidamientos_ultimo_metodo.setCantidadMaximaAnidamientos(anidamientos_ultimo_metodo.getCantidadActualAnidamientos());
						}
					}
					else if (uso.equals("referencia_clase")) {
						String lexema_clase_heredada = obtenerClaseAsociada(lexema);
						atributos_id_nm.setLexemaClaseAsociada(lexema_clase_heredada);
						if (esDeClase(ambito_actual)) { //si la referencia a la clase fue declarada en una clase, se guarda el lexema de la clase heredada tambien en la informacion de la otra clase
							String lexema_clase_nm = ambito_actual.substring(ambito_actual.lastIndexOf("(")+1, ambito_actual.length()-1) + ":" + obtenerLexemaAmbitoSuperior(ambito_actual);
							registrarNuevaClaseHeredada(lexema_clase_nm, lexema_clase_heredada);
						}
						generarVariablesAdicionales(lexema_clase_heredada, true, null); //se generan las variables y referencias heredadas
					}
				}
				else { //es el caso de que ya exista otro identificador con ese nombre declarado en ese ambito, por lo tanto no se guarda el repetido en la tabla de simbolos
					AnalizadorLexico.simbolos.remove(lexema);
					String uso_id_existente = atributos_id_nm.getUso();
					if (uso_id_existente.equals("nombre_variable"))
						AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: Ya existe una variable con el nombre '" + lexema + "' dentro del ambito '" + ambito_actual + "'");
					else if (uso_id_existente.equals("nombre_funcion"))
						AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: Ya existe una funcion con el nombre '" + lexema + "' dentro del ambito '" + ambito_actual + "'");
					else if (uso_id_existente.equals("nombre_atributo"))
						AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: Ya existe un atributo con el nombre '" + lexema + "' dentro del ambito '" + ambito_actual + "'");
					else if (uso_id_existente.equals("nombre_metodo"))
						AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: Ya existe un metodo con el nombre '" + lexema + "' dentro del ambito '" + ambito_actual + "'");
					else if (uso_id_existente.equals("nombre_funcion_clausula_impl")) {
						int posicion_ultimo_ambito_clase = ambito_metodos_impl_actual.lastIndexOf("(");
						String nombre_clase = ambito_metodos_impl_actual.substring(posicion_ultimo_ambito_clase+1, ambito_metodos_impl_actual.length()-1);
						AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: Ya existe una funcion que implementa el metodo '" + lexema + "' de la clase '" + nombre_clase + "' dentro del ambito '" + ambito_actual + "'"); //puede haber distinas clausulas IMPL para la misma clase en un mismo ambito, pero lo que no puede pasar es que para clausulas IMPL de un mismo ambito se repita la implementacion para el mismo metodo (aunque las implementaciones esten en clausulas distinas, se considera como el mismo ambito porque la clausula IMPL no tiene un nombre propio, son implementaciones en el mismo ambito)
					}
					else if (uso_id_existente.equals("nombre_prototipo"))
						AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: Ya existe un prototipo con el nombre '" + lexema + "' dentro del ambito '" + ambito_actual + "'");
					else if (uso_id_existente.equals("nombre_prototipo_metodo"))
						AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: Ya existe un prototipo de metodo con el nombre '" + lexema + "' dentro del ambito '" + ambito_actual + "'");
					else if (uso_id_existente.equals("nombre_prototipo_interfaz"))
						AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: Ya existe un prototipo de interfaz con el nombre '" + lexema + "' dentro del ambito '" + ambito_actual + "'");
					else if (uso_id_existente.equals("nombre_clase"))
						AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: Ya existe una clase con el nombre '" + lexema + "' dentro del ambito '" + ambito_actual + "'");
					else if (uso_id_existente.equals("nombre_interfaz"))
						AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: Ya existe una interfaz con el nombre '" + lexema + "' dentro del ambito '" + ambito_actual + "'");
					else if (uso_id_existente.equals("referencia_clase"))
						AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: Ya existe una referencia a una clase (herencia por composicion) con el nombre '" + lexema + "' dentro del ambito '" + ambito_actual + "'");
					if (uso.equals("nombre_funcion") || uso.equals("nombre_metodo") || uso.equals("nombre_funcion_clausula_impl") || uso.equals("nombre_clase") || uso.equals("nombre_interfaz")) //esos son los casos para los cuales ante una redeclaracion hay que no considerar todo lo que este dentro
						ambito_invalido_detectado = true;
				}
			}
			else if (uso.equals("nombre_funcion_clausula_impl")) { //es el caso de que no exista el prototipo de funcion a implementar por una clausula IMPL en la clase correspondiente
				AnalizadorLexico.simbolos.remove(lexema);
				int posicion_ultimo_ambito_clase = ambito_metodos_impl_actual.lastIndexOf("(");
				String nombre_clase_impl = ambito_metodos_impl_actual.substring(posicion_ultimo_ambito_clase+1, ambito_metodos_impl_actual.length()-1);
				AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: No existe el prototipo de metodo '" + lexema + "' dentro de la clase '" + nombre_clase_impl + "'");
				ambito_invalido_detectado = true;
			}
			else if (uso.equals("referencia_clase")) //es el caso de que no exista una clase al alcance con el mismo nombre que una referencia o que haya un conflicto por sobreescritura de metodos debido a la cadena por composicion que genera esa referencia
				AnalizadorLexico.simbolos.remove(lexema);
			else if (uso.equals("nombre_metodo") || uso.equals("nombre_prototipo_metodo")) { //es el caso de que la declaracion de un metodo o prototipo de metodo no es valida porque implica una sobreescritura de metodo
				AnalizadorLexico.simbolos.remove(lexema);
				ambito_invalido_detectado = true;
			}
		}
		else
			AnalizadorLexico.simbolos.remove(lexema); //si el ambito del identificador por si mismo es invalido, no se lo tiene en cuenta en la tabla de simbolos (por ejemplo si es un atributo o metodo declarado en una clase redeclarada)
	}
	
	public void eliminarEntradaOriginalID(String lexema) {
		AnalizadorLexico.simbolos.remove(lexema);
	}
	
	public void definirUsoPrototipo(String nombre_prototipo) { //este metodo permite setear el uso de un simbolo declarado como funcion o metodo cuando se detecta que en realidad es un prototipo
		if (esAmbitoValido()) { //puede llegar a ser en distintos casos, por ejemplo si se detecto un prototipo con un encabezado erroneo sin el nombre de prototipo (y en tal caso, no tendria sentido hacer la modificacion del uso del nombre ya que no existe)
			String lexema_prototipo_nm = nombre_prototipo + ":" + ambito_actual;
			AtributosSimbolo atributos_prototipo_nm = AnalizadorLexico.simbolos.get(lexema_prototipo_nm);
			if (atributos_prototipo_nm != null) {
				if (esDeInterfaz(ambito_actual)) {
					atributos_prototipo_nm.setUso("nombre_prototipo_interfaz"); //cuando es el nombre de un prototipo declarado en una interfaz, para ser implementado por una clase
					cantidad_prototipos_interfaz_actual++; //permite llevar un recuento de la cantidad de prototipos validos que tiene una interfaz determinada, para asi poder guardar esto en la tabla de simbolos
				}
				else if (esDeClase(ambito_actual))
					atributos_prototipo_nm.setUso("nombre_prototipo_metodo"); //cuando es el nombre de un prototipo de metodo, lo que significa que pertenece a una clase
				else
					atributos_prototipo_nm.setUso("nombre_prototipo"); //cuando es el nombre de un prototipo que no es directamente perteneciente a una clase (si esta declarado dentro de un metodo de una clase, tambien va por este caso) ni a una interfaz
			}
		}
	}
	
	private String obtenerClaseAsociada(String tipo_clase) { //para obtener la clase asociada a una referencia por herencia o a una instancia de una clase
		ArrayList<String> usos = new ArrayList<String>();
		usos.add("nombre_clase");
		return(obtenerLexemaNmMasCercano(tipo_clase, usos, ambito_actual));
	}
	
	public void registrarTipoID(String tipo, String variables_lista) {
		String[] variables = variables_lista.split(";");
		String posible_clase_asociada = obtenerClaseAsociada(tipo); //solo puede valer algo distinto de null si el tipo es el de una clase
		if (tipo.equals("INT") || tipo.equals("ULONG") || tipo.equals("DOUBLE") || posible_clase_asociada != null) {
			for (String variable: variables) {
				String lexema_nm = variable + ":" + ambito_actual;
				AtributosSimbolo atributos_id_nm = AnalizadorLexico.simbolos.get(lexema_nm);
				if (atributos_id_nm != null && atributos_id_nm.getTipo() == null) { //porque si por ejemplo el nombre de una variable ya fue usado en otra declaracion en ese ambito, no se guarda su lexema y entonces tampoco hay que agregar el tipo para ese caso, porque sino se pisaria el de la primera declaracion (si ya tiene seteado el tipo es porque no hay que volver a setearlo)
					atributos_id_nm.setTipo(tipo);
					if (posible_clase_asociada != null) {
						atributos_id_nm.setLexemaClaseAsociada(posible_clase_asociada);
						generarVariablesAdicionales(posible_clase_asociada, false, variable); //se generan las variables adicionales asociadas a la instancia de la clase
					}
				}
			}
		}
		else {
			for (String variable: variables) {
				String lexema_nm = variable + ":" + ambito_actual;
				AtributosSimbolo atributos_id_nm = AnalizadorLexico.simbolos.get(lexema_nm);
				if (atributos_id_nm != null && atributos_id_nm.getTipo() == null) //para eliminar el lexema de la variable solamente si es el de la declaracion con tipo erroneo, porque si se da el caso de que ya existiera otra variable con ese lexema en el mismo ambito (lo que obviamente tambien generaria un error de redeclaracion), entonces se la eliminaria. La forma de saber si esto no ocurre es si su tipo esta seteado en null (ninguna variable ya declarada y que persista en la tabla de simbolos puede tener tipo null)
					AnalizadorLexico.simbolos.remove(lexema_nm);
			}
			AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: El tipo '" + tipo + "' no es valido");
		}
	}
	
	public void registrarTipoConstante(String tipo, String constante) {
		AtributosSimbolo atributos_constante = AnalizadorLexico.simbolos.get(constante);
		if (atributos_constante != null)
			atributos_constante.setTipo(tipo);
	}
	
	public void chequearValidezSimbolo(String lexema) { //permite decrementar la cantidad de ocurrencias de una cadena de caracteres o de una constante si se detecta que estan en un ambito invalido, y eliminarlos en caso de que llegue a 0
		if (!esAmbitoValido()) {
			AtributosSimbolo atributos_id = AnalizadorLexico.simbolos.get(lexema);
			if (atributos_id != null) {
				atributos_id.decrementarCantidad();
				if (atributos_id.getCantidad() == 0)
					AnalizadorLexico.simbolos.remove(lexema);
			}
		}
	}
	
	private String obtenerLexemaAmbitoSuperior(String ambito) {
		int posicion_ultimo_ambito = ambito.lastIndexOf(":");
		int posicion_ultimo_ambito_clase = ambito.lastIndexOf("(");
		int posicion_ultimo_ambito_impl = ambito.lastIndexOf("<");
		int posicion_ultimo_ambito_interfaz = ambito.lastIndexOf("[");
		if (posicion_ultimo_ambito > posicion_ultimo_ambito_clase && posicion_ultimo_ambito > posicion_ultimo_ambito_impl && posicion_ultimo_ambito > posicion_ultimo_ambito_interfaz)
			return ambito.substring(0, posicion_ultimo_ambito);
		else if (posicion_ultimo_ambito_clase > posicion_ultimo_ambito_impl && posicion_ultimo_ambito_clase > posicion_ultimo_ambito_interfaz)
			return ambito.substring(0, posicion_ultimo_ambito_clase);
		else if (posicion_ultimo_ambito_impl > posicion_ultimo_ambito_interfaz)
			return ambito.substring(0, posicion_ultimo_ambito_impl);
		else if (posicion_ultimo_ambito_interfaz > posicion_ultimo_ambito_impl)
			return ambito.substring(0, posicion_ultimo_ambito_interfaz);
		return null;
	}
	
	private boolean coincideUso(ArrayList<String> usos_validos, String uso_real) {
		for (String uso: usos_validos)
			if (uso.equals(uso_real))
					return true;
		return false;
	}
	
	private String obtenerLexemaNmMasCercano(String id, ArrayList<String> usos, String ambito) {
		String id_posible_nm = id + ":" + ambito;
		boolean lexema_encontrado = false;
		boolean todos_los_ambitos_chequeados = false;
		while (!todos_los_ambitos_chequeados && !lexema_encontrado) {
			int posicion_ultimo_ambito = id_posible_nm.lastIndexOf(":");
			if (posicion_ultimo_ambito == -1)
				todos_los_ambitos_chequeados = true;
			else {
				AtributosSimbolo atributos_id_nm = AnalizadorLexico.simbolos.get(id_posible_nm);
				if (atributos_id_nm != null && coincideUso(usos, atributos_id_nm.getUso()))
					lexema_encontrado = true;
				else
					id_posible_nm = obtenerLexemaAmbitoSuperior(id_posible_nm);
			}
		}
		if (lexema_encontrado)
			return id_posible_nm;
		return null;
	}
	
	public void chequearValidezClausulaIMPL(String clase) { //las clausulas IMPL no tienen un ID como nombre que pueda generar una redeclaracion, pero si que pueden provocar que su ambito sea invalido en caso de que la clase de la cual implementan metodos no exista o no este al alcance (eso es lo que se chequea)
		if (esAmbitoValido()) {
			ArrayList<String> uso = new ArrayList<String>();
			uso.add("nombre_clase");
			String lexema_clase_nm = obtenerLexemaNmMasCercano(clase, uso, ambito_actual);
			if (lexema_clase_nm == null) {
				AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: La clausula IMPL implementa la clase '" + clase + "', que hasta el momento no existe en su ambito ni en ninguno que lo contenga");
				ambito_invalido_detectado = true;
			}
			else {
				int posicion_primer_ambito = lexema_clase_nm.indexOf(":");
				ambito_metodos_impl_actual = lexema_clase_nm.substring(posicion_primer_ambito+1, lexema_clase_nm.length()) + "(" + clase + ")";	
				ambitos_metodos_impl.add(ambito_metodos_impl_actual); //se registra el nuevo ambito de clase del cual se estan implementando metodos (sin perder los anteriores, por si hay clausulas IMPL no finalizadas mas arriba, es el caso de que una clausula IMPL se declare dentro de una funcion de otra clausula IMPL)
			}
		}
	}
	
	public void registrarCantidadPrototiposInterfaz(String nombre_interfaz) {
		if (esAmbitoValido()) {
			int posicion_ultimo_ambito_interfaz = ambito_actual.lastIndexOf("[");
			String lexema_interfaz_nm = nombre_interfaz + ":" + ambito_actual.substring(0, posicion_ultimo_ambito_interfaz);
			AtributosSimbolo atributos_interfaz_nm = AnalizadorLexico.simbolos.get(lexema_interfaz_nm);
			if (atributos_interfaz_nm != null && atributos_interfaz_nm.getCantidadPrototipos() == -1) //no tendria sentido que esta condicion no se cumpla ya que si el ambito es valido se supone que la interfaz fue registrada recientemente, pero se hace el chequeo por las dudas
				atributos_interfaz_nm.setCantidadPrototipos(cantidad_prototipos_interfaz_actual);
		}
	}
	
	public void registrarInterfazImplementada(String nombre_clase, String nombre_interfaz) {
		if (esAmbitoValido()) {
			String lexema_clase_nm = nombre_clase + ":" + ambito_actual;
			AtributosSimbolo atributos_clase_nm = AnalizadorLexico.simbolos.get(lexema_clase_nm);
			if (atributos_clase_nm != null && atributos_clase_nm.getLexemaInterfazImplementada() == null) { //no tendria sentido que esta condicion no se cumpla ya que si el ambito es valido se supone que la clase fue registrada recientemente, pero se hace el chequeo por las dudas
				ArrayList<String> uso = new ArrayList<String>();
				uso.add("nombre_interfaz");
				String lexema_interfaz_implementada_nm = obtenerLexemaNmMasCercano(nombre_interfaz, uso, ambito_actual);
				if (lexema_interfaz_implementada_nm == null) {
					AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: La clase '" + nombre_clase + "' implementa la interfaz '" + nombre_interfaz + "', que hasta el momento no existe en su ambito ni en ninguno que lo contenga");
					AnalizadorLexico.simbolos.remove(lexema_clase_nm);
					ambito_invalido_detectado = true;
				}
				else
					atributos_clase_nm.setLexemaInterfazImplementada(lexema_interfaz_implementada_nm);
			}
		}
	}
	
	private boolean perteneceInterfazImplementada(String nombre_metodo_prototipo, String nombre_parametro, String tipo_parametro) {
		String ambito_superior = obtenerLexemaAmbitoSuperior(ambito_actual);
		if (esDeClase(ambito_superior)) {
			int posicion_ultimo_ambito_clase = ambito_superior.lastIndexOf("(");
			String nombre_clase = ambito_superior.substring(posicion_ultimo_ambito_clase+1, ambito_superior.length()-1);
			String lexema_clase_nm = nombre_clase + ":" + obtenerLexemaAmbitoSuperior(ambito_superior);
			AtributosSimbolo atributos_clase_nm = AnalizadorLexico.simbolos.get(lexema_clase_nm);
			if (atributos_clase_nm != null) {
				String lexema_interfaz_implementada_nm = atributos_clase_nm.getLexemaInterfazImplementada();
				if (lexema_interfaz_implementada_nm != null) {
					int posicion_primer_ambito = lexema_interfaz_implementada_nm.indexOf(":");
					String nombre_interfaz_implementada = lexema_interfaz_implementada_nm.substring(0, posicion_primer_ambito);
					String ambito_posible_prototipo_interfaz = lexema_interfaz_implementada_nm.substring(posicion_primer_ambito+1, lexema_interfaz_implementada_nm.length()) + "[" + nombre_interfaz_implementada + "]";
					String lexema_posible_prototipo_interfaz_nm = nombre_metodo_prototipo + ":" + ambito_posible_prototipo_interfaz;
					AtributosSimbolo atributos_posible_implementado = AnalizadorLexico.simbolos.get(lexema_posible_prototipo_interfaz_nm);
					if (atributos_posible_implementado != null) {
						String nombre_parametro_en_interfaz = atributos_posible_implementado.getNombreParametroFormal();
						if (nombre_parametro_en_interfaz != null && nombre_parametro != null && (nombre_parametro_en_interfaz.equals(nombre_parametro))) {
							String lexema_parametro_en_interfaz_nm = nombre_parametro_en_interfaz + ":" + ambito_posible_prototipo_interfaz + ":" + nombre_metodo_prototipo; //es el lexema del parametro del prototipo de la interfaz
							AtributosSimbolo atributos_parametro_interfaz_nm = AnalizadorLexico.simbolos.get(lexema_parametro_en_interfaz_nm);
							if (atributos_parametro_interfaz_nm != null)
								return (atributos_parametro_interfaz_nm.getTipo().equals(tipo_parametro)); //se llaman igual y sus parametros tambien, y se define si es la implementacion de ese metodo en base a si el tipo de los parametros es igual
						}
						else if (nombre_parametro_en_interfaz == null && nombre_parametro == null) //este es el caso de que se llaman igual y ninguno de los dos tienen parametro
							return true;
					}
				}
			}
		}
		return false;
	}
	
	public void verificarPosibleImplementacionFuncionInterfaz(String nombre_metodo_prototipo) {
		if (esAmbitoValido()) {
			String lexema_metodo_prototipo_nm = nombre_metodo_prototipo + ":" + obtenerLexemaAmbitoSuperior(ambito_actual);
			AtributosSimbolo atributos_id_nm = AnalizadorLexico.simbolos.get(lexema_metodo_prototipo_nm);
			if (atributos_id_nm != null) {
				String nombre_parametro_en_clase = atributos_id_nm.getNombreParametroFormal();
				String tipo = null;
				if (nombre_parametro_en_clase != null) {
					String lexema_parametro_en_clase_nm = nombre_parametro_en_clase + ":" + ambito_actual;
					AtributosSimbolo atributos_parametro_clase_nm = AnalizadorLexico.simbolos.get(lexema_parametro_en_clase_nm);
					if (atributos_parametro_clase_nm != null)
						tipo = atributos_parametro_clase_nm.getTipo();
				}
				if (perteneceInterfazImplementada(nombre_metodo_prototipo, nombre_parametro_en_clase, tipo))
					cantidad_prototipos_interfaz_utilizados++;
			}
		}
	}
	
	public void chequearImplementacionTotalInterfaz(String nombre_clase) {
		if (esAmbitoValido()) {
			String lexema_clase_nm = nombre_clase + ":" + obtenerLexemaAmbitoSuperior(ambito_actual);
			AtributosSimbolo atributos_clase_nm = AnalizadorLexico.simbolos.get(lexema_clase_nm);
			if (atributos_clase_nm != null) {
				String lexema_interfaz_implementada_nm = atributos_clase_nm.getLexemaInterfazImplementada();
				AtributosSimbolo atributos_interfaz_implementada_nm = AnalizadorLexico.simbolos.get(lexema_interfaz_implementada_nm);
				if (atributos_interfaz_implementada_nm != null) {
					if (atributos_interfaz_implementada_nm.getCantidadPrototipos() != cantidad_prototipos_interfaz_utilizados) {
						String nombre_interfaz = lexema_interfaz_implementada_nm.substring(0, lexema_interfaz_implementada_nm.indexOf(":"));
						AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: La clase '" + nombre_clase + "' no implementa todos los metodos de la interfaz '" + nombre_interfaz + "'");
					}
				}
			}
		}
	}
	
	public void verificarCantidadAnidamientos(String nombre_funcion) {
		if (esAmbitoValido()) {
			int cantidad_metodos_actuales = metodos_actuales_anidamientos.size();
			if (cantidad_metodos_actuales > 0) {
				DatosAnidamiento anidamientos_ultimo_metodo = metodos_actuales_anidamientos.get(cantidad_metodos_actuales-1);
				String lexema_nm = nombre_funcion + ":" + ambito_actual;
				AtributosSimbolo atributos_lexema_nm = AnalizadorLexico.simbolos.get(lexema_nm);
				if (atributos_lexema_nm != null && (atributos_lexema_nm.getUso().equals("nombre_funcion") || atributos_lexema_nm.getUso().equals("nombre_metodo") || atributos_lexema_nm.getUso().equals("nombre_prototipo_metodo") || atributos_lexema_nm.getUso().equals("nombre_prototipo") || atributos_lexema_nm.getUso().equals("nombre_funcion_clausula_impl"))) {
					if (anidamientos_ultimo_metodo.getCantidadActualAnidamientos() == 0) {
						int cantidad_maxima_anidamientos_metodo = anidamientos_ultimo_metodo.getCantidadMaximaAnidamientos();
						if (cantidad_maxima_anidamientos_metodo > 1) {
							String nombre_metodo = anidamientos_ultimo_metodo.getLexemaMetodo().substring(0, anidamientos_ultimo_metodo.getLexemaMetodo().indexOf(":"));
							AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: El metodo '" + nombre_metodo + "' posee " + cantidad_maxima_anidamientos_metodo + " niveles de anidamiento de funciones locales, y el limite permitido es de uno solo");
						}
						metodos_actuales_anidamientos.remove(cantidad_metodos_actuales-1);
					}
					else
						anidamientos_ultimo_metodo.disminuirCantidadAnidamientos();
				}
	
			}
		}
	}
	
	private String obtenerAmbitoClaseImplementada(String ambito) {
		String tipo_ambito = obtenerTipoAmbitoContenedor(ambito);
		if (tipo_ambito.equals("ambito_simple")) {
			String ambito_superior = obtenerLexemaAmbitoSuperior(ambito);
			if (ambito_superior != null && obtenerTipoAmbitoContenedor(ambito_superior).equals("ambito_clausula_impl")) { //si el ambito contenedor es un ambito_simple y a su vez el ambito contenedor superior es un ambito_clausula_impl, es porque la referencia se encontro de una funcion de clausula IMPL. En este caso, hay que tener en cuenta que la referencia puede no existir en ese ambito pero si en la clase de la cual se implementan sus metodos, lo que implica tener que buscarlo ahi (ya que en teoria habria que poder usar por ejemplo los atributos de esa clase)
				String nombre_clase_implementada = ambito_superior.substring(ambito_superior.lastIndexOf("<")+1, ambito_superior.length()-1);
				String ambito_superior_clausula = obtenerLexemaAmbitoSuperior(ambito_superior); //es el ambito donde esta contenida la clausula
				if (ambito_superior_clausula != null) {
					ArrayList<String> uso_clase = new ArrayList<String>();
					uso_clase.add("nombre_clase");
					String lexema_clase_implementada_nm = obtenerLexemaNmMasCercano(nombre_clase_implementada, uso_clase, ambito_superior_clausula); //se busca el lexema nm perteneciente a la clase que la clausula IMPL esta implementando
					if (lexema_clase_implementada_nm != null) {
						String ambito_clase_implementada = lexema_clase_implementada_nm.substring(lexema_clase_implementada_nm.indexOf(":")+1, lexema_clase_implementada_nm.length()) + "(" + nombre_clase_implementada + ")";
						return ambito_clase_implementada;
					}
				}
			}
		}
		return null;
	}
	
	private String obtenerReferenciaEnAmbitoClaseAsociada(String referencia, ArrayList<String> usos) { //este metodo permite buscar una referencia en el ambito de una clase asociada a una clausula IMPL, ya que si dentro de una funcion de clausula IMPL no se encuentra una referencia, tranquilamente puede estar en la clase que contiene realmente al prototipo de metodo que implementa
		String ambito_clase_implementada = obtenerAmbitoClaseImplementada(ambito_actual);
		if (ambito_clase_implementada != null)
			return obtenerLexemaNmMasCercano(referencia, usos, ambito_clase_implementada); //se busca en el ambito de la clase implementada esa referencia, con los mismos usos que en un principio
		return null;
	}
	
	public String verificarReferenciaValida(String referencia, boolean debeSerFuncion) {
		if (esAmbitoValido() && referencia != null) {
			ArrayList<String> usos = new ArrayList<String>();
			if (debeSerFuncion) {
				int posicion_ultima_referencia = referencia.lastIndexOf(".");
				if (posicion_ultima_referencia != -1) { //si debe ser una referencia a funcion y hay al menos un "." en la referencia, significa que se quiere acceder a un metodo de clase
					String prefijo_referencia_metodo = referencia.substring(0, posicion_ultima_referencia);
					usos.add("nombre_atributo"); //el prefijo de una referencia a un metodo de clase puede ser un atributo de clase, por ejemplo si desde un metodo de una clase se quiere usar una instancia de otra, declarada en forma de atributo en la misma. Entonces se puede acceder a los metodos de la clase asociada a dicha instancia
					usos.add("referencia_clase"); //el prefijo de una referencia a un metodo de clase puede ser una referencia a clase, que puede ser accedida directamente (por ejemplo dentro de la clase), o con una instancia de la clase que contiene esa referencia a otra, o con otras referencias en forma de cadena
					usos.add("nombre_variable"); //el prefijo de una referencia a un metodo de clase puede ser una variable (si por ejemplo se accede con una instancia que se declaro en un ambito que no es una clase, o con atributos de esa instancia, que en ambos casos son variables por estar fuera de una clase)
					AtributosSimbolo atributos_prefijo_nm = AnalizadorLexico.simbolos.get(obtenerLexemaNmMasCercano(prefijo_referencia_metodo, usos, ambito_actual));
					if (atributos_prefijo_nm != null) {
						String lexema_clase_asociada_nm = atributos_prefijo_nm.getLexemaClaseAsociada();
						if (lexema_clase_asociada_nm != null) {
							int posicion_primer_ambito_clase = lexema_clase_asociada_nm.indexOf(":");
							String nombre_clase = lexema_clase_asociada_nm.substring(0, posicion_primer_ambito_clase);
							String nombre_metodo = referencia.substring(posicion_ultima_referencia+1, referencia.length());
							String lexema_metodo_nm = nombre_metodo + ":" + lexema_clase_asociada_nm.substring(posicion_primer_ambito_clase+1, lexema_clase_asociada_nm.length()) + "(" + nombre_clase + ")";
							AtributosSimbolo atributos_metodo_nm = AnalizadorLexico.simbolos.get(lexema_metodo_nm);
							if (atributos_metodo_nm == null || (!atributos_metodo_nm.getUso().equals("nombre_metodo") && !atributos_metodo_nm.getUso().equals("nombre_prototipo_metodo")))
								AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: No existe ningun metodo al alcance que pueda accederse como '" + referencia + "'");		
							else {
								lexema_invocacion_metodo_actual = referencia;
								return lexema_metodo_nm;
							}
						}
						else
							AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: No existe ningun metodo al alcance que pueda accederse como '" + referencia + "'");
					}
					else {
						atributos_prefijo_nm = AnalizadorLexico.simbolos.get(obtenerReferenciaEnAmbitoClaseAsociada(prefijo_referencia_metodo, usos));
						if (atributos_prefijo_nm != null) {
							String lexema_clase_asociada_nm = atributos_prefijo_nm.getLexemaClaseAsociada();
							if (lexema_clase_asociada_nm != null) {
								int posicion_primer_ambito_clase = lexema_clase_asociada_nm.indexOf(":");
								String nombre_clase = lexema_clase_asociada_nm.substring(0, posicion_primer_ambito_clase);
								String nombre_metodo = referencia.substring(posicion_ultima_referencia+1, referencia.length());
								String lexema_metodo_nm = nombre_metodo + ":" + lexema_clase_asociada_nm.substring(posicion_primer_ambito_clase+1, lexema_clase_asociada_nm.length()) + "(" + nombre_clase + ")";
								AtributosSimbolo atributos_metodo_nm = AnalizadorLexico.simbolos.get(lexema_metodo_nm);
								if (atributos_metodo_nm == null || (!atributos_metodo_nm.getUso().equals("nombre_metodo") && !atributos_metodo_nm.getUso().equals("nombre_prototipo_metodo")))
									AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: No existe ningun metodo al alcance que pueda accederse como '" + referencia + "'");		
								else {
									lexema_invocacion_metodo_actual = referencia;
									return lexema_metodo_nm;
								}
							}
							else
								AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: No existe ningun metodo al alcance que pueda accederse como '" + referencia + "'");
						}
						else
							AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: No existe ningun metodo al alcance que pueda accederse como '" + referencia + "'");
					}
				}
				else {
					usos.add("nombre_funcion");
					usos.add("nombre_metodo");
					usos.add("nombre_prototipo");
					usos.add("nombre_prototipo_metodo");
					usos.add("nombre_funcion_clausula_impl");
					String lexema_funcion_nm = obtenerLexemaNmMasCercano(referencia, usos, ambito_actual);
					if (lexema_funcion_nm == null) {
						lexema_funcion_nm = obtenerReferenciaEnAmbitoClaseAsociada(referencia, usos);
						if (lexema_funcion_nm != null)
							return lexema_funcion_nm;
						AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: No existe ninguna funcion, metodo o prototipo al alcance que pueda accederse como '" + referencia + "'");
					}
					else
						return lexema_funcion_nm;
				}
			}
			else {
				usos.add("nombre_variable");
				usos.add("nombre_atributo");
				usos.add("nombre_parametro_formal");
				String lexema_referencia_nm = obtenerLexemaNmMasCercano(referencia, usos, ambito_actual);
				if (lexema_referencia_nm == null) {
					lexema_referencia_nm = obtenerReferenciaEnAmbitoClaseAsociada(referencia, usos);
					if (lexema_referencia_nm != null)
						return lexema_referencia_nm;
					AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: Referencia invalida, no existe ninguna variable o atributo con nombre '" + referencia + "' al alcance");
				}
				else
					return lexema_referencia_nm;
			}
		}
		return null;
	}
	
	public void verificarReferenciasVariablesEnAsignaciones() {
		for (String lexema: AnalizadorLexico.simbolos.keySet()) {
			AtributosSimbolo atributos_variable_nm = AnalizadorLexico.simbolos.get(lexema);
			String uso = atributos_variable_nm.getUso();
			String tipo = atributos_variable_nm.getTipo();
			if (uso != null && (uso.equals("nombre_variable") || uso.equals("nombre_atributo") || uso.equals("nombre_parametro_formal")) && tipo != null) //solo se muestra un WARNING si es realmente una variable, atributo o parametro formal, las referencias a clases para herencia por composicion no se consideraron
				if (!atributos_variable_nm.fueReferenciada() && !lexema.contains(".")) { //si una variable no fue referenciada del lado derecho de una asignacion, solo se muestra un WARNING  si no contiene ".", o sea que no se genera de manera adicional debido a herencia por composicion o a una instancia de una clase
					String nombre_variable = lexema.substring(0, lexema.indexOf(":"));
					AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - WARNING: Una variable llamada '" + nombre_variable + "' fue declarada pero nunca referenciada en el lado derecho de alguna asignacion");
				} //si dos variables se llaman igual pero estan en distintos ambitos y nunca se referenciaron del lado derecho de una asignacion, se marcan con WARNINGS distintos pero con el mismo nombre, de lo contrario hay que poner todo el lexema nm para diferenciarlas, que es sencillo pero no tiene sentido. Tambien cabe aclarar que si por ejemplo hay una instancia de una clase que contiene cierto atributo, si el atributo no se referencia dentro de la clase sera marcado como WARNING, sin importar si para la instancia de la clase se uso ese atributo (ya que en si no son lo mismo y no tienen la misma entrada en la tabla de siumbolos) 
        }
	}
	
	private boolean esConstante(String simbolo) {
		if (simbolo.charAt(0) == '.' || simbolo.charAt(0) == '-' || Character.isDigit(simbolo.charAt(0)))
			return true;
		return false;
	}
	
	public void registrarReferenciasLadoDerechoAsignacion(String expresion_aritmetica) {
		if (esAmbitoValido()) {
			if (expresion_aritmetica != null) {
				String[] elementos_expresion_aritmetica = expresion_aritmetica.split(" ");
				for (String elemento: elementos_expresion_aritmetica)
					if (!elemento.equals("+") && !elemento.equals("-") && !elemento.equals("*") && !elemento.equals("/") && !esConstante(elemento)) {
						AtributosSimbolo atributos_referencia_nm = AnalizadorLexico.simbolos.get(elemento);
						if (atributos_referencia_nm != null && (atributos_referencia_nm.getUso().equals("nombre_variable") || atributos_referencia_nm.getUso().equals("nombre_atributo") || atributos_referencia_nm.getUso().equals("nombre_parametro_formal"))) {
							atributos_referencia_nm.variableReferenciada();
							int posicion_primer_punto = elemento.indexOf(".");
							int posicion_primer_ambito = elemento.indexOf(":");
							if (posicion_primer_punto != -1 && posicion_primer_ambito != -1) {
								String nombre_instancia = elemento.substring(0, posicion_primer_punto);
								String lexema_instancia_nm = nombre_instancia + ":" + elemento.substring(posicion_primer_ambito+1, elemento.length());
								AtributosSimbolo atributos_instancia_nm = AnalizadorLexico.simbolos.get(lexema_instancia_nm);
								if (atributos_instancia_nm != null) //no tendria sentido que no se cumpla, ya que si existe una variable generada por un atributo de una instancia de una clase, entonces tiene que existir la instancia
									atributos_instancia_nm.variableReferenciada(); //si se referencia del lado derecho de una asignacion a cualquier variable propia de una instancia, se considera como referenciada la instancia
							}
						}
					}
			}
		}
	}
	
	public void chequearAsignacionValida(String lexema_referencia_lado_izquierdo, String expresion_aritmetica_lado_derecho, boolean asignacion_menos_igual) { //se chequea que una asignacion sea valida, o sea INT a INT, ULONG a ULONG o DOUBLE a DOUBLE (la funcion es similar a la de chequeo para operaciones entre un termino y un factor, pero no igual)
		if (esAmbitoValido()) {
			AtributosSimbolo atributos_lexema_lado_izquierdo = AnalizadorLexico.simbolos.get(lexema_referencia_lado_izquierdo);
			if (atributos_lexema_lado_izquierdo != null) { //si no hubo error no tiene sentido que sea null
				String tipo_lado_derecho = determinarTipo(expresion_aritmetica_lado_derecho);
				String tipo_lado_izquierdo = atributos_lexema_lado_izquierdo.getTipo();
				if (tipo_lado_derecho != null && tipo_lado_izquierdo != null) {
					if (!tipo_lado_izquierdo.equals(tipo_lado_derecho) && (tipo_lado_izquierdo.equals("INT") || tipo_lado_izquierdo.equals("ULONG") || tipo_lado_izquierdo.equals("DOUBLE")) && (tipo_lado_derecho.equals("INT") || tipo_lado_derecho.equals("ULONG") || tipo_lado_derecho.equals("DOUBLE"))) { //si los tipos no son iguales, hay un error especifico para el caso de que ambos sean de tipo numerico, y hay otros para cuando sean iguales o no, alguno es de un tipo no numerico
						if (asignacion_menos_igual) //ya que si la asignacion es un menos igual, hay que marcar error para la resta implicita
							AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: Operacion invalida, no puede efectuarse una resta entre un " + tipo_lado_izquierdo + " y un " + tipo_lado_derecho);
						else
							AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: Asignacion invalida, no puede asignarse un " + tipo_lado_derecho + " a un " + tipo_lado_izquierdo);
					}
					else if (!tipo_lado_izquierdo.equals("INT") && !tipo_lado_izquierdo.equals("ULONG") && !tipo_lado_izquierdo.equals("DOUBLE") && !tipo_lado_derecho.equals("INT") && !tipo_lado_derecho.equals("ULONG") && !tipo_lado_derecho.equals("DOUBLE")) { //caso de que sean o no iguales los tipos del lado derecho e izquierdo, ambos son de tipos no numericos
						if (asignacion_menos_igual) {
							AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: El tipo " + tipo_lado_izquierdo + " no es valido como factor en una expresion aritmetica");
							AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: Asignacion invalida, tanto el lado izquierdo como derecho deben ser de tipo numerico, pero se esta queriendo asignar un " + tipo_lado_izquierdo + " (no numerico) a un " + tipo_lado_izquierdo + " (no numerico)");
						}
						else
							AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: Asignacion invalida, tanto el lado izquierdo como derecho deben ser de tipo numerico, pero se esta queriendo asignar un " + tipo_lado_derecho + " (no numerico) a un " + tipo_lado_izquierdo + " (no numerico)");
					}
					else if (!tipo_lado_izquierdo.equals("INT") && !tipo_lado_izquierdo.equals("ULONG") && !tipo_lado_izquierdo.equals("DOUBLE")) { //caso de que son tipos distintos y unicamente el lado izquierdo es no numerico
						if (asignacion_menos_igual) {
							AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: El tipo " + tipo_lado_izquierdo + " no es valido como factor en una expresion aritmetica");
							AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: Asignacion invalida, tanto el lado izquierdo como derecho deben ser de tipo numerico, pero se esta queriendo asignar un " + tipo_lado_izquierdo + " (no numerico) a un " + tipo_lado_izquierdo + " (no numerico)");
						}
						else
							AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: Asignacion invalida, tanto el lado izquierdo como derecho deben ser de tipo numerico, pero se esta queriendo asignar un " + tipo_lado_derecho + " a un " + tipo_lado_izquierdo + " (no numerico)");
					}
					else if (!tipo_lado_derecho.equals("INT") && !tipo_lado_derecho.equals("ULONG") && !tipo_lado_derecho.equals("DOUBLE")) { //caso de que son tipos distintos y unicamente el lado derecho es no numerico
						if (asignacion_menos_igual)
							AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: El tipo " + tipo_lado_derecho + " no es valido como factor en una expresion aritmetica");
						else
							AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: Asignacion invalida, tanto el lado izquierdo como derecho deben ser de tipo numerico, pero se esta queriendo asignar un " + tipo_lado_derecho + " (no numerico) a un " + tipo_lado_izquierdo);
					}
				}
				else if (tipo_lado_izquierdo != null) //si por ejemplo hay un nombre de clase del lado derecho
					AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: El lado derecho contiene un elemento que no es una variable ni constante");
			}
		}
	}
	
	public void verificarValidezVariableControlFor(String variable_control) {
		if (esAmbitoValido() && variable_control != null) { //si la variable de control es null, entonces ya hay un error previo
			ArrayList<String> usos = new ArrayList<String>();
			usos.add("nombre_variable");
			usos.add("nombre_atributo");
			usos.add("nombre_parametro_formal");
			String variable_control_nm = obtenerLexemaNmMasCercano(variable_control, usos, ambito_actual);
			AtributosSimbolo atributos_control_nm = AnalizadorLexico.simbolos.get(variable_control_nm);
			if (atributos_control_nm != null) {
				String tipo_variable_control = atributos_control_nm.getTipo();
				if (!tipo_variable_control.equals("INT") && !tipo_variable_control.equals("ULONG"))
					AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: La variable de control '" + variable_control + "' debe ser de tipo INT o ULONG, pero es de tipo " + tipo_variable_control);
			}
		}
	}
	
	public String verificarConstantesControlFor(String constante_inicio, String constante_fin, String constante_variacion) {
		if (esAmbitoValido())  {
			AtributosSimbolo atributos_constante_inicio = AnalizadorLexico.simbolos.get(constante_inicio);
			AtributosSimbolo atributos_constante_fin = AnalizadorLexico.simbolos.get(constante_fin);
			AtributosSimbolo atributos_constante_variacion = AnalizadorLexico.simbolos.get(constante_variacion);
			String resultado = "";
			if (atributos_constante_inicio == null || (!atributos_constante_inicio.getTipo().equals("INT") && !atributos_constante_inicio.getTipo().equals("ULONG")))
				AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: La constante de inicializacion de la variable de control del FOR debe ser una constante entera valida (tipo INT o ULONG)");
			else {
				resultado += constante_inicio;
				if (atributos_constante_fin == null || (!atributos_constante_fin.getTipo().equals("INT") && !atributos_constante_fin.getTipo().equals("ULONG")))
					AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: La constante de valor final de la variable de control del FOR debe ser una constante entera valida (tipo INT o ULONG)");
				else {
					resultado += " " + constante_fin;
					if (atributos_constante_variacion == null || (!atributos_constante_variacion.getTipo().equals("INT") && !atributos_constante_variacion.getTipo().equals("ULONG")))
						AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: La constante de variacion de la variable de control del FOR debe ser una constante entera valida (tipo INT o ULONG)");
					else {
						resultado += " " + constante_variacion;
						return resultado;
					}
				}
			}
		}
		return null;
	}
	
	public void chequearCompatibilidadControladoresFor(String variable_control, String constantes_control) {
		if (esAmbitoValido()) {
			if (variable_control != null && constantes_control != null) { //si no se cumpliera seria porque ya habria un error asociado, ya sea respecto a la variable de control, a las constantes o ambas
				ArrayList<String> usos = new ArrayList<String>();
				usos.add("nombre_variable");
				usos.add("nombre_atributo");
				usos.add("nombre_parametro_formal");
				String variable_control_nm = obtenerLexemaNmMasCercano(variable_control, usos, ambito_actual);
				AtributosSimbolo atributos_control_nm = AnalizadorLexico.simbolos.get(variable_control_nm);
				if (atributos_control_nm != null) { //si no se cumpliera seria porque ya habria un error asociado a la variable de control
					String tipo_variable_control = atributos_control_nm.getTipo();
					String[] constantes = constantes_control.split(" ");
					String tipo_constante_inicio = AnalizadorLexico.simbolos.get(constantes[0]).getTipo();
					String tipo_constante_fin = AnalizadorLexico.simbolos.get(constantes[1]).getTipo();
					String tipo_constante_variacion = AnalizadorLexico.simbolos.get(constantes[2]).getTipo();
					if (!tipo_variable_control.equals(tipo_constante_inicio) || !tipo_variable_control.equals(tipo_constante_fin) || !tipo_variable_control.equals(tipo_constante_variacion)) //ademas de tener que ser todas de un tipo entero (INT o ULONG), la variable de control y las 3 constantes de control deben ser del mismo tipo (todas INT o todas ULONG)
						AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: La variable de control del FOR y las 3 constantes tienen que ser todas del mismo tipo entero, ya sea INT o ULONG");
				}
			}
		}
	}
	
	public String obtenerComparadorAsociado(String constante_variacion) { //permite obtener cual es el comparador de la condicion implicita del FOR
		if (esAmbitoValido() && constante_variacion != null) {
			AtributosSimbolo atributos_constante_variacion = AnalizadorLexico.simbolos.get(constante_variacion);
			if (atributos_constante_variacion != null && (atributos_constante_variacion.getTipo().equals("INT") || atributos_constante_variacion.getTipo().equals("ULONG"))) {
				if (constante_variacion.charAt(0) == '-')
					return ">"; //ya que si la constante de variacion inicia con un "-", se decrementa en cada iteracion la variable de control y por ende se supone que el FOR se ejecuta hasta que sea mayor a la variable de finalizacion
				else
					return "<"; //ya que si la constante de variacion no inicia con un "-", se incrementa en cada iteracion la variable de control y por ende se supone que el FOR se ejecuta hasta que sea menor a la variable de finalizacion
			}
		}
		return null;
	}
	
	public String obtenerConstanteControladoresFor(String controladores_for, int posicion_constante) { //permite obtener alguna de las constantes de los controladores del FOR, donde cada constante se encuentra separada por " ", debido a como se guardo el sval del controlador
		if (esAmbitoValido() && controladores_for != null) {
			String[] partes_controlador = controladores_for.split(" ");
			if (partes_controlador.length == 3) //porque si es valido deberia haber 3 constantes
				return partes_controlador[posicion_constante];
		}
		return null;
	}
	
	public void chequearValidezOperacionComparacionExpresiones(String expresion1, String expresion2, boolean es_comparacion, boolean es_suma) {
		if (esAmbitoValido()) {
			String tipo_expresion1 = determinarTipo(expresion1);
			String tipo_expresion2 = determinarTipo(expresion2);
			if (tipo_expresion1 != null && tipo_expresion2 != null && !tipo_expresion1.equals(tipo_expresion2) && (tipo_expresion1.equals("INT") || tipo_expresion1.equals("ULONG") || tipo_expresion1.equals("DOUBLE")) && (tipo_expresion2.equals("INT") || tipo_expresion2.equals("ULONG") || tipo_expresion2.equals("DOUBLE"))) { //si los tipos no son iguales, solo se imprime el error si ambos son tipos numericos, ya que si uno no es un tipo numerico ya hay un error previo
				if (es_comparacion) //caso de que sea una comparacion entre dos expresiones aritmeticas
					AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: Comparacion invalida, no puede compararse un " + tipo_expresion1 + " con un " + tipo_expresion2);
				else { //caso de que sea una operacion de suma o resta entre una expresion aritmetica y un termino
					if (es_suma)
						AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: Operacion invalida, no puede efectuarse una suma entre un " + tipo_expresion1 + " y un " + tipo_expresion2);
					else
						AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: Operacion invalida, no puede efectuarse una resta entre un " + tipo_expresion1 + " y un " + tipo_expresion2);
				}
			}
		}
	}
	
	public void chequearValidezOperacionTerminoFactor(String termino, String lexema_factor, boolean es_multiplicacion) {
		if (esAmbitoValido()) {
			AtributosSimbolo atributos_lexema_factor = AnalizadorLexico.simbolos.get(lexema_factor);
			if (atributos_lexema_factor != null) { //si no hubo error no tiene sentido que sea null
				String tipo_termino = determinarTipo(termino);
				String tipo_factor = atributos_lexema_factor.getTipo();
				if (tipo_termino != null) {
					if (!tipo_factor.equals(tipo_termino) && (tipo_factor.equals("INT") || tipo_factor.equals("ULONG") || tipo_factor.equals("DOUBLE")) && (tipo_termino.equals("INT") || tipo_termino.equals("ULONG") || tipo_termino.equals("DOUBLE"))) { //si los tipos no son iguales, solo se imprime el error si ambos son tipos numericos, ya que si uno no es un tipo numerico ya hay un error previo
						if (es_multiplicacion)
							AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: Operacion invalida, no puede efectuarse una multiplicacion entre un " + tipo_termino + " y un " + tipo_factor);
						else
							AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: Operacion invalida, no puede efectuarse una division entre un " + tipo_termino + " y un " + tipo_factor);
					}
					//else if (tipo_termino.equals(tipo_factor) && (tipo_termino.equals("INT") || tipo_termino.equals("ULONG")) && !es_multiplicacion) //es el caso de la division entre dos operandos validos e iguales que no son de tipo DOUBLE, por lo cual se genera un WARNING indicando que el resultado sera de tipo DOUBLE
						//AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - WARNING: Se efectuo una division entre dos operandos de tipo " + tipo_termino + ", pero el resultado sera de tipo DOUBLE");
				}
			}
		}
	}
	
	private String determinarTipo(String expresion) { //devuelve el tipo de una expresion aritmetica o de un termino
		if (expresion != null) {
			String[] elementos_expresion = expresion.split(" ");
			String lexema_primer_elemento = elementos_expresion[0];
			AtributosSimbolo atributos_primer_elemento = AnalizadorLexico.simbolos.get(lexema_primer_elemento);
			if (atributos_primer_elemento != null) //si no hubo error no tendria sentido que sea null
				return atributos_primer_elemento.getTipo(); //el tipo de la expresion indefectiblemente tiene que ser igual al del primer operando
		}
		return null;
	}
	
	public void chequearTipoFactorValido(String lexema_referencia_nm) { //permite verificar que sea valido el tipo de una referencia para ser un factor, o sea que el tipo tiene que ser INT, ULONG o DOUBLE
		if (esAmbitoValido()) {
			if (lexema_referencia_nm != null) {
				AtributosSimbolo atributos_referencia_nm = AnalizadorLexico.simbolos.get(lexema_referencia_nm);
				if (atributos_referencia_nm != null) {
					String tipo_factor = atributos_referencia_nm.getTipo();
					if (tipo_factor != null && !tipo_factor.equals("INT") && !tipo_factor.equals("ULONG") && !tipo_factor.equals("DOUBLE"))
						AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: El tipo " + tipo_factor + " no es valido como factor en una expresion aritmetica");
				}
			}
		}
	}
	
	public void chequearInvocacionFuncionValida(String lexema_funcion_nm, String parametro_real) { //para chequear que una invocacion a funcion sea valida, para lo cual el parametro de la funcion o metodo que se invoco, debe tener el mismo tipo que la expresion aritmetica del parametro formal (o que no haya parametro formal ni parametro real). Ademas, en caso de ser un prototipo debe estar implementado, sino tambien hay error
		if (esAmbitoValido()) {
			AtributosSimbolo atributos_lexema_funcion_nm = AnalizadorLexico.simbolos.get(lexema_funcion_nm);
			if (atributos_lexema_funcion_nm != null) { //si no hubo error no tiene sentido que sea null
				String nombre_parametro_formal = atributos_lexema_funcion_nm.getNombreParametroFormal();
				String nombre_funcion = lexema_funcion_nm.substring(0, lexema_funcion_nm.indexOf(":"));
				boolean es_metodo = (atributos_lexema_funcion_nm.getUso().equals("nombre_metodo") || atributos_lexema_funcion_nm.getUso().equals("nombre_prototipo_metodo"));
				if (parametro_real != null && nombre_parametro_formal != null) {
					String tipo_parametro_real = determinarTipo(parametro_real);
					if (tipo_parametro_real != null) { //si no hubo error no tiene sentido que sea null
						int posicion_primer_ambito_funcion = lexema_funcion_nm.indexOf(":");
						String lexema_parametro_formal_nm = nombre_parametro_formal + ":" + lexema_funcion_nm.substring(posicion_primer_ambito_funcion+1, lexema_funcion_nm.length()) + ":" + lexema_funcion_nm.substring(0, posicion_primer_ambito_funcion);
						AtributosSimbolo atributos_parametro_formal_nm = AnalizadorLexico.simbolos.get(lexema_parametro_formal_nm);
						if (atributos_parametro_formal_nm != null && atributos_parametro_formal_nm.getUso().equals("nombre_parametro_formal")) {
							if (!atributos_parametro_formal_nm.getTipo().equals(tipo_parametro_real)) {
								if (es_metodo)
									AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: Se esta intentando invocar al metodo " + nombre_funcion + " con un parametro de tipo " + tipo_parametro_real + " cuando en realidad deberia recibir un parametro de tipo " + atributos_parametro_formal_nm.getTipo());
								else
									AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: Se esta intentando invocar a la funcion " + nombre_funcion + " con un parametro de tipo " + tipo_parametro_real + " cuando en realidad deberia recibir un parametro de tipo " + atributos_parametro_formal_nm.getTipo());
							}
							else if ((atributos_lexema_funcion_nm.getUso().equals("nombre_prototipo") || atributos_lexema_funcion_nm.getUso().equals("nombre_prototipo_metodo")) && atributos_lexema_funcion_nm.getLexemaFuncionImplementacion() == null) { //caso de que se quiera invocar a un prototipo de metodo o de funcion con parametro, jamas implementado
								if (es_metodo)
									AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: Se esta intentando invocar al metodo " + nombre_funcion + " que nunca se implemento");
								else
									AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: Se esta intentando invocar a la funcion " + nombre_funcion + " que nunca se implemento"); //este caso puede ocurrir ya que puede haber prototipos sueltos en una en el main o en una funcion, sin estar de forma directa dentro de una clase, pero siempre que pase eso estara este error dado que la unica forma de implementar un prototipo es con una clausula IMPL y estas solo pueden implementar prototipos de metodos de clases
							}
						}
					}
				}
				else if (parametro_real != null) {
					if (es_metodo)
						AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: Se esta intentando invocar al metodo " + nombre_funcion + " con un parametro cuando en realidad no lleva ninguno");
					else
						AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: Se esta intentando invocar a la funcion " + nombre_funcion + " con un parametro cuando en realidad no lleva ninguno");
				}
				else if (nombre_parametro_formal != null) {
					if (es_metodo)
						AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: Se esta intentando invocar al metodo " + nombre_funcion + " sin ningun parametro cuando en realidad lleva uno");
					else
						AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: Se esta intentando invocar a la funcion " + nombre_funcion + " sin ningun parametro cuando en realidad lleva uno");
				} //si se da el caso de que se efectua una invocacion a funcion con un parametro real correspondiente a una variable no declarada en el ambito actual o en alguno de los que estan por encima, se considera como que se lo esta llamando sin parametro (porque en realidad no existe). Sin embargo, aunque la funcion invocada no tuviera parametro formal, igual habra un error debido a que la referencia es invalida, por lo que no hay problema 
				else if ((atributos_lexema_funcion_nm.getUso().equals("nombre_prototipo") || atributos_lexema_funcion_nm.getUso().equals("nombre_prototipo_metodo")) && atributos_lexema_funcion_nm.getLexemaFuncionImplementacion() == null) { //caso de que se quiera invocar a un prototipo de metodo o de funcion sin parametro, jamas implementado
					if (es_metodo)
						AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: Se esta intentando invocar al metodo " + nombre_funcion + " que nunca se implemento");
					else
						AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: Se esta intentando invocar a la funcion " + nombre_funcion + " que nunca se implemento"); //este caso puede ocurrir ya que puede haber prototipos sueltos en una en el main o en una funcion, sin estar de forma directa dentro de una clase, pero siempre que pase eso estara este error dado que la unica forma de implementar un prototipo es con una clausula IMPL y estas solo pueden implementar prototipos de metodos de clases
				}
			}		
		}
	}
	
	public String obtenerFuncionInvocada(String lexema_funcion_nm) { //este metodo permite obtener la funcion que realmente se invoco, ya que si por ejemplo se invoca a un prototipo, es necesario obtener el lexema de la funcion que realmente lo implementa
		if (esAmbitoValido() && lexema_funcion_nm != null) {
			AtributosSimbolo atributos_lexema_funcion_nm = AnalizadorLexico.simbolos.get(lexema_funcion_nm);
			if (atributos_lexema_funcion_nm != null) {
				String lexema_funcion_implementadora_nm = atributos_lexema_funcion_nm.getLexemaFuncionImplementacion();
				if (lexema_funcion_implementadora_nm != null)
					return lexema_funcion_implementadora_nm;
				return lexema_funcion_nm;
			}
		}
		return null;
	}
	
	public String obtenerLexemaCompletoID(String id) { //permite devolver el nombre completo con lexema nm de un id
		if (esAmbitoValido() && id != null)
			return id + ":" + ambito_actual; //el ambito actual es el nm del id en la tabla de simbolos
		return null;
	}
	
	public ArrayList<String> obtenerVariablesInstanciaInvocacion() {
		if (esAmbitoValido() && lexema_invocacion_metodo_actual != null) {
			int posicion_invocacion = lexema_invocacion_metodo_actual.lastIndexOf(".");
			if (posicion_invocacion != -1) { //igual no deberia ser -1, ya que si el lexema de la invocacion de metodo actual no es null, es porque tiene que contener al menor un ".", de lo contrario no seria una invocacion a un metodo de una clase a partir de una instancia 
				ArrayList<String> variables_instancia_invocacion = new ArrayList<String>();
				String prefijo_variables = lexema_invocacion_metodo_actual.substring(0, posicion_invocacion);
				for (String key: AnalizadorLexico.simbolos.keySet()) {
					AtributosSimbolo atributos_clave_nm = AnalizadorLexico.simbolos.get(key);
					if (atributos_clave_nm != null && atributos_clave_nm.getUso() != null && atributos_clave_nm.getTipo() != null && (atributos_clave_nm.getUso().equals("nombre_variable") || atributos_clave_nm.getUso().equals("nombre_atributo")) && (atributos_clave_nm.getTipo().equals("INT") || atributos_clave_nm.getTipo().equals("ULONG") || atributos_clave_nm.getTipo().equals("DOUBLE"))) { //ya que las variables de una instancia usada para una invocacion pueden ser variables simples, o atributos de una clase en caso de que la instancia en si sea un atributo de una clase. Ademas, solo sirve mapear sus valores a los atributos reales si son de un tipo numerico
						if (key.startsWith(prefijo_variables)) {
							int posicion_ambito_clave_nm = key.indexOf(":");
							if (posicion_ambito_clave_nm != -1) { //no deberia ser -1 de todos modos
								String ambito_clave_nm = key.substring(posicion_ambito_clave_nm+1, key.length());
								if (ambito_actual.contains(ambito_clave_nm)) //dado que las instancias pueden haberse declarado en el ambito actual o en uno en el que este contenido
									variables_instancia_invocacion.add(key); //ya que llegado a este punto quiere decir que se trata de una variable generada por la instancia que corresponde a un atributo de la clase del metodo invocado
								else {
									String ambito_clase_implementada = obtenerAmbitoClaseImplementada(ambito_actual);
									if (ambito_clase_implementada != null && ambito_clase_implementada.contains(ambito_clave_nm)) //es el caso de que se este queriendo invocar a un metodo de una clase dentro de una funcion de una clausula IMPL, pero a traves de una instancia de una clase declarada en la clase que se esta implementando y no en la funcion implementadora que esta en el ambito de la clausula IMPL
										variables_instancia_invocacion.add(key);
								}
							}
						}
					}
				}
				lexema_invocacion_metodo_actual = null; //ya que ya se analizo la invocacion actual
				return variables_instancia_invocacion;
			}
		}
		lexema_invocacion_metodo_actual = null;
		return null;
	}
	
	public ArrayList<ParVariableAtributo> obtenerParesMapeoInvocacion(String lexema_metodo, ArrayList<String> variables_instancia) {
		if (esAmbitoValido() && lexema_metodo != null) {
			ArrayList<ParVariableAtributo> pares_mapeo_invocacion = new ArrayList<ParVariableAtributo>();
			if (variables_instancia != null) {
				int posicion_ambito_metodo_nm = lexema_metodo.indexOf(":");
				if (posicion_ambito_metodo_nm != -1) { //no deberia ser -1
					String ambito_metodo_nm = lexema_metodo.substring(posicion_ambito_metodo_nm+1, lexema_metodo.length());
					if (esDeClase(ambito_metodo_nm)) {
						for (String variable: variables_instancia) {
							String lexema_atributo_nm = variable.substring(variable.lastIndexOf(".")+1, variable.indexOf(":")) + ":" + ambito_metodo_nm;
							AtributosSimbolo atributos_lexema_atributo_nm = AnalizadorLexico.simbolos.get(lexema_atributo_nm);
							if (atributos_lexema_atributo_nm != null && atributos_lexema_atributo_nm.getUso().equals("nombre_atributo")) //en teoria deberia cumplirse siempre si se llego hasta aca
								pares_mapeo_invocacion.add(new ParVariableAtributo(variable, lexema_atributo_nm));
						}
					}
					else if (esDeClausulaIMPL(ambito_metodo_nm)) { //caso de que el metodo implementado en realidad sea un metodo de una clausula IMPL que implementa a un prototipo de metodo de una clase
						String ambito_interno_metodo_nm = ambito_metodo_nm + ":" + lexema_metodo.substring(0, posicion_ambito_metodo_nm);
						String ambito_clase_implementada = obtenerAmbitoClaseImplementada(ambito_interno_metodo_nm);
						for (String variable: variables_instancia) {
							String lexema_atributo_nm = variable.substring(variable.lastIndexOf(".")+1, variable.indexOf(":")) + ":" + ambito_clase_implementada;
							AtributosSimbolo atributos_lexema_atributo_nm = AnalizadorLexico.simbolos.get(lexema_atributo_nm);
							if (atributos_lexema_atributo_nm != null && atributos_lexema_atributo_nm.getUso().equals("nombre_atributo")) //en teoria deberia cumplirse siempre si se llego hasta aca
								pares_mapeo_invocacion.add(new ParVariableAtributo(variable, lexema_atributo_nm));
						}
					}
				}
			}
			return pares_mapeo_invocacion;
		}
		return null;
	}
	
	public String obtenerTipoElemento(String elemento) {
		if (elemento != null) {
			AtributosSimbolo atributos_constante = AnalizadorLexico.simbolos.get(elemento);
			if (atributos_constante != null)
				return atributos_constante.getTipo();
		}
		return null;
	}
	
	public String obtenerParametroFormalFuncion(String lexema_funcion) {
		AtributosSimbolo atributos_lexema_funcion_nm = AnalizadorLexico.simbolos.get(lexema_funcion);
		if (atributos_lexema_funcion_nm != null) {
			String nombre_parametro_formal = atributos_lexema_funcion_nm.getNombreParametroFormal();
			if (nombre_parametro_formal != null) {
				int posicion_ambito_funcion = lexema_funcion.indexOf(":");
				return nombre_parametro_formal + ":" + lexema_funcion.substring(posicion_ambito_funcion+1, lexema_funcion.length()) + ":" + lexema_funcion.substring(0, posicion_ambito_funcion);
			}
		}
		return null;
	}
	
}