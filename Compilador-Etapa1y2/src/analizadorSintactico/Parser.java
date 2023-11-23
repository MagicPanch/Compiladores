package analizadorSintactico;

//### This file created by BYACC 1.8(/Java extension  1.15)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//###           04 Mar 02  -- Yuval Oren  -- improved java performance, added options
//###           14 Mar 02  -- Tomas Hurka -- -d support, static initializer workaround
//### Please send bug reports to tom@hukatronic.cz
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";






//#line 2 "Gramatica.y"
import java.io.*;
import analizadorSintactico.analizadorLexico.AnalizadorLexico;
import analizadorSintactico.analizadorSemantico.AnalizadorSemantico;
import analizadorSintactico.generadorCodigoIntermedio.GeneradorCodigoIntermedio;
import analizadorSintactico.generadorCodigoIntermedio.Nodo;
//#line 23 "Parser.java"




public class Parser
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
if (yydebug)
System.out.println(msg);
}

//########## STATE STACK ##########
final static int YYSTACKSIZE = 500;  //maximum stack size
int statestk[] = new int[YYSTACKSIZE]; //state stack
int stateptr;
int stateptrmax;                     //highest index of stackptr
int statemax;                        //state when highest index reached
//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
final void state_push(int state)
{
try {
stateptr++;
statestk[stateptr]=state;
}
catch (ArrayIndexOutOfBoundsException e) {
 int oldsize = statestk.length;
 int newsize = oldsize * 2;
 int[] newstack = new int[newsize];
 System.arraycopy(statestk,0,newstack,0,oldsize);
 statestk = newstack;
 statestk[stateptr]=state;
}
}
final int state_pop()
{
return statestk[stateptr--];
}
final void state_drop(int cnt)
{
stateptr -= cnt; 
}
final int state_peek(int relative)
{
return statestk[stateptr-relative];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
final boolean init_stacks()
{
stateptr = -1;
val_init();
return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
for (i=0;i<count;i++)
System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
System.out.println("======================");
}


//########## SEMANTIC VALUES ##########
//public class ParserVal is defined in ParserVal.java


String   yytext;//user variable to return contextual strings
ParserVal yyval; //used to return semantic vals from action routines
ParserVal yylval;//the 'lval' (result) I got from yylex()
ParserVal valstk[];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
void val_init()
{
valstk=new ParserVal[YYSTACKSIZE];
yyval=new ParserVal();
yylval=new ParserVal();
valptr=-1;
}
void val_push(ParserVal val)
{
if (valptr>=YYSTACKSIZE)
return;
valstk[++valptr]=val;
}
ParserVal val_pop()
{
if (valptr<0)
return new ParserVal();
return valstk[valptr--];
}
void val_drop(int cnt)
{
int ptr;
ptr=valptr-cnt;
if (ptr<0)
return;
valptr = ptr;
}
ParserVal val_peek(int relative)
{
int ptr;
ptr=valptr-relative;
if (ptr<0)
return new ParserVal();
return valstk[ptr];
}
final ParserVal dup_yyval(ParserVal val)
{
ParserVal dup = new ParserVal();
dup.ival = val.ival;
dup.dval = val.dval;
dup.sval = val.sval;
dup.nodo = val.nodo;
return dup;
}
//#### end semantic value section ####
public final static short IF=257;
public final static short ELSE=258;
public final static short END_IF=259;
public final static short PRINT=260;
public final static short CLASS=261;
public final static short VOID=262;
public final static short INT=263;
public final static short ULONG=264;
public final static short DOUBLE=265;
public final static short FOR=266;
public final static short IN=267;
public final static short RANGE=268;
public final static short IMPL=269;
public final static short INTERFACE=270;
public final static short IMPLEMENT=271;
public final static short RETURN=272;
public final static short CONSTANTE_PF=273;
public final static short CONSTANTE_I=274;
public final static short CONSTANTE_UL=275;
public final static short CADENA_CARACTERES=276;
public final static short ID=277;
public final static short ASIGNADOR_MENOS_IGUAL=278;
public final static short COMP_MAYOR_IGUAL=279;
public final static short COMP_MENOR_IGUAL=280;
public final static short COMP_IGUAL=281;
public final static short COMP_DISTINTO=282;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
0,    0,    0,    0,    0,    0,    0,    1,    1,    2,
2,    2,    2,    3,    3,    3,    8,    8,    8,    9,
9,    9,    9,    9,    9,    9,    9,    6,    6,    6,
6,    6,    6,   11,   11,   17,   17,   17,   18,   18,
18,   12,   13,   13,   21,   21,   21,   22,   22,   19,
23,   23,   24,   24,   24,   24,   24,   25,   25,   25,
26,   26,   20,   27,   27,   27,   27,   27,   27,   27,
27,   27,   28,   28,   28,   28,   14,   29,   29,   29,
29,   29,   29,   30,   30,   15,   31,   31,   31,   31,
31,   31,   31,   31,   32,   32,   16,   33,   33,   34,
34,    7,    7,    7,    7,    7,    4,    4,    5,    5,
42,   44,   45,   45,   45,   45,   46,   47,   47,   47,
47,   10,   10,   10,   35,   35,   49,   49,   49,   50,
50,   50,   51,   51,   53,   53,   53,   53,   53,   36,
54,   48,   52,   52,   55,   55,   55,   37,   37,   40,
40,   40,   40,   40,   40,   40,   40,   40,   43,   57,
57,   57,   58,   59,   59,   60,   61,   61,   62,   56,
56,   56,   56,   56,   63,   63,   63,   63,   63,   63,
38,   38,   39,   66,   66,   66,   41,   67,   67,   67,
67,   64,   64,   64,   64,   64,   64,   64,   64,   65,
65,   65,   65,   65,
};
final static short yylen[] = {                            2,
1,    2,    2,    3,    4,    2,    3,    3,    2,    2,
1,    1,    1,    1,    1,    2,    3,    2,    3,    2,
1,    1,    1,    1,    2,    2,    2,    1,    1,    1,
1,    1,    1,    3,    3,    1,    1,    1,    3,    1,
2,    2,    1,    1,    3,    2,    3,    2,    1,    3,
2,    1,    4,    2,    3,    3,    3,    3,    2,    3,
2,    1,    4,    4,    3,    2,    2,    3,    2,    3,
3,    2,    2,    2,    1,    1,    2,    2,    4,    2,
3,    4,    4,    2,    3,    2,    4,    4,    4,    3,
4,    3,    3,    2,    2,    3,    2,    2,    1,    2,
3,    1,    1,    1,    1,    1,    1,    1,    1,    1,
2,    3,    2,    1,    1,    2,    3,    2,    2,    2,
1,    4,    4,    3,    4,    4,    3,    3,    1,    3,
3,    1,    1,    1,    1,    2,    1,    1,    2,    3,
1,    1,    3,    1,    3,    2,    3,    5,    7,    5,
7,    7,    7,    5,    7,    7,    7,    7,    7,    1,
1,    2,    1,    1,    1,    1,    1,    1,    1,    3,
2,    2,    3,    1,    3,    3,    3,    3,    3,    3,
3,    2,    3,    1,    2,    3,    3,    1,    1,    2,
2,    4,    3,    3,    2,    3,    2,    2,    1,    7,
6,    6,    5,    3,
};
final static short yydefred[] = {                         0,
0,    0,    0,    0,    0,   36,   37,   38,    0,    0,
0,    0,    0,    0,    0,    0,    0,   11,   12,   13,
14,   15,   28,   29,   30,   31,   32,   33,    0,   43,
44,    0,    0,    0,    0,  102,  103,  104,  105,  106,
107,  108,  109,  110,    0,    0,    0,    0,    0,    0,
16,    3,  138,  135,  137,  144,    0,    0,    0,    0,
132,    0,  134,    0,    0,    0,  182,   80,    0,    0,
51,    0,  198,    0,   94,    0,    0,    0,   98,  111,
40,   42,    0,    0,    0,    6,    0,    9,   10,    0,
0,    0,    0,    0,    0,    0,   24,   23,   21,   22,
0,    0,   77,    0,    0,   62,    0,    0,    0,   86,
0,    0,   49,    0,    0,    0,   97,    0,    0,    0,
0,    0,    0,    0,    0,    0,    0,    0,  139,  136,
0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
0,  165,  168,  161,  167,  160,  164,    0,    0,    0,
171,  181,   81,    0,    0,  196,    0,  193,   92,    0,
0,   93,    0,   90,   41,   35,    0,    7,    8,    0,
0,   34,    0,   54,    0,    0,    0,   50,   75,   76,
0,    0,    0,    0,    0,    0,   84,    0,    0,   18,
26,   20,   25,   27,    0,    0,    0,    0,   95,   59,
61,    0,    0,    0,  100,   46,   48,    0,    0,    0,
143,    0,  146,    0,  140,    0,    0,    0,    0,  188,
189,  184,    0,    0,    0,  183,  187,    5,  173,  170,
0,    0,    0,    0,    0,    0,    0,    0,  130,  131,
162,  121,    0,  115,  114,    0,    0,    0,    0,    0,
0,    0,    0,   83,   82,   79,  192,   91,   87,   88,
89,   39,    0,    0,    0,   57,    0,   55,   72,    0,
0,   66,   63,   69,   73,   74,    0,   19,   85,   17,
0,    0,    0,   60,   96,   58,   47,  101,   45,  126,
125,  147,  145,  204,    0,    0,    0,  191,  185,  190,
124,  112,  120,    0,  116,  113,  117,  119,    0,  118,
163,    0,  166,    0,  169,    0,  148,    0,    0,    0,
150,    0,    0,    0,  154,    0,    0,   53,   65,   71,
0,   70,    0,    0,    0,  186,  122,  123,    0,    0,
0,    0,    0,    0,    0,    0,    0,   64,    0,    0,
0,  149,  152,  156,  151,  153,  155,  158,  157,  159,
0,    0,  201,    0,    0,  200,
};
final static short yydgoto[] = {                         15,
16,   17,   18,  142,  143,   21,   22,  101,  102,  145,
23,   24,   25,   26,   27,   28,   29,   83,   30,   31,
114,  115,   32,   94,  108,  109,  182,  183,   33,  103,
34,  110,   35,  117,   36,   37,   38,   39,   40,   41,
42,   43,   44,  146,  246,  147,  247,   45,   59,   60,
61,   46,   63,   47,  122,   64,  311,  312,  313,  323,
315,  316,   65,   48,  125,  226,  227,
};
final static short yysindex[] = {                       411,
297,   44,   -7, -212, -230,    0,    0,    0, -175,   22,
-225,   17,  -38,  678,    0, -189,  778,    0,    0,    0,
0,    0,    0,    0,    0,    0,    0,    0, -198,    0,
0,  149,  442, -118,  -96,    0,    0,    0,    0,    0,
0,    0,    0,    0,  -43,   45,   46,  103,   50,  701,
0,    0,    0,    0,    0,    0,  230, -133,  -14,    3,
0,   45,    0,  540,   65,   67,    0,    0, -203, -153,
0, -147,    0, -169,    0,   15,  -55, -132,    0,    0,
0,    0,  -35, -125,  797,    0,   44,    0,    0,  103,
-31,  346, -127,  316, 1003,  820,    0,    0,    0,    0,
136,  525,    0,  -90, -115,    0,  149,  144, -100,    0,
-90,  -99,    0,  156,  -95,  149,    0,   30,   30,  -84,
69,  172,  255,  194,  608,  -37,  222,  241,    0,    0,
30,   30,   30,   30,   30,   30,   30,   30,   30,   30,
843,    0,    0,    0,    0,    0,    0,  -83,   79,  143,
0,    0,    0,   39, -199,    0,   29,    0,    0,  -46,
59,    0,   66,    0,    0,    0,   43,    0,    0,  650,
655,    0,  285,    0,   53,  291,  719,    0,    0,    0,
220,  303,  737,  559,  312,  590,    0,   44, 1003,    0,
0,    0,    0,    0,  -92,  320,  -71,  464,    0,    0,
0,  -67,  329,   27,    0,    0,    0,  331,   84,   90,
0,  340,    0,   64,    0,  348,  338,  152,  861,    0,
0,    0,  355,  359,  365,    0,    0,    0,    0,    0,
40,   40,   40,   40,    3,    3,   40,   40,    0,    0,
0,    0,  304,    0,    0,  879,  541,  540,  372,  540,
378,  540,  396,    0,    0,    0,    0,    0,    0,    0,
0,    0,  901,  148,  920,    0,  401,    0,    0,  324,
759,    0,    0,    0,    0,    0,  325,    0,    0,    0,
540, 1003,  218,    0,    0,    0,    0,    0,    0,    0,
0,    0,    0,    0,  152,  392,  408,    0,    0,    0,
0,    0,    0,  328,    0,    0,    0,    0,  334,    0,
0,  206,    0,  209,    0,  212,    0,  214,  215,  229,
0,  232,  233,  235,    0,  939,  650,    0,    0,    0,
344,    0,  160,  430,  152,    0,    0,    0,  453,  454,
455,  456,  457,  462,  466,  468,  470,    0,  660,  152,
481,    0,    0,    0,    0,    0,    0,    0,    0,    0,
962,  482,    0,  985,  210,    0,
};
final static short yyrindex[] = {                         0,
0,    0,    0,    0,  191,    0,    0,    0,  109,    0,
-88,    0,  -23,    0,    0,  526,    0,    0,    0,    0,
0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
0,    0,    0,    0,    0,  -19,    0,    0,    0,    0,
0,    0,    0,    0,    0,    0,    0,    0,    0,  200,
0,  164,    0,    0,  609,    0,    0,    0,    0,  498,
0,  158,    0,  189,    0,    0,    0,    0,    0,    0,
0,    0,    0,  535,    0,    0,    0,    0,    0,    0,
0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
0,    0,    0,    0,    0,  537,    0,  616,    0,    0,
0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
0,    0,    0,    0,    0,    0,  246,    0,    0,    0,
0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
91,   95,  114,  128,  236,  267,  138,  151,    0,    0,
0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
0,    0,    0,    0,    0,    0,  335,    0,    0,  494,
0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
496,    0,    0,    0,    0,    0,    0,    0,    0,    0,
0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
634,    0,    0,    0,    0,    0,    0,    0,    0,    0,
0,  642,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
524,   26,   19,    1,   87,  -33,  -25,    0,  254,  -61,
0,    0,    0,    0,    0,    0,   23,  512,  281,  427,
0,  242,  549,  -26,    0,  330,    0,  366,    0,    0,
0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
0,    0,    0, -111, -146,  417, -157,    0,  499,  302,
308,  507,  -41,    0,    0,  -76,  -48, -184,  -45, -228,
-8,  -79,  488,  -13,  460,    0,    0,
};
final static int YYTABLESIZE=1280;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         99,
19,   19,  163,   90,  105,   82,  124,  100,  166,  196,
170,  259,  172,  224,   19,  148,  144,  119,  149,  314,
141,  319,  144,  167,  200,  203,  112,  167,  135,  206,
136,  283,  284,   97,   99,   89,   67,  144,  144,   85,
194,  142,  282,   68,  139,  138,   71,  137,  124,  140,
19,   79,  153,  286,   93,  150,  255,  287,   69,  224,
80,   99,   99,  223,   70,  318,   86,  322,  192,  100,
100,   90,  161,  154,   58,   85,  193,  256,   81,   78,
198,  217,  135,   57,  136,  121,   20,   20,   58,  208,
120,   72,   73,   51,  180,   97,   97,  157,  158,  222,
20,   74,  191,   89,  293,  151,  135,  244,  136,  213,
152,  281,  179,   58,  175,  245,  326,  155,  326,   98,
156,  264,  194,  164,  194,  220,  135,  290,  136,   93,
168,  177,  135,  291,  136,  178,   20,  104,   93,  129,
130,  242,  123,    5,  144,  222,    5,   58,  199,  176,
192,  289,  192,  199,  179,  244,   90,   90,  193,  111,
193,    5,    5,  245,  322,    5,    5,   99,  180,    5,
320,    5,  324,   99,  248,  249,  296,  180,  175,  187,
181,   98,   98,  276,  191,  244,  191,  199,   92,  242,
5,  176,  211,  245,    5,  179,   58,  197,  180,  205,
162,  275,  197,  365,  133,  133,  133,  133,  133,  258,
133,  221,  305,  177,  364,  215,  179,  178,  228,  242,
306,  310,  144,  133,  144,  133,  144,  243,  195,  244,
52,  244,  148,  195,  118,  149,  179,  245,   81,  245,
129,  165,  129,  129,  129,  165,  303,  308,  305,   90,
180,   90,  218,  334,  144,  144,  306,  310,  142,  129,
175,  129,  229,  270,  131,  132,  133,  134,   66,  277,
159,  276,  333,  176,   58,  243,  127,   75,  127,  127,
127,  230,  303,  308,  181,  194,  133,   76,    5,  275,
194,  160,  305,  351,  254,  127,  257,  127,   77,   58,
306,  144,   53,   54,   55,  243,   56,  128,  362,  128,
128,  128,   90,   90,  260,  113,   53,   54,   55,  262,
56,  261,  129,  144,  212,  266,  128,  244,  128,  267,
305,  268,  304,  309,  307,  245,  250,  251,  306,  310,
51,   53,   54,   55,  272,   56,  273,  177,  184,  186,
177,  178,  202,  204,  178,  279,  177,  331,  127,  178,
178,  242,  177,  285,  303,  308,  178,  177,  304,  309,
179,  178,  288,  179,  178,   53,   54,   55,   56,  179,
292,  199,  199,  199,  180,  179,  174,  180,  294,  128,
179,  113,  113,  180,  175,  207,  295,  175,  298,  180,
252,  253,  299,  175,  180,  327,  249,  176,  300,  175,
176,    6,    7,    8,  175,  317,  176,  349,  253,   50,
133,  321,  176,  133,   53,   54,   55,  176,  301,  133,
197,  197,  197,  195,  197,  133,  235,  236,  177,  325,
133,  328,  133,  133,  133,  133,  239,  240,  329,  332,
335,  336,  337,   52,   52,   52,  129,   56,  338,  129,
106,  195,  195,  195,  339,  129,  188,  340,  348,    3,
341,  129,  342,  343,    2,    9,  129,    3,  129,  129,
129,  129,  207,    9,  207,  127,   56,  344,  350,   12,
345,  346,  127,  347,   56,  127,  352,  353,  354,  355,
356,  127,   53,   54,   55,  357,   56,  127,   62,  358,
216,  359,  127,  360,  127,  127,  127,  127,  194,  194,
194,  363,  366,  128,   52,    1,  128,   53,   54,   55,
106,  106,  128,   14,    2,  201,    4,   67,  128,   68,
91,  225,  271,  128,  128,  128,  128,  128,  128,  171,
0,    0,   49,    2,    0,    0,    3,    4,    5,    6,
7,    8,    9,   62,   96,   10,   11,    0,   12,    0,
0,   49,    2,   13,    0,    3,    4,    5,    6,    7,
8,    9,  107,  116,   10,   11,  177,   12,    0,    0,
56,   56,   13,   62,   56,   56,   56,   56,   56,   56,
56,  173,    0,   56,   56,    0,   56,    0,    6,    7,
8,   56,    0,    0,    0,    0,  209,  210,    0,  214,
78,  201,    0,  201,   62,   62,    0,   62,    0,  231,
232,  233,  234,    0,    0,  237,  238,   62,   62,   62,
62,   62,   62,   62,   62,   62,   62,  189,    0,  190,
0,    0,  107,  107,    0,    0,    0,  107,    0,  116,
116,    0,  141,  116,    0,  307,    1,    2,    0,    0,
3,    4,    5,    6,    7,    8,    9,    0,    0,   10,
11,  189,   12,  278,    0,    0,    0,   13,    0,    0,
0,    0,    0,    0,   62,    0,    0,   95,    2,    0,
0,    3,    4,    5,    6,    7,    8,    9,    0,    0,
10,   11,  189,   12,  280,    0,    0,    0,   13,   49,
2,    0,    0,    3,    4,    5,    6,    7,    8,    9,
219,  174,   10,   11,    0,   12,    0,    0,  172,    0,
13,    0,    0,  107,    0,  107,    0,    0,    0,    0,
116,    0,  116,   78,   78,    0,  203,   78,   78,   78,
78,   78,   78,   78,  202,    0,   78,   78,    0,   78,
0,    0,  263,    0,   78,    0,    0,  265,    0,    0,
0,  188,  361,    0,    3,    4,    5,    6,    7,    8,
9,    0,    0,   10,   11,    0,    2,    2,    0,    3,
3,   13,   84,    0,    0,    9,    9,    0,    0,    0,
0,   12,   12,    0,    0,  188,   56,   56,    3,    4,
5,    6,    7,    8,    9,  126,    0,   10,   11,    0,
0,    0,    0,    0,    0,   13,    0,    0,    0,    0,
0,    0,    0,  269,    0,    0,  188,    0,    0,    3,
4,    5,    6,    7,    8,    9,    0,    0,   10,   11,
0,  274,    0,    0,    2,  174,   13,    3,  174,    0,
0,    0,  172,    9,  174,  172,    0,    0,    0,   12,
174,  172,    0,  330,   56,  174,    0,  172,    0,    0,
203,    0,  172,  203,    0,    0,    0,    0,  202,  203,
0,  202,   88,    0,    0,  203,   87,  202,    0,    3,
203,   87,    0,  202,    3,    9,  188,    0,  202,    3,
9,  169,    0,    0,    0,    9,   56,    0,    0,    0,
0,   56,    0,   49,    2,    0,   56,    3,    4,    5,
6,    7,    8,    9,  185,    0,   10,   11,    0,   12,
0,    0,    0,    0,   13,    0,   49,    2,    0,    0,
3,    4,    5,    6,    7,    8,    9,  241,    0,   10,
11,    0,   12,    0,   49,    2,    0,   13,    3,    4,
5,    6,    7,    8,    9,  297,    0,   10,   11,    0,
12,    0,   49,    2,    0,   13,    3,    4,    5,    6,
7,    8,    9,  302,    0,   10,   11,    0,   12,    0,
0,    0,    0,   13,   49,    2,    0,    0,    3,    4,
5,    6,    7,    8,    9,  241,    0,   10,   11,    0,
12,    0,    0,   49,   87,   13,    0,    3,    4,    5,
6,    7,    8,    9,  297,    0,   10,   11,    0,    0,
0,    0,   49,   87,   13,    0,    3,    4,    5,    6,
7,    8,    9,  302,    0,   10,   11,    0,    0,    0,
0,    0,    0,   13,    0,    0,    2,    0,    0,    3,
4,    5,    6,    7,    8,    9,  241,    0,   10,   11,
0,   12,    0,    0,    0,    0,   13,    0,    0,    2,
0,    0,    3,    4,    5,    6,    7,    8,    9,  302,
0,   10,   11,    0,   12,    0,    0,    2,    0,   13,
3,    4,    5,    6,    7,    8,    9,    0,    0,   10,
11,    0,   12,    0,    0,    2,    0,   13,    3,    4,
5,    6,    7,    8,    9,    0,    0,   10,   11,    0,
12,    0,    0,    0,    0,   13,    0,   87,    0,    0,
3,    4,    5,    6,    7,    8,    9,    0,    0,   10,
11,    0,    0,    0,    0,    0,   87,   13,    0,    3,
4,    5,    6,    7,    8,    9,    0,    0,   10,   11,
0,    0,    0,    0,    0,   87,   13,    0,    3,    4,
5,    6,    7,    8,    9,    0,    0,   10,   11,    0,
0,    0,    0,    0,    0,   13,    0,    0,  188,    0,
0,    3,    4,    5,    6,    7,    8,    9,    0,    0,
10,   11,    0,    0,    0,    0,    0,    0,   13,    0,
0,  188,    0,    0,    3,    4,    5,    6,    7,    8,
9,    0,    0,   10,   11,    0,    0,    0,    0,    2,
0,   13,    3,    4,    5,    6,    7,    8,    9,    0,
0,   10,   11,    0,   12,    0,    0,    0,    0,   13,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         33,
0,    1,   58,   17,  123,   44,   48,   33,   44,  125,
87,   58,   44,  125,   14,   64,   40,   61,   64,  248,
40,  250,   46,   59,  125,  125,  123,   59,   43,  125,
45,  189,  125,   33,  123,   17,   44,   61,   64,   14,
102,   61,  189,  256,   42,   60,  277,   62,   90,   47,
50,  277,  256,  125,   32,   64,  256,  125,  271,  171,
44,   95,   96,  125,  277,  250,  256,  252,  102,   95,
96,   85,   58,  277,   45,   50,  102,  277,  277,   58,
107,  123,   43,   40,   45,   40,    0,    1,   45,  116,
46,  267,  268,   44,   94,   95,   96,  267,  268,  125,
14,  277,  102,   85,   41,   41,   43,  141,   45,   41,
44,  188,   94,   45,   92,  141,  263,  271,  265,   33,
268,  170,  184,  256,  186,  125,   43,   44,   45,  107,
256,   41,   43,   44,   45,   41,   50,  256,  116,  273,
274,  141,   40,  262,  170,  171,  262,   45,   40,  277,
184,  125,  186,   45,   41,  189,  170,  171,  184,  256,
186,  262,  262,  189,  349,  262,  262,  256,   41,  262,
250,  262,  252,  262,  258,  259,  218,  177,   41,   44,
94,   95,   96,  183,  184,  219,  186,   44,   40,  189,
262,   41,  277,  219,  262,  177,   45,   40,  198,   44,
256,  183,   45,  361,   41,   42,   43,   44,   45,  256,
47,  125,  246,  123,  361,   44,  198,  123,  256,  219,
246,  247,  248,   60,  250,   62,  252,  141,   40,  263,
40,  265,  281,   45,  278,  281,  123,  263,  277,  265,
41,  277,   43,   44,   45,  277,  246,  247,  282,  263,
123,  265,   59,  295,  278,  281,  282,  283,  278,   60,
123,   62,   41,  177,  279,  280,  281,  282,  276,  183,
256,  271,  281,  123,   45,  189,   41,  256,   43,   44,
45,   41,  282,  283,  198,   40,  123,  266,  262,  271,
45,  277,  326,  335,  256,   60,  268,   62,  277,   45,
326,  327,  273,  274,  275,  219,  277,   41,  350,   43,
44,   45,  326,  327,  256,   35,  273,  274,  275,  277,
277,  256,  123,  349,  256,   41,   60,  361,   62,  277,
364,   41,  246,  247,  125,  361,  258,  259,  364,  365,
44,  273,  274,  275,  125,  277,   44,  257,   95,   96,
260,  257,  111,  112,  260,   44,  266,  271,  123,   44,
266,  361,  272,   44,  364,  365,  272,  277,  282,  283,
257,  277,   44,  260,   44,  273,  274,  275,   44,  266,
41,  273,  274,  275,  257,  272,   41,  260,   41,  123,
277,  111,  112,  266,  257,  115,   59,  260,   44,  272,
258,  259,   44,  266,  277,  258,  259,  257,   44,  272,
260,  263,  264,  265,  277,   44,  266,  258,  259,  123,
257,   44,  272,  260,  273,  274,  275,  277,  125,  266,
273,  274,  275,  104,  105,  272,  135,  136,  123,   44,
277,   41,  279,  280,  281,  282,  139,  140,  125,  125,
59,   44,  125,  263,  264,  265,  257,  123,  125,  260,
34,  273,  274,  275,  259,  266,  257,  259,  125,  260,
259,  272,  259,  259,  257,  266,  277,  260,  279,  280,
281,  282,  202,  266,  204,  256,  277,  259,   59,  272,
259,  259,  257,  259,  277,  260,   44,   44,   44,   44,
44,  266,  273,  274,  275,   44,  277,  272,    2,   44,
256,   44,  277,   44,  279,  280,  281,  282,  273,  274,
275,   41,   41,  257,    1,    0,  260,  273,  274,  275,
104,  105,  266,  123,    0,  109,    0,   44,  272,   44,
29,  125,  177,  277,   57,  279,  280,  281,  282,   90,
-1,   -1,  256,  257,   -1,   -1,  260,  261,  262,  263,
264,  265,  266,   57,  123,  269,  270,   -1,  272,   -1,
-1,  256,  257,  277,   -1,  260,  261,  262,  263,  264,
265,  266,   34,   35,  269,  270,  123,  272,   -1,   -1,
256,  257,  277,   87,  260,  261,  262,  263,  264,  265,
266,  256,   -1,  269,  270,   -1,  272,   -1,  263,  264,
265,  277,   -1,   -1,   -1,   -1,  118,  119,   -1,  121,
123,  195,   -1,  197,  118,  119,   -1,  121,   -1,  131,
132,  133,  134,   -1,   -1,  137,  138,  131,  132,  133,
134,  135,  136,  137,  138,  139,  140,  123,   -1,  125,
-1,   -1,  104,  105,   -1,   -1,   -1,  109,   -1,  111,
112,   -1,  123,  115,   -1,  125,  256,  257,   -1,   -1,
260,  261,  262,  263,  264,  265,  266,   -1,   -1,  269,
270,  123,  272,  125,   -1,   -1,   -1,  277,   -1,   -1,
-1,   -1,   -1,   -1,  188,   -1,   -1,  256,  257,   -1,
-1,  260,  261,  262,  263,  264,  265,  266,   -1,   -1,
269,  270,  123,  272,  125,   -1,   -1,   -1,  277,  256,
257,   -1,   -1,  260,  261,  262,  263,  264,  265,  266,
123,  123,  269,  270,   -1,  272,   -1,   -1,  123,   -1,
277,   -1,   -1,  195,   -1,  197,   -1,   -1,   -1,   -1,
202,   -1,  204,  256,  257,   -1,  123,  260,  261,  262,
263,  264,  265,  266,  123,   -1,  269,  270,   -1,  272,
-1,   -1,  123,   -1,  277,   -1,   -1,  123,   -1,   -1,
-1,  257,  123,   -1,  260,  261,  262,  263,  264,  265,
266,   -1,   -1,  269,  270,   -1,  257,  257,   -1,  260,
260,  277,  125,   -1,   -1,  266,  266,   -1,   -1,   -1,
-1,  272,  272,   -1,   -1,  257,  277,  277,  260,  261,
262,  263,  264,  265,  266,  125,   -1,  269,  270,   -1,
-1,   -1,   -1,   -1,   -1,  277,   -1,   -1,   -1,   -1,
-1,   -1,   -1,  125,   -1,   -1,  257,   -1,   -1,  260,
261,  262,  263,  264,  265,  266,   -1,   -1,  269,  270,
-1,  125,   -1,   -1,  257,  257,  277,  260,  260,   -1,
-1,   -1,  257,  266,  266,  260,   -1,   -1,   -1,  272,
272,  266,   -1,  125,  277,  277,   -1,  272,   -1,   -1,
257,   -1,  277,  260,   -1,   -1,   -1,   -1,  257,  266,
-1,  260,  125,   -1,   -1,  272,  257,  266,   -1,  260,
277,  257,   -1,  272,  260,  266,  257,   -1,  277,  260,
266,  125,   -1,   -1,   -1,  266,  277,   -1,   -1,   -1,
-1,  277,   -1,  256,  257,   -1,  277,  260,  261,  262,
263,  264,  265,  266,  125,   -1,  269,  270,   -1,  272,
-1,   -1,   -1,   -1,  277,   -1,  256,  257,   -1,   -1,
260,  261,  262,  263,  264,  265,  266,  125,   -1,  269,
270,   -1,  272,   -1,  256,  257,   -1,  277,  260,  261,
262,  263,  264,  265,  266,  125,   -1,  269,  270,   -1,
272,   -1,  256,  257,   -1,  277,  260,  261,  262,  263,
264,  265,  266,  125,   -1,  269,  270,   -1,  272,   -1,
-1,   -1,   -1,  277,  256,  257,   -1,   -1,  260,  261,
262,  263,  264,  265,  266,  125,   -1,  269,  270,   -1,
272,   -1,   -1,  256,  257,  277,   -1,  260,  261,  262,
263,  264,  265,  266,  125,   -1,  269,  270,   -1,   -1,
-1,   -1,  256,  257,  277,   -1,  260,  261,  262,  263,
264,  265,  266,  125,   -1,  269,  270,   -1,   -1,   -1,
-1,   -1,   -1,  277,   -1,   -1,  257,   -1,   -1,  260,
261,  262,  263,  264,  265,  266,  125,   -1,  269,  270,
-1,  272,   -1,   -1,   -1,   -1,  277,   -1,   -1,  257,
-1,   -1,  260,  261,  262,  263,  264,  265,  266,  125,
-1,  269,  270,   -1,  272,   -1,   -1,  257,   -1,  277,
260,  261,  262,  263,  264,  265,  266,   -1,   -1,  269,
270,   -1,  272,   -1,   -1,  257,   -1,  277,  260,  261,
262,  263,  264,  265,  266,   -1,   -1,  269,  270,   -1,
272,   -1,   -1,   -1,   -1,  277,   -1,  257,   -1,   -1,
260,  261,  262,  263,  264,  265,  266,   -1,   -1,  269,
270,   -1,   -1,   -1,   -1,   -1,  257,  277,   -1,  260,
261,  262,  263,  264,  265,  266,   -1,   -1,  269,  270,
-1,   -1,   -1,   -1,   -1,  257,  277,   -1,  260,  261,
262,  263,  264,  265,  266,   -1,   -1,  269,  270,   -1,
-1,   -1,   -1,   -1,   -1,  277,   -1,   -1,  257,   -1,
-1,  260,  261,  262,  263,  264,  265,  266,   -1,   -1,
269,  270,   -1,   -1,   -1,   -1,   -1,   -1,  277,   -1,
-1,  257,   -1,   -1,  260,  261,  262,  263,  264,  265,
266,   -1,   -1,  269,  270,   -1,   -1,   -1,   -1,  257,
-1,  277,  260,  261,  262,  263,  264,  265,  266,   -1,
-1,  269,  270,   -1,  272,   -1,   -1,   -1,   -1,  277,
};
}
final static short YYFINAL=15;
final static short YYMAXTOKEN=282;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,"'('","')'","'*'","'+'","','",
"'-'","'.'","'/'",null,null,null,null,null,null,null,null,null,null,"':'","';'",
"'<'","'='","'>'",null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
"'{'",null,"'}'",null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,"IF","ELSE","END_IF","PRINT","CLASS","VOID",
"INT","ULONG","DOUBLE","FOR","IN","RANGE","IMPL","INTERFACE","IMPLEMENT",
"RETURN","CONSTANTE_PF","CONSTANTE_I","CONSTANTE_UL","CADENA_CARACTERES","ID",
"ASIGNADOR_MENOS_IGUAL","COMP_MAYOR_IGUAL","COMP_MENOR_IGUAL","COMP_IGUAL",
"COMP_DISTINTO",
};
final static String yyrule[] = {
"$accept : programa",
"programa : bloque_sentencias",
"programa : '{' '}'",
"programa : error bloque_sentencias",
"programa : error '{' '}'",
"programa : error '{' '}' error",
"programa : bloque_sentencias error",
"programa : '{' '}' error",
"bloque_sentencias : '{' lista_sentencias '}'",
"bloque_sentencias : lista_sentencias '}'",
"lista_sentencias : lista_sentencias sentencia",
"lista_sentencias : sentencia",
"lista_sentencias : sentencia_ejecutable_de_return_parcial",
"lista_sentencias : sentencia_ejecutable_de_return_completa",
"sentencia : sentencia_declarativa",
"sentencia : sentencia_ejecutable",
"sentencia : error ','",
"bloque_sentencias_declarativas : '{' lista_sentencias_declarativas '}'",
"bloque_sentencias_declarativas : lista_sentencias_declarativas '}'",
"bloque_sentencias_declarativas : error lista_sentencias_declarativas '}'",
"lista_sentencias_declarativas : lista_sentencias_declarativas sentencia_declarativa",
"lista_sentencias_declarativas : sentencia_declarativa",
"lista_sentencias_declarativas : sentencia_ejecutable",
"lista_sentencias_declarativas : sentencia_ejecutable_de_return_completa",
"lista_sentencias_declarativas : sentencia_ejecutable_de_return_parcial",
"lista_sentencias_declarativas : lista_sentencias_declarativas sentencia_ejecutable",
"lista_sentencias_declarativas : lista_sentencias_declarativas sentencia_ejecutable_de_return_parcial",
"lista_sentencias_declarativas : lista_sentencias_declarativas bloque_sentencias_ejecutables_de_return_completo",
"sentencia_declarativa : declaracion_variables",
"sentencia_declarativa : declaracion_referencia_clase",
"sentencia_declarativa : declaracion_metodo",
"sentencia_declarativa : declaracion_clase",
"sentencia_declarativa : declaracion_clausula_impl",
"sentencia_declarativa : declaracion_interfaz",
"declaracion_variables : tipo_numerico lista_variables ','",
"declaracion_variables : ID lista_variables ','",
"tipo_numerico : INT",
"tipo_numerico : ULONG",
"tipo_numerico : DOUBLE",
"lista_variables : lista_variables ';' ID",
"lista_variables : ID",
"lista_variables : lista_variables ID",
"declaracion_referencia_clase : ID ','",
"declaracion_metodo : declaracion_prototipo",
"declaracion_metodo : declaracion_funcion",
"bloque_declaraciones_prototipos : '{' lista_declaraciones_prototipos '}'",
"bloque_declaraciones_prototipos : lista_declaraciones_prototipos '}'",
"bloque_declaraciones_prototipos : error lista_declaraciones_prototipos '}'",
"lista_declaraciones_prototipos : lista_declaraciones_prototipos declaracion_prototipo",
"lista_declaraciones_prototipos : declaracion_prototipo",
"declaracion_prototipo : encabezado_metodo parametro ','",
"encabezado_metodo : VOID ID",
"encabezado_metodo : VOID",
"parametro : '(' tipo_numerico ID ')'",
"parametro : '(' ')'",
"parametro : tipo_numerico ID ')'",
"parametro : '(' tipo_numerico ID",
"parametro : '(' error ')'",
"bloque_declaraciones_funciones : '{' lista_declaraciones_funciones '}'",
"bloque_declaraciones_funciones : lista_declaraciones_funciones '}'",
"bloque_declaraciones_funciones : error lista_declaraciones_funciones '}'",
"lista_declaraciones_funciones : lista_declaraciones_funciones declaracion_funcion",
"lista_declaraciones_funciones : declaracion_funcion",
"declaracion_funcion : encabezado_metodo parametro cuerpo_funcion ','",
"cuerpo_funcion : '{' lista_sentencias_funcion sentencia_ejecutable_de_return_completa '}'",
"cuerpo_funcion : '{' sentencia_ejecutable_de_return_completa '}'",
"cuerpo_funcion : sentencia_ejecutable_de_return_completa '}'",
"cuerpo_funcion : '{' sentencia_ejecutable_de_return_completa",
"cuerpo_funcion : '{' lista_sentencias_funcion sentencia_ejecutable_de_return_completa",
"cuerpo_funcion : lista_sentencias_funcion '}'",
"cuerpo_funcion : lista_sentencias_funcion sentencia_ejecutable_de_return_completa '}'",
"cuerpo_funcion : '{' lista_sentencias_funcion '}'",
"cuerpo_funcion : '{' '}'",
"lista_sentencias_funcion : lista_sentencias_funcion sentencia",
"lista_sentencias_funcion : lista_sentencias_funcion sentencia_ejecutable_de_return_parcial",
"lista_sentencias_funcion : sentencia",
"lista_sentencias_funcion : sentencia_ejecutable_de_return_parcial",
"declaracion_clase : encabezado_clase cuerpo_clase",
"encabezado_clase : CLASS ID",
"encabezado_clase : CLASS ID IMPLEMENT ID",
"encabezado_clase : CLASS error",
"encabezado_clase : CLASS IMPLEMENT error",
"encabezado_clase : CLASS ID IMPLEMENT error",
"encabezado_clase : CLASS IMPLEMENT ID error",
"cuerpo_clase : bloque_sentencias_declarativas ','",
"cuerpo_clase : '{' '}' ','",
"declaracion_clausula_impl : encabezado_clausula_impl cuerpo_clausula_impl",
"encabezado_clausula_impl : IMPL FOR ID ':'",
"encabezado_clausula_impl : IMPL FOR ':' error",
"encabezado_clausula_impl : IMPL ID ':' error",
"encabezado_clausula_impl : IMPL ':' error",
"encabezado_clausula_impl : IMPL FOR ID error",
"encabezado_clausula_impl : IMPL FOR error",
"encabezado_clausula_impl : IMPL ID error",
"encabezado_clausula_impl : IMPL error",
"cuerpo_clausula_impl : bloque_declaraciones_funciones ','",
"cuerpo_clausula_impl : '{' '}' ','",
"declaracion_interfaz : encabezado_interfaz cuerpo_interfaz",
"encabezado_interfaz : INTERFACE ID",
"encabezado_interfaz : INTERFACE",
"cuerpo_interfaz : bloque_declaraciones_prototipos ','",
"cuerpo_interfaz : '{' '}' ','",
"sentencia_ejecutable : asignacion",
"sentencia_ejecutable : invocacion_funcion",
"sentencia_ejecutable : clausula_seleccion_if",
"sentencia_ejecutable : salida_mensaje",
"sentencia_ejecutable : sentencia_for",
"sentencia_ejecutable_de_return_parcial : clausula_seleccion_if_de_return_parcial",
"sentencia_ejecutable_de_return_parcial : sentencia_for_de_return",
"sentencia_ejecutable_de_return_completa : sentencia_return",
"sentencia_ejecutable_de_return_completa : clausula_seleccion_if_de_return_completa",
"sentencia_return : RETURN ','",
"bloque_sentencias_ejecutables : '{' lista_sentencias_ejecutables '}'",
"lista_sentencias_ejecutables : lista_sentencias_ejecutables sentencia_ejecutable",
"lista_sentencias_ejecutables : sentencia_ejecutable",
"lista_sentencias_ejecutables : sentencia_declarativa",
"lista_sentencias_ejecutables : lista_sentencias_ejecutables sentencia_declarativa",
"bloque_sentencias_ejecutables_de_return_parcial : '{' lista_sentencias_ejecutables_de_return_parcial '}'",
"lista_sentencias_ejecutables_de_return_parcial : lista_sentencias_ejecutables_de_return_parcial sentencia_ejecutable",
"lista_sentencias_ejecutables_de_return_parcial : lista_sentencias_ejecutables_de_return_parcial sentencia_ejecutable_de_return_parcial",
"lista_sentencias_ejecutables_de_return_parcial : lista_sentencias_ejecutables sentencia_ejecutable_de_return_parcial",
"lista_sentencias_ejecutables_de_return_parcial : sentencia_ejecutable_de_return_parcial",
"bloque_sentencias_ejecutables_de_return_completo : '{' lista_sentencias_ejecutables sentencia_ejecutable_de_return_completa '}'",
"bloque_sentencias_ejecutables_de_return_completo : '{' lista_sentencias_ejecutables_de_return_parcial sentencia_ejecutable_de_return_completa '}'",
"bloque_sentencias_ejecutables_de_return_completo : '{' sentencia_ejecutable_de_return_completa '}'",
"asignacion : referencia_resultado '=' expresion_aritmetica ','",
"asignacion : referencia_resultado ASIGNADOR_MENOS_IGUAL expresion_aritmetica ','",
"expresion_aritmetica : expresion_aritmetica '+' termino",
"expresion_aritmetica : expresion_aritmetica '-' termino",
"expresion_aritmetica : termino",
"termino : termino '*' factor",
"termino : termino '/' factor",
"termino : factor",
"factor : referencia",
"factor : constante",
"constante : CONSTANTE_I",
"constante : '-' CONSTANTE_I",
"constante : CONSTANTE_UL",
"constante : CONSTANTE_PF",
"constante : '-' CONSTANTE_PF",
"invocacion_funcion : referencia_funcion parametro_real ','",
"referencia_funcion : referencia",
"referencia_resultado : referencia",
"referencia : referencia '.' ID",
"referencia : ID",
"parametro_real : '(' expresion_aritmetica ')'",
"parametro_real : '(' ')'",
"parametro_real : '(' error ')'",
"clausula_seleccion_if : IF condicion cuerpo_if END_IF ','",
"clausula_seleccion_if : IF condicion cuerpo_if ELSE cuerpo_else END_IF ','",
"clausula_seleccion_if_de_return_parcial : IF condicion cuerpo_if_retorno_parcial END_IF ','",
"clausula_seleccion_if_de_return_parcial : IF condicion cuerpo_if_retorno_parcial ELSE cuerpo_else END_IF ','",
"clausula_seleccion_if_de_return_parcial : IF condicion cuerpo_if ELSE cuerpo_else_retorno_parcial END_IF ','",
"clausula_seleccion_if_de_return_parcial : IF condicion cuerpo_if_retorno_parcial ELSE cuerpo_else_retorno_parcial END_IF ','",
"clausula_seleccion_if_de_return_parcial : IF condicion cuerpo_if_retorno_completo END_IF ','",
"clausula_seleccion_if_de_return_parcial : IF condicion cuerpo_if_retorno_parcial ELSE cuerpo_else_retorno_completo END_IF ','",
"clausula_seleccion_if_de_return_parcial : IF condicion cuerpo_if ELSE cuerpo_else_retorno_completo END_IF ','",
"clausula_seleccion_if_de_return_parcial : IF condicion cuerpo_if_retorno_completo ELSE cuerpo_else_retorno_parcial END_IF ','",
"clausula_seleccion_if_de_return_parcial : IF condicion cuerpo_if_retorno_completo ELSE cuerpo_else END_IF ','",
"clausula_seleccion_if_de_return_completa : IF condicion cuerpo_if_retorno_completo ELSE cuerpo_else_retorno_completo END_IF ','",
"cuerpo_if : bloque_sentencias_ejecutables",
"cuerpo_if : sentencia_ejecutable",
"cuerpo_if : '{' '}'",
"cuerpo_else : cuerpo_if",
"cuerpo_if_retorno_parcial : bloque_sentencias_ejecutables_de_return_parcial",
"cuerpo_if_retorno_parcial : sentencia_ejecutable_de_return_parcial",
"cuerpo_else_retorno_parcial : cuerpo_if_retorno_parcial",
"cuerpo_if_retorno_completo : bloque_sentencias_ejecutables_de_return_completo",
"cuerpo_if_retorno_completo : sentencia_ejecutable_de_return_completa",
"cuerpo_else_retorno_completo : cuerpo_if_retorno_completo",
"condicion : '(' comparacion ')'",
"condicion : comparacion ')'",
"condicion : '(' comparacion",
"condicion : '(' error ')'",
"condicion : comparacion",
"comparacion : expresion_aritmetica '>' expresion_aritmetica",
"comparacion : expresion_aritmetica '<' expresion_aritmetica",
"comparacion : expresion_aritmetica COMP_MAYOR_IGUAL expresion_aritmetica",
"comparacion : expresion_aritmetica COMP_MENOR_IGUAL expresion_aritmetica",
"comparacion : expresion_aritmetica COMP_IGUAL expresion_aritmetica",
"comparacion : expresion_aritmetica COMP_DISTINTO expresion_aritmetica",
"salida_mensaje : PRINT CADENA_CARACTERES ','",
"salida_mensaje : PRINT ','",
"sentencia_for : encabezado_for control_rango_iteraciones cuerpo_for",
"cuerpo_for : sentencia_ejecutable",
"cuerpo_for : bloque_sentencias_ejecutables ','",
"cuerpo_for : '{' '}' ','",
"sentencia_for_de_return : encabezado_for control_rango_iteraciones cuerpo_for_retorno",
"cuerpo_for_retorno : sentencia_ejecutable_de_return_parcial",
"cuerpo_for_retorno : sentencia_ejecutable_de_return_completa",
"cuerpo_for_retorno : bloque_sentencias_ejecutables_de_return_parcial ','",
"cuerpo_for_retorno : bloque_sentencias_ejecutables_de_return_completo ','",
"encabezado_for : FOR ID IN RANGE",
"encabezado_for : FOR ID RANGE",
"encabezado_for : FOR ID IN",
"encabezado_for : FOR ID",
"encabezado_for : FOR IN RANGE",
"encabezado_for : FOR IN",
"encabezado_for : FOR RANGE",
"encabezado_for : FOR",
"control_rango_iteraciones : '(' constante ';' constante ';' constante ')'",
"control_rango_iteraciones : constante ';' constante ';' constante ')'",
"control_rango_iteraciones : '(' constante ';' constante ';' constante",
"control_rango_iteraciones : constante ';' constante ';' constante",
"control_rango_iteraciones : '(' error ')'",
};

//#line 421 "Gramatica.y"

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

//#line 908 "Parser.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
if (ch < 0) ch=0;
if (ch <= YYMAXTOKEN) //check index bounds
 s = yyname[ch];    //now get it
if (s==null)
s = "illegal-symbol";
debug("state "+state+", reading "+ch+" ("+s+")");
}





//The following are now global, to aid in error reporting
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string


//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse()
{
boolean doaction;
init_stacks();
yynerrs = 0;
yyerrflag = 0;
yychar = -1;          //impossible char forces a read
yystate=0;            //initial state
state_push(yystate);  //save it
val_push(yylval);     //save empty value
while (true) //until parsing is done, either correctly, or w/error
{
doaction=true;
if (yydebug) debug("loop"); 
//#### NEXT ACTION (from reduction table)
for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
  {
  if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
  if (yychar < 0)      //we want a char?
    {
    yychar = yylex();  //get next token
    if (yydebug) debug(" next yychar:"+yychar);
    //#### ERROR CHECK ####
    if (yychar < 0)    //it it didn't work/error
      {
      yychar = 0;      //change it to default string (no -1!)
      if (yydebug)
        yylexdebug(yystate,yychar);
      }
    }//yychar<0
  yyn = yysindex[yystate];  //get amount to shift by (shift index)
  if ((yyn != 0) && (yyn += yychar) >= 0 &&
      yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
    {
    if (yydebug)
      debug("state "+yystate+", shifting to state "+yytable[yyn]);
    //#### NEXT STATE ####
    yystate = yytable[yyn];//we are in a new state
    state_push(yystate);   //save it
    val_push(yylval);      //push our lval as the input for next rule
    yychar = -1;           //since we have 'eaten' a token, say we need another
    if (yyerrflag > 0)     //have we recovered an error?
       --yyerrflag;        //give ourselves credit
    doaction=false;        //but don't process yet
    break;   //quit the yyn=0 loop
    }

yyn = yyrindex[yystate];  //reduce
if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
        yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
  {   //we reduced!
  if (yydebug) debug("reduce");
  yyn = yytable[yyn];
  doaction=true; //get ready to execute
  break;         //drop down to actions
  }
else //ERROR RECOVERY
  {
  if (yyerrflag==0)
    {
    yyerror("syntax error");
    yynerrs++;
    }
  if (yyerrflag < 3) //low error count?
    {
    yyerrflag = 3;
    while (true)   //do until break
      {
      if (stateptr<0)   //check for under & overflow here
        {
        yyerror("stack underflow. aborting...");  //note lower case 's'
        return 1;
        }
      yyn = yysindex[state_peek(0)];
      if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
        {
        if (yydebug)
          debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
        yystate = yytable[yyn];
        state_push(yystate);
        val_push(yylval);
        doaction=false;
        break;
        }
      else
        {
        if (yydebug)
          debug("error recovery discarding state "+state_peek(0)+" ");
        if (stateptr<0)   //check for under & overflow here
          {
          yyerror("Stack underflow. aborting...");  //capital 'S'
          return 1;
          }
        state_pop();
        val_pop();
        }
      }
    }
  else            //discard this token
    {
    if (yychar == 0)
      return 1; //yyabort
    if (yydebug)
      {
      yys = null;
      if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
      if (yys == null) yys = "illegal-symbol";
      debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
      }
    yychar = -1;  //read another
    }
  }//end error recovery
}//yyn=0 loop
if (!doaction)   //any reason not to proceed?
  continue;      //skip action
yym = yylen[yyn];          //get count of terminals on rhs
if (yydebug)
  debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
if (yym>0)                 //if count of rhs not 'nil'
  yyval = val_peek(yym-1); //get current semantic value
yyval = dup_yyval(yyval); //duplicate yyval if ParserVal is used as semantic value
switch(yyn)
  {
//########## USER-SUPPLIED ACTIONS ##########
case 1:
//#line 14 "Gramatica.y"
{System.out.print("(PROGRAMA) "); analizador_semantico.actualizarAmbitoActual("-"); analizador_semantico.verificarReferenciasVariablesEnAsignaciones(); yyval.nodo = generador_codigo_intermedio.generarNodoUnidireccional("PROGRAMA", val_peek(0).nodo); GeneradorCodigoIntermedio.nodo_programa = yyval.nodo;}
break;
case 2:
//#line 15 "Gramatica.y"
{System.out.print("(PROGRAMA VACIO) "); yyval.nodo = generador_codigo_intermedio.generarNodoUnidireccional("PROGRAMA", null); GeneradorCodigoIntermedio.nodo_programa = yyval.nodo;}
break;
case 3:
//#line 16 "Gramatica.y"
{System.out.print("(PROGRAMA) "); agregarError("ERROR: Solo puede haber sentencias dentro de las llaves del programa"); analizador_semantico.actualizarAmbitoActual("-"); analizador_semantico.verificarReferenciasVariablesEnAsignaciones(); yyval.nodo = null;}
break;
case 4:
//#line 17 "Gramatica.y"
{System.out.print("(PROGRAMA VACIO) "); agregarError("ERROR: Solo puede haber sentencias dentro de las llaves del programa"); analizador_semantico.actualizarAmbitoActual("-"); yyval.nodo = null;}
break;
case 5:
//#line 18 "Gramatica.y"
{System.out.print("(PROGRAMA VACIO) "); agregarError("ERROR: Solo puede haber sentencias dentro de las llaves del programa"); analizador_semantico.actualizarAmbitoActual("-"); yyval.nodo = null;}
break;
case 6:
//#line 19 "Gramatica.y"
{System.out.print("(PROGRAMA) "); agregarError("ERROR: Solo puede haber sentencias dentro de las llaves del programa"); analizador_semantico.actualizarAmbitoActual("-"); analizador_semantico.verificarReferenciasVariablesEnAsignaciones(); yyval.nodo = null;}
break;
case 7:
//#line 20 "Gramatica.y"
{System.out.print("(PROGRAMA VACIO) "); agregarError("ERROR: Solo puede haber sentencias dentro de las llaves del programa"); analizador_semantico.actualizarAmbitoActual("-"); yyval.nodo = null;}
break;
case 8:
//#line 24 "Gramatica.y"
{yyval.nodo = generador_codigo_intermedio.generarNodoUnidireccional("BLOQUE", val_peek(1).nodo);}
break;
case 9:
//#line 25 "Gramatica.y"
{agregarError("ERROR: Bloque de sentencias de programa invalido, el formato correcto es: '{' lista_sentencias '}' con cada sentencia delimitada por ','"); yyval.nodo = generador_codigo_intermedio.generarNodoUnidireccional("BLOQUE", val_peek(1).nodo);}
break;
case 10:
//#line 29 "Gramatica.y"
{if (val_peek(0).nodo != null) {if (val_peek(1).nodo != null) yyval.nodo = generador_codigo_intermedio.generarNodo("BLOQUE", val_peek(1).nodo, val_peek(0).nodo); else yyval.nodo = val_peek(0).nodo;} else yyval.nodo = val_peek(1).nodo;}
break;
case 11:
//#line 30 "Gramatica.y"
{yyval.nodo = val_peek(0).nodo;}
break;
case 12:
//#line 31 "Gramatica.y"
{agregarError("ERROR: El programa principal no puede contener de manera directa sentencias que impliquen posibles retornos"); yyval.nodo = null;}
break;
case 13:
//#line 32 "Gramatica.y"
{agregarError("ERROR: El programa principal no puede contener de manera directa sentencias que impliquen retornos"); yyval.nodo = null;}
break;
case 14:
//#line 36 "Gramatica.y"
{yyval.nodo = null;}
break;
case 15:
//#line 37 "Gramatica.y"
{yyval.nodo = val_peek(0).nodo;}
break;
case 16:
//#line 38 "Gramatica.y"
{agregarError("ERROR: Sentencia invalida sintacticamente");}
break;
case 18:
//#line 43 "Gramatica.y"
{agregarError("ERROR: Bloque de sentencias declarativas invalido, el formato correcto es: '{' lista_sentencias_declarativas '}' con cada sentencia delimitada por ','");}
break;
case 19:
//#line 44 "Gramatica.y"
{agregarError("ERROR: Bloque de sentencias declarativas invalido, el formato correcto es: '{' lista_sentencias_declarativas '}' con cada sentencia delimitada por ','");}
break;
case 22:
//#line 50 "Gramatica.y"
{agregarError("ERROR: No puede haber sentencias ejecutables, en este bloque solo se aceptan sentencias declarativas");}
break;
case 23:
//#line 51 "Gramatica.y"
{agregarError("ERROR: No puede haber sentencias ejecutables, en este bloque solo se aceptan sentencias declarativas");}
break;
case 24:
//#line 52 "Gramatica.y"
{agregarError("ERROR: No puede haber sentencias ejecutables, en este bloque solo se aceptan sentencias declarativas");}
break;
case 25:
//#line 53 "Gramatica.y"
{agregarError("ERROR: No puede haber sentencias ejecutables, en este bloque solo se aceptan sentencias declarativas");}
break;
case 26:
//#line 54 "Gramatica.y"
{agregarError("ERROR: No puede haber sentencias ejecutables, en este bloque solo se aceptan sentencias declarativas");}
break;
case 27:
//#line 55 "Gramatica.y"
{agregarError("ERROR: No puede haber sentencias ejecutables, en este bloque solo se aceptan sentencias declarativas");}
break;
case 34:
//#line 67 "Gramatica.y"
{System.out.print("(DECLARACION DE LISTA DE VARIABLES NUMERICAS) "); analizador_semantico.registrarTipoID(val_peek(2).sval, val_peek(1).sval);}
break;
case 35:
//#line 68 "Gramatica.y"
{System.out.print("(DECLARACION DE LISTA DE VARIABLES DEL TIPO DE UNA CLASE) "); analizador_semantico.eliminarEntradaOriginalID(val_peek(2).sval); analizador_semantico.registrarTipoID(val_peek(2).sval, val_peek(1).sval);}
break;
case 36:
//#line 72 "Gramatica.y"
{yyval.sval = "INT";}
break;
case 37:
//#line 73 "Gramatica.y"
{yyval.sval = "ULONG";}
break;
case 38:
//#line 74 "Gramatica.y"
{yyval.sval = "DOUBLE";}
break;
case 39:
//#line 78 "Gramatica.y"
{analizador_semantico.registrarAmbitoUsoID(val_peek(0).sval, "nombre_variable"); yyval.sval = val_peek(2).sval + ";" + val_peek(0).sval;}
break;
case 40:
//#line 79 "Gramatica.y"
{analizador_semantico.registrarAmbitoUsoID(val_peek(0).sval, "nombre_variable"); yyval.sval = val_peek(0).sval;}
break;
case 41:
//#line 80 "Gramatica.y"
{agregarError("ERROR: Declaracion invalida de variable en lista de variables, falta el ';'"); analizador_semantico.registrarAmbitoUsoID(val_peek(0).sval, "nombre_variable"); yyval.sval = val_peek(1).sval + ";" + val_peek(0).sval;}
break;
case 42:
//#line 84 "Gramatica.y"
{System.out.print("(DECLARACION DE REFERENCIA A CLASE) "); analizador_semantico.registrarAmbitoUsoID(val_peek(1).sval, "referencia_clase");}
break;
case 46:
//#line 94 "Gramatica.y"
{agregarError("ERROR: Bloque de declaraciones de prototipos invalido, el formato correcto es: '{' lista_declaraciones_prototipos '}' con cada prototipo delimitado por ','");}
break;
case 47:
//#line 95 "Gramatica.y"
{agregarError("ERROR: Bloque de declaraciones de prototipos invalido, el formato correcto es: '{' lista_declaraciones_prototipos '}' con cada prototipo delimitado por ','");}
break;
case 50:
//#line 104 "Gramatica.y"
{System.out.print("(DECLARACION DE PROTOTIPO DE FUNCION) "); analizador_semantico.verificarPosibleImplementacionFuncionInterfaz(val_peek(2).sval); analizador_semantico.actualizarAmbitoActual("-"); analizador_semantico.definirUsoPrototipo(val_peek(2).sval); analizador_semantico.verificarCantidadAnidamientos(val_peek(2).sval);}
break;
case 51:
//#line 108 "Gramatica.y"
{analizador_semantico.registrarAmbitoUsoID(val_peek(0).sval, "nombre_funcion"); analizador_semantico.actualizarAmbitoActual(val_peek(0).sval); yyval.sval = val_peek(0).sval;}
break;
case 52:
//#line 109 "Gramatica.y"
{agregarError("ERROR: Encabezado de funcion invalido, el formato correcto es: VOID ID"); analizador_semantico.marcarAmbitoInvalido(":"); yyval.sval = null;}
break;
case 53:
//#line 113 "Gramatica.y"
{analizador_semantico.registrarAmbitoUsoID(val_peek(1).sval, "nombre_parametro_formal"); analizador_semantico.eliminarEntradaOriginalID(val_peek(1).sval); analizador_semantico.registrarTipoID(val_peek(2).sval, val_peek(1).sval); analizador_semantico.chequearParametroFuncionIMPL(val_peek(1).sval, val_peek(2).sval); yyval.nodo = generador_codigo_intermedio.generarNodoUnidireccional("PARAMETRO_FORMAL", generador_codigo_intermedio.generarNodo(analizador_semantico.obtenerLexemaCompletoID(val_peek(1).sval), null, null));}
break;
case 54:
//#line 114 "Gramatica.y"
{analizador_semantico.chequearParametroFuncionIMPL(null, null); yyval.nodo = generador_codigo_intermedio.generarNodoUnidireccional("PARAMETRO_FORMAL", null);}
break;
case 55:
//#line 115 "Gramatica.y"
{agregarError("ERROR: Parametro invalido, el formato correcto es: '(' tipo_numerico ID ')' o '(' ')'"); analizador_semantico.registrarAmbitoUsoID(val_peek(1).sval, "nombre_parametro_formal"); analizador_semantico.eliminarEntradaOriginalID(val_peek(1).sval); analizador_semantico.registrarTipoID(val_peek(2).sval, val_peek(1).sval); analizador_semantico.chequearParametroFuncionIMPL(val_peek(1).sval, val_peek(2).sval); yyval.nodo = generador_codigo_intermedio.generarNodoUnidireccional("PARAMETRO_FORMAL", generador_codigo_intermedio.generarNodo(analizador_semantico.obtenerLexemaCompletoID(val_peek(1).sval), null, null));}
break;
case 56:
//#line 116 "Gramatica.y"
{agregarError("ERROR: Parametro invalido, el formato correcto es: '(' tipo_numerico ID ')' o '(' ')'"); analizador_semantico.registrarAmbitoUsoID(val_peek(0).sval, "nombre_parametro_formal"); analizador_semantico.eliminarEntradaOriginalID(val_peek(0).sval); analizador_semantico.registrarTipoID(val_peek(1).sval, val_peek(0).sval); analizador_semantico.chequearParametroFuncionIMPL(val_peek(0).sval, val_peek(1).sval); yyval.nodo = generador_codigo_intermedio.generarNodoUnidireccional("PARAMETRO_FORMAL", generador_codigo_intermedio.generarNodo(analizador_semantico.obtenerLexemaCompletoID(val_peek(0).sval), null, null));}
break;
case 57:
//#line 117 "Gramatica.y"
{agregarError("ERROR: Parametro invalido, el formato correcto es: '(' tipo_numerico ID ')' o '(' ')'"); analizador_semantico.actualizarAmbitoActual("-") ; analizador_semantico.marcarAmbitoInvalido(":"); analizador_semantico.chequearParametroFuncionIMPL(null, null); yyval.nodo = null;}
break;
case 59:
//#line 122 "Gramatica.y"
{agregarError("ERROR: Bloque de declaraciones de funciones invalido, el formato correcto es: '{' lista_declaraciones_funciones '}' con cada funcion delimitada por ','");}
break;
case 60:
//#line 123 "Gramatica.y"
{agregarError("ERROR: Bloque de declaraciones de funciones invalido, el formato correcto es: '{' lista_declaraciones_funciones '}' con cada funcion delimitada por ','");}
break;
case 63:
//#line 132 "Gramatica.y"
{System.out.print("(DECLARACION DE FUNCION) "); analizador_semantico.verificarPosibleImplementacionFuncionInterfaz(val_peek(3).sval); analizador_semantico.actualizarAmbitoActual("-"); analizador_semantico.verificarCantidadAnidamientos(val_peek(3).sval); yyval.nodo = generador_codigo_intermedio.generarNodo(analizador_semantico.obtenerLexemaCompletoID(val_peek(3).sval), val_peek(2).nodo, val_peek(1).nodo); GeneradorCodigoIntermedio.funciones.add(yyval.nodo);}
break;
case 64:
//#line 136 "Gramatica.y"
{yyval.nodo = generador_codigo_intermedio.generarNodoUnidireccional("CUERPO_FUNCION", generador_codigo_intermedio.generarNodo("BLOQUE", val_peek(2).nodo, val_peek(1).nodo));}
break;
case 65:
//#line 137 "Gramatica.y"
{yyval.nodo = generador_codigo_intermedio.generarNodoUnidireccional("CUERPO_FUNCION", val_peek(1).nodo);}
break;
case 66:
//#line 138 "Gramatica.y"
{agregarError("ERROR: Cuerpo de funcion sin llave de apertura"); yyval.nodo = generador_codigo_intermedio.generarNodoUnidireccional("CUERPO_FUNCION", val_peek(1).nodo);}
break;
case 67:
//#line 139 "Gramatica.y"
{agregarError("ERROR: Cuerpo de funcion sin llave de cierre"); yyval.nodo = generador_codigo_intermedio.generarNodoUnidireccional("CUERPO_FUNCION", val_peek(0).nodo);}
break;
case 68:
//#line 140 "Gramatica.y"
{agregarError("ERROR: Cuerpo de funcion sin llave de cierre"); yyval.nodo = generador_codigo_intermedio.generarNodoUnidireccional("CUERPO_FUNCION", generador_codigo_intermedio.generarNodo("BLOQUE", val_peek(1).nodo, val_peek(0).nodo));}
break;
case 69:
//#line 141 "Gramatica.y"
{agregarError("ERROR: Cuerpo de funcion sin llave de apertura y no se garantiza retorno para todas las ramas del mismo"); yyval.nodo = null;}
break;
case 70:
//#line 142 "Gramatica.y"
{agregarError("ERROR: Cuerpo de funcion sin llave de apertura"); yyval.nodo = generador_codigo_intermedio.generarNodoUnidireccional("CUERPO_FUNCION", generador_codigo_intermedio.generarNodo("BLOQUE", val_peek(2).nodo, val_peek(1).nodo));}
break;
case 71:
//#line 143 "Gramatica.y"
{agregarError("ERROR: No se garantiza retorno para todas las ramas del cuerpo de funcion"); yyval.nodo = null;}
break;
case 72:
//#line 144 "Gramatica.y"
{agregarError("ERROR: Cuerpo de funcion sin retorno"); yyval.nodo = null;}
break;
case 73:
//#line 148 "Gramatica.y"
{if (val_peek(0).nodo != null) {if (val_peek(1).nodo != null) yyval.nodo = generador_codigo_intermedio.generarNodo("BLOQUE", val_peek(1).nodo, val_peek(0).nodo); else yyval.nodo = val_peek(0).nodo;} else yyval.nodo = val_peek(1).nodo;}
break;
case 74:
//#line 149 "Gramatica.y"
{if (val_peek(1).nodo != null) yyval.nodo = generador_codigo_intermedio.generarNodo("BLOQUE", val_peek(1).nodo, val_peek(0).nodo); else yyval.nodo = val_peek(0).nodo;}
break;
case 75:
//#line 150 "Gramatica.y"
{yyval.nodo = val_peek(0).nodo;}
break;
case 76:
//#line 151 "Gramatica.y"
{yyval.nodo = val_peek(0).nodo;}
break;
case 77:
//#line 155 "Gramatica.y"
{System.out.print("(DECLARACION DE CLASE) "); analizador_semantico.chequearImplementacionTotalInterfaz(val_peek(1).sval); analizador_semantico.actualizarAmbitoActual("()");}
break;
case 78:
//#line 159 "Gramatica.y"
{analizador_semantico.registrarAmbitoUsoID(val_peek(0).sval, "nombre_clase"); analizador_semantico.actualizarAmbitoActual("(" + val_peek(0).sval + ")"); yyval.sval = val_peek(0).sval;}
break;
case 79:
//#line 160 "Gramatica.y"
{analizador_semantico.registrarAmbitoUsoID(val_peek(2).sval, "nombre_clase"); analizador_semantico.registrarInterfazImplementada(val_peek(2).sval, val_peek(0).sval); analizador_semantico.actualizarAmbitoActual("(" + val_peek(2).sval + ")"); analizador_semantico.eliminarEntradaOriginalID(val_peek(0).sval); yyval.sval = val_peek(2).sval;}
break;
case 80:
//#line 161 "Gramatica.y"
{agregarError("ERROR: Encabezado de clase invalido, los formatos correctos son: CLASS ID y CLASS ID IMPLEMENT ID"); analizador_semantico.marcarAmbitoInvalido("()"); yyval.sval = null;}
break;
case 81:
//#line 162 "Gramatica.y"
{agregarError("ERROR: Encabezado de clase invalido, los formatos correctos son: CLASS ID y CLASS ID IMPLEMENT ID"); analizador_semantico.marcarAmbitoInvalido("()"); yyval.sval = null;}
break;
case 82:
//#line 163 "Gramatica.y"
{agregarError("ERROR: Encabezado de clase invalido, los formatos correctos son: CLASS ID y CLASS ID IMPLEMENT ID"); analizador_semantico.registrarAmbitoUsoID(val_peek(2).sval, "nombre_clase"); analizador_semantico.actualizarAmbitoActual("(" + val_peek(2).sval + ")"); yyval.sval = val_peek(2).sval;}
break;
case 83:
//#line 164 "Gramatica.y"
{agregarError("ERROR: Encabezado de clase invalido, los formatos correctos son: CLASS ID y CLASS ID IMPLEMENT ID"); analizador_semantico.eliminarEntradaOriginalID(val_peek(1).sval); analizador_semantico.marcarAmbitoInvalido("()"); yyval.sval = null;}
break;
case 86:
//#line 173 "Gramatica.y"
{System.out.print("(DECLARACION DE CLAUSULA IMPL) "); analizador_semantico.actualizarAmbitoActual("<>");}
break;
case 87:
//#line 177 "Gramatica.y"
{analizador_semantico.eliminarEntradaOriginalID(val_peek(1).sval); analizador_semantico.chequearValidezClausulaIMPL(val_peek(1).sval); analizador_semantico.actualizarAmbitoActual("<" + val_peek(1).sval + ">");}
break;
case 88:
//#line 178 "Gramatica.y"
{agregarError("ERROR: Encabezado de clausula IMPL invalido, el formato correcto es: IMPL FOR ID ':'"); analizador_semantico.marcarAmbitoInvalido("<>");}
break;
case 89:
//#line 179 "Gramatica.y"
{agregarError("ERROR: Encabezado de clausula IMPL invalido, el formato correcto es: IMPL FOR ID ':'"); analizador_semantico.eliminarEntradaOriginalID(val_peek(2).sval); analizador_semantico.chequearValidezClausulaIMPL(val_peek(2).sval); analizador_semantico.actualizarAmbitoActual("<" + val_peek(2).sval + ">");}
break;
case 90:
//#line 180 "Gramatica.y"
{agregarError("ERROR: Encabezado de clausula IMPL invalido, el formato correcto es: IMPL FOR ID ':'"); analizador_semantico.marcarAmbitoInvalido("<>");}
break;
case 91:
//#line 181 "Gramatica.y"
{agregarError("ERROR: Encabezado de clausula IMPL invalido, el formato correcto es: IMPL FOR ID ':'"); analizador_semantico.eliminarEntradaOriginalID(val_peek(1).sval); analizador_semantico.chequearValidezClausulaIMPL(val_peek(1).sval); analizador_semantico.actualizarAmbitoActual("<" + val_peek(1).sval + ">");}
break;
case 92:
//#line 182 "Gramatica.y"
{agregarError("ERROR: Encabezado de clausula IMPL invalido, el formato correcto es: IMPL FOR ID ':'"); analizador_semantico.marcarAmbitoInvalido("<>");}
break;
case 93:
//#line 183 "Gramatica.y"
{agregarError("ERROR: Encabezado de clausula IMPL invalido, el formato correcto es: IMPL FOR ID ':'"); analizador_semantico.eliminarEntradaOriginalID(val_peek(1).sval); analizador_semantico.chequearValidezClausulaIMPL(val_peek(1).sval); analizador_semantico.actualizarAmbitoActual("<" + val_peek(1).sval + ">");}
break;
case 94:
//#line 184 "Gramatica.y"
{agregarError("ERROR: Encabezado de clausula IMPL invalido, el formato correcto es: IMPL FOR ID ':'"); analizador_semantico.marcarAmbitoInvalido("<>");}
break;
case 97:
//#line 193 "Gramatica.y"
{System.out.print("(DECLARACION DE INTERFAZ) "); analizador_semantico.registrarCantidadPrototiposInterfaz(val_peek(1).sval); analizador_semantico.actualizarAmbitoActual("[]");}
break;
case 98:
//#line 197 "Gramatica.y"
{analizador_semantico.registrarAmbitoUsoID(val_peek(0).sval, "nombre_interfaz"); analizador_semantico.actualizarAmbitoActual("[" + val_peek(0).sval + "]"); yyval.sval = val_peek(0).sval;}
break;
case 99:
//#line 198 "Gramatica.y"
{agregarError("ERROR: Encabezado de interfaz invalido, el formato correcto es: INTERFACE ID"); analizador_semantico.marcarAmbitoInvalido("[]"); yyval.sval = null;}
break;
case 102:
//#line 207 "Gramatica.y"
{yyval.nodo = val_peek(0).nodo;}
break;
case 103:
//#line 208 "Gramatica.y"
{yyval.nodo = val_peek(0).nodo;}
break;
case 104:
//#line 209 "Gramatica.y"
{yyval.nodo = val_peek(0).nodo;}
break;
case 105:
//#line 210 "Gramatica.y"
{yyval.nodo = val_peek(0).nodo;}
break;
case 106:
//#line 211 "Gramatica.y"
{yyval.nodo = val_peek(0).nodo;}
break;
case 107:
//#line 215 "Gramatica.y"
{yyval.nodo = val_peek(0).nodo;}
break;
case 108:
//#line 216 "Gramatica.y"
{yyval.nodo = val_peek(0).nodo;}
break;
case 109:
//#line 220 "Gramatica.y"
{yyval.nodo = val_peek(0).nodo;}
break;
case 110:
//#line 221 "Gramatica.y"
{yyval.nodo = val_peek(0).nodo;}
break;
case 111:
//#line 225 "Gramatica.y"
{yyval.nodo = generador_codigo_intermedio.generarNodo("RETURN", null, null);}
break;
case 112:
//#line 229 "Gramatica.y"
{yyval.nodo = val_peek(1).nodo;}
break;
case 113:
//#line 233 "Gramatica.y"
{yyval.nodo = generador_codigo_intermedio.generarNodo("BLOQUE", val_peek(1).nodo, val_peek(0).nodo);}
break;
case 114:
//#line 234 "Gramatica.y"
{yyval.nodo = val_peek(0).nodo;}
break;
case 115:
//#line 235 "Gramatica.y"
{agregarError("ERROR: No puede haber sentencias declarativas, en este bloque solo se aceptan sentencias ejecutables"); yyval.nodo = null;}
break;
case 116:
//#line 236 "Gramatica.y"
{agregarError("ERROR: No puede haber sentencias declarativas, en este bloque solo se aceptan sentencias ejecutables"); yyval.nodo = null;}
break;
case 117:
//#line 240 "Gramatica.y"
{yyval.nodo = val_peek(1).nodo;}
break;
case 118:
//#line 244 "Gramatica.y"
{yyval.nodo = generador_codigo_intermedio.generarNodo("BLOQUE", val_peek(1).nodo, val_peek(0).nodo);}
break;
case 119:
//#line 245 "Gramatica.y"
{yyval.nodo = generador_codigo_intermedio.generarNodo("BLOQUE", val_peek(1).nodo, val_peek(0).nodo);}
break;
case 120:
//#line 246 "Gramatica.y"
{yyval.nodo = generador_codigo_intermedio.generarNodo("BLOQUE", val_peek(1).nodo, val_peek(0).nodo);}
break;
case 121:
//#line 247 "Gramatica.y"
{yyval.nodo = val_peek(0).nodo;}
break;
case 122:
//#line 251 "Gramatica.y"
{yyval.nodo = generador_codigo_intermedio.generarNodo("BLOQUE", val_peek(2).nodo, val_peek(1).nodo);}
break;
case 123:
//#line 252 "Gramatica.y"
{yyval.nodo = generador_codigo_intermedio.generarNodo("BLOQUE", val_peek(2).nodo, val_peek(1).nodo);}
break;
case 124:
//#line 253 "Gramatica.y"
{yyval.nodo = val_peek(1).nodo;}
break;
case 125:
//#line 257 "Gramatica.y"
{System.out.print("(ASIGNACION USANDO OPERADOR DE IGUAL) "); analizador_semantico.registrarReferenciasLadoDerechoAsignacion(val_peek(1).sval); analizador_semantico.chequearAsignacionValida(val_peek(3).sval, val_peek(1).sval); yyval.nodo = generador_codigo_intermedio.generarNodo("=", val_peek(3).nodo, val_peek(1).nodo);}
break;
case 126:
//#line 258 "Gramatica.y"
{System.out.print("(ASIGNACION USANDO OPERADOR DE MENOS IGUAL) "); analizador_semantico.registrarReferenciasLadoDerechoAsignacion(val_peek(1).sval); analizador_semantico.chequearAsignacionValida(val_peek(3).sval, val_peek(1).sval); yyval.nodo = generador_codigo_intermedio.generarNodo("=", val_peek(3).nodo, obtenerNodoExpresion("-", generador_codigo_intermedio.generarNodo(val_peek(3).sval, null, null), val_peek(1).nodo));}
break;
case 127:
//#line 262 "Gramatica.y"
{yyval.sval = val_peek(2).sval + " " + "+" + " " + val_peek(0).sval; analizador_semantico.chequearValidezOperacionComparacionExpresiones(val_peek(2).sval, val_peek(0).sval, false, true); yyval.nodo = obtenerNodoExpresion("+", val_peek(2).nodo, val_peek(0).nodo);}
break;
case 128:
//#line 263 "Gramatica.y"
{yyval.sval = val_peek(2).sval + " " + "-" + " " + val_peek(0).sval; analizador_semantico.chequearValidezOperacionComparacionExpresiones(val_peek(2).sval, val_peek(0).sval, false, false); yyval.nodo = obtenerNodoExpresion("-", val_peek(2).nodo, val_peek(0).nodo);}
break;
case 129:
//#line 264 "Gramatica.y"
{yyval.sval = val_peek(0).sval; yyval.nodo = val_peek(0).nodo;}
break;
case 130:
//#line 268 "Gramatica.y"
{yyval.sval = val_peek(2).sval + " " + "*" + " " + val_peek(0).sval; analizador_semantico.chequearValidezOperacionTerminoFactor(val_peek(2).sval, val_peek(0).sval, true); yyval.nodo = obtenerNodoExpresion("*", val_peek(2).nodo, val_peek(0).nodo);}
break;
case 131:
//#line 269 "Gramatica.y"
{yyval.sval = val_peek(2).sval + " " + "/" + " " + val_peek(0).sval; analizador_semantico.chequearValidezOperacionTerminoFactor(val_peek(2).sval, val_peek(0).sval, false); yyval.nodo = obtenerNodoExpresion("/", val_peek(2).nodo, val_peek(0).nodo);}
break;
case 132:
//#line 270 "Gramatica.y"
{yyval.sval = val_peek(0).sval; yyval.nodo = val_peek(0).nodo; if (yyval.nodo != null && val_peek(0).sval != null) yyval.nodo.setTipo(AnalizadorLexico.simbolos.get(val_peek(0).sval).getTipo());}
break;
case 133:
//#line 274 "Gramatica.y"
{yyval.sval = analizador_semantico.verificarReferenciaValida(val_peek(0).sval, false); analizador_semantico.chequearTipoFactorValido(yyval.sval); yyval.nodo = generador_codigo_intermedio.generarNodo(yyval.sval, null, null);}
break;
case 134:
//#line 275 "Gramatica.y"
{yyval.sval = val_peek(0).sval; yyval.nodo = val_peek(0).nodo;}
break;
case 135:
//#line 279 "Gramatica.y"
{verificarRango(val_peek(0).sval); analizador_semantico.chequearValidezSimbolo(val_peek(0).sval); yyval.sval = val_peek(0).sval; yyval.nodo = obtenerNodoConstante(val_peek(0).sval, "INT");}
break;
case 136:
//#line 280 "Gramatica.y"
{analizador_lexico.constanteNegativaDetectada(val_peek(0).sval); analizador_semantico.chequearValidezSimbolo("-" + val_peek(0).sval); analizador_semantico.registrarTipoConstante("INT", "-" + val_peek(0).sval); yyval.sval = "-" + val_peek(0).sval; yyval.nodo = obtenerNodoConstante("-" + val_peek(0).sval, "INT");}
break;
case 137:
//#line 281 "Gramatica.y"
{analizador_semantico.registrarTipoConstante("ULONG", val_peek(0).sval); analizador_semantico.chequearValidezSimbolo(val_peek(0).sval); yyval.sval = val_peek(0).sval; yyval.nodo = obtenerNodoConstante(val_peek(0).sval, "ULONG");}
break;
case 138:
//#line 282 "Gramatica.y"
{analizador_semantico.registrarTipoConstante("DOUBLE", val_peek(0).sval); analizador_semantico.chequearValidezSimbolo(val_peek(0).sval); yyval.sval = val_peek(0).sval; yyval.nodo = obtenerNodoConstante(val_peek(0).sval, "DOUBLE");}
break;
case 139:
//#line 283 "Gramatica.y"
{analizador_lexico.constanteNegativaDetectada(val_peek(0).sval); analizador_semantico.chequearValidezSimbolo("-" + val_peek(0).sval); analizador_semantico.registrarTipoConstante("DOUBLE", "-" + val_peek(0).sval); yyval.sval = "-" + val_peek(0).sval; yyval.nodo = obtenerNodoConstante("-" + val_peek(0).sval, "DOUBLE");}
break;
case 140:
//#line 287 "Gramatica.y"
{System.out.print("(INVOCACION A FUNCION) "); analizador_semantico.chequearInvocacionFuncionValida(analizador_semantico.verificarReferenciaValida(val_peek(2).sval, true), val_peek(1).sval); String funcion_invocada = analizador_semantico.obtenerFuncionInvocada(val_peek(2).sval); yyval.nodo = generador_codigo_intermedio.generarNodo("CALL", generador_codigo_intermedio.generarNodo(funcion_invocada, null, null), val_peek(1).nodo); if (yyval.nodo != null) yyval.nodo.setParesVariableAtributo(analizador_semantico.obtenerParesMapeoInvocacion(funcion_invocada, analizador_semantico.obtenerVariablesInstanciaInvocacion()));}
break;
case 141:
//#line 291 "Gramatica.y"
{yyval.sval = analizador_semantico.verificarReferenciaValida(val_peek(0).sval, true); AnalizadorSemantico.lexema_invocacion_metodo_actual = val_peek(0).sval;}
break;
case 142:
//#line 295 "Gramatica.y"
{yyval.sval = analizador_semantico.verificarReferenciaValida(val_peek(0).sval, false); yyval.nodo = generador_codigo_intermedio.generarNodo(yyval.sval, null, null);}
break;
case 143:
//#line 299 "Gramatica.y"
{analizador_semantico.eliminarEntradaOriginalID(val_peek(0).sval); yyval.sval = val_peek(2).sval + "." + val_peek(0).sval;}
break;
case 144:
//#line 300 "Gramatica.y"
{analizador_semantico.eliminarEntradaOriginalID(val_peek(0).sval); yyval.sval = val_peek(0).sval;}
break;
case 145:
//#line 304 "Gramatica.y"
{yyval.sval = val_peek(1).sval; yyval.nodo = generador_codigo_intermedio.generarNodoUnidireccional("PARAMETRO_REAL", val_peek(1).nodo);}
break;
case 146:
//#line 305 "Gramatica.y"
{yyval.sval = null; yyval.nodo = generador_codigo_intermedio.generarNodoUnidireccional("PARAMETRO_REAL", null);}
break;
case 147:
//#line 306 "Gramatica.y"
{agregarError("ERROR: Parametro real invalido, el formato correcto es: '(' expresion_aritmetica ')' o '(' ')' "); yyval.sval = null; yyval.nodo = null;}
break;
case 148:
//#line 310 "Gramatica.y"
{System.out.print("(CLAUSULA DE SELECCION IF SIN POSIBLES RETORNOS Y SIN ELSE) "); yyval.nodo = generador_codigo_intermedio.generarNodo("IF", val_peek(3).nodo, generador_codigo_intermedio.generarNodo("CUERPO_IF", generador_codigo_intermedio.generarNodoUnidireccional("THEN", val_peek(2).nodo), generador_codigo_intermedio.generarNodoUnidireccional("ELSE", null)));}
break;
case 149:
//#line 311 "Gramatica.y"
{System.out.print("(CLAUSULA DE SELECCION IF SIN POSIBLES RETORNOS Y ELSE SIN POSIBLES RETORNOS) "); yyval.nodo = generador_codigo_intermedio.generarNodo("IF", val_peek(5).nodo, generador_codigo_intermedio.generarNodo("CUERPO_IF", generador_codigo_intermedio.generarNodoUnidireccional("THEN", val_peek(4).nodo), generador_codigo_intermedio.generarNodoUnidireccional("ELSE", val_peek(2).nodo)));}
break;
case 150:
//#line 315 "Gramatica.y"
{System.out.print("(CLAUSULA DE SELECCION IF DE RETORNO PARCIAL Y SIN ELSE) "); yyval.nodo = generador_codigo_intermedio.generarNodo("IF", val_peek(3).nodo, generador_codigo_intermedio.generarNodo("CUERPO_IF", generador_codigo_intermedio.generarNodoUnidireccional("THEN", val_peek(2).nodo), generador_codigo_intermedio.generarNodoUnidireccional("ELSE", null)));}
break;
case 151:
//#line 316 "Gramatica.y"
{System.out.print("(CLAUSULA DE SELECCION IF DE RETORNO PARCIAL Y ELSE SIN POSIBLES RETORNOS) "); yyval.nodo = generador_codigo_intermedio.generarNodo("IF", val_peek(5).nodo, generador_codigo_intermedio.generarNodo("CUERPO_IF", generador_codigo_intermedio.generarNodoUnidireccional("THEN", val_peek(4).nodo), generador_codigo_intermedio.generarNodoUnidireccional("ELSE", val_peek(2).nodo)));}
break;
case 152:
//#line 317 "Gramatica.y"
{System.out.print("(CLAUSULA DE SELECCION IF SIN POSIBLES RETORNOS Y ELSE DE RETORNO PARCIAL) "); yyval.nodo = generador_codigo_intermedio.generarNodo("IF", val_peek(5).nodo, generador_codigo_intermedio.generarNodo("CUERPO_IF", generador_codigo_intermedio.generarNodoUnidireccional("THEN", val_peek(4).nodo), generador_codigo_intermedio.generarNodoUnidireccional("ELSE", val_peek(2).nodo)));}
break;
case 153:
//#line 318 "Gramatica.y"
{System.out.print("(CLAUSULA DE SELECCION IF DE RETORNO PARCIAL Y ELSE DE RETORNO PARCIAL) "); yyval.nodo = generador_codigo_intermedio.generarNodo("IF", val_peek(5).nodo, generador_codigo_intermedio.generarNodo("CUERPO_IF", generador_codigo_intermedio.generarNodoUnidireccional("THEN", val_peek(4).nodo), generador_codigo_intermedio.generarNodoUnidireccional("ELSE", val_peek(2).nodo)));}
break;
case 154:
//#line 319 "Gramatica.y"
{System.out.print("(CLAUSULA DE SELECCION IF DE RETORNO COMPLETO Y SIN ELSE) "); yyval.nodo = generador_codigo_intermedio.generarNodo("IF", val_peek(3).nodo, generador_codigo_intermedio.generarNodo("CUERPO_IF", generador_codigo_intermedio.generarNodoUnidireccional("THEN", val_peek(2).nodo), generador_codigo_intermedio.generarNodoUnidireccional("ELSE", null)));}
break;
case 155:
//#line 320 "Gramatica.y"
{System.out.print("(CLAUSULA DE SELECCION IF DE RETORNO PARCIAL Y ELSE DE RETORNO COMPLETO) "); yyval.nodo = generador_codigo_intermedio.generarNodo("IF", val_peek(5).nodo, generador_codigo_intermedio.generarNodo("CUERPO_IF", generador_codigo_intermedio.generarNodoUnidireccional("THEN", val_peek(4).nodo), generador_codigo_intermedio.generarNodoUnidireccional("ELSE", val_peek(2).nodo)));}
break;
case 156:
//#line 321 "Gramatica.y"
{System.out.print("(CLAUSULA DE SELECCION IF SIN POSIBLES RETORNOS Y ELSE DE RETORNO COMPLETO) "); yyval.nodo = generador_codigo_intermedio.generarNodo("IF", val_peek(5).nodo, generador_codigo_intermedio.generarNodo("CUERPO_IF", generador_codigo_intermedio.generarNodoUnidireccional("THEN", val_peek(4).nodo), generador_codigo_intermedio.generarNodoUnidireccional("ELSE", val_peek(2).nodo)));}
break;
case 157:
//#line 322 "Gramatica.y"
{System.out.print("(CLAUSULA DE SELECCION IF DE RETORNO COMPLETO Y ELSE DE RETORNO PARCIAL) "); yyval.nodo = generador_codigo_intermedio.generarNodo("IF", val_peek(5).nodo, generador_codigo_intermedio.generarNodo("CUERPO_IF", generador_codigo_intermedio.generarNodoUnidireccional("THEN", val_peek(4).nodo), generador_codigo_intermedio.generarNodoUnidireccional("ELSE", val_peek(2).nodo)));}
break;
case 158:
//#line 323 "Gramatica.y"
{System.out.print("(CLAUSULA DE SELECCION IF DE RETORNO COMPLETO Y ELSE SIN POSIBLES RETORNOS) "); yyval.nodo = generador_codigo_intermedio.generarNodo("IF", val_peek(5).nodo, generador_codigo_intermedio.generarNodo("CUERPO_IF", generador_codigo_intermedio.generarNodoUnidireccional("THEN", val_peek(4).nodo), generador_codigo_intermedio.generarNodoUnidireccional("ELSE", val_peek(2).nodo)));}
break;
case 159:
//#line 326 "Gramatica.y"
{System.out.print("(CLAUSULA DE SELECCION IF ELSE DE RETORNO COMPLETO) "); yyval.nodo = generador_codigo_intermedio.generarNodo("IF", val_peek(5).nodo, generador_codigo_intermedio.generarNodo("CUERPO_IF", generador_codigo_intermedio.generarNodoUnidireccional("THEN", val_peek(4).nodo), generador_codigo_intermedio.generarNodoUnidireccional("ELSE", val_peek(2).nodo)));}
break;
case 160:
//#line 330 "Gramatica.y"
{yyval.nodo = val_peek(0).nodo;}
break;
case 161:
//#line 331 "Gramatica.y"
{yyval.nodo = val_peek(0).nodo;}
break;
case 162:
//#line 332 "Gramatica.y"
{yyval.nodo = null;}
break;
case 163:
//#line 336 "Gramatica.y"
{yyval.nodo = val_peek(0).nodo;}
break;
case 164:
//#line 340 "Gramatica.y"
{yyval.nodo = val_peek(0).nodo;}
break;
case 165:
//#line 341 "Gramatica.y"
{yyval.nodo = val_peek(0).nodo;}
break;
case 166:
//#line 345 "Gramatica.y"
{yyval.nodo = val_peek(0).nodo;}
break;
case 167:
//#line 349 "Gramatica.y"
{yyval.nodo = val_peek(0).nodo;}
break;
case 168:
//#line 350 "Gramatica.y"
{yyval.nodo = val_peek(0).nodo;}
break;
case 169:
//#line 354 "Gramatica.y"
{yyval.nodo = val_peek(0).nodo;}
break;
case 170:
//#line 358 "Gramatica.y"
{yyval.nodo = generador_codigo_intermedio.generarNodoUnidireccional("CONDICION", val_peek(1).nodo);}
break;
case 171:
//#line 359 "Gramatica.y"
{agregarError("ERROR: Condicion de clausula IF invalida, el formato correcto es: '(' comparacion ')'"); yyval.nodo = generador_codigo_intermedio.generarNodoUnidireccional("CONDICION", val_peek(1).nodo);}
break;
case 172:
//#line 360 "Gramatica.y"
{agregarError("ERROR: Condicion de clausula IF invalida, el formato correcto es: '(' comparacion ')'"); yyval.nodo = generador_codigo_intermedio.generarNodoUnidireccional("CONDICION", val_peek(0).nodo);}
break;
case 173:
//#line 361 "Gramatica.y"
{agregarError("ERROR: Condicion de clausula IF invalida, el formato correcto es: '(' comparacion ')'"); yyval.nodo = null;}
break;
case 174:
//#line 362 "Gramatica.y"
{agregarError("ERROR: Condicion de clausula IF invalida, el formato correcto es: '(' comparacion ')'"); yyval.nodo = generador_codigo_intermedio.generarNodoUnidireccional("CONDICION", val_peek(0).nodo);}
break;
case 175:
//#line 366 "Gramatica.y"
{analizador_semantico.chequearValidezOperacionComparacionExpresiones(val_peek(2).sval, val_peek(0).sval, true, false); yyval.nodo = generador_codigo_intermedio.generarNodo(">", val_peek(2).nodo, val_peek(0).nodo); if (yyval.nodo != null && val_peek(2).nodo != null) yyval.nodo.setTipo(val_peek(2).nodo.getTipo());}
break;
case 176:
//#line 367 "Gramatica.y"
{analizador_semantico.chequearValidezOperacionComparacionExpresiones(val_peek(2).sval, val_peek(0).sval, true, false); yyval.nodo = generador_codigo_intermedio.generarNodo("<", val_peek(2).nodo, val_peek(0).nodo); if (yyval.nodo != null && val_peek(2).nodo != null) yyval.nodo.setTipo(val_peek(2).nodo.getTipo());}
break;
case 177:
//#line 368 "Gramatica.y"
{analizador_semantico.chequearValidezOperacionComparacionExpresiones(val_peek(2).sval, val_peek(0).sval, true, false); yyval.nodo = generador_codigo_intermedio.generarNodo(">=", val_peek(2).nodo, val_peek(0).nodo); if (yyval.nodo != null && val_peek(2).nodo != null) yyval.nodo.setTipo(val_peek(2).nodo.getTipo());}
break;
case 178:
//#line 369 "Gramatica.y"
{analizador_semantico.chequearValidezOperacionComparacionExpresiones(val_peek(2).sval, val_peek(0).sval, true, false); yyval.nodo = generador_codigo_intermedio.generarNodo("<=", val_peek(2).nodo, val_peek(0).nodo); if (yyval.nodo != null && val_peek(2).nodo != null) yyval.nodo.setTipo(val_peek(2).nodo.getTipo());}
break;
case 179:
//#line 370 "Gramatica.y"
{analizador_semantico.chequearValidezOperacionComparacionExpresiones(val_peek(2).sval, val_peek(0).sval, true, false); yyval.nodo = generador_codigo_intermedio.generarNodo("==", val_peek(2).nodo, val_peek(0).nodo); if (yyval.nodo != null && val_peek(2).nodo != null) yyval.nodo.setTipo(val_peek(2).nodo.getTipo());}
break;
case 180:
//#line 371 "Gramatica.y"
{analizador_semantico.chequearValidezOperacionComparacionExpresiones(val_peek(2).sval, val_peek(0).sval, true, false); yyval.nodo = generador_codigo_intermedio.generarNodo("!!", val_peek(2).nodo, val_peek(0).nodo); if (yyval.nodo != null && val_peek(2).nodo != null) yyval.nodo.setTipo(val_peek(2).nodo.getTipo());}
break;
case 181:
//#line 375 "Gramatica.y"
{System.out.print("(SALIDA DE MENSAJE) "); analizador_semantico.chequearValidezSimbolo(val_peek(1).sval); yyval.nodo = generador_codigo_intermedio.generarNodo("PRINT", null, null); if (yyval.nodo != null && val_peek(1).sval != null) yyval.nodo.setCadenaImpresion(val_peek(1).sval.replaceAll("%", ""));}
break;
case 182:
//#line 376 "Gramatica.y"
{System.out.print("(SALIDA DE MENSAJE) "); agregarError("ERROR: Salida de mensaje invalida, el formato correcto es: PRINT CADENA_CARACTERES ','"); yyval.nodo = null;}
break;
case 183:
//#line 380 "Gramatica.y"
{System.out.print("(SENTENCIA FOR) "); analizador_semantico.chequearCompatibilidadControladoresFor(val_peek(2).sval, val_peek(1).sval); if (val_peek(1).nodo != null) val_peek(1).nodo.getNodoHijoIzquierdo().setSimbolo(val_peek(2).sval); yyval.nodo = generador_codigo_intermedio.generarNodo("FOR", generador_codigo_intermedio.generarNodo("=", generador_codigo_intermedio.generarNodo(val_peek(2).sval, null, null), obtenerNodoConstante(analizador_semantico.obtenerConstanteControladoresFor(val_peek(1).sval, 0), analizador_semantico.obtenerTipoElemento(analizador_semantico.obtenerConstanteControladoresFor(val_peek(1).sval, 2)))), generador_codigo_intermedio.generarNodo("BUCLE", generador_codigo_intermedio.generarNodoUnidireccional("CONDICION", val_peek(1).nodo), generador_codigo_intermedio.generarNodo("CUERPO_FOR", val_peek(0).nodo, generador_codigo_intermedio.generarNodo("=", generador_codigo_intermedio.generarNodo(val_peek(2).sval, null, null), obtenerNodoExpresion("+", generador_codigo_intermedio.generarNodo(val_peek(2).sval, null, null), obtenerNodoConstante(analizador_semantico.obtenerConstanteControladoresFor(val_peek(1).sval, 2), analizador_semantico.obtenerTipoElemento(analizador_semantico.obtenerConstanteControladoresFor(val_peek(1).sval, 2))))))));}
break;
case 184:
//#line 384 "Gramatica.y"
{yyval.nodo = val_peek(0).nodo;}
break;
case 185:
//#line 385 "Gramatica.y"
{yyval.nodo = val_peek(1).nodo;}
break;
case 186:
//#line 386 "Gramatica.y"
{yyval.nodo = null;}
break;
case 187:
//#line 390 "Gramatica.y"
{System.out.print("(SENTENCIA FOR CON SENTENCIA EJECUTABLE DE RETORNO PARCIAL) "); analizador_semantico.chequearCompatibilidadControladoresFor(val_peek(2).sval, val_peek(1).sval); if (val_peek(1).nodo != null) val_peek(1).nodo.getNodoHijoIzquierdo().setSimbolo(val_peek(2).sval); yyval.nodo = generador_codigo_intermedio.generarNodo("FOR", generador_codigo_intermedio.generarNodo("=", generador_codigo_intermedio.generarNodo(val_peek(2).sval, null, null), obtenerNodoConstante(analizador_semantico.obtenerConstanteControladoresFor(val_peek(1).sval, 0), analizador_semantico.obtenerTipoElemento(analizador_semantico.obtenerConstanteControladoresFor(val_peek(1).sval, 2)))), generador_codigo_intermedio.generarNodo("BUCLE", generador_codigo_intermedio.generarNodoUnidireccional("CONDICION", val_peek(1).nodo), generador_codigo_intermedio.generarNodo("CUERPO_FOR", val_peek(0).nodo, generador_codigo_intermedio.generarNodo("=", generador_codigo_intermedio.generarNodo(val_peek(2).sval, null, null), obtenerNodoExpresion("+", generador_codigo_intermedio.generarNodo(val_peek(2).sval, null, null), obtenerNodoConstante(analizador_semantico.obtenerConstanteControladoresFor(val_peek(1).sval, 2), analizador_semantico.obtenerTipoElemento(analizador_semantico.obtenerConstanteControladoresFor(val_peek(1).sval, 2))))))));}
break;
case 188:
//#line 394 "Gramatica.y"
{yyval.nodo = val_peek(0).nodo;}
break;
case 189:
//#line 395 "Gramatica.y"
{yyval.nodo = val_peek(0).nodo;}
break;
case 190:
//#line 396 "Gramatica.y"
{yyval.nodo = val_peek(1).nodo;}
break;
case 191:
//#line 397 "Gramatica.y"
{yyval.nodo = val_peek(1).nodo;}
break;
case 192:
//#line 401 "Gramatica.y"
{analizador_semantico.eliminarEntradaOriginalID(val_peek(2).sval); yyval.sval = analizador_semantico.verificarReferenciaValida(val_peek(2).sval, false); analizador_semantico.verificarValidezVariableControlFor(val_peek(2).sval);}
break;
case 193:
//#line 402 "Gramatica.y"
{agregarError("ERROR: Encabezado de bucle for invalido, el formato correcto es: FOR ID IN RANGE"); analizador_semantico.eliminarEntradaOriginalID(val_peek(1).sval); yyval.sval = analizador_semantico.verificarReferenciaValida(val_peek(1).sval, false); analizador_semantico.verificarValidezVariableControlFor(val_peek(1).sval);}
break;
case 194:
//#line 403 "Gramatica.y"
{agregarError("ERROR: Encabezado de bucle for invalido, el formato correcto es: FOR ID IN RANGE"); analizador_semantico.eliminarEntradaOriginalID(val_peek(1).sval); yyval.sval = analizador_semantico.verificarReferenciaValida(val_peek(1).sval, false); analizador_semantico.verificarValidezVariableControlFor(val_peek(1).sval);}
break;
case 195:
//#line 404 "Gramatica.y"
{agregarError("ERROR: Encabezado de bucle for invalido, el formato correcto es: FOR ID IN RANGE"); analizador_semantico.eliminarEntradaOriginalID(val_peek(0).sval); yyval.sval = analizador_semantico.verificarReferenciaValida(val_peek(0).sval, false); analizador_semantico.verificarValidezVariableControlFor(val_peek(0).sval);}
break;
case 196:
//#line 405 "Gramatica.y"
{agregarError("ERROR: Encabezado de bucle for invalido, el formato correcto es: FOR ID IN RANGE"); yyval.sval = null;}
break;
case 197:
//#line 406 "Gramatica.y"
{agregarError("ERROR: Encabezado de bucle for invalido, el formato correcto es: FOR ID IN RANGE"); yyval.sval = null;}
break;
case 198:
//#line 407 "Gramatica.y"
{agregarError("ERROR: Encabezado de bucle for invalido, el formato correcto es: FOR ID IN RANGE"); yyval.sval = null;}
break;
case 199:
//#line 408 "Gramatica.y"
{agregarError("ERROR: Encabezado de bucle for invalido, el formato correcto es: FOR ID IN RANGE"); yyval.sval = null;}
break;
case 200:
//#line 412 "Gramatica.y"
{yyval.sval = analizador_semantico.verificarConstantesControlFor(val_peek(5).sval, val_peek(3).sval, val_peek(1).sval); yyval.nodo = generador_codigo_intermedio.generarNodo(analizador_semantico.obtenerComparadorAsociado(val_peek(1).sval), generador_codigo_intermedio.generarNodo("", null, null), val_peek(3).nodo); if (yyval.nodo != null && val_peek(5).sval != null) yyval.nodo.setTipo(AnalizadorLexico.simbolos.get(val_peek(5).sval).getTipo());}
break;
case 201:
//#line 413 "Gramatica.y"
{agregarError("ERROR: Control del rango de iteraciones de bucle FOR invalido, el formato correcto es: '(' constante ';' constante ';' constante ')'"); yyval.sval = analizador_semantico.verificarConstantesControlFor(val_peek(5).sval, val_peek(3).sval, val_peek(1).sval); yyval.nodo = generador_codigo_intermedio.generarNodo(analizador_semantico.obtenerComparadorAsociado(val_peek(1).sval), generador_codigo_intermedio.generarNodo("", null, null), val_peek(3).nodo); if (yyval.nodo != null && val_peek(5).sval != null) yyval.nodo.setTipo(AnalizadorLexico.simbolos.get(val_peek(5).sval).getTipo());}
break;
case 202:
//#line 414 "Gramatica.y"
{agregarError("ERROR: Control del rango de iteraciones de bucle FOR invalido, el formato correcto es: '(' constante ';' constante ';' constante ')'"); yyval.sval = analizador_semantico.verificarConstantesControlFor(val_peek(4).sval, val_peek(2).sval, val_peek(0).sval); yyval.nodo = generador_codigo_intermedio.generarNodo(analizador_semantico.obtenerComparadorAsociado(val_peek(0).sval), generador_codigo_intermedio.generarNodo("", null, null), val_peek(2).nodo); if (yyval.nodo != null && val_peek(4).sval != null) yyval.nodo.setTipo(AnalizadorLexico.simbolos.get(val_peek(4).sval).getTipo());}
break;
case 203:
//#line 415 "Gramatica.y"
{agregarError("ERROR: Control del rango de iteraciones de bucle FOR invalido, el formato correcto es: '(' constante ';' constante ';' constante ')'"); yyval.sval = analizador_semantico.verificarConstantesControlFor(val_peek(4).sval, val_peek(2).sval, val_peek(0).sval); yyval.nodo = generador_codigo_intermedio.generarNodo(analizador_semantico.obtenerComparadorAsociado(val_peek(0).sval), generador_codigo_intermedio.generarNodo("", null, null), val_peek(2).nodo); if (yyval.nodo != null && val_peek(4).sval != null) yyval.nodo.setTipo(AnalizadorLexico.simbolos.get(val_peek(4).sval).getTipo());}
break;
case 204:
//#line 416 "Gramatica.y"
{agregarError("ERROR: Control del rango de iteraciones de bucle FOR invalido, el formato correcto es: '(' constante ';' constante ';' constante ')'"); yyval.sval = null; yyval.nodo = null;}
break;
//#line 1781 "Parser.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
}//switch
//#### Now let's reduce... ####
if (yydebug) debug("reduce");
state_drop(yym);             //we just reduced yylen states
yystate = state_peek(0);     //get new state
val_drop(yym);               //corresponding value drop
yym = yylhs[yyn];            //select next TERMINAL(on lhs)
if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
  {
  if (yydebug) debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
  yystate = YYFINAL;         //explicitly say we're done
  state_push(YYFINAL);       //and save it
  val_push(yyval);           //also save the semantic value of parsing
  if (yychar < 0)            //we want another character?
    {
    yychar = yylex();        //get next character
    if (yychar<0) yychar=0;  //clean, if necessary
    if (yydebug)
      yylexdebug(yystate,yychar);
    }
  if (yychar == 0)          //Good exit (if lex returns 0 ;-)
     break;                 //quit the loop--all DONE
  }//if yystate
else                        //else not done yet
  {                         //get next state and push, for next yydefred[]
  yyn = yygindex[yym];      //find out where to go
  if ((yyn != 0) && (yyn += yystate) >= 0 &&
        yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
    yystate = yytable[yyn]; //get new state
  else
    yystate = yydgoto[yym]; //else go to new defred
  if (yydebug) debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
  state_push(yystate);     //going again, so push state & val...
  val_push(yyval);         //for next action
  }
}//main loop
return 0;//yyaccept!!
}
//## end of method parse() ######################################



//## run() --- for Thread #######################################
/**
* A default run method, used for operating this parser
* object in the background.  It is intended for extending Thread
* or implementing Runnable.  Turn off with -Jnorun .
*/
public void run()
{
yyparse();
}
//## end of method run() ########################################



//## Constructors ###############################################
/**
* Default constructor.  Turn off with -Jnoconstruct .

*/
public Parser()
{
//nothing to do
}


/**
* Create a parser, setting the debug to true or false.
* @param debugMe true for debugging, false for no debug.
*/
public Parser(boolean debugMe)
{
yydebug=debugMe;
}
//###############################################################



}
//################### END OF CLASS ##############################








































































