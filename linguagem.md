NOMES: Kristen Arguello, Ramiro Barros e Vinícius Turani

Prog --> ListaDecl

ListaDecl -->  DeclVar  ListaDecl
            |  DeclFun  ListaDecl
            |  /* vazio */

DeclVar --> Tipo ListaIdent ';' DeclVar
        | /* vazio */

Tipo --> int | double | boolean

FATORACAO----------------------
antes:          ListaIdent --> IDENT , ListaIdent
                             | IDENT

ListaIdent --> IDENT RestoListaIdent

RestoListaIdent --> , ListaIdent    
                  | /* vazio */


DeclFun --> FUNC tipoOuVoid IDENT '(' FormalPar ')' '{' DeclVar ListaCmd '}' DeclFun
        | /* vazio */

TipoOuVoid --> Tipo | VOID

FormalPar -> paramList | /* vazio */

FATORACAO----------------------
antes:          paramList --> Tipo IDENT , ParamList
                            | Tipo IDENT 

paramList --> Tipo IDENT RestoParamList

RestoParamList --> "," paramList
            | /* vazio */

Bloco --> { ListaCmd } DONE 

ListaCmd --> Cmd ListaCmd 
            |    /* vazio */

Cmd --> Bloco
    | while ( E ) Cmd
    | IDENT = E ;
    | if ( E ) Cmd RestoIf

RestoIf -> else Cmd
        |    /* vazio */

RECURSAO A ESQUERDA----------------------
antes:          E --> E + T
                    | E - T
                    | T

E  --> T AuxE

AuxE --> + T AuxE
       | - T Auxe
       | /* vazio */

RECURSAO A ESQUERDA----------------------
antes:          T --> T * F
                    | T / F
                    | F    

T --> F AuxT

AuxT --> * F AuxT
        | / F AuxT  
        | /* vazio */



F -->  IDENT
    | NUM
    | ( E )












NAO TERMINAIS = 
  Prog
  ListaDecl
  DeclVar
  Tipo
  ListaIdent
  DeclFun
  TipoOuVoid
  FormalPar
  paramList
  RestoParamList
  Bloco
  ListaCmd
  Cmd
  RestoIf
  E
  AuxE
  T
  AuxT
  F


TERMINAIS LITERAL =
  ;
  int
  double
  boolean
  , 
  (
  )
  {
  }
  while
  if 
  =
  else
  + 
  -
  * 
  /
  VOID = "void"
  FUNC = "func"

TERMINAIS CLASSES =
  IDENT
  NUM
