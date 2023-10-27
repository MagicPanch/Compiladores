package analizadorSintactico.analizadorLexico;

public class Main {

	public static void main(String[] args) {
		if (args.length == 1) {
			System.out.println();
			System.out.println("------------------------ANALISIS LEXICO DEL PROGRAMA------------------------");
			System.out.println();
			AnalizadorLexico analizador_lexico = new AnalizadorLexico(args[0]);
			boolean fin = false;
			while (!fin) {
				int token = analizador_lexico.obtenerSiguienteToken();
				if (token == 0)
					fin = true;
			}
			analizador_lexico.imprimirTsYErrores();
		}
		else
			System.out.println("ERROR: Parametros invalidos");
	}

}
