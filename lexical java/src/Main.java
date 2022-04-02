//This program displays this line forever. Line: 7, spelling = [], kind = -1
//After you completed the switch of the class: Lexer.java this will be fixed.
public class Main{
  public static void main(String[] args){
    Token token;
    Lexer s = new Lexer();   
    do{
      token = s.nextToken(); 
      System.out.println("Line: " + token.line + 
      		", spelling = [" + token.spelling + "], " + "kind = " +
      		token.kind);
    }while(token.kind != Token.EOT);
  }
}
