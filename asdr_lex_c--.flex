%% 

%{
  private AsdrSample yyparser;

  public Yylex(java.io.Reader r, AsdrSample yyparser) {
    this(r);
    this.yyparser = yyparser;
  }
%}

/* macros */
WHITE_SPACE_CHAR=[\n\r\ \t\b\012]
LETTER = [A-Za-z]
DIGIT = [0-9]
NUMBER = {DIGIT}+


%% 
"int" {return Asdr.INT; }
"double" {return Asdr.DOUBLE; }


";" |
"," { return yytext().charAt(0); }

IDENT = 