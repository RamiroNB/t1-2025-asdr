%% 

%{
  private Asdr yyparser;

  public Yylex(java.io.Reader r, Asdr yyparser) {
    this(r);
    this.yyparser = yyparser;
  }
%}

%integer
%line
%char

/* macros */
WHITE_SPACE_CHAR=[\n\r\ \t\b\012]
DIGIT = [0-9]
NUM {DIGIT}+


%% 
"$TRACE_ON"   { yyparser.setDebug(true); }
"$TRACE_OFF"  { yyparser.setDebug(false); }

"int" {return Asdr.TIPO; }
"double" {return Asdr.TIPO; }
"boolean" {return Asdr.TIPO; }
"while" {return Asdr.WHILE; }
"if"		{ return Asdr.IF; }
"else"		{ return Asdr.ELSE; }

"func" { return Asdr.FUNC; }
"void" { return Asdr.VOID; }

";" |
"(" |
")" |
"{" |
"}" |
"=" |
"+" |
"-" | 
"*" |
"/" |
"," { return yytext().charAt(0); }

[:jletter:][:jletterdigit:]* { return Asdr.IDENT; }  
{NUM} { return Asdr.NUMBER; }

{WHITE_SPACE_CHAR}+ { }

. { System.out.println("Erro lexico: caracter invalido: <" + yytext() + ">"); }
