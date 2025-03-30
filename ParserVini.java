import java.io.*;

public class Parser {
    private Lexer lexer;
    private Token token;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        this.token = lexer.nextToken();
    }

    private void match(TokenType expected) throws Exception {
        if (token.getType() == expected) {
            token = lexer.nextToken();
        } else {
            throw new Exception("Erro de sintaxe: esperado " + expected + " mas encontrado " + token.getType());
        }
    }

    public void parse() throws Exception {
        ListaDecl();
        if (token.getType() != TokenType.EOF) {
            throw new Exception("Erro de sintaxe: código após a última declaração");
        }
    }

    private void ListaDecl() throws Exception {
        if (token.getType() == TokenType.INT || token.getType() == TokenType.DOUBLE || token.getType() == TokenType.BOOLEAN) {
            DeclVar();
            ListaDecl();
        } else if (token.getType() == TokenType.FUNC) {
            DeclFun();
            ListaDecl();
        } // vazio permitido
    }

    private void DeclVar() throws Exception {
        Tipo();
        ListaIdent();
        match(TokenType.SEMICOLON);
        DeclVar(); // Recursivo para múltiplas declarações
    }

    private void Tipo() throws Exception {
        if (token.getType() == TokenType.INT || token.getType() == TokenType.DOUBLE || token.getType() == TokenType.BOOLEAN) {
            token = lexer.nextToken();
        } else {
            throw new Exception("Erro de sintaxe: tipo esperado");
        }
    }

    private void ListaIdent() throws Exception {
        match(TokenType.IDENT);
        if (token.getType() == TokenType.COMMA) {
            match(TokenType.COMMA);
            ListaIdent();
        }
    }

    private void DeclFun() throws Exception {
        match(TokenType.FUNC);
        TipoOuVoid();
        match(TokenType.IDENT);
        match(TokenType.LPAREN);
        FormalPar();
        match(TokenType.RPAREN);
        match(TokenType.LBRACE);
        DeclVar();
        ListaCmd();
        match(TokenType.RBRACE);
        DeclFun();
    }

    private void TipoOuVoid() throws Exception {
        if (token.getType() == TokenType.VOID) {
            match(TokenType.VOID);
        } else {
            Tipo();
        }
    }

    private void FormalPar() throws Exception {
        if (token.getType() == TokenType.INT || token.getType() == TokenType.DOUBLE || token.getType() == TokenType.BOOLEAN) {
            ParamList();
        }
    }

    private void ParamList() throws Exception {
        Tipo();
        match(TokenType.IDENT);
        if (token.getType() == TokenType.COMMA) {
            match(TokenType.COMMA);
            ParamList();
        }
    }

    private void ListaCmd() throws Exception {
        if (token.getType() == TokenType.WHILE || token.getType() == TokenType.IDENT || token.getType() == TokenType.IF || token.getType() == TokenType.LBRACE) {
            Cmd();
            ListaCmd();
        }
    }

    private void Cmd() throws Exception {
        if (token.getType() == TokenType.LBRACE) {
            Bloco();
        } else if (token.getType() == TokenType.WHILE) {
            match(TokenType.WHILE);
            match(TokenType.LPAREN);
            E();
            match(TokenType.RPAREN);
            Cmd();
        } else if (token.getType() == TokenType.IDENT) {
            match(TokenType.IDENT);
            match(TokenType.ASSIGN);
            E();
            match(TokenType.SEMICOLON);
        } else if (token.getType() == TokenType.IF) {
            match(TokenType.IF);
            match(TokenType.LPAREN);
            E();
            match(TokenType.RPAREN);
            Cmd();
            RestoIf();
        } else {
            throw new Exception("Erro de sintaxe: comando esperado");
        }
    }

    private void RestoIf() throws Exception {
        if (token.getType() == TokenType.ELSE) {
            match(TokenType.ELSE);
            Cmd();
        }
    }

    private void Bloco() throws Exception {
        match(TokenType.LBRACE);
        ListaCmd();
        match(TokenType.RBRACE);
    }

    private void E() throws Exception {
        T();
        while (token.getType() == TokenType.PLUS || token.getType() == TokenType.MINUS) {
            token = lexer.nextToken();
            T();
        }
    }

    private void T() throws Exception {
        F();
        while (token.getType() == TokenType.MULT || token.getType() == TokenType.DIV) {
            token = lexer.nextToken();
            F();
        }
    }

    private void F() throws Exception {
        if (token.getType() == TokenType.IDENT || token.getType() == TokenType.NUM) {
            token = lexer.nextToken();
        } else if (token.getType() == TokenType.LPAREN) {
            match(TokenType.LPAREN);
            E();
            match(TokenType.RPAREN);
        } else {
            throw new Exception("Erro de sintaxe: fator esperado");
        }
    }
}
