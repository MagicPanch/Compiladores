.386
.model flat, stdcall
option casemap :none
include \masm32\include\masm32rt.inc
includelib \masm32\lib\msvcrtprintf.lib
.data
@aux7 dq ?
msj_0 db "	hola         		mundo	", 0
msj_error_rdo_negativo_resta_ulong db "ERROR: RESULTADO NEGATIVO EN RESTA DE DOS ENTEROS SIN SIGNO (ULONG)", 0
ulong1_main dd ?
_PRINT_ulong1_main db "ulong1:main: ", 0
@aux6 dq ?
@aux5 dq -5.3
@aux4 dq ?
@aux9 dd ?
msj_error_overflow_suma_int db "ERROR: OVERFLOW EN SUMA DE ENTEROS CON SIGNO (INT)", 0
@aux8 dq 1.2E+5
numero_mainCc1C_funcion_interfaz dw ?
_PRINT_numero_mainCc1C_funcion_interfaz db "numero:main(c1):funcion_interfaz:", 0
j_main dw ?
_PRINT_j_main db "j:main:", 0
_atributo1_mainCc1C dw ?
inst_c2$c1$_atributo2_main dw ?
_PRINT_inst_c2$c1$_atributo2_main db "inst_c2.c1._atributo2:main:", 0
@aux3 dq 1.
@aux2 dw ?
@aux1 dw ?
double3_main dq ?
_PRINT_double3_main db "double3:main:", 0
funcionxd_main_funcion2 dw ?
_PRINT_funcionxd_main_funcion2 db "funcionxd:main:funcion2:", 0
inst_c1$_atributo1_main dw ?
_PRINT_inst_c1$_atributo1_main db "inst_c1._atributo1:main:", 0
t_main dw ?
_PRINT_t_main db "t:main:", 0
_ulong1_main dd ?
_PRINT__ulong1_main db "_ulong1:main: ", 0
int1_main dw ?
_PRINT_int1_main db "int1:main:", 0
i_main dw ?
_PRINT_i_main db "i:main:", 0
c1$_atributo1_mainCc2C dw ?
_atributo2_mainCc1C dw ?
double2_main dq ?
_PRINT_double2_main db "double2:main:", 0
double1_main dq ?
_PRINT_double1_main db "double1:main:", 0
inst_c1$_atributo2_main dw ?
_PRINT_inst_c1$_atributo2_main db "inst_c1._atributo2:main:", 0
n_main dw ?
_PRINT_n_main db "n:main:", 0
int3_main dw ?
_PRINT_int3_main db "int3:main:", 0
@aux11 dw ?
@aux12 dq ?
@aux13 dq 3.0
@aux14 dq ?
@aux10 dq 1.5
inst_c2$c1$_atributo1_main dw ?
_PRINT_inst_c2$c1$_atributo1_main db "inst_c2.c1._atributo1:main:", 0
int2_main dw ?
_PRINT_int2_main db "int2:main:", 0
@aux15 dw ?
numero_mainIinterfazI_funcion_interfaz dw ?
_PRINT_numero_mainIinterfazI_funcion_interfaz db "numero:main[interfaz]:funcion_interfaz:", 0
msj_error_overflow_multiplicacion_double db "ERROR: OVERFLOW EN MULTIPLICACION DE NUMEROS DE PUNTO FLOTANTE (DOUBLE)", 0
parametro_main_funcion1 dd ?
_PRINT_parametro_main_funcion1 db "parametro:main:funcion1: ", 0
c1$_atributo2_mainCc2C dw ?
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
funcion_anidada_mainCc1C_funcion_interfaz:
ret


funcion_interfaz_mainCc1C:
MOV _atributo1_mainCc1C,15
MOV t_main,3
label1:
MOV AX,t_main
CMP AX,15
JGE label2
MOV AX,numero_mainCc1C_funcion_interfaz
SUB AX,1
MOV @aux1,AX
MOV AX,@aux1
MOV numero_mainCc1C_funcion_interfaz,AX
MOV AX,numero_mainCc1C_funcion_interfaz
CMP AX,12
JE label3
ret
label3:
MOV AX,t_main
ADD AX,3
JO error_overflow_suma_int
MOV @aux2,AX
MOV AX,@aux2
MOV t_main,AX
JMP label1
label2:
ret


funcion_c2_mainZc2Z:
MOV AX,1
MOV numero_mainCc1C_funcion_interfaz,AX
MOV AX,c1$_atributo1_mainCc2C
MOV _atributo1_mainCc1C,AX
MOV AX,c1$_atributo2_mainCc2C
MOV _atributo2_mainCc1C,AX
CALL funcion_interfaz_mainCc1C
MOV AX,_atributo1_mainCc1C
MOV c1$_atributo1_mainCc2C,AX
MOV AX,_atributo2_mainCc1C
MOV c1$_atributo2_mainCc2C,AX
ret


funcion1_main:
ret


funcionxd_main:
ret


n_main_funcion2:
ret


funcion2_main:
CALL funcionxd_main
MOV n_main,1
FNINIT
FLD double1_main
FLD @aux3
FMUL 
FSTP @aux4
FLD @aux4
FLD limite_positivo_superior_double
FCOM
FSTSW AX
SAHF
JBE error_overflow_multiplicacion_double
FLD @aux4
FLD limite_positivo_inferior_double
FCOM
FSTSW AX
SAHF
JAE chequeo_cero4
JMP continuacion_multiplicacion4
chequeo_cero4:
FLD @aux4
FLDZ
FCOM
FSTSW AX
SAHF
JNE chequeo_negativo4
JMP continuacion_multiplicacion4
chequeo_negativo4:
FLD @aux4
FLD limite_negativo_superior_double
FCOM
FSTSW AX
SAHF
JBE error_overflow_multiplicacion_double 
JMP chequeo_negativo_inferior4
chequeo_negativo_inferior4:
FLD @aux4
FLD limite_negativo_inferior_double
FCOM
FSTSW AX
SAHF
JAE error_overflow_multiplicacion_double 
JMP continuacion_multiplicacion4
continuacion_multiplicacion4:
FLD @aux5

FLD @aux4
FADD 
FSTP @aux6
FLD double3_main
FLD @aux6
FSUB 
FSTP @aux7
FNINIT
FLD @aux7
FSTP double3_main
FNINIT 
FLD double3_main
FCOM double2_main
FSTSW AX
SAHF
JAE label4
FNINIT
FLD @aux8
FSTP double2_main
MOV AX,int1_main
CMP AX,int2_main
JNE label5
ret
JMP label6

label5:
ret
label6:
JMP label7

label4:
ret
label7:


start:
MOV EAX,ulong1_main
ADD EAX,ulong1_main
MOV @aux9,EAX
MOV EAX,@aux9
MOV parametro_main_funcion1,EAX
CALL funcion1_main
MOV EAX,ulong1_main
MOV parametro_main_funcion1,EAX
CALL funcion1_main
MOV EAX,1234
MOV parametro_main_funcion1,EAX
CALL funcion1_main
CALL funcion2_main
invoke printf, ADDR msj_0
invoke printf, ADDR newline
MOV AX,3
MOV numero_mainCc1C_funcion_interfaz,AX
MOV AX,inst_c1$_atributo2_main
MOV _atributo2_mainCc1C,AX
MOV AX,inst_c1$_atributo1_main
MOV _atributo1_mainCc1C,AX
CALL funcion_interfaz_mainCc1C
MOV AX,_atributo2_mainCc1C
MOV inst_c1$_atributo2_main,AX
MOV AX,_atributo1_mainCc1C
MOV inst_c1$_atributo1_main,AX
MOV AX,5
MOV numero_mainCc1C_funcion_interfaz,AX
MOV AX,inst_c2$c1$_atributo2_main
MOV _atributo2_mainCc1C,AX
MOV AX,inst_c2$c1$_atributo1_main
MOV _atributo1_mainCc1C,AX
CALL funcion_interfaz_mainCc1C
MOV AX,_atributo2_mainCc1C
MOV inst_c2$c1$_atributo2_main,AX
MOV AX,_atributo1_mainCc1C
MOV inst_c2$c1$_atributo1_main,AX
FNINIT
FLD @aux10
FSTP double1_main
MOV i_main,4
label8:
MOV AX,i_main
CMP AX,32767
JGE label9
MOV j_main,0
label10:
MOV AX,j_main
CMP AX,100
JLE label11
CALL funcion_c2_mainZc2Z
MOV AX,j_main
ADD AX,-10
JO error_overflow_suma_int
MOV @aux11,AX
MOV AX,@aux11
MOV j_main,AX
JMP label10
label11:
FLD double3_main
FLD double1_main
FDIV 
FSTP @aux12
FLD @aux12
FLD @aux13
FADD 
FSTP @aux14
FNINIT
FLD @aux14
FSTP double1_main
CALL funcion2_main
MOV AX,i_main
ADD AX,3
JO error_overflow_suma_int
MOV @aux15,AX
MOV AX,@aux15
MOV i_main,AX
JMP label8
label9:
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
invoke printf,ADDR _PRINT_ulong1_main
invoke printf,ADDR formatoUlong, ulong1_main
invoke printf, ADDR newline
invoke printf,ADDR _PRINT_numero_mainCc1C_funcion_interfaz
invoke printf,ADDR formatoInt, numero_mainCc1C_funcion_interfaz
invoke printf, ADDR newline
invoke printf,ADDR _PRINT_j_main
invoke printf,ADDR formatoInt, j_main
invoke printf, ADDR newline
invoke printf,ADDR _PRINT_inst_c2$c1$_atributo2_main
invoke printf,ADDR formatoInt, inst_c2$c1$_atributo2_main
invoke printf, ADDR newline
invoke printf,ADDR _PRINT_double3_main
invoke printf,ADDR formatoDouble, double3_main
invoke printf, ADDR newline
invoke printf,ADDR _PRINT_funcionxd_main_funcion2
invoke printf,ADDR formatoInt, funcionxd_main_funcion2
invoke printf, ADDR newline
invoke printf,ADDR _PRINT_inst_c1$_atributo1_main
invoke printf,ADDR formatoInt, inst_c1$_atributo1_main
invoke printf, ADDR newline
invoke printf,ADDR _PRINT_t_main
invoke printf,ADDR formatoInt, t_main
invoke printf, ADDR newline
invoke printf,ADDR _PRINT__ulong1_main
invoke printf,ADDR formatoUlong, _ulong1_main
invoke printf, ADDR newline
invoke printf,ADDR _PRINT_int1_main
invoke printf,ADDR formatoInt, int1_main
invoke printf, ADDR newline
invoke printf,ADDR _PRINT_i_main
invoke printf,ADDR formatoInt, i_main
invoke printf, ADDR newline
invoke printf,ADDR _PRINT_double2_main
invoke printf,ADDR formatoDouble, double2_main
invoke printf, ADDR newline
invoke printf,ADDR _PRINT_double1_main
invoke printf,ADDR formatoDouble, double1_main
invoke printf, ADDR newline
invoke printf,ADDR _PRINT_inst_c1$_atributo2_main
invoke printf,ADDR formatoInt, inst_c1$_atributo2_main
invoke printf, ADDR newline
invoke printf,ADDR _PRINT_n_main
invoke printf,ADDR formatoInt, n_main
invoke printf, ADDR newline
invoke printf,ADDR _PRINT_int3_main
invoke printf,ADDR formatoInt, int3_main
invoke printf, ADDR newline
invoke printf,ADDR _PRINT_inst_c2$c1$_atributo1_main
invoke printf,ADDR formatoInt, inst_c2$c1$_atributo1_main
invoke printf, ADDR newline
invoke printf,ADDR _PRINT_int2_main
invoke printf,ADDR formatoInt, int2_main
invoke printf, ADDR newline
invoke printf,ADDR _PRINT_numero_mainIinterfazI_funcion_interfaz
invoke printf,ADDR formatoInt, numero_mainIinterfazI_funcion_interfaz
invoke printf, ADDR newline
invoke printf,ADDR _PRINT_parametro_main_funcion1
invoke printf,ADDR formatoUlong, parametro_main_funcion1
invoke printf, ADDR newline
invoke ExitProcess, 0
end start