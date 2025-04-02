import java.io.*;

public class Asdr {

    private static final int BASE_TOKEN_NUM = 301;
    
    public static final int IDENT   = 301;
    public static final int NUMBER  = 302;
    public static final int WHILE   = 303;
    public static final int IF      = 304;
    public static final int ELSE    = 305;
    
    public static final int TIPO     = 306;
    public static final int VOID    = 307;
    public static final int FUNC    = 308;
    
    public static final String tokenList[] = {
        "IDENT",    // 301
        "NUMBER",   // 302
        "WHILE",    // 303
        "IF",       // 304
        "ELSE",     // 305
        "TIPO",     // 306
        "VOID",     // 307
        "FUNC"      // 308
    };

  /* referencia ao objeto Scanner gerado pelo JFLEX */
  private Yylex lexer;

  public ParserVal yylval;

  private static int laToken;
  private boolean debug;

  
  /* construtor da classe */
  public Asdr (Reader r) {
      lexer = new Yylex (r, this);
  }

  private void Prog() {
    if (laToken == TIPO || laToken == FUNC || laToken == Yylex.YYEOF) {
      ListaDecl();
    } else {
      yyerror("Erro: esperado TIPO | FUNC | EOF");
    }
   }

  private void ListaDecl(){
    if (laToken == TIPO){
      DeclVar();
      ListaDecl();
    } else if (laToken==FUNC){
      DeclFunc();
      ListaDecl();
    } else  { 
      if (debug) System.out.println("ListaDecl --> (vazio)");            
    }
  }
              
  private void DeclVar() {
    if (laToken == TIPO) {
      verifica(TIPO);
      ListaIdent();
      verifica(';');
      DeclVar();
    } else {
      // vazio 
    }
  }

  private void ListaIdent() {
    if (laToken == IDENT) {
      verifica(IDENT);
      RestoListaIdent();
    } else {
      yyerror("Erro: esperado IDENT");
    }
  }
  
  private void RestoListaIdent() {
    if (laToken == ',') {
      verifica(',');
      ListaIdent();
    } else {
      // vazio
    }
  }
  
  private void DeclFunc(){
    if (laToken==FUNC){
        if (debug) System.out.println("DeclFun --> FUNC TipoOuVoid IDENT '(' FormalPar ')' '{' DeclVar ListaCmd '}' DeclFun");    
        verifica(FUNC);
        TipoOuVoid();
        verifica(IDENT);
        verifica('(');
        FormalPar();
        verifica(')');
        verifica('{');
        DeclVar();
        ListaCmd();
        verifica('}');
        DeclFunc();
    }else {
        if (debug) System.out.println("DeclFun --> (vazio)");
    }    
  }
  
  private void TipoOuVoid(){
    if(laToken==TIPO){
        verifica(TIPO);
    }else if(laToken==VOID){
        if (debug) System.out.println("TipoOuVoid --> VOID");
        verifica(VOID);
    }else{
        yyerror("Erro: Esperado tipo ou void");
    }
  }

  private void FormalPar() {
    // ou param list
    if (laToken == TIPO ) {
      ParamList();
    }
    //ou vazio
  }

  private void ParamList(){ 
    verifica(TIPO);
    verifica(IDENT);
    RestoParamList();
  }

  private void RestoParamList() {
    if (laToken == ',') {
      verifica(',');
      ParamList();
    } else { 
      //vazio
    }
  }
   
private void verifica(int expected) {
    if (laToken == expected)
        laToken = this.yylex();
    else {
        String expStr, laStr;       

        expStr = (expected < BASE_TOKEN_NUM)
                 ? "" + (char)expected
                 : tokenList[expected - BASE_TOKEN_NUM];

        if (laToken == -1) {
            laStr = "EOF";
        } else if (laToken < BASE_TOKEN_NUM) {
            laStr = Character.toString((char)laToken);
        } else {
            laStr = tokenList[laToken - BASE_TOKEN_NUM];
        }

        yyerror("esperado token: " + expStr + " na entrada: " + laStr);
    }
}


   /* metodo de acesso ao Scanner gerado pelo JFLEX */
   private int yylex() {
       int retVal = -1;
       try {
           yylval = new ParserVal(0); //zera o valor do token
           retVal = lexer.yylex(); //le a entrada do arquivo e retorna um token
       } catch (IOException e) {
           System.err.println("IO Error:" + e);
          }
       return retVal; //retorna o token para o Parser 
   }

  /* metodo de manipulacao de erros de sintaxe */
  public void yyerror (String error) {
     System.err.println("Erro: " + error);
     System.err.println("Entrada rejeitada");
     System.out.println("\n\nFalhou!!!");
     System.exit(1);
     
  }

  public void setDebug(boolean trace) {
      debug =  trace;
  }

  
private void Bloco() {
  if (debug) System.out.println("Bloco --> { ListaCmd }");
  // if (laToken == '{') {
      verifica('{');
      ListaCmd();
      verifica('}');
  // }
}


private void ListaCmd(){
  if (laToken == '{' || laToken ==  WHILE || laToken ==  IDENT || laToken ==  IF) {
    if (debug) System.out.println("Lista Cmd --> Cmd");
    Cmd();
    ListaCmd();
  }
  else{
    if (debug) System.out.println("Lista Cmd --> (vazio)");
  }
}


private void Cmd() {
    if (laToken == '{') {
      if (debug) System.out.println("Cmd --> Bloco");
      Bloco();
    }    
    else if (laToken == WHILE) {
      if (debug) System.out.println("Cmd --> WHILE ( E ) Cmd");
      verifica(WHILE);    // laToken = this.yylex(); 
      verifica('(');
      E();
      verifica(')');
      Cmd();
    }
    else if (laToken == IDENT ) {
      if (debug) System.out.println("Cmd --> IDENT = E ;");
      verifica(IDENT);  
      verifica('='); 
      E();
      verifica(';');
    }
    else if (laToken == IF) {
      if (debug) System.out.println("Cmd --> if (E) Cmd RestoIF");
      verifica(IF);
      verifica('(');
      E();
      verifica(')');
      Cmd();
      RestoIf();
    } 
    else yyerror("Esperado {, if, while ou identificador");
  }

  private void RestoIf() {
    if (laToken == ELSE){
      if (debug) System.out.println("RestoIF --> Cmd");
      verifica(ELSE);
      Cmd();
    }else{
      if (debug) System.out.println("RestoIF --> vazio");
    }

   }

  private void E() {
    // precisa fazer esse if se vai entrar na funcao e la embaixo vai ter a verificacao final?
    if (laToken == IDENT || laToken == NUMBER || laToken == '(') {
      T();
      AuxE();
    }
  }

  private void AuxE() {
    if (laToken == '+') {
      verifica('+');
      T();
      AuxE();
    } else if (laToken == '-') {
      verifica('-');
      T();
      AuxE();
    } else {
      // vazio
    }
  }

  private void T() {
    if (laToken == IDENT || laToken == NUMBER || laToken == '(') {
      F();
      AuxT();
    }
  }

  private void AuxT() {
    if (laToken == '*') {
      verifica('*');
      F();  
      AuxT();
    } else if (laToken == '/') {
      verifica('/');
      F();
      AuxT();
    } else {
      //vazio
    }
  }

  private void F() {
    if (laToken == IDENT) {
      verifica(IDENT);
    } else if (laToken == NUMBER) {
      verifica(NUMBER);
    } else if (laToken == '(') {
      verifica('(');
      E();
      verifica(')');
    }
  }
  


  /**
   * Runs the scanner on input files.
   *
   * This main method is the debugging routine for the scanner.
   * It prints debugging information about each returned token to
   * System.out until the end of file is reached, or an error occured.
   *
   * @param args   the command line, contains the filenames to run
   *               the scanner on.
   */
  public static void main(String[] args) {
     Asdr parser = null;
     try {
         if (args.length == 0)
            parser = new Asdr(new InputStreamReader(System.in));
         else 
            parser = new  Asdr( new java.io.FileReader(args[0]));

          parser.setDebug(false);
          laToken = parser.yylex();          

          parser.Prog();
     
          if (laToken== Yylex.YYEOF)
             System.out.println("\n\nSucesso!");
          else     
             System.out.println("\n\nFalhou - esperado EOF.");               

        }
        catch (java.io.FileNotFoundException e) {
          System.out.println("File not found : \""+args[0]+"\"");
        }
//        catch (java.io.IOException e) {
//          System.out.println("IO error scanning file \""+args[0]+"\"");
//          System.out.println(e);
//        }
//        catch (Exception e) {
//          System.out.println("Unexpected exception:");
//          e.printStackTrace();
//      }
    
  }
  
}


