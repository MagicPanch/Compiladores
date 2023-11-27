.386
.model flat, stdcall
option casemap :none
include \masm32\include\windows.inc
include \masm32\include\kernel32.inc
include \masm32\include\user32.inc
includelib \masm32\lib\kernel32.lib
includelib \masm32\lib\user32.lib
.data
msj_0 db " LLEGO HASTA ACA ", 0
msj_error_rdo_negativo_resta_ulong db "ERROR: RESULTADO NEGATIVO EN RESTA DE DOS ENTEROS SIN SIGNO (ULONG)", 0
y_main dq ?
msj_error_overflow_suma_int db "ERROR: OVERFLOW EN SUMA DE ENTEROS CON SIGNO (INT)", 0
z_main dq ?
msj_error_overflow_multiplicacion_double db "ERROR: OVERFLOW EN MULTIPLICACION DE NUMEROS DE PUNTO FLOTANTE (DOUBLE)", 0
x_main dq ?
@aux3 dq ?
@aux2 dq 1.0
@aux1 dq 1.79769313486231E+308
limite_positivo_superior_double dq 1.7976931348623157E+308
limite_positivo_inferior_double dq 2.2250738585072014E-308
limite_negativo_superior_double dq -2.2250738585072014E-308
limite_negativo_inferior_double dq -1.7976931348623157E+308
.code
start:
FLD @aux1
FSTP x_main
FLD @aux2
FSTP y_main
FLD x_main
FLD y_main
FMUL 
FSTP @aux3
FLD limite_positivo_superior_double
FCOM
FSTSW AX
SAHF
JB error_overflow_multiplicacion_double
FLD @aux3
FLD limite_positivo_inferior_double
FCOM
FSTSW AX
SAHF
JA chequeo_negativo3
JMP continuacion_multiplicacion3
chequeo_negativo3:
FLD @aux3
FLD limite_negativo_superior_double
FCOM
FSTSW AX
SAHF
JB error_overflow_multiplicacion_double 
JMP chequeo_negativo_inferior3
chequeo_negativo_inferior3:
FLD @aux3
FLD limite_negativo_inferior_double
FCOM
FSTSW AX
SAHF
JA error_overflow_multiplicacion_double 
JMP continuacion_multiplicacion3
continuacion_multiplicacion3:
FLD @aux3
FSTP z_main
invoke MessageBox, NULL, addr msj_0, addr msj_0, MB_OK
JMP fin
error_rdo_negativo_resta_ulong:
invoke MessageBox, NULL, addr msj_error_rdo_negativo_resta_ulong, addr msj_error_rdo_negativo_resta_ulong, MB_OK
JMP fin
error_overflow_multiplicacion_double:
invoke MessageBox, NULL, addr msj_error_overflow_multiplicacion_double, addr msj_error_overflow_multiplicacion_double, MB_OK
JMP fin
error_overflow_suma_int:
invoke MessageBox, NULL, addr msj_error_overflow_suma_int, addr msj_error_overflow_suma_int, MB_OK
JMP fin
fin:
invoke ExitProcess, 0
end start