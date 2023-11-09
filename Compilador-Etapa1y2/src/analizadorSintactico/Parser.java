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
//#line 21 "Parser.java"




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
  dup.obj = val.obj;
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
   50,   50,   51,   51,   52,   52,   52,   52,   52,   36,
   48,   48,   53,   53,   53,   37,   37,   40,   40,   40,
   40,   40,   40,   40,   40,   40,   43,   55,   55,   55,
   56,   57,   57,   58,   59,   59,   60,   54,   54,   54,
   54,   54,   61,   61,   61,   61,   61,   61,   38,   38,
   39,   64,   64,   64,   41,   65,   65,   65,   65,   62,
   62,   62,   62,   62,   62,   62,   62,   63,   63,   63,
   63,   63,
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
    3,    1,    3,    2,    3,    5,    7,    5,    7,    7,
    7,    5,    7,    7,    7,    7,    7,    1,    1,    2,
    1,    1,    1,    1,    1,    1,    1,    3,    2,    2,
    3,    1,    3,    3,    3,    3,    3,    3,    3,    2,
    3,    1,    2,    3,    3,    1,    1,    2,    2,    4,
    3,    3,    2,    3,    2,    2,    1,    7,    6,    6,
    5,    3,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    0,    0,   36,   37,   38,    0,    0,
    0,    0,    0,    0,    0,    0,    0,   11,   12,   13,
   14,   15,   28,   29,   30,   31,   32,   33,    0,   43,
   44,    0,    0,    0,    0,  102,  103,  104,  105,  106,
  107,  108,  109,  110,    0,    0,    0,    0,   16,    3,
  138,  135,  137,  142,    0,    0,    0,    0,    0,  132,
  134,    0,    0,    0,  180,   80,    0,    0,   51,    0,
  196,    0,   94,    0,    0,    0,   98,  111,   40,   42,
    0,    0,    0,    6,    0,    9,   10,    0,    0,    0,
    0,    0,    0,    0,   24,   23,   21,   22,    0,    0,
   77,    0,    0,   62,    0,    0,    0,   86,    0,    0,
   49,    0,    0,    0,   97,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  139,  136,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  163,
  166,  159,  165,  158,  162,    0,    0,    0,  169,  179,
   81,    0,    0,  194,    0,  191,   92,    0,    0,   93,
    0,   90,   41,   35,    0,    7,    8,    0,    0,   34,
    0,   54,    0,    0,    0,   50,   75,   76,    0,    0,
    0,    0,    0,    0,   84,    0,    0,   18,   26,   20,
   25,   27,    0,    0,    0,    0,   95,   59,   61,    0,
    0,    0,  100,   46,   48,    0,    0,    0,  144,    0,
    0,  141,  140,    0,    0,    0,    0,  186,  187,  182,
    0,    0,    0,  181,  185,    5,  171,  168,    0,    0,
    0,    0,    0,    0,    0,    0,  130,  131,  160,  121,
    0,  115,  114,    0,    0,    0,    0,    0,    0,    0,
    0,   83,   82,   79,  190,   91,   87,   88,   89,   39,
    0,    0,    0,   57,    0,   55,   72,    0,    0,   66,
   63,   69,   73,   74,    0,   19,   85,   17,    0,    0,
    0,   60,   96,   58,   47,  101,   45,  126,  145,  143,
  125,  202,    0,    0,    0,  189,  183,  188,  124,  112,
  120,    0,  116,  113,  117,  119,    0,  118,  161,    0,
  164,    0,  167,    0,  146,    0,    0,    0,  148,    0,
    0,    0,  152,    0,    0,   53,   65,   71,    0,   70,
    0,    0,    0,  184,  122,  123,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   64,    0,    0,    0,  147,
  150,  154,  149,  151,  153,  156,  155,  157,    0,    0,
  199,    0,    0,  198,
};
final static short yydgoto[] = {                         15,
   16,   17,   18,  140,  141,   21,   22,   99,  100,  143,
   23,   24,   25,   26,   27,   28,   29,   81,   30,   31,
  112,  113,   32,   92,  106,  107,  180,  181,   33,  101,
   34,  108,   35,  115,   36,   37,   38,   39,   40,   41,
   42,   43,   44,  144,  244,  145,  245,   45,   58,   59,
   60,   61,  120,   62,  309,  310,  311,  321,  313,  314,
   63,   46,  123,  224,  225,
};
final static short yysindex[] = {                       400,
  295,   41,  -12, -194, -235,    0,    0,    0, -138,   47,
 -218,   45,  -16,  530,    0, -204,  718,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0, -185,    0,
    0,   32,  418, -110,  -93,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  -37,    5,   58,  639,    0,    0,
    0,    0,    0,    0,  128, -142,   62,   -5,   -3,    0,
    0,  562,   71,   72,    0,    0, -178, -143,    0, -134,
    0, -171,    0,  -17,  -47, -115,    0,    0,    0,    0,
  -26,  -95,  745,    0,   41,    0,    0,    5,  -24,  244,
 -132,  321,  939,  763,    0,    0,    0,    0,  123,  491,
    0,  -91, -109,    0,   32,  136,  -72,    0,  -91,  -71,
    0,  143,  -67,   32,    0,  198,   46,  198,  -70,  166,
  238,  167,  563,  -21,  196,  225,    0,    0,  198,  198,
  198,  198,  198,  198,  198,  198,  198,  198,  781,    0,
    0,    0,    0,    0,    0, -111,  -81,  134,    0,    0,
    0,   33, -177,    0,   25,    0,    0,  -39,   42,    0,
   49,    0,    0,    0,   40,    0,    0,  365,  609,    0,
  287,    0,   53,  291,  658,    0,    0,    0,  210,  297,
  676,  512,  315,  548,    0,   41,  939,    0,    0,    0,
    0,    0,  -62,  318,  -61,  443,    0,    0,    0,  -60,
  324,  -59,    0,    0,    0,  329,  433,  313,    0,  163,
  448,    0,    0,  337,  331,   -2,  800,    0,    0,    0,
  336,  351,  360,    0,    0,    0,    0,    0,    3,    3,
    3,    3,   -3,   -3,    3,    3,    0,    0,    0,    0,
  286,    0,    0,  818,  621,  562,  369,  562,  385,  562,
  388,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  841,  149,  860,    0,  392,    0,    0,  317,  699,    0,
    0,    0,    0,    0,  322,    0,    0,    0,  562,  939,
  958,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,   -2,  376,  410,    0,    0,    0,    0,    0,
    0,  333,    0,    0,    0,    0,  334,    0,    0,  211,
    0,  220,    0,  227,    0,  230,  236,  239,    0,  240,
  243,  251,    0,  878,  365,    0,    0,    0,  342,    0,
  157,  438,   -2,    0,    0,    0,  425,  470,  471,  472,
  473,  475,  476,  477,  480,    0,  617,   -2,  485,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  896,  486,
    0,  914,  929,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,   86,    0,    0,    0,   70,    0,
  -92,    0,  -34,    0,    0,  532,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,   76,    0,  171,    0,
    0,    0,  570,    0,    0,    0,    0,  465,    0,   97,
    0,  182,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  534,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  538,    0,  588,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  189,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  109,  117,
  119,  140,  224,  265,  164,  208,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  343,    0,    0,  496,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  499,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  595,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  596,
    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
  529,    8,   -7,    1,  165,  -33,  -25,    0,  305,  -49,
    0,    0,    0,    0,    0,    0,  335,  519,   -6,  380,
    0,  310,  426,  -88,    0,  320,    0,  374,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  -87, -173,  427, -166,  516, 1096,  293,
  301,  -41,    0,  -14,  -55, -225,   11, -137,   22, -123,
  498,  -13,  466,    0,    0,
};
final static int YYTABLESIZE=1235;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         97,
   19,   19,  117,   88,  122,  142,  146,   98,  119,   87,
  161,  142,  103,  280,   19,  194,  196,  164,  257,  170,
  281,   83,  316,  118,  320,  206,  142,   80,  111,  110,
   99,   65,  165,   95,  165,  222,  142,  133,  137,  134,
  159,   69,   56,  138,  121,  133,  122,  134,   19,   56,
  192,   84,  198,  201,  136,   83,  135,  204,   77,   97,
   97,   66,  282,  284,  285,  287,  190,   98,   98,   88,
  168,   90,  147,  221,  191,   87,   67,  151,  253,  215,
   55,  222,   68,  148,  177,   56,  209,  324,   78,  324,
   56,   79,  178,   95,   95,  155,  156,  220,  152,  254,
  189,   49,  111,  111,   76,  242,  205,  119,  312,  197,
  317,  149,  262,  243,  197,  150,  133,  133,  133,  133,
  133,  320,  133,  218,  318,   52,  322,  153,   70,   71,
  127,  128,  192,  154,  192,  133,  195,  133,   72,  240,
  162,  195,  142,  220,  174,  102,  246,  247,  190,  175,
  190,    5,    5,  242,   88,   88,  191,  176,  191,  177,
  166,  243,  109,   99,   20,   20,  185,  177,    5,   99,
    5,  279,   56,  273,  294,  178,  248,  249,   20,  197,
  178,  274,  189,  242,  189,  362,  203,  240,  177,    5,
    5,  243,  363,  205,    5,  205,  178,   96,  133,    5,
    5,    5,    5,  290,  173,  133,  212,  134,  160,  213,
  303,  129,   20,  129,  129,  129,  256,  240,  304,  308,
  142,  193,  142,  146,  142,  216,  193,  242,  192,  242,
  129,  175,  129,  192,  226,  243,  227,  243,  157,  176,
  116,  177,   56,  142,  301,  306,  303,   88,  174,   88,
  163,  332,  163,  142,  304,  308,  179,   96,   96,  158,
   79,  273,  178,   64,  127,  228,  127,  127,  127,  274,
   51,   52,   53,  129,  130,  131,  132,   51,   52,   53,
  301,  306,   56,  127,  172,  127,  173,  219,  252,  147,
  303,  349,  255,  129,    6,    7,    8,  258,  304,  142,
  331,  208,   73,  241,  259,  128,  360,  128,  128,  128,
   88,   88,   74,   51,   52,   53,  260,   54,   51,   52,
   53,  142,   54,   75,  128,  242,  128,  264,  303,  265,
  174,  266,  133,  243,  270,  133,  304,  308,   49,  268,
  271,  133,  197,  197,  197,  275,  127,  133,   52,   52,
   52,  241,  133,  289,  133,  133,  133,  133,  277,  240,
  179,  283,  301,  306,  176,  175,   91,  286,  175,  195,
  195,  195,  176,  176,  175,  177,  176,  292,  177,  296,
  175,  241,  176,  125,  177,  175,   56,  128,  176,  293,
  177,  250,  251,  176,  297,  177,  178,  182,  184,  178,
   51,   52,   53,  298,   54,  178,  325,  247,  302,  307,
  299,  178,  315,  104,  347,  251,  178,   48,  200,  202,
  173,  193,  195,  173,  173,  233,  234,  129,  319,  173,
  129,  323,  326,  329,  333,  173,  129,  237,  238,   91,
  173,  327,  129,  175,  302,  307,  330,  129,   91,  129,
  129,  129,  129,  334,  193,  193,  193,  335,  336,  105,
  114,  192,  192,  192,  174,   56,  346,  174,  350,  337,
   51,   52,   53,  174,   54,  133,  288,  134,  338,  174,
  127,  104,  104,  127,  174,  339,  199,  261,  340,  127,
  133,  291,  134,  214,  341,  127,  348,  342,  343,  171,
  127,  344,  127,  127,  127,  127,    6,    7,    8,  345,
   51,   52,   53,  351,  352,  353,  354,   57,  355,  356,
  357,  128,   14,  358,  128,  361,  364,  105,  105,   50,
  128,    1,  105,    2,  114,  114,  128,    4,  114,   67,
   94,  128,   68,  128,  128,  128,  128,   89,  269,  223,
   47,    2,  126,  169,    3,    4,    5,    6,    7,    8,
    9,    0,    0,   10,   11,  175,   12,    0,    0,    0,
   57,   13,  199,    0,  199,    0,   47,    2,    0,    0,
    3,    4,    5,    6,    7,    8,    9,   78,    0,   10,
   11,    0,   12,    0,    0,    0,    0,   13,   56,   56,
   57,    0,   56,   56,   56,   56,   56,   56,   56,    0,
    0,   56,   56,  187,   56,  188,    0,    0,  105,   56,
  105,   85,    0,    0,    3,  114,    0,  114,    0,    0,
    9,   57,   57,   57,  187,    0,  276,    0,    0,    0,
    0,   54,    0,    0,   57,   57,   57,   57,   57,   57,
   57,   57,   57,   57,   82,    1,    2,    0,    0,    3,
    4,    5,    6,    7,    8,    9,    0,    0,   10,   11,
  187,   12,  278,   93,    2,    0,   13,    3,    4,    5,
    6,    7,    8,    9,  139,  217,   10,   11,    0,   12,
    0,    0,  172,    0,   13,    0,    0,    0,   47,    2,
    0,   57,    3,    4,    5,    6,    7,    8,    9,    0,
  170,   10,   11,    0,   12,    0,    0,  201,  200,   13,
   78,   78,    0,    0,   78,   78,   78,   78,   78,   78,
   78,  263,    0,   78,   78,    0,   78,    0,    0,  359,
    0,   78,    0,    0,    0,  305,    0,  186,    0,    0,
    3,    4,    5,    6,    7,    8,    9,    0,    0,   10,
   11,    0,    0,  124,    0,    0,    0,   13,  186,    0,
    0,    3,    4,    5,    6,    7,    8,    9,    0,    0,
   10,   11,  267,    0,    0,   47,    2,    0,   13,    3,
    4,    5,    6,    7,    8,    9,    0,    0,   10,   11,
  272,   12,    0,    0,  186,    0,   13,    3,    4,    5,
    6,    7,    8,    9,    0,    0,   10,   11,    2,    2,
    0,    3,    3,  328,   13,    0,  172,    9,    9,  172,
    0,    0,    0,   12,   12,  172,    0,    0,   54,   54,
    0,  172,   86,    0,  170,    0,  172,  170,    0,    0,
    0,  201,  200,  170,  201,  200,    0,    0,    0,  170,
  201,  200,    0,    0,  170,   85,  201,  200,    3,  167,
    0,  201,  200,  186,    9,    0,    3,    2,    0,    0,
    3,    0,    9,    0,    0,   54,    9,  183,    0,    0,
    0,    0,   12,   54,   47,    2,    0,   54,    3,    4,
    5,    6,    7,    8,    9,  239,    0,   10,   11,    0,
   12,    0,    0,   47,    2,   13,    0,    3,    4,    5,
    6,    7,    8,    9,  295,    0,   10,   11,    0,   12,
    0,   47,    2,    0,   13,    3,    4,    5,    6,    7,
    8,    9,  300,    0,   10,   11,    0,   12,    0,    0,
    0,    0,   13,    0,   47,    2,    0,    0,    3,    4,
    5,    6,    7,    8,    9,  239,    0,   10,   11,    0,
   12,    0,    0,   47,   85,   13,    0,    3,    4,    5,
    6,    7,    8,    9,  295,    0,   10,   11,    0,    0,
    0,    0,    0,    0,   13,    0,    0,    0,    0,    0,
   47,   85,  300,    0,    3,    4,    5,    6,    7,    8,
    9,    0,    0,   10,   11,    0,    0,    0,    0,    2,
  239,   13,    3,    4,    5,    6,    7,    8,    9,    0,
    0,   10,   11,    0,   12,    0,    0,    2,  300,   13,
    3,    4,    5,    6,    7,    8,    9,    0,    0,   10,
   11,    0,   12,  305,    0,    0,    2,   13,    0,    3,
    4,    5,    6,    7,    8,    9,    0,    0,   10,   11,
    0,   12,    0,    0,    2,    0,   13,    3,    4,    5,
    6,    7,    8,    9,    0,    0,   10,   11,    0,   12,
    0,    0,    0,    0,   13,    0,    0,   85,    0,    0,
    3,    4,    5,    6,    7,    8,    9,    0,    0,   10,
   11,    0,    0,    0,    0,    0,   85,   13,    0,    3,
    4,    5,    6,    7,    8,    9,    0,    0,   10,   11,
    0,    0,    0,    0,   85,    0,   13,    3,    4,    5,
    6,    7,    8,    9,    0,    0,   10,   11,    0,    0,
    0,    0,  186,    0,   13,    3,    4,    5,    6,    7,
    8,    9,    0,    0,   10,   11,    0,    0,    0,    0,
  186,    0,   13,    3,    4,    5,    6,    7,    8,    9,
    0,    0,   10,   11,    0,  186,    0,    0,    3,    0,
   13,    0,    0,    0,    9,    2,    0,    0,    3,    4,
    5,    6,    7,    8,    9,   54,    0,   10,   11,    0,
   12,  207,  210,  211,    2,   13,    0,    3,    0,    0,
    0,    0,    0,    9,  229,  230,  231,  232,    0,   12,
  235,  236,    0,    0,   54,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         33,
    0,    1,   40,   17,   46,   40,   62,   33,   46,   17,
   58,   46,  123,  187,   14,  125,  105,   44,   58,   44,
  187,   14,  248,   61,  250,  114,   61,   44,   35,  123,
  123,   44,   59,   33,   59,  123,   62,   43,   42,   45,
   58,  277,   45,   47,   40,   43,   88,   45,   48,   45,
  100,  256,  125,  125,   60,   48,   62,  125,  277,   93,
   94,  256,  125,  125,  125,  125,  100,   93,   94,   83,
   85,   40,   62,  123,  100,   83,  271,  256,  256,  121,
   40,  169,  277,   62,   92,   45,   41,  261,   44,  263,
   45,  277,   92,   93,   94,  267,  268,  123,  277,  277,
  100,   44,  109,  110,   58,  139,  113,   46,  246,   40,
  248,   41,  168,  139,   45,   44,   41,   42,   43,   44,
   45,  347,   47,  123,  248,   40,  250,  271,  267,  268,
  273,  274,  182,  268,  184,   60,   40,   62,  277,  139,
  256,   45,  168,  169,  277,  256,  258,  259,  182,   41,
  184,  262,  262,  187,  168,  169,  182,   41,  184,   41,
  256,  187,  256,  256,    0,    1,   44,  175,  262,  262,
  262,  186,   45,  181,  216,  175,  258,  259,   14,   44,
   41,  181,  182,  217,  184,  359,   44,  187,  196,  262,
  262,  217,  359,  200,  262,  202,  196,   33,  123,  262,
  262,  262,  262,   41,   41,   43,  277,   45,  256,   44,
  244,   41,   48,   43,   44,   45,  256,  217,  244,  245,
  246,   40,  248,  279,  250,   59,   45,  261,   40,  263,
   60,  123,   62,   45,  256,  261,   41,  263,  256,  123,
  278,  123,   45,  278,  244,  245,  280,  261,   41,  263,
  277,  293,  277,  279,  280,  281,   92,   93,   94,  277,
  277,  269,  123,  276,   41,   41,   43,   44,   45,  269,
  273,  274,  275,  279,  280,  281,  282,  273,  274,  275,
  280,  281,   45,   60,   41,   62,  123,  123,  256,  279,
  324,  333,  268,  123,  263,  264,  265,  256,  324,  325,
  279,  256,  256,  139,  256,   41,  348,   43,   44,   45,
  324,  325,  266,  273,  274,  275,  277,  277,  273,  274,
  275,  347,  277,  277,   60,  359,   62,   41,  362,  277,
  123,   41,  257,  359,  125,  260,  362,  363,   44,  175,
   44,  266,  273,  274,  275,  181,  123,  272,  263,  264,
  265,  187,  277,   41,  279,  280,  281,  282,   44,  359,
  196,   44,  362,  363,   44,  257,   32,   44,  260,  273,
  274,  275,   44,  257,  266,  257,  260,   41,  260,   44,
  272,  217,  266,  256,  266,  277,   44,  123,  272,   59,
  272,  258,  259,  277,   44,  277,  257,   93,   94,  260,
  273,  274,  275,   44,  277,  266,  258,  259,  244,  245,
  125,  272,   44,   34,  258,  259,  277,  123,  109,  110,
  257,  102,  103,  260,   90,  133,  134,  257,   44,  266,
  260,   44,   41,  269,   59,  272,  266,  137,  138,  105,
  277,  125,  272,  123,  280,  281,  125,  277,  114,  279,
  280,  281,  282,   44,  273,  274,  275,  125,  125,   34,
   35,  273,  274,  275,  257,  123,  125,  260,   44,  259,
  273,  274,  275,  266,  277,   43,   44,   45,  259,  272,
  257,  102,  103,  260,  277,  259,  107,  123,  259,  266,
   43,   44,   45,  256,  259,  272,   59,  259,  259,  256,
  277,  259,  279,  280,  281,  282,  263,  264,  265,  259,
  273,  274,  275,   44,   44,   44,   44,    2,   44,   44,
   44,  257,  123,   44,  260,   41,   41,  102,  103,    1,
  266,    0,  107,    0,  109,  110,  272,    0,  113,   44,
  123,  277,   44,  279,  280,  281,  282,   29,  175,  123,
  256,  257,   55,   88,  260,  261,  262,  263,  264,  265,
  266,   -1,   -1,  269,  270,  123,  272,   -1,   -1,   -1,
   55,  277,  193,   -1,  195,   -1,  256,  257,   -1,   -1,
  260,  261,  262,  263,  264,  265,  266,  123,   -1,  269,
  270,   -1,  272,   -1,   -1,   -1,   -1,  277,  256,  257,
   85,   -1,  260,  261,  262,  263,  264,  265,  266,   -1,
   -1,  269,  270,  123,  272,  125,   -1,   -1,  193,  277,
  195,  257,   -1,   -1,  260,  200,   -1,  202,   -1,   -1,
  266,  116,  117,  118,  123,   -1,  125,   -1,   -1,   -1,
   -1,  277,   -1,   -1,  129,  130,  131,  132,  133,  134,
  135,  136,  137,  138,  125,  256,  257,   -1,   -1,  260,
  261,  262,  263,  264,  265,  266,   -1,   -1,  269,  270,
  123,  272,  125,  256,  257,   -1,  277,  260,  261,  262,
  263,  264,  265,  266,  123,  123,  269,  270,   -1,  272,
   -1,   -1,  123,   -1,  277,   -1,   -1,   -1,  256,  257,
   -1,  186,  260,  261,  262,  263,  264,  265,  266,   -1,
  123,  269,  270,   -1,  272,   -1,   -1,  123,  123,  277,
  256,  257,   -1,   -1,  260,  261,  262,  263,  264,  265,
  266,  123,   -1,  269,  270,   -1,  272,   -1,   -1,  123,
   -1,  277,   -1,   -1,   -1,  125,   -1,  257,   -1,   -1,
  260,  261,  262,  263,  264,  265,  266,   -1,   -1,  269,
  270,   -1,   -1,  125,   -1,   -1,   -1,  277,  257,   -1,
   -1,  260,  261,  262,  263,  264,  265,  266,   -1,   -1,
  269,  270,  125,   -1,   -1,  256,  257,   -1,  277,  260,
  261,  262,  263,  264,  265,  266,   -1,   -1,  269,  270,
  125,  272,   -1,   -1,  257,   -1,  277,  260,  261,  262,
  263,  264,  265,  266,   -1,   -1,  269,  270,  257,  257,
   -1,  260,  260,  125,  277,   -1,  257,  266,  266,  260,
   -1,   -1,   -1,  272,  272,  266,   -1,   -1,  277,  277,
   -1,  272,  125,   -1,  257,   -1,  277,  260,   -1,   -1,
   -1,  257,  257,  266,  260,  260,   -1,   -1,   -1,  272,
  266,  266,   -1,   -1,  277,  257,  272,  272,  260,  125,
   -1,  277,  277,  257,  266,   -1,  260,  257,   -1,   -1,
  260,   -1,  266,   -1,   -1,  277,  266,  125,   -1,   -1,
   -1,   -1,  272,  277,  256,  257,   -1,  277,  260,  261,
  262,  263,  264,  265,  266,  125,   -1,  269,  270,   -1,
  272,   -1,   -1,  256,  257,  277,   -1,  260,  261,  262,
  263,  264,  265,  266,  125,   -1,  269,  270,   -1,  272,
   -1,  256,  257,   -1,  277,  260,  261,  262,  263,  264,
  265,  266,  125,   -1,  269,  270,   -1,  272,   -1,   -1,
   -1,   -1,  277,   -1,  256,  257,   -1,   -1,  260,  261,
  262,  263,  264,  265,  266,  125,   -1,  269,  270,   -1,
  272,   -1,   -1,  256,  257,  277,   -1,  260,  261,  262,
  263,  264,  265,  266,  125,   -1,  269,  270,   -1,   -1,
   -1,   -1,   -1,   -1,  277,   -1,   -1,   -1,   -1,   -1,
  256,  257,  125,   -1,  260,  261,  262,  263,  264,  265,
  266,   -1,   -1,  269,  270,   -1,   -1,   -1,   -1,  257,
  125,  277,  260,  261,  262,  263,  264,  265,  266,   -1,
   -1,  269,  270,   -1,  272,   -1,   -1,  257,  125,  277,
  260,  261,  262,  263,  264,  265,  266,   -1,   -1,  269,
  270,   -1,  272,  125,   -1,   -1,  257,  277,   -1,  260,
  261,  262,  263,  264,  265,  266,   -1,   -1,  269,  270,
   -1,  272,   -1,   -1,  257,   -1,  277,  260,  261,  262,
  263,  264,  265,  266,   -1,   -1,  269,  270,   -1,  272,
   -1,   -1,   -1,   -1,  277,   -1,   -1,  257,   -1,   -1,
  260,  261,  262,  263,  264,  265,  266,   -1,   -1,  269,
  270,   -1,   -1,   -1,   -1,   -1,  257,  277,   -1,  260,
  261,  262,  263,  264,  265,  266,   -1,   -1,  269,  270,
   -1,   -1,   -1,   -1,  257,   -1,  277,  260,  261,  262,
  263,  264,  265,  266,   -1,   -1,  269,  270,   -1,   -1,
   -1,   -1,  257,   -1,  277,  260,  261,  262,  263,  264,
  265,  266,   -1,   -1,  269,  270,   -1,   -1,   -1,   -1,
  257,   -1,  277,  260,  261,  262,  263,  264,  265,  266,
   -1,   -1,  269,  270,   -1,  257,   -1,   -1,  260,   -1,
  277,   -1,   -1,   -1,  266,  257,   -1,   -1,  260,  261,
  262,  263,  264,  265,  266,  277,   -1,  269,  270,   -1,
  272,  116,  117,  118,  257,  277,   -1,  260,   -1,   -1,
   -1,   -1,   -1,  266,  129,  130,  131,  132,   -1,  272,
  135,  136,   -1,   -1,  277,
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
"asignacion : referencia '=' expresion_aritmetica ','",
"asignacion : referencia ASIGNADOR_MENOS_IGUAL expresion_aritmetica ','",
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
"invocacion_funcion : referencia parametro_real ','",
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

//#line 411 "Gramatica.y"

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
    else
        analizador_semantico.registrarTipoConstante("INT", constante);
}

//#line 854 "Parser.java"
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
//#line 12 "Gramatica.y"
{System.out.print("(PROGRAMA) "); analizador_semantico.actualizarAmbitoActual("-"); analizador_semantico.verificarReferenciasVariablesEnAsignaciones();}
break;
case 2:
//#line 13 "Gramatica.y"
{System.out.print("(PROGRAMA VACIO) ");}
break;
case 3:
//#line 14 "Gramatica.y"
{System.out.print("(PROGRAMA) "); agregarError("ERROR: Solo puede haber sentencias dentro de las llaves del programa"); analizador_semantico.actualizarAmbitoActual("-"); analizador_semantico.verificarReferenciasVariablesEnAsignaciones();}
break;
case 4:
//#line 15 "Gramatica.y"
{System.out.print("(PROGRAMA VACIO) "); agregarError("ERROR: Solo puede haber sentencias dentro de las llaves del programa"); analizador_semantico.actualizarAmbitoActual("-");}
break;
case 5:
//#line 16 "Gramatica.y"
{System.out.print("(PROGRAMA VACIO) "); agregarError("ERROR: Solo puede haber sentencias dentro de las llaves del programa"); analizador_semantico.actualizarAmbitoActual("-");}
break;
case 6:
//#line 17 "Gramatica.y"
{System.out.print("(PROGRAMA) "); agregarError("ERROR: Solo puede haber sentencias dentro de las llaves del programa"); analizador_semantico.actualizarAmbitoActual("-"); analizador_semantico.verificarReferenciasVariablesEnAsignaciones();}
break;
case 7:
//#line 18 "Gramatica.y"
{System.out.print("(PROGRAMA VACIO) "); agregarError("ERROR: Solo puede haber sentencias dentro de las llaves del programa"); analizador_semantico.actualizarAmbitoActual("-");}
break;
case 9:
//#line 23 "Gramatica.y"
{agregarError("ERROR: Bloque de sentencias de programa invalido, el formato correcto es: '{' lista_sentencias '}' con cada sentencia delimitada por ','");}
break;
case 12:
//#line 29 "Gramatica.y"
{agregarError("ERROR: El programa principal no puede contener de manera directa sentencias que impliquen posibles retornos");}
break;
case 13:
//#line 30 "Gramatica.y"
{agregarError("ERROR: El programa principal no puede contener de manera directa sentencias que impliquen retornos");}
break;
case 16:
//#line 36 "Gramatica.y"
{agregarError("ERROR: Sentencia invalida sintacticamente");}
break;
case 18:
//#line 41 "Gramatica.y"
{agregarError("ERROR: Bloque de sentencias declarativas invalido, el formato correcto es: '{' lista_sentencias_declarativas '}' con cada sentencia delimitada por ','");}
break;
case 19:
//#line 42 "Gramatica.y"
{agregarError("ERROR: Bloque de sentencias declarativas invalido, el formato correcto es: '{' lista_sentencias_declarativas '}' con cada sentencia delimitada por ','");}
break;
case 22:
//#line 48 "Gramatica.y"
{agregarError("ERROR: No puede haber sentencias ejecutables, en este bloque solo se aceptan sentencias declarativas");}
break;
case 23:
//#line 49 "Gramatica.y"
{agregarError("ERROR: No puede haber sentencias ejecutables, en este bloque solo se aceptan sentencias declarativas");}
break;
case 24:
//#line 50 "Gramatica.y"
{agregarError("ERROR: No puede haber sentencias ejecutables, en este bloque solo se aceptan sentencias declarativas");}
break;
case 25:
//#line 51 "Gramatica.y"
{agregarError("ERROR: No puede haber sentencias ejecutables, en este bloque solo se aceptan sentencias declarativas");}
break;
case 26:
//#line 52 "Gramatica.y"
{agregarError("ERROR: No puede haber sentencias ejecutables, en este bloque solo se aceptan sentencias declarativas");}
break;
case 27:
//#line 53 "Gramatica.y"
{agregarError("ERROR: No puede haber sentencias ejecutables, en este bloque solo se aceptan sentencias declarativas");}
break;
case 34:
//#line 65 "Gramatica.y"
{System.out.print("(DECLARACION DE LISTA DE VARIABLES NUMERICAS) "); analizador_semantico.registrarTipoID(val_peek(2).sval, val_peek(1).sval);}
break;
case 35:
//#line 66 "Gramatica.y"
{System.out.print("(DECLARACION DE LISTA DE VARIABLES DEL TIPO DE UNA CLASE) "); analizador_semantico.eliminarEntradaOriginalID(val_peek(2).sval); analizador_semantico.registrarTipoID(val_peek(2).sval, val_peek(1).sval);}
break;
case 36:
//#line 70 "Gramatica.y"
{yyval.sval = "INT";}
break;
case 37:
//#line 71 "Gramatica.y"
{yyval.sval = "ULONG";}
break;
case 38:
//#line 72 "Gramatica.y"
{yyval.sval = "DOUBLE";}
break;
case 39:
//#line 76 "Gramatica.y"
{analizador_semantico.registrarAmbitoUsoID(val_peek(0).sval, "nombre_variable"); yyval.sval = val_peek(2).sval + ";" + val_peek(0).sval;}
break;
case 40:
//#line 77 "Gramatica.y"
{analizador_semantico.registrarAmbitoUsoID(val_peek(0).sval, "nombre_variable"); yyval.sval = val_peek(0).sval;}
break;
case 41:
//#line 78 "Gramatica.y"
{agregarError("ERROR: Declaracion invalida de variable en lista de variables, falta el ';'"); analizador_semantico.registrarAmbitoUsoID(val_peek(0).sval, "nombre_variable"); yyval.sval = val_peek(1).sval + ";" + val_peek(0).sval;}
break;
case 42:
//#line 82 "Gramatica.y"
{System.out.print("(DECLARACION DE REFERENCIA A CLASE) "); analizador_semantico.registrarAmbitoUsoID(val_peek(1).sval, "referencia_clase");}
break;
case 46:
//#line 92 "Gramatica.y"
{agregarError("ERROR: Bloque de declaraciones de prototipos invalido, el formato correcto es: '{' lista_declaraciones_prototipos '}' con cada prototipo delimitado por ','");}
break;
case 47:
//#line 93 "Gramatica.y"
{agregarError("ERROR: Bloque de declaraciones de prototipos invalido, el formato correcto es: '{' lista_declaraciones_prototipos '}' con cada prototipo delimitado por ','");}
break;
case 50:
//#line 102 "Gramatica.y"
{System.out.print("(DECLARACION DE PROTOTIPO DE FUNCION) "); analizador_semantico.verificarPosibleImplementacionFuncionInterfaz(val_peek(2).sval); analizador_semantico.actualizarAmbitoActual("-"); analizador_semantico.definirUsoPrototipo(val_peek(2).sval); analizador_semantico.verificarCantidadAnidamientos(val_peek(2).sval);}
break;
case 51:
//#line 106 "Gramatica.y"
{analizador_semantico.registrarAmbitoUsoID(val_peek(0).sval, "nombre_funcion"); analizador_semantico.actualizarAmbitoActual(val_peek(0).sval); yyval.sval = val_peek(0).sval;}
break;
case 52:
//#line 107 "Gramatica.y"
{agregarError("ERROR: Encabezado de funcion invalido, el formato correcto es: VOID ID"); analizador_semantico.marcarAmbitoInvalido(":"); yyval.sval = null;}
break;
case 53:
//#line 111 "Gramatica.y"
{analizador_semantico.registrarAmbitoUsoID(val_peek(1).sval, "nombre_parametro_formal"); analizador_semantico.eliminarEntradaOriginalID(val_peek(1).sval); analizador_semantico.registrarTipoID(val_peek(2).sval, val_peek(1).sval); analizador_semantico.chequearParametroFuncionIMPL(val_peek(1).sval, val_peek(2).sval);}
break;
case 54:
//#line 112 "Gramatica.y"
{analizador_semantico.chequearParametroFuncionIMPL(null, null);}
break;
case 55:
//#line 113 "Gramatica.y"
{agregarError("ERROR: Parametro invalido, el formato correcto es: '(' tipo_numerico ID ')' o '(' ')'"); analizador_semantico.registrarAmbitoUsoID(val_peek(1).sval, "nombre_parametro_formal"); analizador_semantico.eliminarEntradaOriginalID(val_peek(1).sval); analizador_semantico.registrarTipoID(val_peek(2).sval, val_peek(1).sval); analizador_semantico.chequearParametroFuncionIMPL(val_peek(1).sval, val_peek(2).sval);}
break;
case 56:
//#line 114 "Gramatica.y"
{agregarError("ERROR: Parametro invalido, el formato correcto es: '(' tipo_numerico ID ')' o '(' ')'"); analizador_semantico.registrarAmbitoUsoID(val_peek(0).sval, "nombre_parametro_formal"); analizador_semantico.eliminarEntradaOriginalID(val_peek(0).sval); analizador_semantico.registrarTipoID(val_peek(1).sval, val_peek(0).sval); analizador_semantico.chequearParametroFuncionIMPL(val_peek(0).sval, val_peek(1).sval);}
break;
case 57:
//#line 115 "Gramatica.y"
{agregarError("ERROR: Parametro invalido, el formato correcto es: '(' tipo_numerico ID ')' o '(' ')'"); analizador_semantico.actualizarAmbitoActual("-") ; analizador_semantico.marcarAmbitoInvalido(":"); analizador_semantico.chequearParametroFuncionIMPL(null, null);}
break;
case 59:
//#line 120 "Gramatica.y"
{agregarError("ERROR: Bloque de declaraciones de funciones invalido, el formato correcto es: '{' lista_declaraciones_funciones '}' con cada funcion delimitada por ','");}
break;
case 60:
//#line 121 "Gramatica.y"
{agregarError("ERROR: Bloque de declaraciones de funciones invalido, el formato correcto es: '{' lista_declaraciones_funciones '}' con cada funcion delimitada por ','");}
break;
case 63:
//#line 130 "Gramatica.y"
{System.out.print("(DECLARACION DE FUNCION) "); analizador_semantico.verificarPosibleImplementacionFuncionInterfaz(val_peek(3).sval); analizador_semantico.actualizarAmbitoActual("-"); analizador_semantico.verificarCantidadAnidamientos(val_peek(3).sval);}
break;
case 66:
//#line 136 "Gramatica.y"
{agregarError("ERROR: Cuerpo de funcion sin llave de apertura");}
break;
case 67:
//#line 137 "Gramatica.y"
{agregarError("ERROR: Cuerpo de funcion sin llave de cierre");}
break;
case 68:
//#line 138 "Gramatica.y"
{agregarError("ERROR: Cuerpo de funcion sin llave de cierre");}
break;
case 69:
//#line 139 "Gramatica.y"
{agregarError("ERROR: Cuerpo de funcion sin llave de apertura y no se garantiza retorno para todas las ramas del mismo");}
break;
case 70:
//#line 140 "Gramatica.y"
{agregarError("ERROR: Cuerpo de funcion sin llave de apertura");}
break;
case 71:
//#line 141 "Gramatica.y"
{agregarError("ERROR: No se garantiza retorno para todas las ramas del cuerpo de funcion");}
break;
case 72:
//#line 142 "Gramatica.y"
{agregarError("ERROR: Cuerpo de funcion sin retorno");}
break;
case 77:
//#line 153 "Gramatica.y"
{System.out.print("(DECLARACION DE CLASE) "); analizador_semantico.chequearImplementacionTotalInterfaz(val_peek(1).sval); analizador_semantico.actualizarAmbitoActual("()");}
break;
case 78:
//#line 157 "Gramatica.y"
{analizador_semantico.registrarAmbitoUsoID(val_peek(0).sval, "nombre_clase"); analizador_semantico.actualizarAmbitoActual("(" + val_peek(0).sval + ")"); yyval.sval = val_peek(0).sval;}
break;
case 79:
//#line 158 "Gramatica.y"
{analizador_semantico.registrarAmbitoUsoID(val_peek(2).sval, "nombre_clase"); analizador_semantico.registrarInterfazImplementada(val_peek(2).sval, val_peek(0).sval); analizador_semantico.actualizarAmbitoActual("(" + val_peek(2).sval + ")"); analizador_semantico.eliminarEntradaOriginalID(val_peek(0).sval); yyval.sval = val_peek(2).sval;}
break;
case 80:
//#line 159 "Gramatica.y"
{agregarError("ERROR: Encabezado de clase invalido, los formatos correctos son: CLASS ID y CLASS ID IMPLEMENT ID"); analizador_semantico.marcarAmbitoInvalido("()"); yyval.sval = null;}
break;
case 81:
//#line 160 "Gramatica.y"
{agregarError("ERROR: Encabezado de clase invalido, los formatos correctos son: CLASS ID y CLASS ID IMPLEMENT ID"); analizador_semantico.marcarAmbitoInvalido("()"); yyval.sval = null;}
break;
case 82:
//#line 161 "Gramatica.y"
{agregarError("ERROR: Encabezado de clase invalido, los formatos correctos son: CLASS ID y CLASS ID IMPLEMENT ID"); analizador_semantico.registrarAmbitoUsoID(val_peek(2).sval, "nombre_clase"); analizador_semantico.actualizarAmbitoActual("(" + val_peek(2).sval + ")"); yyval.sval = val_peek(2).sval;}
break;
case 83:
//#line 162 "Gramatica.y"
{agregarError("ERROR: Encabezado de clase invalido, los formatos correctos son: CLASS ID y CLASS ID IMPLEMENT ID"); analizador_semantico.eliminarEntradaOriginalID(val_peek(1).sval); analizador_semantico.marcarAmbitoInvalido("()"); yyval.sval = null;}
break;
case 86:
//#line 171 "Gramatica.y"
{System.out.print("(DECLARACION DE CLAUSULA IMPL) "); analizador_semantico.actualizarAmbitoActual("<>");}
break;
case 87:
//#line 175 "Gramatica.y"
{analizador_semantico.eliminarEntradaOriginalID(val_peek(1).sval); analizador_semantico.chequearValidezClausulaIMPL(val_peek(1).sval); analizador_semantico.actualizarAmbitoActual("<" + val_peek(1).sval + ">");}
break;
case 88:
//#line 176 "Gramatica.y"
{agregarError("ERROR: Encabezado de clausula IMPL invalido, el formato correcto es: IMPL FOR ID ':'"); analizador_semantico.marcarAmbitoInvalido("<>");}
break;
case 89:
//#line 177 "Gramatica.y"
{agregarError("ERROR: Encabezado de clausula IMPL invalido, el formato correcto es: IMPL FOR ID ':'"); analizador_semantico.eliminarEntradaOriginalID(val_peek(2).sval); analizador_semantico.chequearValidezClausulaIMPL(val_peek(2).sval); analizador_semantico.actualizarAmbitoActual("<" + val_peek(2).sval + ">");}
break;
case 90:
//#line 178 "Gramatica.y"
{agregarError("ERROR: Encabezado de clausula IMPL invalido, el formato correcto es: IMPL FOR ID ':'"); analizador_semantico.marcarAmbitoInvalido("<>");}
break;
case 91:
//#line 179 "Gramatica.y"
{agregarError("ERROR: Encabezado de clausula IMPL invalido, el formato correcto es: IMPL FOR ID ':'"); analizador_semantico.eliminarEntradaOriginalID(val_peek(1).sval); analizador_semantico.chequearValidezClausulaIMPL(val_peek(1).sval); analizador_semantico.actualizarAmbitoActual("<" + val_peek(1).sval + ">");}
break;
case 92:
//#line 180 "Gramatica.y"
{agregarError("ERROR: Encabezado de clausula IMPL invalido, el formato correcto es: IMPL FOR ID ':'"); analizador_semantico.marcarAmbitoInvalido("<>");}
break;
case 93:
//#line 181 "Gramatica.y"
{agregarError("ERROR: Encabezado de clausula IMPL invalido, el formato correcto es: IMPL FOR ID ':'"); analizador_semantico.eliminarEntradaOriginalID(val_peek(1).sval); analizador_semantico.chequearValidezClausulaIMPL(val_peek(1).sval); analizador_semantico.actualizarAmbitoActual("<" + val_peek(1).sval + ">");}
break;
case 94:
//#line 182 "Gramatica.y"
{agregarError("ERROR: Encabezado de clausula IMPL invalido, el formato correcto es: IMPL FOR ID ':'"); analizador_semantico.marcarAmbitoInvalido("<>");}
break;
case 97:
//#line 191 "Gramatica.y"
{System.out.print("(DECLARACION DE INTERFAZ) "); analizador_semantico.registrarCantidadPrototiposInterfaz(val_peek(1).sval); analizador_semantico.actualizarAmbitoActual("[]");}
break;
case 98:
//#line 195 "Gramatica.y"
{analizador_semantico.registrarAmbitoUsoID(val_peek(0).sval, "nombre_interfaz"); analizador_semantico.actualizarAmbitoActual("[" + val_peek(0).sval + "]"); yyval.sval = val_peek(0).sval;}
break;
case 99:
//#line 196 "Gramatica.y"
{agregarError("ERROR: Encabezado de interfaz invalido, el formato correcto es: INTERFACE ID"); analizador_semantico.marcarAmbitoInvalido("[]");}
break;
case 115:
//#line 233 "Gramatica.y"
{agregarError("ERROR: No puede haber sentencias declarativas, en este bloque solo se aceptan sentencias ejecutables");}
break;
case 116:
//#line 234 "Gramatica.y"
{agregarError("ERROR: No puede haber sentencias declarativas, en este bloque solo se aceptan sentencias ejecutables");}
break;
case 125:
//#line 255 "Gramatica.y"
{System.out.print("(ASIGNACION USANDO OPERADOR DE IGUAL) "); analizador_semantico.verificarReferenciaValida(val_peek(3).sval, false); analizador_semantico.registrarReferenciasLadoDerechoAsignacion(val_peek(1).sval);}
break;
case 126:
//#line 256 "Gramatica.y"
{System.out.print("(ASIGNACION USANDO OPERADOR DE MENOS IGUAL) "); analizador_semantico.verificarReferenciaValida(val_peek(3).sval, false); analizador_semantico.registrarReferenciasLadoDerechoAsignacion(val_peek(1).sval);}
break;
case 127:
//#line 260 "Gramatica.y"
{yyval.sval = val_peek(2).sval + " " + "+" + " " + val_peek(0).sval;}
break;
case 128:
//#line 261 "Gramatica.y"
{yyval.sval = val_peek(2).sval + " " + "-" + " " + val_peek(0).sval;}
break;
case 129:
//#line 262 "Gramatica.y"
{yyval.sval = val_peek(0).sval;}
break;
case 130:
//#line 266 "Gramatica.y"
{yyval.sval = val_peek(2).sval + " " + "*" + " " + val_peek(0).sval;}
break;
case 131:
//#line 267 "Gramatica.y"
{yyval.sval = val_peek(2).sval + " " + "/" + " " + val_peek(0).sval;}
break;
case 132:
//#line 268 "Gramatica.y"
{yyval.sval = val_peek(0).sval;}
break;
case 133:
//#line 272 "Gramatica.y"
{yyval.sval = analizador_semantico.verificarReferenciaValida(val_peek(0).sval, false);}
break;
case 134:
//#line 273 "Gramatica.y"
{yyval.sval = val_peek(0).sval;}
break;
case 135:
//#line 277 "Gramatica.y"
{verificarRango(val_peek(0).sval); analizador_semantico.chequearValidezSimbolo(val_peek(0).sval); yyval.sval = val_peek(0).sval;}
break;
case 136:
//#line 278 "Gramatica.y"
{analizador_lexico.constanteNegativaDetectada(val_peek(0).sval); analizador_semantico.chequearValidezSimbolo("-" + val_peek(0).sval); analizador_semantico.registrarTipoConstante("INT", "-" + val_peek(0).sval); yyval.sval = "-" + val_peek(0).sval;}
break;
case 137:
//#line 279 "Gramatica.y"
{analizador_semantico.registrarTipoConstante("ULONG", val_peek(0).sval); analizador_semantico.chequearValidezSimbolo(val_peek(0).sval); yyval.sval = val_peek(0).sval;}
break;
case 138:
//#line 280 "Gramatica.y"
{analizador_semantico.registrarTipoConstante("DOUBLE", val_peek(0).sval); analizador_semantico.chequearValidezSimbolo(val_peek(0).sval); yyval.sval = val_peek(0).sval;}
break;
case 139:
//#line 281 "Gramatica.y"
{analizador_lexico.constanteNegativaDetectada(val_peek(0).sval); analizador_semantico.chequearValidezSimbolo("-" + val_peek(0).sval); analizador_semantico.registrarTipoConstante("DOUBLE", "-" + val_peek(0).sval); yyval.sval = "-" + val_peek(0).sval;}
break;
case 140:
//#line 285 "Gramatica.y"
{System.out.print("(INVOCACION A FUNCION) "); analizador_semantico.verificarReferenciaValida(val_peek(2).sval, true);}
break;
case 141:
//#line 289 "Gramatica.y"
{analizador_semantico.eliminarEntradaOriginalID(val_peek(0).sval); yyval.sval = val_peek(2).sval + "." + val_peek(0).sval;}
break;
case 142:
//#line 290 "Gramatica.y"
{analizador_semantico.eliminarEntradaOriginalID(val_peek(0).sval); yyval.sval = val_peek(0).sval;}
break;
case 145:
//#line 296 "Gramatica.y"
{agregarError("ERROR: Parametro real invalido, el formato correcto es: '(' expresion_aritmetica ')' o '(' ')' ");}
break;
case 146:
//#line 300 "Gramatica.y"
{System.out.print("(CLAUSULA DE SELECCION IF SIN POSIBLES RETORNOS Y SIN ELSE) ");}
break;
case 147:
//#line 301 "Gramatica.y"
{System.out.print("(CLAUSULA DE SELECCION IF SIN POSIBLES RETORNOS Y ELSE SIN POSIBLES RETORNOS) ");}
break;
case 148:
//#line 305 "Gramatica.y"
{System.out.print("(CLAUSULA DE SELECCION IF DE RETORNO PARCIAL Y SIN ELSE) ");}
break;
case 149:
//#line 306 "Gramatica.y"
{System.out.print("(CLAUSULA DE SELECCION IF DE RETORNO PARCIAL Y ELSE SIN POSIBLES RETORNOS) ");}
break;
case 150:
//#line 307 "Gramatica.y"
{System.out.print("(CLAUSULA DE SELECCION IF SIN POSIBLES RETORNOS Y ELSE DE RETORNO PARCIAL) ");}
break;
case 151:
//#line 308 "Gramatica.y"
{System.out.print("(CLAUSULA DE SELECCION IF DE RETORNO PARCIAL Y ELSE DE RETORNO PARCIAL) ");}
break;
case 152:
//#line 309 "Gramatica.y"
{System.out.print("(CLAUSULA DE SELECCION IF DE RETORNO COMPLETO Y SIN ELSE) ");}
break;
case 153:
//#line 310 "Gramatica.y"
{System.out.print("(CLAUSULA DE SELECCION IF DE RETORNO PARCIAL Y ELSE DE RETORNO COMPLETO) ");}
break;
case 154:
//#line 311 "Gramatica.y"
{System.out.print("(CLAUSULA DE SELECCION IF SIN POSIBLES RETORNOS Y ELSE DE RETORNO COMPLETO) ");}
break;
case 155:
//#line 312 "Gramatica.y"
{System.out.print("(CLAUSULA DE SELECCION IF DE RETORNO COMPLETO Y ELSE DE RETORNO PARCIAL) ");}
break;
case 156:
//#line 313 "Gramatica.y"
{System.out.print("(CLAUSULA DE SELECCION IF DE RETORNO COMPLETO Y ELSE SIN POSIBLES RETORNOS) ");}
break;
case 157:
//#line 316 "Gramatica.y"
{System.out.print("(CLAUSULA DE SELECCION IF ELSE DE RETORNO COMPLETO) ");}
break;
case 169:
//#line 349 "Gramatica.y"
{agregarError("ERROR: Condicion de clausula IF invalida, el formato correcto es: '(' comparacion ')'");}
break;
case 170:
//#line 350 "Gramatica.y"
{agregarError("ERROR: Condicion de clausula IF invalida, el formato correcto es: '(' comparacion ')'");}
break;
case 171:
//#line 351 "Gramatica.y"
{agregarError("ERROR: Condicion de clausula IF invalida, el formato correcto es: '(' comparacion ')'");}
break;
case 172:
//#line 352 "Gramatica.y"
{agregarError("ERROR: Condicion de clausula IF invalida, el formato correcto es: '(' comparacion ')'");}
break;
case 179:
//#line 365 "Gramatica.y"
{System.out.print("(SALIDA DE MENSAJE) "); analizador_semantico.chequearValidezSimbolo(val_peek(1).sval);}
break;
case 180:
//#line 366 "Gramatica.y"
{System.out.print("(SALIDA DE MENSAJE) "); agregarError("ERROR: Salida de mensaje invalida, el formato correcto es: PRINT CADENA_CARACTERES ','");}
break;
case 181:
//#line 370 "Gramatica.y"
{System.out.print("(SENTENCIA FOR) ");}
break;
case 185:
//#line 380 "Gramatica.y"
{System.out.print("(SENTENCIA FOR CON SENTENCIA EJECUTABLE DE RETORNO PARCIAL) ");}
break;
case 190:
//#line 391 "Gramatica.y"
{analizador_semantico.eliminarEntradaOriginalID(val_peek(2).sval); analizador_semantico.verificarReferenciaValida(val_peek(2).sval, false); analizador_semantico.verificarValidezVariableControlFor(val_peek(2).sval);}
break;
case 191:
//#line 392 "Gramatica.y"
{agregarError("ERROR: Encabezado de bucle for invalido, el formato correcto es: FOR ID IN RANGE"); analizador_semantico.eliminarEntradaOriginalID(val_peek(1).sval); analizador_semantico.verificarReferenciaValida(val_peek(1).sval, false); analizador_semantico.verificarValidezVariableControlFor(val_peek(1).sval);}
break;
case 192:
//#line 393 "Gramatica.y"
{agregarError("ERROR: Encabezado de bucle for invalido, el formato correcto es: FOR ID IN RANGE"); analizador_semantico.eliminarEntradaOriginalID(val_peek(1).sval); analizador_semantico.verificarReferenciaValida(val_peek(1).sval, false); analizador_semantico.verificarValidezVariableControlFor(val_peek(1).sval);}
break;
case 193:
//#line 394 "Gramatica.y"
{agregarError("ERROR: Encabezado de bucle for invalido, el formato correcto es: FOR ID IN RANGE"); analizador_semantico.eliminarEntradaOriginalID(val_peek(0).sval); analizador_semantico.verificarReferenciaValida(val_peek(0).sval, false); analizador_semantico.verificarValidezVariableControlFor(val_peek(0).sval);}
break;
case 194:
//#line 395 "Gramatica.y"
{agregarError("ERROR: Encabezado de bucle for invalido, el formato correcto es: FOR ID IN RANGE");}
break;
case 195:
//#line 396 "Gramatica.y"
{agregarError("ERROR: Encabezado de bucle for invalido, el formato correcto es: FOR ID IN RANGE");}
break;
case 196:
//#line 397 "Gramatica.y"
{agregarError("ERROR: Encabezado de bucle for invalido, el formato correcto es: FOR ID IN RANGE");}
break;
case 197:
//#line 398 "Gramatica.y"
{agregarError("ERROR: Encabezado de bucle for invalido, el formato correcto es: FOR ID IN RANGE");}
break;
case 198:
//#line 402 "Gramatica.y"
{analizador_semantico.verificarConstantesControlFor(val_peek(5).sval, val_peek(3).sval, val_peek(1).sval);}
break;
case 199:
//#line 403 "Gramatica.y"
{agregarError("ERROR: Control del rango de iteraciones de bucle FOR invalido, el formato correcto es: '(' constante ';' constante ';' constante ')'"); analizador_semantico.verificarConstantesControlFor(val_peek(5).sval, val_peek(3).sval, val_peek(1).sval);}
break;
case 200:
//#line 404 "Gramatica.y"
{agregarError("ERROR: Control del rango de iteraciones de bucle FOR invalido, el formato correcto es: '(' constante ';' constante ';' constante ')'"); analizador_semantico.verificarConstantesControlFor(val_peek(4).sval, val_peek(2).sval, val_peek(0).sval);}
break;
case 201:
//#line 405 "Gramatica.y"
{agregarError("ERROR: Control del rango de iteraciones de bucle FOR invalido, el formato correcto es: '(' constante ';' constante ';' constante ')'"); analizador_semantico.verificarConstantesControlFor(val_peek(4).sval, val_peek(2).sval, val_peek(0).sval);}
break;
case 202:
//#line 406 "Gramatica.y"
{agregarError("ERROR: Control del rango de iteraciones de bucle FOR invalido, el formato correcto es: '(' constante ';' constante ';' constante ')'");}
break;
//#line 1487 "Parser.java"
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

























