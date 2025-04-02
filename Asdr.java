/*
 * Alunos: Kristen Arguello, Ramiro Barros e Vinícius Turani
 * 
 * Arquivo construído a partir do arquivo AsdrSample.java
 */

import java.io.*;

public class Asdr {

  private static final int BASE_TOKEN_NUM = 301;

  public static final int IDENT = 301;
  public static final int NUMBER = 302;
  public static final int WHILE = 303;
  public static final int IF = 304;
  public static final int ELSE = 305;

  public static final int TIPO = 306;
  public static final int VOID = 307;
  public static final int FUNC = 308;

  public static final String tokenList[] = {
      "IDENT", // 301
      "NUMBER", // 302
      "WHILE", // 303
      "IF", // 304
      "ELSE", // 305
      "TIPO", // 306
      "VOID", // 307
      "FUNC" // 308
  };

  /* referencia ao objeto Scanner gerado pelo JFLEX */
  private Yylex lexer;

  public ParserVal yylval;

  private static int laToken;
  private boolean debug;

  /* construtor da classe */
  public Asdr(Reader r) {
    lexer = new Yylex(r, this);
  }

  /**
   * Initial symbol for the parser.
   * 
   * Prog --> ListaDecl
   */
  private void Prog() {
    if (laToken == TIPO || laToken == FUNC || laToken == Yylex.YYEOF) {
      if (debug)
        System.out.println("Prog --> ListaDecl");
      ListaDecl();
    } else {
      yyerror("Erro: esperado TIPO | FUNC | EOF");
    }
  }

  /**
   * ListaDecl --> DeclVar ListaDecl | DeclFunc ListaDecl | (vazio)
   */
  private void ListaDecl() {
    if (laToken == TIPO) {
      if (debug)
        System.out.println("ListaDecl --> DeclVar ListaDecl");
      DeclVar();
      ListaDecl();
    } else if (laToken == FUNC) {
      if (debug)
        System.out.println("ListaDecl --> DeclFunc ListaDecl");
      DeclFunc();
      ListaDecl();
    } else {
      if (debug)
        System.out.println("ListaDecl --> (vazio)");
    }
  }

  /**
   * DeclVar --> TIPO ListaIdent ; DeclVar | (vazio)
   */
  private void DeclVar() {
    if (laToken == TIPO) {
      if (debug)
        System.out.println("DeclVar --> TIPO ListaIdent ; DeclVar");
      verifica(TIPO);
      ListaIdent();
      verifica(';');
      DeclVar();
    } else {
      if (debug)
        System.out.println("DeclVar --> (vazio)");
      // vazio
    }
  }

  /**
   * ListaIdent --> IDENT RestoListaIdent
   */
  private void ListaIdent() {
    if (laToken == IDENT) {
      if (debug)
        System.out.println("ListaIdent --> IDENT RestoListaIdent");
      verifica(IDENT);
      RestoListaIdent();
    } else {
      yyerror("Erro: esperado IDENT");
    }
  }

  /**
   * RestoListaIdent --> , ListaIdent | (vazio)
   */
  private void RestoListaIdent() {
    if (laToken == ',') {
      if (debug)
        System.out.println("RestoListaIdent --> ',' ListaIdent");
      verifica(',');
      ListaIdent();
    } else {
      if (debug)
        System.out.println("RestoListaIdent --> (vazio)");
      // vazio
    }
  }

  /**
   * DeclFunc --> FUNC TipoOuVoid IDENT '(' FormalPar ')' '{' DeclVar ListaCmd '}' DeclFun
   * | (vazio)
   */
  private void DeclFunc() {
    if (laToken == FUNC) {
      if (debug)
        System.out.println("DeclFun --> FUNC TipoOuVoid IDENT '(' FormalPar ')' '{' DeclVar ListaCmd '}' DeclFun");
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
    } else {
      if (debug)
        System.out.println("DeclFun --> (vazio)");
    }
  }

  /**
   * TipoOuVoid --> TIPO | VOID
   */
  private void TipoOuVoid() {
    if (laToken == TIPO) {
      if (debug)
        System.out.println("TipoOuVoid --> TIPO");
      verifica(TIPO);
    } else if (laToken == VOID) {
      if (debug)
        System.out.println("TipoOuVoid --> VOID");
      verifica(VOID);
    } else {
      yyerror("Erro: Esperado TIPO ou VOID");
    }
  }

  /**
   * FormalPar --> ParamList | (vazio)
   */
  private void FormalPar() {
    if (laToken == TIPO) {
      if (debug)
        System.out.println("FormalPar --> ParamList");
      ParamList();
    } else {
      if (debug)
        System.out.println("FormalPar --> (vazio)");
    }
  }

  /**
   * ParamList --> TIPO IDENT RestoParamList
   */
  private void ParamList() {
    if (laToken == TIPO) {
      if (debug)
        System.out.println("ParamList --> TIPO IDENT RestoParamList");
      verifica(TIPO);
      verifica(IDENT);
      RestoParamList();
    } else {
      yyerror("Erro: esperado TIPO");
    }
  }

  /**
   * RestoParamList --> , ParamList | (vazio)
   */
  private void RestoParamList() {
    if (laToken == ',') {
      if (debug)
        System.out.println("RestoParamList --> ',' ParamList");
      verifica(',');
      ParamList();
    } else {
      if (debug)
        System.out.println("RestoParamList --> (vazio)");
      // vazio
    }
  }

  /**
   * Verifica se o token lido é o esperado. Se sim, lê o próximo token.
   * Caso contrário, gera um erro de sintaxe.
   * 
   * @param expected o token esperado.
   */
  private void verifica(int expected) {
    if (laToken == expected)
      laToken = this.yylex();
    else {
      String expStr, laStr;

      expStr = (expected < BASE_TOKEN_NUM)
          ? "" + (char) expected
          : tokenList[expected - BASE_TOKEN_NUM];

      if (laToken == -1) {
        laStr = "EOF";
      } else if (laToken < BASE_TOKEN_NUM) {
        laStr = Character.toString((char) laToken);
      } else {
        laStr = tokenList[laToken - BASE_TOKEN_NUM];
      }

      yyerror("esperado token: " + expStr + " na entrada: " + laStr);
    }
  }

  /**
   * Habilita ou desabilita o modo de depuração.
   * 
   * @param trace true para habilitar o modo de depuração, false para desabilitar.
   */
  public void setDebug(boolean trace) {
    debug = trace;
  }

  /**
   * Bloco --> { ListaCmd }
   */
  private void Bloco() {
    if (laToken == '{') {
      if (debug)
        System.out.println("Bloco --> { ListaCmd }");
      verifica('{');
      ListaCmd();
      verifica('}');
    } else {
      yyerror("Erro: esperado {");
    }
  }

  /**
   * ListaCmd --> Cmd ListaCmd | (vazio)
   */
  private void ListaCmd() {
    if (laToken == '{' || laToken == WHILE || laToken == IDENT || laToken == IF) {
      if (debug)
        System.out.println("Lista Cmd --> Cmd");
      Cmd();
      ListaCmd();
    } else {
      if (debug)
        System.out.println("Lista Cmd --> (vazio)");
    }
  }

  /**
   * Cmd --> Bloco | WHILE ( E ) Cmd | IDENT = E ; | if ( E ) Cmd RestoIF
   */
  private void Cmd() {
    if (laToken == '{') {
      if (debug)
        System.out.println("Cmd --> Bloco");
      Bloco();
    } else if (laToken == WHILE) {
      if (debug)
        System.out.println("Cmd --> WHILE ( E ) Cmd");
      verifica(WHILE); // laToken = this.yylex();
      verifica('(');
      E();
      verifica(')');
      Cmd();
    } else if (laToken == IDENT) {
      if (debug)
        System.out.println("Cmd --> IDENT = E ;");
      verifica(IDENT);
      verifica('=');
      E();
      verifica(';');
    } else if (laToken == IF) {
      if (debug)
        System.out.println("Cmd --> if (E) Cmd RestoIF");
      verifica(IF);
      verifica('(');
      E();
      verifica(')');
      Cmd();
      RestoIf();
    } else
      yyerror("Erro: esperado {, if, while ou identificador");
  }

  /**
   * RestoIF --> else Cmd | (vazio)
   */
  private void RestoIf() {
    if (laToken == ELSE) {
      if (debug)
        System.out.println("RestoIF --> else Cmd");
      verifica(ELSE);
      Cmd();
    } else {
      if (debug)
        System.out.println("RestoIF --> (vazio)");
    }

  }

  /**
   * E --> T AuxE
   */
  private void E() {
    if (laToken == IDENT || laToken == NUMBER || laToken == '(') {
      if (debug)
        System.out.println("E --> T AuxE");
      T();
      AuxE();
    } else {
      yyerror("Erro: esperado IDENT, NUMBER ou (");
    }
  }

  /**
   * AuxE --> + T AuxE | - T AuxE | (vazio)
   */
  private void AuxE() {
    if (laToken == '+') {
      if (debug)
        System.out.println("AuxE --> '+' T AuxE");
      verifica('+');
      T();
      AuxE();
    } else if (laToken == '-') {
      if (debug)
        System.out.println("AuxE --> '-' T AuxE");
      verifica('-');
      T();
      AuxE();
    } else {
      if (debug)
        System.out.println("AuxE --> (vazio)");
      // vazio
    }
  }

  /**
   * T --> F AuxT
   */
  private void T() {
    if (laToken == IDENT || laToken == NUMBER || laToken == '(') {
      if (debug)
        System.out.println("T --> F AuxT");
      F();
      AuxT();
    } else {
      yyerror("Erro: esperado IDENT, NUMBER ou (");
    }
  }

  /**
   * AuxT --> * F AuxT | / F AuxT | (vazio)
   */
  private void AuxT() {
    if (laToken == '*') {
      if (debug)
        System.out.println("AuxT --> '*' F AuxT");
      verifica('*');
      F();
      AuxT();
    } else if (laToken == '/') {
      if (debug)
        System.out.println("AuxT --> '/' F AuxT");
      verifica('/');
      F();
      AuxT();
    } else {
      if (debug)
        System.out.println("AuxT --> (vazio)");
      // vazio
    }
  }

  /**
   * F --> IDENT | NUMBER | ( E )
   */
  private void F() {
    if (laToken == IDENT) {
      if (debug)
        System.out.println("F --> IDENT");
      verifica(IDENT);
    } else if (laToken == NUMBER) {
      if (debug)
        System.out.println("F --> NUMBER");
      verifica(NUMBER);
    } else if (laToken == '(') {
      if (debug)
        System.out.println("F --> '(' E ')'");
      verifica('(');
      E();
      verifica(')');
    } else {
      yyerror("Erro: esperado IDENT, NUMBER ou (");
    }
  }

  /* metodo de acesso ao Scanner gerado pelo JFLEX */
  /**
   * Lê o próximo token do arquivo de entrada.
   * @return o token lido.
   * @throws IOException se ocorrer um erro de entrada/saída.
   */
  private int yylex() {
    int retVal = -1;
    try {
      yylval = new ParserVal(0); // zera o valor do token
      retVal = lexer.yylex(); // le a entrada do arquivo e retorna um token
    } catch (IOException e) {
      System.err.println("IO Error:" + e);
    }
    return retVal; // retorna o token para o Parser
  }

  /* metodo (modo panico) de manipulacao de erros de sintaxe */
  /**
   * Gera uma mensagem de erro e encerra o programa.
   * Utiliza o modo pânico para manipulação de erros. 
   *
   * @param error a mensagem de erro a ser exibida.
   */
  public void yyerror(String error) {
    System.err.println(error);
    System.err.println("Entrada rejeitada");
    System.out.println("\n\nFalhou!!!");
    System.exit(1);
  }

  /**
   * Runs the scanner on input files.
   *
   * This main method is the debugging routine for the scanner.
   * It prints debugging information about each returned token to
   * System.out until the end of file is reached, or an error occured.
   *
   * @param args the command line, contains the filenames to run
   *             the scanner on.
   */
  public static void main(String[] args) {
    Asdr parser = null;
    try {
      if (args.length == 0)
        parser = new Asdr(new InputStreamReader(System.in));
      else
        parser = new Asdr(new java.io.FileReader(args[0]));

      parser.setDebug(false);
      laToken = parser.yylex();

      parser.Prog();

      if (laToken == Yylex.YYEOF)
        System.out.println("\n\nSucesso!");
      else
        System.out.println("\n\nFalhou - esperado EOF.");

    } catch (java.io.FileNotFoundException e) {
      System.out.println("File not found : \"" + args[0] + "\"");
    }
  }

}
