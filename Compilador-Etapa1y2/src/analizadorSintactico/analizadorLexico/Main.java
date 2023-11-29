package analizadorSintactico.analizadorLexico;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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
			try (BufferedReader br = new BufferedReader(new FileReader(args[0]));
            BufferedWriter bw = new BufferedWriter(new FileWriter("./Codigo_Testeo_Numerado.txt"))) {
                String linea;
                int numeroLinea = 1;
                while ((linea = br.readLine()) != null) {
                    bw.write("[" + numeroLinea + "]			" + linea);
                    bw.newLine();
                    numeroLinea++;
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
		}
		else
			System.out.println("ERROR: Parametros invalidos");
	}

}
