%{
    import java.io.*;
    import analizadorSintactico.analizadorLexico.AnalizadorLexico;
    import analizadorSintactico.analizadorSemantico.AnalizadorSemantico;
%}

%token IF ELSE END_IF PRINT CLASS VOID INT ULONG DOUBLE FOR IN RANGE IMPL INTERFACE IMPLEMENT RETURN CONSTANTE_PF CONSTANTE_I CONSTANTE_UL CADENA_CARACTERES ID ASIGNADOR_MENOS_IGUAL COMP_MAYOR_IGUAL COMP_MENOR_IGUAL COMP_IGUAL COMP_DISTINTO
%start programa

%%

programa:	bloque_sentencias               {System.out.print("(PROGRAMA) "); actualizarAmbitoActual("-");}
		    | '{' '}'                       {System.out.print("(PROGRAMA VACIO) ");}
            | error bloque_sentencias       {System.out.print("(PROGRAMA) "); agregarError("ERROR: Solo puede haber sentencias dentro de las llaves del programa"); actualizarAmbitoActual("-");}
            | error '{' '}'                 {System.out.print("(PROGRAMA VACIO) "); agregarError("ERROR: Solo puede haber sentencias dentro de las llaves del programa"); actualizarAmbitoActual("-");}
            | error '{' '}' error           {System.out.print("(PROGRAMA VACIO) "); agregarError("ERROR: Solo puede haber sentencias dentro de las llaves del programa"); actualizarAmbitoActual("-");}      
            | bloque_sentencias error       {System.out.print("(PROGRAMA) "); agregarError("ERROR: Solo puede haber sentencias dentro de las llaves del programa"); actualizarAmbitoActual("-");}
            | '{' '}' error                 {System.out.print("(PROGRAMA VACIO) "); agregarError("ERROR: Solo puede haber sentencias dentro de las llaves del programa"); actualizarAmbitoActual("-");}

;

bloque_sentencias:  '{' lista_sentencias '}'
                    | lista_sentencias  '}'     {agregarError("ERROR: Bloque de sentencias de programa invalido, el formato correcto es: '{' lista_sentencias '}' con cada sentencia delimitada por ','");} 

;

lista_sentencias:   lista_sentencias sentencia
                    | sentencia
                    | sentencia_ejecutable_de_return_parcial        {agregarError("ERROR: El programa principal no puede contener de manera directa sentencias que impliquen posibles retornos");}
                    | sentencia_ejecutable_de_return_completa       {agregarError("ERROR: El programa principal no puede contener de manera directa sentencias que impliquen retornos");}

;

sentencia:  sentencia_declarativa
            | sentencia_ejecutable
            | error ','                 {agregarError("ERROR: Sentencia invalida sintacticamente");}

;

bloque_sentencias_declarativas:     '{' lista_sentencias_declarativas '}'
                                    | lista_sentencias_declarativas '}'             {agregarError("ERROR: Bloque de sentencias declarativas invalido, el formato correcto es: '{' lista_sentencias_declarativas '}' con cada sentencia delimitada por ','");}
                                    | error lista_sentencias_declarativas '}'       {agregarError("ERROR: Bloque de sentencias declarativas invalido, el formato correcto es: '{' lista_sentencias_declarativas '}' con cada sentencia delimitada por ','");}

;

lista_sentencias_declarativas:      lista_sentencias_declarativas sentencia_declarativa
                                    | sentencia_declarativa
                                    | sentencia_ejecutable                                                              {agregarError("ERROR: No puede haber sentencias ejecutables, en este bloque solo se aceptan sentencias declarativas");}
                                    | sentencia_ejecutable_de_return_completa                                           {agregarError("ERROR: No puede haber sentencias ejecutables, en este bloque solo se aceptan sentencias declarativas");}
                                    | sentencia_ejecutable_de_return_parcial                                            {agregarError("ERROR: No puede haber sentencias ejecutables, en este bloque solo se aceptan sentencias declarativas");}
                                    | lista_sentencias_declarativas sentencia_ejecutable                                {agregarError("ERROR: No puede haber sentencias ejecutables, en este bloque solo se aceptan sentencias declarativas");}
                                    | lista_sentencias_declarativas sentencia_ejecutable_de_return_parcial              {agregarError("ERROR: No puede haber sentencias ejecutables, en este bloque solo se aceptan sentencias declarativas");}
                                    | lista_sentencias_declarativas bloque_sentencias_ejecutables_de_return_completo    {agregarError("ERROR: No puede haber sentencias ejecutables, en este bloque solo se aceptan sentencias declarativas");}
;

sentencia_declarativa:  declaracion_variables
                        | declaracion_referencia_clase
                        | declaracion_metodo
                        | declaracion_clase
                        | declaracion_clausula_impl
                        | declaracion_interfaz

;

declaracion_variables:  tipo_numerico lista_variables ','   {System.out.print("(DECLARACION DE LISTA DE VARIABLES NUMERICAS) ");}
                        | ID lista_variables ','            {System.out.print("(DECLARACION DE LISTA DE VARIABLES DEL TIPO DE UNA CLASE) ");}

;

tipo_numerico:	INT
                | ULONG
                | DOUBLE

;

lista_variables:	lista_variables ';' ID
			        | ID
                    | lista_variables ID        {agregarError("ERROR: Declaracion invalida de variable en lista de variables, falta el ';'");}

;

declaracion_referencia_clase:   ID ','      {System.out.print("(DECLARACION DE REFERENCIA A CLASE) ");}

;

declaracion_metodo:     declaracion_prototipo
                        | declaracion_funcion

;

bloque_declaraciones_prototipos:    '{' lista_declaraciones_prototipos '}'
                                    | lista_declaraciones_prototipos '}'                {agregarError("ERROR: Bloque de declaraciones de prototipos invalido, el formato correcto es: '{' lista_declaraciones_prototipos '}' con cada prototipo delimitado por ','");}
                                    | error lista_declaraciones_prototipos '}'          {agregarError("ERROR: Bloque de declaraciones de prototipos invalido, el formato correcto es: '{' lista_declaraciones_prototipos '}' con cada prototipo delimitado por ','");}

;

lista_declaraciones_prototipos:     lista_declaraciones_prototipos declaracion_prototipo
                                    | declaracion_prototipo

;

declaracion_prototipo:      encabezado_metodo parametro ','       {System.out.print("(DECLARACION DE PROTOTIPO DE FUNCION) "); actualizarAmbitoActual("-");}

;

encabezado_metodo:      VOID ID      {actualizarAmbitoActual($2.sval);}        
                        | VOID       {agregarError("ERROR: Encabezado de metodo invalido, el formato correcto es: VOID ID");}

;

parametro:	'(' tipo_numerico ID ')'
            | '(' ')'
            | tipo_numerico ID ')'              {agregarError("ERROR: Parametro invalido, el formato correcto es: '(' tipo_numerico ID ')' o '(' ')'");}   
            | '(' tipo_numerico ID              {agregarError("ERROR: Parametro invalido, el formato correcto es: '(' tipo_numerico ID ')' o '(' ')'");}
            | '(' error ')'                     {agregarError("ERROR: Parametro invalido, el formato correcto es: '(' tipo_numerico ID ')' o '(' ')'");}

;

bloque_declaraciones_funciones:     '{' lista_declaraciones_funciones '}'
                                    | lista_declaraciones_funciones '}'                 {agregarError("ERROR: Bloque de declaraciones de funciones invalido, el formato correcto es: '{' lista_declaraciones_funciones '}' con cada funcion delimitada por ','");}
                                    | error lista_declaraciones_funciones '}'           {agregarError("ERROR: Bloque de declaraciones de funciones invalido, el formato correcto es: '{' lista_declaraciones_funciones '}' con cada funcion delimitada por ','");}

;

lista_declaraciones_funciones:      lista_declaraciones_funciones declaracion_funcion
                                    | declaracion_funcion

;

declaracion_funcion:	encabezado_metodo parametro cuerpo_funcion ','        {System.out.print("(DECLARACION DE FUNCION) "); actualizarAmbitoActual("-");}

;

cuerpo_funcion:	    '{' lista_sentencias_funcion sentencia_ejecutable_de_return_completa '}'
                    | '{' sentencia_ejecutable_de_return_completa '}'
                    | sentencia_ejecutable_de_return_completa '}'                                   {agregarError("ERROR: Cuerpo de funcion sin llave de apertura");}
                    | '{' sentencia_ejecutable_de_return_completa                                   {agregarError("ERROR: Cuerpo de funcion sin llave de cierre");}
                    | '{' lista_sentencias_funcion sentencia_ejecutable_de_return_completa          {agregarError("ERROR: Cuerpo de funcion sin llave de cierre");}
                    | lista_sentencias_funcion '}'                                                  {agregarError("ERROR: Cuerpo de funcion sin llave de apertura y no se garantiza retorno para todas las ramas del mismo");}
                    | lista_sentencias_funcion sentencia_ejecutable_de_return_completa '}'          {agregarError("ERROR: Cuerpo de funcion sin llave de apertura");}
                    | '{' lista_sentencias_funcion '}'                                              {agregarError("ERROR: No se garantiza retorno para todas las ramas del cuerpo de funcion");}
                    | '{'  '}'                                                                      {agregarError("ERROR: Cuerpo de funcion sin retorno");}

;

lista_sentencias_funcion:       lista_sentencias_funcion sentencia
                                | lista_sentencias_funcion sentencia_ejecutable_de_return_parcial
                                | sentencia
                                | sentencia_ejecutable_de_return_parcial

;

declaracion_clase:  encabezado_clase cuerpo_clase           {System.out.print("(DECLARACION DE CLASE) ");}

;

encabezado_clase:   CLASS ID                            
                    | CLASS ID IMPLEMENT ID            
                    | CLASS error                           {agregarError("ERROR: Encabezado de clase invalido, los formatos correctos son: CLASS ID y CLASS ID IMPLEMENT ID");}
                    | CLASS IMPLEMENT error                 {agregarError("ERROR: Encabezado de clase invalido, los formatos correctos son: CLASS ID y CLASS ID IMPLEMENT ID");}
                    | CLASS ID IMPLEMENT error              {agregarError("ERROR: Encabezado de clase invalido, los formatos correctos son: CLASS ID y CLASS ID IMPLEMENT ID");}
                    | CLASS IMPLEMENT ID  error             {agregarError("ERROR: Encabezado de clase invalido, los formatos correctos son: CLASS ID y CLASS ID IMPLEMENT ID");}
                    
;

cuerpo_clase:   bloque_sentencias_declarativas ','
                | '{' '}' ','

;

declaracion_clausula_impl:  encabezado_clausula_impl cuerpo_clausula_impl      {System.out.print("(DECLARACION DE CLAUSULA IMPL) ");}

;

encabezado_clausula_impl:   IMPL FOR ID ':'     
                            | IMPL FOR ':' error        {agregarError("ERROR: Encabezado de clausula IMPL invalido, el formato correcto es: IMPL FOR ID ':'");}
                            | IMPL ID ':' error         {agregarError("ERROR: Encabezado de clausula IMPL invalido, el formato correcto es: IMPL FOR ID ':'");}
                            | IMPL ':' error            {agregarError("ERROR: Encabezado de clausula IMPL invalido, el formato correcto es: IMPL FOR ID ':'");}
                            | IMPL FOR ID error         {agregarError("ERROR: Encabezado de clausula IMPL invalido, el formato correcto es: IMPL FOR ID ':'");}
                            | IMPL FOR error            {agregarError("ERROR: Encabezado de clausula IMPL invalido, el formato correcto es: IMPL FOR ID ':'");}
                            | IMPL ID error             {agregarError("ERROR: Encabezado de clausula IMPL invalido, el formato correcto es: IMPL FOR ID ':'");}
                            | IMPL error                {agregarError("ERROR: Encabezado de clausula IMPL invalido, el formato correcto es: IMPL FOR ID ':'");}

;

cuerpo_clausula_impl:       bloque_declaraciones_funciones ','
                            | '{' '}' ','

;

declaracion_interfaz:   encabezado_interfaz cuerpo_interfaz             {System.out.print("(DECLARACION DE INTERFAZ) ");}

;

encabezado_interfaz:    INTERFACE ID
                        | INTERFACE     {agregarError("ERROR: Encabezado de interfaz invalido, el formato correcto es: INTERFACE ID");}

;

cuerpo_interfaz:        bloque_declaraciones_prototipos ','
                        | '{' '}' ','

;

sentencia_ejecutable:	asignacion
                        | invocacion_funcion
                        | clausula_seleccion_if
                        | salida_mensaje
                        | sentencia_for

;

sentencia_ejecutable_de_return_parcial:      clausula_seleccion_if_de_return_parcial
                                            | sentencia_for_de_return

;

sentencia_ejecutable_de_return_completa:    sentencia_return
                                            | clausula_seleccion_if_de_return_completa

;

sentencia_return:   RETURN ','

;
			
bloque_sentencias_ejecutables:	'{' lista_sentencias_ejecutables '}'

;

lista_sentencias_ejecutables:   lista_sentencias_ejecutables sentencia_ejecutable
				                | sentencia_ejecutable
                                | sentencia_declarativa     {agregarError("ERROR: No puede haber sentencias declarativas, en este bloque solo se aceptan sentencias ejecutables");}
                                | lista_sentencias_ejecutables sentencia_declarativa    {agregarError("ERROR: No puede haber sentencias declarativas, en este bloque solo se aceptan sentencias ejecutables");}

;

bloque_sentencias_ejecutables_de_return_parcial:     '{' lista_sentencias_ejecutables_de_return_parcial '}'

;

lista_sentencias_ejecutables_de_return_parcial:      lista_sentencias_ejecutables_de_return_parcial sentencia_ejecutable
                                                    | lista_sentencias_ejecutables_de_return_parcial sentencia_ejecutable_de_return_parcial
                                                    | lista_sentencias_ejecutables sentencia_ejecutable_de_return_parcial
                                                    | sentencia_ejecutable_de_return_parcial

;

bloque_sentencias_ejecutables_de_return_completo:       '{' lista_sentencias_ejecutables sentencia_ejecutable_de_return_completa '}'
                                                        | '{' lista_sentencias_ejecutables_de_return_parcial sentencia_ejecutable_de_return_completa '}'
                                                        | '{' sentencia_ejecutable_de_return_completa '}'

;

asignacion: referencia '=' expresion_aritmetica ','                         {System.out.print("(ASIGNACION USANDO OPERADOR DE IGUAL) ");}
		    | referencia ASIGNADOR_MENOS_IGUAL expresion_aritmetica ','     {System.out.print("(ASIGNACION USANDO OPERADOR DE MENOS IGUAL) ");}                  

;

expresion_aritmetica:	expresion_aritmetica '+' termino
                        | expresion_aritmetica '-' termino
                        | termino

;

termino:	termino '*' factor
            | termino '/' factor
            | factor

;

factor:		referencia
		    | constante

;

constante:	CONSTANTE_I         {verificarRango($1.sval);}
            | '-' CONSTANTE_I   {analizador_lexico.constanteNegativaDetectada($2.sval);}
            | CONSTANTE_UL
            | CONSTANTE_PF
            | '-' CONSTANTE_PF  {analizador_lexico.constanteNegativaDetectada($2.sval);}

;

invocacion_funcion:	    referencia parametro_real ','       {System.out.print("(INVOCACION A FUNCION) ");}

;

referencia:	referencia '.' ID
		    | ID

;

parametro_real:     '(' expresion_aritmetica ')'
                    | '(' ')'
                    | '(' error ')'                 {agregarError("ERROR: Parametro real invalido, el formato correcto es: '(' expresion_aritmetica ')' o '(' ')' ");}

;

clausula_seleccion_if:	IF condicion cuerpo_if END_IF ','                                               {System.out.print("(CLAUSULA DE SELECCION IF SIN POSIBLES RETORNOS Y SIN ELSE) ");}
                        | IF condicion cuerpo_if ELSE cuerpo_else END_IF ','                            {System.out.print("(CLAUSULA DE SELECCION IF SIN POSIBLES RETORNOS Y ELSE SIN POSIBLES RETORNOS) ");}

;

clausula_seleccion_if_de_return_parcial:    IF condicion cuerpo_if_retorno_parcial END_IF ','                                                               {System.out.print("(CLAUSULA DE SELECCION IF DE RETORNO PARCIAL Y SIN ELSE) ");}
                                            | IF condicion cuerpo_if_retorno_parcial ELSE cuerpo_else END_IF ','                                            {System.out.print("(CLAUSULA DE SELECCION IF DE RETORNO PARCIAL Y ELSE SIN POSIBLES RETORNOS) ");}
                                            | IF condicion cuerpo_if ELSE cuerpo_else_retorno_parcial END_IF ','                                            {System.out.print("(CLAUSULA DE SELECCION IF SIN POSIBLES RETORNOS Y ELSE DE RETORNO PARCIAL) ");}
                                            | IF condicion cuerpo_if_retorno_parcial ELSE cuerpo_else_retorno_parcial END_IF ','                            {System.out.print("(CLAUSULA DE SELECCION IF DE RETORNO PARCIAL Y ELSE DE RETORNO PARCIAL) ");}     
                                            | IF condicion cuerpo_if_retorno_completo END_IF ','                                                            {System.out.print("(CLAUSULA DE SELECCION IF DE RETORNO COMPLETO Y SIN ELSE) ");}
                                            | IF condicion cuerpo_if_retorno_parcial ELSE cuerpo_else_retorno_completo END_IF ','                           {System.out.print("(CLAUSULA DE SELECCION IF DE RETORNO PARCIAL Y ELSE DE RETORNO COMPLETO) ");}
                                            | IF condicion cuerpo_if ELSE cuerpo_else_retorno_completo END_IF ','                                           {System.out.print("(CLAUSULA DE SELECCION IF SIN POSIBLES RETORNOS Y ELSE DE RETORNO COMPLETO) ");}
                                            | IF condicion cuerpo_if_retorno_completo ELSE cuerpo_else_retorno_parcial END_IF ','                           {System.out.print("(CLAUSULA DE SELECCION IF DE RETORNO COMPLETO Y ELSE DE RETORNO PARCIAL) ");}
                                            | IF condicion cuerpo_if_retorno_completo ELSE cuerpo_else END_IF ','                                           {System.out.print("(CLAUSULA DE SELECCION IF DE RETORNO COMPLETO Y ELSE SIN POSIBLES RETORNOS) ");}
;

clausula_seleccion_if_de_return_completa:       IF condicion cuerpo_if_retorno_completo ELSE cuerpo_else_retorno_completo END_IF ','                        {System.out.print("(CLAUSULA DE SELECCION IF ELSE DE RETORNO COMPLETO) ");}

;

cuerpo_if:      bloque_sentencias_ejecutables               
                | sentencia_ejecutable
                | '{' '}'

;

cuerpo_else:    cuerpo_if  

;

cuerpo_if_retorno_parcial:      bloque_sentencias_ejecutables_de_return_parcial
                                | sentencia_ejecutable_de_return_parcial

;

cuerpo_else_retorno_parcial:    cuerpo_if_retorno_parcial

;

cuerpo_if_retorno_completo:     bloque_sentencias_ejecutables_de_return_completo
                                | sentencia_ejecutable_de_return_completa

;

cuerpo_else_retorno_completo:   cuerpo_if_retorno_completo

;
 
condicion:      '(' comparacion ')'
                | comparacion ')'       {agregarError("ERROR: Condicion de clausula IF invalida, el formato correcto es: '(' comparacion ')'");}
                | '(' comparacion       {agregarError("ERROR: Condicion de clausula IF invalida, el formato correcto es: '(' comparacion ')'");}
                | '(' error ')'         {agregarError("ERROR: Condicion de clausula IF invalida, el formato correcto es: '(' comparacion ')'");}
                | comparacion           {agregarError("ERROR: Condicion de clausula IF invalida, el formato correcto es: '(' comparacion ')'");}

;

comparacion:	expresion_aritmetica '>' expresion_aritmetica
                | expresion_aritmetica '<' expresion_aritmetica
                | expresion_aritmetica COMP_MAYOR_IGUAL expresion_aritmetica
                | expresion_aritmetica COMP_MENOR_IGUAL expresion_aritmetica
                | expresion_aritmetica COMP_IGUAL expresion_aritmetica
                | expresion_aritmetica COMP_DISTINTO expresion_aritmetica

;

salida_mensaje:	    PRINT CADENA_CARACTERES ','     {System.out.print("(SALIDA DE MENSAJE) ");}
                    | PRINT ','                     {System.out.print("(SALIDA DE MENSAJE) "); agregarError("ERROR: Salida de mensaje invalida, el formato correcto es: PRINT CADENA_CARACTERES ','");}

;

sentencia_for:	encabezado_for control_rango_iteraciones cuerpo_for             {System.out.print("(SENTENCIA FOR) ");}

;

cuerpo_for:     sentencia_ejecutable
                | bloque_sentencias_ejecutables ','
                | '{' '}' ','     

;

sentencia_for_de_return:    encabezado_for control_rango_iteraciones cuerpo_for_retorno                 {System.out.print("(SENTENCIA FOR CON SENTENCIA EJECUTABLE DE RETORNO PARCIAL) ");}

;

cuerpo_for_retorno:     sentencia_ejecutable_de_return_parcial
                        | sentencia_ejecutable_de_return_completa
                        | bloque_sentencias_ejecutables_de_return_parcial ','
                        | bloque_sentencias_ejecutables_de_return_completo  ','     

;

encabezado_for:     FOR ID IN RANGE    
                    | FOR ID RANGE      {agregarError("ERROR: Encabezado de bucle for invalido, el formato correcto es: FOR ID IN RANGE");}
                    | FOR ID IN         {agregarError("ERROR: Encabezado de bucle for invalido, el formato correcto es: FOR ID IN RANGE");}
                    | FOR ID            {agregarError("ERROR: Encabezado de bucle for invalido, el formato correcto es: FOR ID IN RANGE");}
                    | FOR IN RANGE      {agregarError("ERROR: Encabezado de bucle for invalido, el formato correcto es: FOR ID IN RANGE");}
                    | FOR IN            {agregarError("ERROR: Encabezado de bucle for invalido, el formato correcto es: FOR ID IN RANGE");}
                    | FOR RANGE         {agregarError("ERROR: Encabezado de bucle for invalido, el formato correcto es: FOR ID IN RANGE");}
                    | FOR               {agregarError("ERROR: Encabezado de bucle for invalido, el formato correcto es: FOR ID IN RANGE");}

;

control_rango_iteraciones:      '(' constante ';' constante ';' constante ')'
                                | constante ';' constante ';' constante ')'     {agregarError("ERROR: Control del rango de iteraciones de bucle FOR invalido, el formato correcto es: '(' constante ';' constante ';' constante ')'");}
                                | '(' constante ';' constante ';' constante     {agregarError("ERROR: Control del rango de iteraciones de bucle FOR invalido, el formato correcto es: '(' constante ';' constante ';' constante ')'");}
                                | constante ';' constante ';' constante         {agregarError("ERROR: Control del rango de iteraciones de bucle FOR invalido, el formato correcto es: '(' constante ';' constante ';' constante ')'");}
                                | '(' error ')'                                 {agregarError("ERROR: Control del rango de iteraciones de bucle FOR invalido, el formato correcto es: '(' constante ';' constante ';' constante ')'");}       

;

%%

static AnalizadorLexico analizador_lexico = null;
static AnalizadorSemantico analizador_semantico = null;
static Parser par = null;
static boolean error_compilacion = false;

public static void main (String [] args) {
    if (args.length == 1) {
            System.out.println();
            System.out.println("------------------------ANALISIS LEXICO Y SINTACTICO DEL PROGRAMA------------------------");
            System.out.println();
			System.out.println("Iniciando compilacion");
            analizador_lexico = new AnalizadorLexico(args[0]);
            analizador_semantico = new AnalizadorSemantico();
            par = new Parser(false);
            par.run();
            System.out.println();
            System.out.println("Fin compilacion");
            if (error_compilacion) {
                System.out.println();
                System.out.println("        ----------------------------------------");
                System.out.println("        - El programa no compilo correctamente -");
                System.out.println("        ----------------------------------------");
            }
            else {
                System.out.println();
                System.out.println("        -------------------------------------");
                System.out.println("        - El programa compilo correctamente -");
                System.out.println("        -------------------------------------");
            }
            analizador_lexico.imprimirTsYErrores();
	}
	else
		System.out.println("ERROR: Parametros invalidos"); 
}

int yylex() {
    int token = analizador_lexico.obtenerSiguienteToken();
    yylval = new ParserVal(AnalizadorLexico.clave_tabla_simbolos);
    return token;
}

void yyerror (String s) {
    System.out.print(s + " ");
    error_compilacion = true;
}

void agregarError(String error) {
    AnalizadorLexico.errores_y_warnings.add("Linea " + AnalizadorLexico.numero_linea + " / Posicion " + (AnalizadorLexico.indice_caracter_leer -1) + " - " + error);
    error_compilacion = true; //ya que hay ciertos errores para los cuales no se lanza el syntax error y cuentan con un error especificado a traves de una accion de una regla (aquellos que se "solucionan" usando solo gramatica de errores pura), pero hay que considerarlos 
}

void verificarRango(String constante) {
    int posicion_guion = constante.indexOf('_');
    Integer parte_numerica = Integer.valueOf(constante.substring(0, posicion_guion));
    if (parte_numerica == 32768) {
        agregarError("ERROR: Constante entera simple fuera de rango");
        analizador_lexico.eliminarConstanteTS(constante);
    }
}

void actualizarAmbitoActual(String ambito_nuevo) {
    analizador_semantico.actualizarAmbitoActual(ambito_nuevo);
}