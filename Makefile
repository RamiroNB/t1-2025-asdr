#JFLEX  = jflex
JFLEX  = java -jar jflex.jar
JAVAC  = javac

# targets:

all: Asdr.class

clean:
	rm -f *~ *.class Yylex.java

Asdr.class: Asdr.java Yylex.java
	$(JAVAC) Asdr.java

Yylex.java: asdr_lex_c--.flex
	$(JFLEX) asdr_lex_c--.flex

