%{
    import java.io.*;
    import analizadorSintactico.analizadorLexico.AnalizadorLexico;
    import analizadorSintactico.analizadorSemantico.AnalizadorSemantico;
    import analizadorSintactico.generadorCodigoIntermedio.GeneradorCodigoIntermedio;
    import analizadorSintactico.generadorCodigoIntermedio.Nodo;
%}

%token IF ELSE END_IF PRINT CLASS VOID INT ULONG DOUBLE FOR IN RANGE IMPL INTERFACE IMPLEMENT RETURN CONSTANTE_PF CONSTANTE_I CONSTANTE_UL CADENA_CARACTERES ID ASIGNADOR_MENOS_IGUAL COMP_MAYOR_IGUAL COMP_MENOR_IGUAL COMP_IGUAL COMP_DISTINTO
%start programa

%%

programa:	bloque_sentencias               {System.out.print("(PROGRAMA) "); analizador_semantico.actualizarAmbitoActual("-"); analizador_semantico.verificarReferenciasVariablesEnAsignaciones(); $$.nodo = generador_codigo_intermedio.generarNodoUnidireccional("PROGRAMA", $1.nodo); GeneradorCodigoIntermedio.nodo_programa = $$.nodo;}
		    | '{' '}'                       {System.out.print("(PROGRAMA VACIO) "); $$.nodo = generador_codigo_intermedio.generarNodoUnidireccional("PROGRAMA", null); GeneradorCodigoIntermedio.nodo_programa = $$.nodo;}
            | error bloque_sentencias       {System.out.print("(PROGRAMA) "); agregarError("ERROR: Solo puede haber sentencias dentro de las llaves del programa"); analizador_semantico.actualizarAmbitoActual("-"); analizador_semantico.verificarReferenciasVariablesEnAsignaciones(); $$.nodo = null;}
            | error '{' '}'                 {System.out.print("(PROGRAMA VACIO) "); agregarError("ERROR: Solo puede haber sentencias dentro de las llaves del programa"); analizador_semantico.actualizarAmbitoActual("-"); $$.nodo = null;}
            | error '{' '}' error           {System.out.print("(PROGRAMA VACIO) "); agregarError("ERROR: Solo puede haber sentencias dentro de las llaves del programa"); analizador_semantico.actualizarAmbitoActual("-"); $$.nodo = null;}      
            | bloque_sentencias error       {System.out.print("(PROGRAMA) "); agregarError("ERROR: Solo puede haber sentencias dentro de las llaves del programa"); analizador_semantico.actualizarAmbitoActual("-"); analizador_semantico.verificarReferenciasVariablesEnAsignaciones(); $$.nodo = null;}
            | '{' '}' error                 {System.out.print("(PROGRAMA VACIO) "); agregarError("ERROR: Solo puede haber sentencias dentro de las llaves del programa"); analizador_semantico.actualizarAmbitoActual("-"); $$.nodo = null;}

;

bloque_sentencias:  '{' lista_sentencias '}'    {$$.nodo = generador_codigo_intermedio.generarNodoUnidireccional("BLOQUE", $2.nodo);}
                    | lista_sentencias  '}'     {agregarError("ERROR: Bloque de sentencias de programa invalido, el formato correcto es: '{' lista_sentencias '}' con cada sentencia delimitada por ','"); $$.nodo = generador_codigo_intermedio.generarNodoUnidireccional("BLOQUE", $1.nodo);} 

;

lista_sentencias:   lista_sentencias sentencia                      {if ($2.nodo != null) {if ($1.nodo != null) $$.nodo = generador_codigo_intermedio.generarNodo("BLOQUE", $1.nodo, $2.nodo); else $$.nodo = $2.nodo;} else $$.nodo = $1.nodo;}            
                    | sentencia                                     {$$.nodo = $1.nodo;}
                    | sentencia_ejecutable_de_return_parcial        {agregarError("ERROR: El programa principal no puede contener de manera directa sentencias que impliquen posibles retornos"); $$.nodo = null;}
                    | sentencia_ejecutable_de_return_completa       {agregarError("ERROR: El programa principal no puede contener de manera directa sentencias que impliquen retornos"); $$.nodo = null;}

;

sentencia:  sentencia_declarativa       {$$.nodo = null;}
            | sentencia_ejecutable      {$$.nodo = $1.nodo;}
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

declaracion_variables:  tipo_numerico lista_variables ','   {System.out.print("(DECLARACION DE LISTA DE VARIABLES NUMERICAS) "); analizador_semantico.registrarTipoID($1.sval, $2.sval);}
                        | ID lista_variables ','            {System.out.print("(DECLARACION DE LISTA DE VARIABLES DEL TIPO DE UNA CLASE) "); analizador_semantico.eliminarEntradaOriginalID($1.sval); analizador_semantico.registrarTipoID($1.sval, $2.sval);}

;

tipo_numerico:	INT             {$$.sval = "INT";}
                | ULONG         {$$.sval = "ULONG";}         
                | DOUBLE        {$$.sval = "DOUBLE";}

;

lista_variables:	lista_variables ';' ID      {analizador_semantico.registrarAmbitoUsoID($3.sval, "nombre_variable"); $$.sval = $1.sval + ";" + $3.sval;}
			        | ID                        {analizador_semantico.registrarAmbitoUsoID($1.sval, "nombre_variable"); $$.sval = $1.sval;}
                    | lista_variables ID        {agregarError("ERROR: Declaracion invalida de variable en lista de variables, falta el ';'"); analizador_semantico.registrarAmbitoUsoID($2.sval, "nombre_variable"); $$.sval = $1.sval + ";" + $2.sval;}

;

declaracion_referencia_clase:   ID ','      {System.out.print("(DECLARACION DE REFERENCIA A CLASE) "); analizador_semantico.registrarAmbitoUsoID($1.sval, "referencia_clase");}

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

declaracion_prototipo:      encabezado_metodo parametro ','       {System.out.print("(DECLARACION DE PROTOTIPO DE FUNCION) "); analizador_semantico.verificarPosibleImplementacionFuncionInterfaz($1.sval); analizador_semantico.actualizarAmbitoActual("-"); analizador_semantico.definirUsoPrototipo($1.sval); analizador_semantico.verificarCantidadAnidamientos($1.sval);}

;

encabezado_metodo:      VOID ID         {analizador_semantico.registrarAmbitoUsoID($2.sval, "nombre_funcion"); analizador_semantico.actualizarAmbitoActual($2.sval); $$.sval = $2.sval;}        
                        | VOID          {agregarError("ERROR: Encabezado de funcion invalido, el formato correcto es: VOID ID"); analizador_semantico.marcarAmbitoInvalido(":"); $$.sval = null;}

; 

parametro:	'(' tipo_numerico ID ')'            {analizador_semantico.registrarAmbitoUsoID($3.sval, "nombre_parametro_formal"); analizador_semantico.eliminarEntradaOriginalID($3.sval); analizador_semantico.registrarTipoID($2.sval, $3.sval); analizador_semantico.chequearParametroFuncionIMPL($3.sval, $2.sval); $$.nodo = generador_codigo_intermedio.generarNodoUnidireccional("PARAMETRO_FORMAL", generador_codigo_intermedio.generarNodo(analizador_semantico.obtenerLexemaCompletoID($3.sval), null, null));}
            | '(' ')'                           {analizador_semantico.chequearParametroFuncionIMPL(null, null); $$.nodo = generador_codigo_intermedio.generarNodoUnidireccional("PARAMETRO_FORMAL", null);}
            | tipo_numerico ID ')'              {agregarError("ERROR: Parametro invalido, el formato correcto es: '(' tipo_numerico ID ')' o '(' ')'"); analizador_semantico.registrarAmbitoUsoID($2.sval, "nombre_parametro_formal"); analizador_semantico.eliminarEntradaOriginalID($2.sval); analizador_semantico.registrarTipoID($1.sval, $2.sval); analizador_semantico.chequearParametroFuncionIMPL($2.sval, $1.sval); $$.nodo = generador_codigo_intermedio.generarNodoUnidireccional("PARAMETRO_FORMAL", generador_codigo_intermedio.generarNodo(analizador_semantico.obtenerLexemaCompletoID($2.sval), null, null));}   
            | '(' tipo_numerico ID              {agregarError("ERROR: Parametro invalido, el formato correcto es: '(' tipo_numerico ID ')' o '(' ')'"); analizador_semantico.registrarAmbitoUsoID($3.sval, "nombre_parametro_formal"); analizador_semantico.eliminarEntradaOriginalID($3.sval); analizador_semantico.registrarTipoID($2.sval, $3.sval); analizador_semantico.chequearParametroFuncionIMPL($3.sval, $2.sval); $$.nodo = generador_codigo_intermedio.generarNodoUnidireccional("PARAMETRO_FORMAL", generador_codigo_intermedio.generarNodo(analizador_semantico.obtenerLexemaCompletoID($3.sval), null, null));}
            | '(' error ')'                     {agregarError("ERROR: Parametro invalido, el formato correcto es: '(' tipo_numerico ID ')' o '(' ')'"); analizador_semantico.actualizarAmbitoActual("-") ; analizador_semantico.marcarAmbitoInvalido(":"); analizador_semantico.chequearParametroFuncionIMPL(null, null); $$.nodo = null;}

;

bloque_declaraciones_funciones:     '{' lista_declaraciones_funciones '}'
                                    | lista_declaraciones_funciones '}'                 {agregarError("ERROR: Bloque de declaraciones de funciones invalido, el formato correcto es: '{' lista_declaraciones_funciones '}' con cada funcion delimitada por ','");}
                                    | error lista_declaraciones_funciones '}'           {agregarError("ERROR: Bloque de declaraciones de funciones invalido, el formato correcto es: '{' lista_declaraciones_funciones '}' con cada funcion delimitada por ','");}

;

lista_declaraciones_funciones:      lista_declaraciones_funciones declaracion_funcion
                                    | declaracion_funcion

;

declaracion_funcion:	encabezado_metodo parametro cuerpo_funcion ','                              {System.out.print("(DECLARACION DE FUNCION) "); analizador_semantico.verificarPosibleImplementacionFuncionInterfaz($1.sval); analizador_semantico.actualizarAmbitoActual("-"); analizador_semantico.verificarCantidadAnidamientos($1.sval); $$.nodo = generador_codigo_intermedio.generarNodo(analizador_semantico.obtenerLexemaCompletoID($1.sval), $2.nodo, $3.nodo); GeneradorCodigoIntermedio.funciones.add($$.nodo);}

;

cuerpo_funcion:	    '{' lista_sentencias_funcion sentencia_ejecutable_de_return_completa '}'        {$$.nodo = generador_codigo_intermedio.generarNodoUnidireccional("CUERPO_FUNCION", generador_codigo_intermedio.generarNodo("BLOQUE", $2.nodo, $3.nodo));}
                    | '{' sentencia_ejecutable_de_return_completa '}'                               {$$.nodo = generador_codigo_intermedio.generarNodoUnidireccional("CUERPO_FUNCION", $2.nodo);}
                    | sentencia_ejecutable_de_return_completa '}'                                   {agregarError("ERROR: Cuerpo de funcion sin llave de apertura"); $$.nodo = generador_codigo_intermedio.generarNodoUnidireccional("CUERPO_FUNCION", $1.nodo);}
                    | '{' sentencia_ejecutable_de_return_completa                                   {agregarError("ERROR: Cuerpo de funcion sin llave de cierre"); $$.nodo = generador_codigo_intermedio.generarNodoUnidireccional("CUERPO_FUNCION", $2.nodo);}
                    | '{' lista_sentencias_funcion sentencia_ejecutable_de_return_completa          {agregarError("ERROR: Cuerpo de funcion sin llave de cierre"); $$.nodo = generador_codigo_intermedio.generarNodoUnidireccional("CUERPO_FUNCION", generador_codigo_intermedio.generarNodo("BLOQUE", $2.nodo, $3.nodo));}
                    | lista_sentencias_funcion '}'                                                  {agregarError("ERROR: Cuerpo de funcion sin llave de apertura y no se garantiza retorno para todas las ramas del mismo"); $$.nodo = null;}
                    | lista_sentencias_funcion sentencia_ejecutable_de_return_completa '}'          {agregarError("ERROR: Cuerpo de funcion sin llave de apertura"); $$.nodo = generador_codigo_intermedio.generarNodoUnidireccional("CUERPO_FUNCION", generador_codigo_intermedio.generarNodo("BLOQUE", $1.nodo, $2.nodo));}
                    | '{' lista_sentencias_funcion '}'                                              {agregarError("ERROR: No se garantiza retorno para todas las ramas del cuerpo de funcion"); $$.nodo = null;}
                    | '{'  '}'                                                                      {agregarError("ERROR: Cuerpo de funcion sin retorno"); $$.nodo = null;}

;

lista_sentencias_funcion:       lista_sentencias_funcion sentencia                                      {if ($2.nodo != null) {if ($1.nodo != null) $$.nodo = generador_codigo_intermedio.generarNodo("BLOQUE", $1.nodo, $2.nodo); else $$.nodo = $2.nodo;} else $$.nodo = $1.nodo;}
                                | lista_sentencias_funcion sentencia_ejecutable_de_return_parcial       {if ($1.nodo != null) $$.nodo = generador_codigo_intermedio.generarNodo("BLOQUE", $1.nodo, $2.nodo); else $$.nodo = $2.nodo;}
                                | sentencia                                                             {$$.nodo = $1.nodo;}
                                | sentencia_ejecutable_de_return_parcial                                {$$.nodo = $1.nodo;}

;

declaracion_clase:  encabezado_clase cuerpo_clase           {System.out.print("(DECLARACION DE CLASE) "); analizador_semantico.chequearImplementacionTotalInterfaz($1.sval); analizador_semantico.actualizarAmbitoActual("()");}

;

encabezado_clase:   CLASS ID                                {analizador_semantico.registrarAmbitoUsoID($2.sval, "nombre_clase"); analizador_semantico.actualizarAmbitoActual("(" + $2.sval + ")"); $$.sval = $2.sval;}
                    | CLASS ID IMPLEMENT ID                 {analizador_semantico.registrarAmbitoUsoID($2.sval, "nombre_clase"); analizador_semantico.registrarInterfazImplementada($2.sval, $4.sval); analizador_semantico.actualizarAmbitoActual("(" + $2.sval + ")"); analizador_semantico.eliminarEntradaOriginalID($4.sval); $$.sval = $2.sval;}
                    | CLASS error                           {agregarError("ERROR: Encabezado de clase invalido, los formatos correctos son: CLASS ID y CLASS ID IMPLEMENT ID"); analizador_semantico.marcarAmbitoInvalido("()"); $$.sval = null;}
                    | CLASS IMPLEMENT error                 {agregarError("ERROR: Encabezado de clase invalido, los formatos correctos son: CLASS ID y CLASS ID IMPLEMENT ID"); analizador_semantico.marcarAmbitoInvalido("()"); $$.sval = null;}
                    | CLASS ID IMPLEMENT error              {agregarError("ERROR: Encabezado de clase invalido, los formatos correctos son: CLASS ID y CLASS ID IMPLEMENT ID"); analizador_semantico.registrarAmbitoUsoID($2.sval, "nombre_clase"); analizador_semantico.actualizarAmbitoActual("(" + $2.sval + ")"); $$.sval = $2.sval;}
                    | CLASS IMPLEMENT ID  error             {agregarError("ERROR: Encabezado de clase invalido, los formatos correctos son: CLASS ID y CLASS ID IMPLEMENT ID"); analizador_semantico.eliminarEntradaOriginalID($3.sval); analizador_semantico.marcarAmbitoInvalido("()"); $$.sval = null;}
                    
;

cuerpo_clase:   bloque_sentencias_declarativas ','
                | '{' '}' ','

;

declaracion_clausula_impl:  encabezado_clausula_impl cuerpo_clausula_impl      {System.out.print("(DECLARACION DE CLAUSULA IMPL) "); analizador_semantico.actualizarAmbitoActual("<>");}

;

encabezado_clausula_impl:   IMPL FOR ID ':'             {analizador_semantico.eliminarEntradaOriginalID($3.sval); analizador_semantico.chequearValidezClausulaIMPL($3.sval); analizador_semantico.actualizarAmbitoActual("<" + $3.sval + ">");}
                            | IMPL FOR ':' error        {agregarError("ERROR: Encabezado de clausula IMPL invalido, el formato correcto es: IMPL FOR ID ':'"); analizador_semantico.marcarAmbitoInvalido("<>");}
                            | IMPL ID ':' error         {agregarError("ERROR: Encabezado de clausula IMPL invalido, el formato correcto es: IMPL FOR ID ':'"); analizador_semantico.eliminarEntradaOriginalID($2.sval); analizador_semantico.chequearValidezClausulaIMPL($2.sval); analizador_semantico.actualizarAmbitoActual("<" + $2.sval + ">");}
                            | IMPL ':' error            {agregarError("ERROR: Encabezado de clausula IMPL invalido, el formato correcto es: IMPL FOR ID ':'"); analizador_semantico.marcarAmbitoInvalido("<>");}
                            | IMPL FOR ID error         {agregarError("ERROR: Encabezado de clausula IMPL invalido, el formato correcto es: IMPL FOR ID ':'"); analizador_semantico.eliminarEntradaOriginalID($3.sval); analizador_semantico.chequearValidezClausulaIMPL($3.sval); analizador_semantico.actualizarAmbitoActual("<" + $3.sval + ">");}
                            | IMPL FOR error            {agregarError("ERROR: Encabezado de clausula IMPL invalido, el formato correcto es: IMPL FOR ID ':'"); analizador_semantico.marcarAmbitoInvalido("<>");}
                            | IMPL ID error             {agregarError("ERROR: Encabezado de clausula IMPL invalido, el formato correcto es: IMPL FOR ID ':'"); analizador_semantico.eliminarEntradaOriginalID($2.sval); analizador_semantico.chequearValidezClausulaIMPL($2.sval); analizador_semantico.actualizarAmbitoActual("<" + $2.sval + ">");}
                            | IMPL error                {agregarError("ERROR: Encabezado de clausula IMPL invalido, el formato correcto es: IMPL FOR ID ':'"); analizador_semantico.marcarAmbitoInvalido("<>");}

;

cuerpo_clausula_impl:       bloque_declaraciones_funciones ','
                            | '{' '}' ','

;

declaracion_interfaz:   encabezado_interfaz cuerpo_interfaz             {System.out.print("(DECLARACION DE INTERFAZ) "); analizador_semantico.registrarCantidadPrototiposInterfaz($1.sval); analizador_semantico.actualizarAmbitoActual("[]");}

;

encabezado_interfaz:    INTERFACE ID    {analizador_semantico.registrarAmbitoUsoID($2.sval, "nombre_interfaz"); analizador_semantico.actualizarAmbitoActual("[" + $2.sval + "]"); $$.sval = $2.sval;}
                        | INTERFACE     {agregarError("ERROR: Encabezado de interfaz invalido, el formato correcto es: INTERFACE ID"); analizador_semantico.marcarAmbitoInvalido("[]"); $$.sval = null;}

;

cuerpo_interfaz:        bloque_declaraciones_prototipos ','
                        | '{' '}' ','

;

sentencia_ejecutable:	asignacion                      {$$.nodo = $1.nodo;}
                        | invocacion_funcion            {$$.nodo = $1.nodo;}
                        | clausula_seleccion_if         {$$.nodo = $1.nodo;}
                        | salida_mensaje                {$$.nodo = $1.nodo;}
                        | sentencia_for                 {$$.nodo = $1.nodo;}

;

sentencia_ejecutable_de_return_parcial:      clausula_seleccion_if_de_return_parcial        {$$.nodo = $1.nodo;}
                                            | sentencia_for_de_return                       {$$.nodo = $1.nodo;}

;

sentencia_ejecutable_de_return_completa:    sentencia_return                                {$$.nodo = $1.nodo;}
                                            | clausula_seleccion_if_de_return_completa      {$$.nodo = $1.nodo;}

;

sentencia_return:   RETURN ','      {$$.nodo = generador_codigo_intermedio.generarNodo("RETURN", null, null);}

;
			
bloque_sentencias_ejecutables:	'{' lista_sentencias_ejecutables '}'        {$$.nodo = $2.nodo;}

;

lista_sentencias_ejecutables:   lista_sentencias_ejecutables sentencia_ejecutable       {$$.nodo = generador_codigo_intermedio.generarNodo("BLOQUE", $1.nodo, $2.nodo);}
				                | sentencia_ejecutable                                  {$$.nodo = $1.nodo;}
                                | sentencia_declarativa                                 {agregarError("ERROR: No puede haber sentencias declarativas, en este bloque solo se aceptan sentencias ejecutables"); $$.nodo = null;}
                                | lista_sentencias_ejecutables sentencia_declarativa    {agregarError("ERROR: No puede haber sentencias declarativas, en este bloque solo se aceptan sentencias ejecutables"); $$.nodo = null;}

;

bloque_sentencias_ejecutables_de_return_parcial:     '{' lista_sentencias_ejecutables_de_return_parcial '}'     {$$.nodo = $2.nodo;}

;

lista_sentencias_ejecutables_de_return_parcial:      lista_sentencias_ejecutables_de_return_parcial sentencia_ejecutable                                {$$.nodo = generador_codigo_intermedio.generarNodo("BLOQUE", $1.nodo, $2.nodo);}
                                                    | lista_sentencias_ejecutables_de_return_parcial sentencia_ejecutable_de_return_parcial             {$$.nodo = generador_codigo_intermedio.generarNodo("BLOQUE", $1.nodo, $2.nodo);}
                                                    | lista_sentencias_ejecutables sentencia_ejecutable_de_return_parcial                               {$$.nodo = generador_codigo_intermedio.generarNodo("BLOQUE", $1.nodo, $2.nodo);}
                                                    | sentencia_ejecutable_de_return_parcial                                                            {$$.nodo = $1.nodo;}

;

bloque_sentencias_ejecutables_de_return_completo:       '{' lista_sentencias_ejecutables sentencia_ejecutable_de_return_completa '}'                            {$$.nodo = generador_codigo_intermedio.generarNodo("BLOQUE", $2.nodo, $3.nodo);}
                                                        | '{' lista_sentencias_ejecutables_de_return_parcial sentencia_ejecutable_de_return_completa '}'        {$$.nodo = generador_codigo_intermedio.generarNodo("BLOQUE", $2.nodo, $3.nodo);}
                                                        | '{' sentencia_ejecutable_de_return_completa '}'                                                       {$$.nodo = $2.nodo;}

;

asignacion: referencia_resultado '=' expresion_aritmetica ','                         {System.out.print("(ASIGNACION USANDO OPERADOR DE IGUAL) "); analizador_semantico.registrarReferenciasLadoDerechoAsignacion($3.sval); analizador_semantico.chequearAsignacionValida($1.sval, $3.sval); $$.nodo = generador_codigo_intermedio.generarNodo("=", $1.nodo, $3.nodo);}
		    | referencia_resultado ASIGNADOR_MENOS_IGUAL expresion_aritmetica ','     {System.out.print("(ASIGNACION USANDO OPERADOR DE MENOS IGUAL) "); analizador_semantico.registrarReferenciasLadoDerechoAsignacion($3.sval); analizador_semantico.chequearAsignacionValida($1.sval, $3.sval); $$.nodo = generador_codigo_intermedio.generarNodo("=", $1.nodo, obtenerNodoExpresion("-", generador_codigo_intermedio.generarNodo($1.sval, null, null), $3.nodo));}                  

;

expresion_aritmetica:	expresion_aritmetica '+' termino            {$$.sval = $1.sval + " " + "+" + " " + $3.sval; analizador_semantico.chequearValidezOperacionComparacionExpresiones($1.sval, $3.sval, false, true); $$.nodo = obtenerNodoExpresion("+", $1.nodo, $3.nodo);}
                        | expresion_aritmetica '-' termino          {$$.sval = $1.sval + " " + "-" + " " + $3.sval; analizador_semantico.chequearValidezOperacionComparacionExpresiones($1.sval, $3.sval, false, false); $$.nodo = obtenerNodoExpresion("-", $1.nodo, $3.nodo);}          
                        | termino                                   {$$.sval = $1.sval; $$.nodo = $1.nodo;}

;

termino:	termino '*' factor      {$$.sval = $1.sval + " " + "*" + " " + $3.sval; analizador_semantico.chequearValidezOperacionTerminoFactor($1.sval, $3.sval, true); $$.nodo = obtenerNodoExpresion("*", $1.nodo, $3.nodo);}
            | termino '/' factor    {$$.sval = $1.sval + " " + "/" + " " + $3.sval; analizador_semantico.chequearValidezOperacionTerminoFactor($1.sval, $3.sval, false); $$.nodo = obtenerNodoExpresion("/", $1.nodo, $3.nodo);}
            | factor                {$$.sval = $1.sval; $$.nodo = $1.nodo; if ($$.nodo != null && $1.sval != null) $$.nodo.setTipo(AnalizadorLexico.simbolos.get($1.sval).getTipo());}

;

factor:		referencia          {$$.sval = analizador_semantico.verificarReferenciaValida($1.sval, false); analizador_semantico.chequearTipoFactorValido($$.sval); $$.nodo = generador_codigo_intermedio.generarNodo($$.sval, null, null);}
		    | constante         {$$.sval = $1.sval; $$.nodo = $1.nodo;}

;

constante:	CONSTANTE_I         {verificarRango($1.sval); analizador_semantico.chequearValidezSimbolo($1.sval); $$.sval = $1.sval; $$.nodo = obtenerNodoConstante($1.sval, "INT");}
            | '-' CONSTANTE_I   {analizador_lexico.constanteNegativaDetectada($2.sval); analizador_semantico.chequearValidezSimbolo("-" + $2.sval); analizador_semantico.registrarTipoConstante("INT", "-" + $2.sval); $$.sval = "-" + $2.sval; $$.nodo = obtenerNodoConstante("-" + $2.sval, "INT");}
            | CONSTANTE_UL      {analizador_semantico.registrarTipoConstante("ULONG", $1.sval); analizador_semantico.chequearValidezSimbolo($1.sval); $$.sval = $1.sval; $$.nodo = obtenerNodoConstante($1.sval, "ULONG");}      
            | CONSTANTE_PF      {analizador_semantico.registrarTipoConstante("DOUBLE", $1.sval); analizador_semantico.chequearValidezSimbolo($1.sval); $$.sval = $1.sval; $$.nodo = obtenerNodoConstante($1.sval, "DOUBLE");}
            | '-' CONSTANTE_PF  {analizador_lexico.constanteNegativaDetectada($2.sval); analizador_semantico.chequearValidezSimbolo("-" + $2.sval); analizador_semantico.registrarTipoConstante("DOUBLE", "-" + $2.sval); $$.sval = "-" + $2.sval; $$.nodo = obtenerNodoConstante("-" + $2.sval, "DOUBLE");}

;

invocacion_funcion:	    referencia_funcion parametro_real ','       {System.out.print("(INVOCACION A FUNCION) "); analizador_semantico.chequearInvocacionFuncionValida(analizador_semantico.verificarReferenciaValida($1.sval, true), $2.sval); String funcion_invocada = analizador_semantico.obtenerFuncionInvocada($1.sval); $$.nodo = generador_codigo_intermedio.generarNodo("CALL", generador_codigo_intermedio.generarNodo(funcion_invocada, null, null), $2.nodo); if ($$.nodo != null) $$.nodo.setParesVariableAtributo(analizador_semantico.obtenerParesMapeoInvocacion(funcion_invocada, analizador_semantico.obtenerVariablesInstanciaInvocacion()));}

;

referencia_funcion:     referencia                  {$$.sval = analizador_semantico.verificarReferenciaValida($1.sval, true); AnalizadorSemantico.lexema_invocacion_metodo_actual = $1.sval;}

;

referencia_resultado:   referencia                  {$$.sval = analizador_semantico.verificarReferenciaValida($1.sval, false); $$.nodo = generador_codigo_intermedio.generarNodo($$.sval, null, null);}

;

referencia:	referencia '.' ID                       {analizador_semantico.eliminarEntradaOriginalID($3.sval); $$.sval = $1.sval + "." + $3.sval;}
		    | ID                                    {analizador_semantico.eliminarEntradaOriginalID($1.sval); $$.sval = $1.sval;}

;

parametro_real:     '(' expresion_aritmetica ')'    {$$.sval = $2.sval; $$.nodo = generador_codigo_intermedio.generarNodoUnidireccional("PARAMETRO_REAL", $2.nodo);}
                    | '(' ')'                       {$$.sval = null; $$.nodo = generador_codigo_intermedio.generarNodoUnidireccional("PARAMETRO_REAL", null);}  
                    | '(' error ')'                 {agregarError("ERROR: Parametro real invalido, el formato correcto es: '(' expresion_aritmetica ')' o '(' ')' "); $$.sval = null; $$.nodo = null;}

;

clausula_seleccion_if:	IF condicion cuerpo_if END_IF ','                                               {System.out.print("(CLAUSULA DE SELECCION IF SIN POSIBLES RETORNOS Y SIN ELSE) "); $$.nodo = generador_codigo_intermedio.generarNodo("IF", $2.nodo, generador_codigo_intermedio.generarNodo("CUERPO_IF", generador_codigo_intermedio.generarNodoUnidireccional("THEN", $3.nodo), generador_codigo_intermedio.generarNodoUnidireccional("ELSE", null)));}
                        | IF condicion cuerpo_if ELSE cuerpo_else END_IF ','                            {System.out.print("(CLAUSULA DE SELECCION IF SIN POSIBLES RETORNOS Y ELSE SIN POSIBLES RETORNOS) "); $$.nodo = generador_codigo_intermedio.generarNodo("IF", $2.nodo, generador_codigo_intermedio.generarNodo("CUERPO_IF", generador_codigo_intermedio.generarNodoUnidireccional("THEN", $3.nodo), generador_codigo_intermedio.generarNodoUnidireccional("ELSE", $5.nodo)));}

;

clausula_seleccion_if_de_return_parcial:    IF condicion cuerpo_if_retorno_parcial END_IF ','                                                               {System.out.print("(CLAUSULA DE SELECCION IF DE RETORNO PARCIAL Y SIN ELSE) "); $$.nodo = generador_codigo_intermedio.generarNodo("IF", $2.nodo, generador_codigo_intermedio.generarNodo("CUERPO_IF", generador_codigo_intermedio.generarNodoUnidireccional("THEN", $3.nodo), generador_codigo_intermedio.generarNodoUnidireccional("ELSE", null)));}
                                            | IF condicion cuerpo_if_retorno_parcial ELSE cuerpo_else END_IF ','                                            {System.out.print("(CLAUSULA DE SELECCION IF DE RETORNO PARCIAL Y ELSE SIN POSIBLES RETORNOS) "); $$.nodo = generador_codigo_intermedio.generarNodo("IF", $2.nodo, generador_codigo_intermedio.generarNodo("CUERPO_IF", generador_codigo_intermedio.generarNodoUnidireccional("THEN", $3.nodo), generador_codigo_intermedio.generarNodoUnidireccional("ELSE", $5.nodo)));}
                                            | IF condicion cuerpo_if ELSE cuerpo_else_retorno_parcial END_IF ','                                            {System.out.print("(CLAUSULA DE SELECCION IF SIN POSIBLES RETORNOS Y ELSE DE RETORNO PARCIAL) "); $$.nodo = generador_codigo_intermedio.generarNodo("IF", $2.nodo, generador_codigo_intermedio.generarNodo("CUERPO_IF", generador_codigo_intermedio.generarNodoUnidireccional("THEN", $3.nodo), generador_codigo_intermedio.generarNodoUnidireccional("ELSE", $5.nodo)));}
                                            | IF condicion cuerpo_if_retorno_parcial ELSE cuerpo_else_retorno_parcial END_IF ','                            {System.out.print("(CLAUSULA DE SELECCION IF DE RETORNO PARCIAL Y ELSE DE RETORNO PARCIAL) "); $$.nodo = generador_codigo_intermedio.generarNodo("IF", $2.nodo, generador_codigo_intermedio.generarNodo("CUERPO_IF", generador_codigo_intermedio.generarNodoUnidireccional("THEN", $3.nodo), generador_codigo_intermedio.generarNodoUnidireccional("ELSE", $5.nodo)));}     
                                            | IF condicion cuerpo_if_retorno_completo END_IF ','                                                            {System.out.print("(CLAUSULA DE SELECCION IF DE RETORNO COMPLETO Y SIN ELSE) "); $$.nodo = generador_codigo_intermedio.generarNodo("IF", $2.nodo, generador_codigo_intermedio.generarNodo("CUERPO_IF", generador_codigo_intermedio.generarNodoUnidireccional("THEN", $3.nodo), generador_codigo_intermedio.generarNodoUnidireccional("ELSE", null)));}
                                            | IF condicion cuerpo_if_retorno_parcial ELSE cuerpo_else_retorno_completo END_IF ','                           {System.out.print("(CLAUSULA DE SELECCION IF DE RETORNO PARCIAL Y ELSE DE RETORNO COMPLETO) "); $$.nodo = generador_codigo_intermedio.generarNodo("IF", $2.nodo, generador_codigo_intermedio.generarNodo("CUERPO_IF", generador_codigo_intermedio.generarNodoUnidireccional("THEN", $3.nodo), generador_codigo_intermedio.generarNodoUnidireccional("ELSE", $5.nodo)));}
                                            | IF condicion cuerpo_if ELSE cuerpo_else_retorno_completo END_IF ','                                           {System.out.print("(CLAUSULA DE SELECCION IF SIN POSIBLES RETORNOS Y ELSE DE RETORNO COMPLETO) "); $$.nodo = generador_codigo_intermedio.generarNodo("IF", $2.nodo, generador_codigo_intermedio.generarNodo("CUERPO_IF", generador_codigo_intermedio.generarNodoUnidireccional("THEN", $3.nodo), generador_codigo_intermedio.generarNodoUnidireccional("ELSE", $5.nodo)));}
                                            | IF condicion cuerpo_if_retorno_completo ELSE cuerpo_else_retorno_parcial END_IF ','                           {System.out.print("(CLAUSULA DE SELECCION IF DE RETORNO COMPLETO Y ELSE DE RETORNO PARCIAL) "); $$.nodo = generador_codigo_intermedio.generarNodo("IF", $2.nodo, generador_codigo_intermedio.generarNodo("CUERPO_IF", generador_codigo_intermedio.generarNodoUnidireccional("THEN", $3.nodo), generador_codigo_intermedio.generarNodoUnidireccional("ELSE", $5.nodo)));}
                                            | IF condicion cuerpo_if_retorno_completo ELSE cuerpo_else END_IF ','                                           {System.out.print("(CLAUSULA DE SELECCION IF DE RETORNO COMPLETO Y ELSE SIN POSIBLES RETORNOS) "); $$.nodo = generador_codigo_intermedio.generarNodo("IF", $2.nodo, generador_codigo_intermedio.generarNodo("CUERPO_IF", generador_codigo_intermedio.generarNodoUnidireccional("THEN", $3.nodo), generador_codigo_intermedio.generarNodoUnidireccional("ELSE", $5.nodo)));}
;

clausula_seleccion_if_de_return_completa:       IF condicion cuerpo_if_retorno_completo ELSE cuerpo_else_retorno_completo END_IF ','                        {System.out.print("(CLAUSULA DE SELECCION IF ELSE DE RETORNO COMPLETO) "); $$.nodo = generador_codigo_intermedio.generarNodo("IF", $2.nodo, generador_codigo_intermedio.generarNodo("CUERPO_IF", generador_codigo_intermedio.generarNodoUnidireccional("THEN", $3.nodo), generador_codigo_intermedio.generarNodoUnidireccional("ELSE", $5.nodo)));}

;

cuerpo_if:      bloque_sentencias_ejecutables       {$$.nodo = $1.nodo;}               
                | sentencia_ejecutable              {$$.nodo = $1.nodo;}
                | '{' '}'                           {$$.nodo = null;}

;

cuerpo_else:    cuerpo_if                           {$$.nodo = $1.nodo;}

;

cuerpo_if_retorno_parcial:      bloque_sentencias_ejecutables_de_return_parcial             {$$.nodo = $1.nodo;}
                                | sentencia_ejecutable_de_return_parcial                    {$$.nodo = $1.nodo;}

;

cuerpo_else_retorno_parcial:    cuerpo_if_retorno_parcial                                   {$$.nodo = $1.nodo;}

;

cuerpo_if_retorno_completo:     bloque_sentencias_ejecutables_de_return_completo            {$$.nodo = $1.nodo;}
                                | sentencia_ejecutable_de_return_completa                   {$$.nodo = $1.nodo;}

;

cuerpo_else_retorno_completo:   cuerpo_if_retorno_completo                                  {$$.nodo = $1.nodo;}

;
 
condicion:      '(' comparacion ')'     {$$.nodo = generador_codigo_intermedio.generarNodoUnidireccional("CONDICION", $2.nodo);}
                | comparacion ')'       {agregarError("ERROR: Condicion de clausula IF invalida, el formato correcto es: '(' comparacion ')'"); $$.nodo = generador_codigo_intermedio.generarNodoUnidireccional("CONDICION", $1.nodo);}
                | '(' comparacion       {agregarError("ERROR: Condicion de clausula IF invalida, el formato correcto es: '(' comparacion ')'"); $$.nodo = generador_codigo_intermedio.generarNodoUnidireccional("CONDICION", $2.nodo);}
                | '(' error ')'         {agregarError("ERROR: Condicion de clausula IF invalida, el formato correcto es: '(' comparacion ')'"); $$.nodo = null;}
                | comparacion           {agregarError("ERROR: Condicion de clausula IF invalida, el formato correcto es: '(' comparacion ')'"); $$.nodo = generador_codigo_intermedio.generarNodoUnidireccional("CONDICION", $1.nodo);}

;

comparacion:	expresion_aritmetica '>' expresion_aritmetica                       {analizador_semantico.chequearValidezOperacionComparacionExpresiones($1.sval, $3.sval, true, false); $$.nodo = generador_codigo_intermedio.generarNodo(">", $1.nodo, $3.nodo); if ($$.nodo != null && $1.nodo != null) $$.nodo.setTipo($1.nodo.getTipo());}
                | expresion_aritmetica '<' expresion_aritmetica                     {analizador_semantico.chequearValidezOperacionComparacionExpresiones($1.sval, $3.sval, true, false); $$.nodo = generador_codigo_intermedio.generarNodo("<", $1.nodo, $3.nodo); if ($$.nodo != null && $1.nodo != null) $$.nodo.setTipo($1.nodo.getTipo());}
                | expresion_aritmetica COMP_MAYOR_IGUAL expresion_aritmetica        {analizador_semantico.chequearValidezOperacionComparacionExpresiones($1.sval, $3.sval, true, false); $$.nodo = generador_codigo_intermedio.generarNodo(">=", $1.nodo, $3.nodo); if ($$.nodo != null && $1.nodo != null) $$.nodo.setTipo($1.nodo.getTipo());}
                | expresion_aritmetica COMP_MENOR_IGUAL expresion_aritmetica        {analizador_semantico.chequearValidezOperacionComparacionExpresiones($1.sval, $3.sval, true, false); $$.nodo = generador_codigo_intermedio.generarNodo("<=", $1.nodo, $3.nodo); if ($$.nodo != null && $1.nodo != null) $$.nodo.setTipo($1.nodo.getTipo());}
                | expresion_aritmetica COMP_IGUAL expresion_aritmetica              {analizador_semantico.chequearValidezOperacionComparacionExpresiones($1.sval, $3.sval, true, false); $$.nodo = generador_codigo_intermedio.generarNodo("==", $1.nodo, $3.nodo); if ($$.nodo != null && $1.nodo != null) $$.nodo.setTipo($1.nodo.getTipo());}
                | expresion_aritmetica COMP_DISTINTO expresion_aritmetica           {analizador_semantico.chequearValidezOperacionComparacionExpresiones($1.sval, $3.sval, true, false); $$.nodo = generador_codigo_intermedio.generarNodo("!!", $1.nodo, $3.nodo); if ($$.nodo != null && $1.nodo != null) $$.nodo.setTipo($1.nodo.getTipo());}

;

salida_mensaje:	    PRINT CADENA_CARACTERES ','     {System.out.print("(SALIDA DE MENSAJE) "); analizador_semantico.chequearValidezSimbolo($2.sval); $$.nodo = generador_codigo_intermedio.generarNodo("PRINT", null, null); if ($$.nodo != null && $2.sval != null) $$.nodo.setCadenaImpresion($2.sval.replaceAll("%", ""));}
                    | PRINT ','                     {System.out.print("(SALIDA DE MENSAJE) "); agregarError("ERROR: Salida de mensaje invalida, el formato correcto es: PRINT CADENA_CARACTERES ','"); $$.nodo = null;}

;

sentencia_for:	encabezado_for control_rango_iteraciones cuerpo_for             {System.out.print("(SENTENCIA FOR) "); analizador_semantico.chequearCompatibilidadControladoresFor($1.sval, $2.sval); if ($2.nodo != null) $2.nodo.getNodoHijoIzquierdo().setSimbolo($1.sval); $$.nodo = generador_codigo_intermedio.generarNodo("FOR", generador_codigo_intermedio.generarNodo("=", generador_codigo_intermedio.generarNodo($1.sval, null, null), obtenerNodoConstante(analizador_semantico.obtenerConstanteControladoresFor($2.sval, 0), analizador_semantico.obtenerTipoElemento(analizador_semantico.obtenerConstanteControladoresFor($2.sval, 2)))), generador_codigo_intermedio.generarNodo("BUCLE", generador_codigo_intermedio.generarNodoUnidireccional("CONDICION", $2.nodo), generador_codigo_intermedio.generarNodo("CUERPO_FOR", $3.nodo, generador_codigo_intermedio.generarNodo("=", generador_codigo_intermedio.generarNodo($1.sval, null, null), obtenerNodoExpresion("+", generador_codigo_intermedio.generarNodo($1.sval, null, null), obtenerNodoConstante(analizador_semantico.obtenerConstanteControladoresFor($2.sval, 2), analizador_semantico.obtenerTipoElemento(analizador_semantico.obtenerConstanteControladoresFor($2.sval, 2))))))));}

;

cuerpo_for:     sentencia_ejecutable                        {$$.nodo = $1.nodo;}
                | bloque_sentencias_ejecutables ','         {$$.nodo = $1.nodo;}
                | '{' '}' ','                               {$$.nodo = null;}

;

sentencia_for_de_return:    encabezado_for control_rango_iteraciones cuerpo_for_retorno                 {System.out.print("(SENTENCIA FOR CON SENTENCIA EJECUTABLE DE RETORNO PARCIAL) "); analizador_semantico.chequearCompatibilidadControladoresFor($1.sval, $2.sval); if ($2.nodo != null) $2.nodo.getNodoHijoIzquierdo().setSimbolo($1.sval); $$.nodo = generador_codigo_intermedio.generarNodo("FOR", generador_codigo_intermedio.generarNodo("=", generador_codigo_intermedio.generarNodo($1.sval, null, null), obtenerNodoConstante(analizador_semantico.obtenerConstanteControladoresFor($2.sval, 0), analizador_semantico.obtenerTipoElemento(analizador_semantico.obtenerConstanteControladoresFor($2.sval, 2)))), generador_codigo_intermedio.generarNodo("BUCLE", generador_codigo_intermedio.generarNodoUnidireccional("CONDICION", $2.nodo), generador_codigo_intermedio.generarNodo("CUERPO_FOR", $3.nodo, generador_codigo_intermedio.generarNodo("=", generador_codigo_intermedio.generarNodo($1.sval, null, null), obtenerNodoExpresion("+", generador_codigo_intermedio.generarNodo($1.sval, null, null), obtenerNodoConstante(analizador_semantico.obtenerConstanteControladoresFor($2.sval, 2), analizador_semantico.obtenerTipoElemento(analizador_semantico.obtenerConstanteControladoresFor($2.sval, 2))))))));}

;

cuerpo_for_retorno:     sentencia_ejecutable_de_return_parcial                      {$$.nodo = $1.nodo;}
                        | sentencia_ejecutable_de_return_completa                   {$$.nodo = $1.nodo;}
                        | bloque_sentencias_ejecutables_de_return_parcial ','       {$$.nodo = $1.nodo;}
                        | bloque_sentencias_ejecutables_de_return_completo  ','     {$$.nodo = $1.nodo;}

;

encabezado_for:     FOR ID IN RANGE     {analizador_semantico.eliminarEntradaOriginalID($2.sval); $$.sval = analizador_semantico.verificarReferenciaValida($2.sval, false); analizador_semantico.verificarValidezVariableControlFor($2.sval);}
                    | FOR ID RANGE      {agregarError("ERROR: Encabezado de bucle for invalido, el formato correcto es: FOR ID IN RANGE"); analizador_semantico.eliminarEntradaOriginalID($2.sval); $$.sval = analizador_semantico.verificarReferenciaValida($2.sval, false); analizador_semantico.verificarValidezVariableControlFor($2.sval);}
                    | FOR ID IN         {agregarError("ERROR: Encabezado de bucle for invalido, el formato correcto es: FOR ID IN RANGE"); analizador_semantico.eliminarEntradaOriginalID($2.sval); $$.sval = analizador_semantico.verificarReferenciaValida($2.sval, false); analizador_semantico.verificarValidezVariableControlFor($2.sval);}
                    | FOR ID            {agregarError("ERROR: Encabezado de bucle for invalido, el formato correcto es: FOR ID IN RANGE"); analizador_semantico.eliminarEntradaOriginalID($2.sval); $$.sval = analizador_semantico.verificarReferenciaValida($2.sval, false); analizador_semantico.verificarValidezVariableControlFor($2.sval);}
                    | FOR IN RANGE      {agregarError("ERROR: Encabezado de bucle for invalido, el formato correcto es: FOR ID IN RANGE"); $$.sval = null;}
                    | FOR IN            {agregarError("ERROR: Encabezado de bucle for invalido, el formato correcto es: FOR ID IN RANGE"); $$.sval = null;}
                    | FOR RANGE         {agregarError("ERROR: Encabezado de bucle for invalido, el formato correcto es: FOR ID IN RANGE"); $$.sval = null;}
                    | FOR               {agregarError("ERROR: Encabezado de bucle for invalido, el formato correcto es: FOR ID IN RANGE"); $$.sval = null;}

;

control_rango_iteraciones:      '(' constante ';' constante ';' constante ')'   {$$.sval = analizador_semantico.verificarConstantesControlFor($2.sval, $4.sval, $6.sval); $$.nodo = generador_codigo_intermedio.generarNodo(analizador_semantico.obtenerComparadorAsociado($6.sval), generador_codigo_intermedio.generarNodo("", null, null), $4.nodo); if ($$.nodo != null && $2.sval != null) $$.nodo.setTipo(AnalizadorLexico.simbolos.get($2.sval).getTipo());}
                                | constante ';' constante ';' constante ')'     {agregarError("ERROR: Control del rango de iteraciones de bucle FOR invalido, el formato correcto es: '(' constante ';' constante ';' constante ')'"); $$.sval = analizador_semantico.verificarConstantesControlFor($1.sval, $3.sval, $5.sval); $$.nodo = generador_codigo_intermedio.generarNodo(analizador_semantico.obtenerComparadorAsociado($5.sval), generador_codigo_intermedio.generarNodo("", null, null), $3.nodo); if ($$.nodo != null && $1.sval != null) $$.nodo.setTipo(AnalizadorLexico.simbolos.get($1.sval).getTipo());}
                                | '(' constante ';' constante ';' constante     {agregarError("ERROR: Control del rango de iteraciones de bucle FOR invalido, el formato correcto es: '(' constante ';' constante ';' constante ')'"); $$.sval = analizador_semantico.verificarConstantesControlFor($2.sval, $4.sval, $6.sval); $$.nodo = generador_codigo_intermedio.generarNodo(analizador_semantico.obtenerComparadorAsociado($6.sval), generador_codigo_intermedio.generarNodo("", null, null), $4.nodo); if ($$.nodo != null && $2.sval != null) $$.nodo.setTipo(AnalizadorLexico.simbolos.get($2.sval).getTipo());}
                                | constante ';' constante ';' constante         {agregarError("ERROR: Control del rango de iteraciones de bucle FOR invalido, el formato correcto es: '(' constante ';' constante ';' constante ')'"); $$.sval = analizador_semantico.verificarConstantesControlFor($1.sval, $3.sval, $5.sval); $$.nodo = generador_codigo_intermedio.generarNodo(analizador_semantico.obtenerComparadorAsociado($5.sval), generador_codigo_intermedio.generarNodo("", null, null), $3.nodo); if ($$.nodo != null && $1.sval != null) $$.nodo.setTipo(AnalizadorLexico.simbolos.get($1.sval).getTipo());}
                                | '(' error ')'                                 {agregarError("ERROR: Control del rango de iteraciones de bucle FOR invalido, el formato correcto es: '(' constante ';' constante ';' constante ')'"); $$.sval = null; $$.nodo = null;}       

;

%%

static AnalizadorLexico analizador_lexico = null;
static AnalizadorSemantico analizador_semantico = null;
static GeneradorCodigoIntermedio generador_codigo_intermedio = null;
static Parser par = null;
static boolean error_compilacion = false;

public static void main (String [] args) {
    if (args.length == 1) {
            System.out.println();
            System.out.println("------------------------ANALISIS LEXICO, SINTACTICO Y SEMANTICO DEL PROGRAMA------------------------");
            System.out.println();
			System.out.println("Iniciando compilacion");
            analizador_lexico = new AnalizadorLexico(args[0]);
            analizador_semantico = new AnalizadorSemantico();
            generador_codigo_intermedio = new GeneradorCodigoIntermedio();
            par = new Parser(false);
            par.run();
            System.out.println();
            System.out.println("Fin compilacion");
            if (error_compilacion || AnalizadorLexico.hayErrores()) { //si hay error en cualquiera de las etapas de chequeos de compilacion entonces no se cumplira (ya sea lexico, sintactico o semantico)
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
            if (!AnalizadorLexico.hayErrores())
                generador_codigo_intermedio.imprimirArbol();
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
    else
        analizador_semantico.registrarTipoConstante("INT", constante);
}

Nodo obtenerNodoConstante(String constante, String tipo) { //permite obtener el nodo asociado a una constante ya que hay veces que se genera una que no esta explicita (por ejemplo para el incremento de un FOR) y en esos casos se dificulta aplicar toda la logica de seteado de valor desde la accion
    if (constante != null && tipo != null) {
        if (tipo.equals("INT")) {
            Nodo nodo_constante = generador_codigo_intermedio.generarNodo(constante, null, null);
            if (nodo_constante != null)
                nodo_constante.setValorConstante(constante.replaceAll("_i", ""));
            return nodo_constante;
        }
        else if (tipo.equals("ULONG")) {
            Nodo nodo_constante = generador_codigo_intermedio.generarNodo(constante, null, null);
            if (nodo_constante != null)
                nodo_constante.setValorConstante(constante.replaceAll("_ul", ""));
            return nodo_constante;
        }

        else if (tipo.equals("DOUBLE")) {
            Nodo nodo_constante = generador_codigo_intermedio.generarNodo(constante, null, null);
            if (nodo_constante != null) {
                if (constante.contains("d")) nodo_constante.setValorConstante(constante.replaceAll("d", "E")); else if (constante.contains("D")) nodo_constante.setValorConstante(constante.replaceAll("D", "E")); else nodo_constante.setValorConstante(constante);
            };
            return nodo_constante;
        }
    }
    return null;
}

Nodo obtenerNodoExpresion(String operador, Nodo operando_1, Nodo operando_2) { //permite obtener el nodo asociado a una expresion ya que hay veces que se genera una que no esta explicita y en esos casos se dificulta aplicar toda la logica de seteado de tipo desde la accion
    if (operador != null && operando_1 != null && operando_2 != null) {
        Nodo nodo_expresion = generador_codigo_intermedio.generarNodo(operador, operando_1, operando_2);
        if (nodo_expresion != null && operando_1.getTipo() != null) //ya que si la expresion proviene del no terminal de termino o de expresion_aritmetica, ambos nodos operandos contienen ya el tipo de por si
            nodo_expresion.setTipo(operando_1.getTipo());
        else if (nodo_expresion != null) //ya que si la expresion no se genera de manera explicita, sino que es por ejemplo ante el incremento implicito en un FOR o por la resta que se genera implicitamente ante un -=, entonces los operandos poueden no provenir de los no terminales de termino y expresion_aritmetica, por lo que es necesario obtener su tipo a partir de su lexema
            nodo_expresion.setTipo(analizador_semantico.obtenerTipoElemento(operando_1.getSimbolo()));
        return nodo_expresion;
    }
    return null;
}

