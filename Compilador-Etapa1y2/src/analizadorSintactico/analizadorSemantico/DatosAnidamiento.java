package analizadorSintactico.analizadorSemantico;

public class DatosAnidamiento {
	private String lexema_metodo;
	private int cantidad_actual_anidamientos; //permite registrar la cantidad de anidamientos de la rama actual del metodo
	private int cantidad_maxima_anidamientos; //permite registrar la cantidad maxima de anidamientos de funciones locales registrados en el metodo
	
	public DatosAnidamiento(String lexema_metodo, int cantidad_actual_anidamientos, int cantidad_maxima_anidamientos) {
		this.lexema_metodo = lexema_metodo;
		this.cantidad_actual_anidamientos = cantidad_actual_anidamientos;
		this.cantidad_maxima_anidamientos = cantidad_maxima_anidamientos;
	}

	public String getLexemaMetodo() {
		return lexema_metodo;
	}

	public void setLexemaMetodo(String lexema_metodo) {
		this.lexema_metodo = lexema_metodo;
	}

	public int getCantidadActualAnidamientos() {
		return cantidad_actual_anidamientos;
	}
	
	public void aumentarCantidadAnidamientos() {
		cantidad_actual_anidamientos++;
	}
	
	public void disminuirCantidadAnidamientos() {
		cantidad_actual_anidamientos--;
	}

	public void setCantidadActualAnidamientos(int cantidad_actual_anidamientos) {
		this.cantidad_actual_anidamientos = cantidad_actual_anidamientos;
	}

	public int getCantidadMaximaAnidamientos() {
		return cantidad_maxima_anidamientos;
	}

	public void setCantidadMaximaAnidamientos(int cantidad_maxima_anidamientos) {
		this.cantidad_maxima_anidamientos = cantidad_maxima_anidamientos;
	}
	
}
