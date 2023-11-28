.386
.model flat, stdcall
option casemap :none
include \masm32\include\masm32rt.inc
includelib \masm32\lib\msvcrtprintf.lib
.data
msj_error_rdo_negativo_resta_ulong db "ERROR: RESULTADO NEGATIVO EN RESTA DE DOS ENTEROS SIN SIGNO (ULONG)", 0
@aux4 dw ?
_PRINT_@aux4 db "@aux4:", 0
numero_mainCc2C_funcion_c2 dw ?
_PRINT_numero_mainCc2C_funcion_c2 db "numero:main(c2):funcion_c2:", 0
y_main dw ?
_PRINT_y_main db "y:main:", 0
msj_error_overflow_suma_int db "ERROR: OVERFLOW EN SUMA DE ENTEROS CON SIGNO (INT)", 0
inst_c2$c1$z_main dd ?
_PRINT_inst_c2$c1$z_main db "inst_c2.c1.z:main: ", 0
inst_c2$c1$w_main dq ?
_PRINT_inst_c2$c1$w_main db "inst_c2.c1.w:main:", 0
inst_c2$x_main dw ?
_PRINT_inst_c2$x_main db "inst_c2.x:main:", 0
x_main dw ?
_PRINT_x_main db "x:main:", 0
c1$w_mainCc2C dq ?
c1$z_mainCc2C dd ?
@aux3 dq 3.5
@aux2 dd ?
_PRINT_@aux2 db "@aux2: ", 0
@aux1 dw ?
_PRINT_@aux1 db "@aux1:", 0
z_mainCc1C dd ?
w_mainCc1C dq ?
inst_c2$y_main dw ?
_PRINT_inst_c2$y_main db "inst_c2.y:main:", 0
y_mainCc2C dw ?
msj_error_overflow_multiplicacion_double db "ERROR: OVERFLOW EN MULTIPLICACION DE NUMEROS DE PUNTO FLOTANTE (DOUBLE)", 0
x_mainCc2C dw ?
_AVISO_PRINT_ db "------------------------VALORES RESULTANTES DE CADA VARIABLE------------------------", 0
newline db 13, 10, 0 ; CRLF
formatoDouble db "%.20Lf", 0
formatoInt db "%d", 0
formatoUlong db "%u", 0
printf PROTO C :VARARG
limite_positivo_superior_double dq 1.7976931348623157E+308
limite_positivo_inferior_double dq 2.2250738585072014E-308
limite_negativo_superior_double dq -2.2250738585072014E-308
limite_negativo_inferior_double dq -1.7976931348623157E+308
.code
funcion_c1_mainCc1C:
MOV z_mainCc1C,30
ret


funcion_c2_mainCc2C:
MOV AX,numero_mainCc2C_funcion_c2
ADD AX,1
JO error_overflow_suma_int
MOV @aux1,AX
MOV AX,@aux1
MOV x_mainCc2C,AX
ret


funcion_impl_c1_mainZc1Z:
MOV EAX,z_mainCc1C
CDQ 
MOV EBX,2
IDIV EBX
MOV @aux2,EAX
MOV EAX,@aux2
MOV z_mainCc1C,EAX
FNINIT
FLD @aux3
FSTP w_mainCc1C
ret


start:
MOV x_main,5
MOV y_main,1
MOV AX,x_main
ADD AX,y_main
JO error_overflow_suma_int
MOV @aux4,AX
MOV AX,@aux4
MOV numero_mainCc2C_funcion_c2,AX
MOV AX,inst_c2$x_main
MOV x_mainCc2C,AX
MOV AX,inst_c2$y_main
MOV y_mainCc2C,AX
CALL funcion_c2_mainCc2C
MOV AX,x_mainCc2C
MOV inst_c2$x_main,AX
MOV AX,y_mainCc2C
MOV inst_c2$y_main,AX
MOV EAX,inst_c2$c1$z_main
MOV z_mainCc1C,EAX
FLD inst_c2$c1$w_main
FSTP w_mainCc1C
CALL funcion_c1_mainCc1C
MOV EAX,z_mainCc1C
MOV inst_c2$c1$z_main,EAX
FLD w_mainCc1C
FSTP inst_c2$c1$w_main
MOV EAX,inst_c2$c1$z_main
MOV z_mainCc1C,EAX
FLD inst_c2$c1$w_main
FSTP w_mainCc1C
CALL funcion_impl_c1_mainZc1Z
MOV EAX,z_mainCc1C
MOV inst_c2$c1$z_main,EAX
FLD w_mainCc1C
FSTP inst_c2$c1$w_main
MOV AX,2
MOV numero_mainCc2C_funcion_c2,AX
MOV AX,inst_c2$x_main
MOV x_mainCc2C,AX
MOV AX,inst_c2$y_main
MOV y_mainCc2C,AX
CALL funcion_c2_mainCc2C
MOV AX,x_mainCc2C
MOV inst_c2$x_main,AX
MOV AX,y_mainCc2C
MOV inst_c2$y_main,AX
JMP fin
error_rdo_negativo_resta_ulong:
invoke printf, ADDR msj_error_rdo_negativo_resta_ulong
invoke printf, ADDR newline
JMP fin
error_overflow_multiplicacion_double:
invoke printf, ADDR msj_error_overflow_multiplicacion_double
invoke printf, ADDR newline
JMP fin
error_overflow_suma_int:
invoke printf, ADDR msj_error_overflow_suma_int
invoke printf, ADDR newline
JMP fin
fin:
invoke printf, ADDR newline
invoke printf, ADDR _AVISO_PRINT_
invoke printf, ADDR newline
invoke printf,ADDR _PRINT_@aux4
invoke printf,ADDR formatoInt, @aux4
invoke printf, ADDR newline
invoke printf,ADDR _PRINT_numero_mainCc2C_funcion_c2
invoke printf,ADDR formatoInt, numero_mainCc2C_funcion_c2
invoke printf, ADDR newline
invoke printf,ADDR _PRINT_y_main
invoke printf,ADDR formatoInt, y_main
invoke printf, ADDR newline
invoke printf,ADDR _PRINT_inst_c2$c1$z_main
invoke printf,ADDR formatoUlong, inst_c2$c1$z_main
invoke printf, ADDR newline
invoke printf,ADDR _PRINT_inst_c2$c1$w_main
invoke printf,ADDR formatoDouble, inst_c2$c1$w_main
invoke printf, ADDR newline
invoke printf,ADDR _PRINT_inst_c2$x_main
invoke printf,ADDR formatoInt, inst_c2$x_main
invoke printf, ADDR newline
invoke printf,ADDR _PRINT_x_main
invoke printf,ADDR formatoInt, x_main
invoke printf, ADDR newline
invoke printf,ADDR _PRINT_@aux2
invoke printf,ADDR formatoUlong, @aux2
invoke printf, ADDR newline
invoke printf,ADDR _PRINT_@aux1
invoke printf,ADDR formatoInt, @aux1
invoke printf, ADDR newline
invoke printf,ADDR _PRINT_inst_c2$y_main
invoke printf,ADDR formatoInt, inst_c2$y_main
invoke printf, ADDR newline
invoke ExitProcess, 0
end start