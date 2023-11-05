package analizadorSintactico.analizadorSemantico;
import java.util.ArrayList;

import analizadorSintactico.analizadorLexico.AnalizadorLexico;
import analizadorSintactico.analizadorLexico.AtributosSimbolo;

public class AnalizadorSemantico {
	public static String ambito_actual = "main";
	public static boolean ambito_invalido_detectado = false;
	public static String ambito_metodos_impl_actual = "";
	public static int cantidad_prototipos_interfaz_actual = 0;
	public static int cantidad_prototipos_interfaz_utilizados = 0;
	public static ArrayList<DatosAnidamiento> metodos_actuales_anidamientos = new ArrayList<DatosAnidamiento>();
	
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
			else if (ambito_nuevo.charAt(0) == '<')
				ambito_actual = ambito_actual + "<INVALIDO>";
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
			if (!uso.equals("nombre_funcion_clausula_impl") || (existePrototipoMetodo(lexema, ambito_metodos_impl_actual))) {
				String lexema_nm = lexema + ":" + ambito_actual; //nm quiere decir "name mangling", porque se obtiene el lexema completo con el ambito del identificador
				AtributosSimbolo atributos_id_nm = AnalizadorLexico.simbolos.get(lexema_nm);
				if (atributos_id_nm == null) {
					atributos_id_nm = AnalizadorLexico.simbolos.remove(lexema);
					atributos_id_nm.setUso(uso);
					AnalizadorLexico.simbolos.put(lexema_nm, atributos_id_nm);
					if (uso.equals("nombre_funcion_clausula_impl")) {
						setearLexemaImplementacionPrototipoMetodo(lexema, lexema_nm, ambito_metodos_impl_actual); //permite setear el lexema de implementacion de un prototipo de metodo (o sea cual es la funcion que realmente la esta definiendo)
						metodos_actuales_anidamientos.add(new DatosAnidamiento(lexema_nm, 0, 0)); //porque lo que implementa una clausula IMPL es un metodo de una clase, y entonces es necesario hacer el control de la cantidad de anidamientos ahi tambien
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
					if (uso.equals("nombre_funcion") || uso.equals("nombre_metodo") || uso.equals("nombre_funcion_clausula_impl") || uso.equals("nombre_clase") || uso.equals("nombre_interfaz")) //esos son los casos para los cuales ante una redeclaracion hay que no considerar todo lo que este dentro
						ambito_invalido_detectado = true;
				}
			}
			else { //es el caso de que no exista el prototipo de funcion a implementar por una clausula IMPL en la clase correspondiente
				AnalizadorLexico.simbolos.remove(lexema);
				int posicion_ultimo_ambito_clase = ambito_metodos_impl_actual.lastIndexOf("(");
				String nombre_clase_impl = ambito_metodos_impl_actual.substring(posicion_ultimo_ambito_clase+1, ambito_metodos_impl_actual.length()-1);
				AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: No existe el prototipo de metodo '" + lexema + "' dentro de la clase '" + nombre_clase_impl + "'");
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
	
	public void registrarTipoID(String tipo, String variables_lista) {
		String[] variables = variables_lista.split(";");
		for (String variable: variables) {
			String lexema_nm = variable + ":" + ambito_actual;
			AtributosSimbolo atributos_id_nm = AnalizadorLexico.simbolos.get(lexema_nm);
			if (atributos_id_nm != null && atributos_id_nm.getTipo() == null) //porque si por ejemplo el nombre de una variable ya fue usado en otra declaracion en ese ambito, no se guarda su lexema y entonces tampoco hay que agregar el tipo para ese caso, porque sino se pisaria el de la primera declaracion (si ya tiene seteado el tipo es porque no hay que volver a setearlo)
				atributos_id_nm.setTipo(tipo);
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
		return ambito.substring(0, posicion_ultimo_ambito_interfaz);
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
						if (nombre_parametro_en_interfaz != null && nombre_parametro != null) {
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
	
	public String verificarVariableFuncionReferenciada(String nombre_variable, String ambito, boolean referencia_compuesta) {
		if (esAmbitoValido()) {
			ArrayList<String> usos = new ArrayList<String>();
			usos.add("nombre_atributo");
			usos.add("nombre_metodo");
			if (!referencia_compuesta) { //ya que si se trata de una referencia concatenada con ".", no se tiene que permitir acceder a variables o funciones, solamente a atributos o metodos de clase
				usos.add("nombre_variable");
				usos.add("nombre_parametro_formal");
				usos.add("nombre_funcion");
			}
			String lexema_variable_referenciada_nm = obtenerLexemaNmMasCercano(nombre_variable, usos, ambito);
			AtributosSimbolo atributos_variable_referenciada_nm = AnalizadorLexico.simbolos.get(lexema_variable_referenciada_nm);
			if (atributos_variable_referenciada_nm != null)
				return lexema_variable_referenciada_nm;
			else
				AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - ERROR: No existe ninguna variable o funcion llamada '" + nombre_variable + "' al alcance");
		}
		return null;
	}
	/*
	public String verificarReferenciaValida(String referencia_parcial, String nombre_variable) {
		
	}*/
	
	public void verificarReferenciasVariablesEnAsignaciones() {
		for (String lexema: AnalizadorLexico.simbolos.keySet()) {
			AtributosSimbolo atributos_variable_nm = AnalizadorLexico.simbolos.get(lexema);
			String uso = atributos_variable_nm.getUso();
			if (uso.equals("nombre_variable") || uso.equals("nombre_atributo") || uso.equals("nombre_parametro_formal"))
				if (!atributos_variable_nm.fueReferenciada()) {
					String nombre_variable = lexema.substring(0, lexema.indexOf(":"));
					AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer - 1) + " - WARNING: Una variable llamada '" + nombre_variable + "' fue declarada pero nunca referenciada en el lado derecho de alguna asignacion");
				}
        }
	}
	
	private boolean esConstante(String simbolo) {
		if (simbolo.charAt(0) == '.' || simbolo.charAt(0) == '-' || Character.isDigit(simbolo.charAt(0)))
			return true;
		return false;
	}
	
	public void registrarReferenciasLadoDerechoAsignacion(String expresion_aritmetica) {
		if (esAmbitoValido()) {
			String[] elementos_expresion_aritmetica = expresion_aritmetica.split(" ");
			for (String elemento: elementos_expresion_aritmetica)
				if (!elemento.equals("+") && !elemento.equals("-") && !elemento.equals("*") && !elemento.equals("/") && !esConstante(elemento)) {
					AtributosSimbolo atributos_referencia_nm = AnalizadorLexico.simbolos.get(elemento);
					if (atributos_referencia_nm != null && (atributos_referencia_nm.getUso().equals("nombre_variable") ||  atributos_referencia_nm.getUso().equals("nombre_atributo")))
						atributos_referencia_nm.fueReferenciada();
				}
		}
	}
	
}