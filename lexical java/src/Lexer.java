
//In the switch you need to code the label that I wrote: "return -1"; and change return -1 to return a token.
//By token I mean one of the constant identifier in the class: Token.java
//The labels that I did not write "return -1" are correct. Do not change the code.
//You only code the switch.

import java.io.*;
import java.util.Scanner;

public class Lexer{
  private char currentChar;
  private static int line = 1;
  private BufferedReader inFile = null;
  private StringBuffer currentSpelling;
  private int currentKind;
  
  public Lexer(){
  	Scanner keyboard = null;
  	try{
      inFile = new BufferedReader(new FileReader("a.txt"));
      int i = inFile.read();
      if(i == -1) //end of file
        currentChar = '\u0000';
      else
        currentChar = (char)i;
      takeIt();
    }catch(Exception e){}
  }

  private void takeIt(){
    currentSpelling.append(currentChar);
    try{
      int i = inFile.read();
      if(i == -1) //end of file
        currentChar = '\u0000';
      else
        currentChar = (char)i;
    }
    catch(IOException e){
        System.out.println(e);
    }
  }

  private void discard(){
    try{
      int i = inFile.read();
      if(i == -1) //end of file
        currentChar = '\u0000';
      else
        currentChar = (char)i;
    }
    catch(IOException e){
        System.out.println(e);
    }
  }

  private boolean isDigit(char c){
    return '0' <= c && c <= '9';
  }

  private boolean isLetter(char c){
    return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z');
  }

  private boolean isGraphic(char c){
    return c == '\t' ||(' ' <= c && c <= '~');
  }

  private byte scanToken(){
    switch(currentChar){
      case 'a': case 'b': case 'c': case 'd': case 'e': case 'f': case 'g': case 'h': case 'i':
      case 'j': case 'k': case 'l': case 'm': case 'n': case 'o': case 'p': case 'q': case 'r':
      case 's': case 't': case 'u': case 'v': case 'w': case 'x': case 'y': case 'z':
      case 'A': case 'B': case 'C': case 'D': case 'E': case 'F': case 'G': case 'H': case 'I':
      case 'J': case 'K': case 'L': case 'M': case 'N': case 'O': case 'P': case 'Q': case 'R':
      case 'S': case 'T': case 'U': case 'V': case 'W': case 'X': case 'Y': case 'Z':
        takeIt();
        while(isLetter(currentChar) || isDigit(currentChar) || currentChar == '_')
          takeIt();
        return Token.IDENTIFIER;
      case '0': case '1': case '2': case '3': case '4':
      case '5': case '6': case '7': case '8': case '9':
        takeIt();
        while(isDigit(currentChar))
          takeIt();
        return Token.LITERAL;
      case '$'://This label is correct. Do not change.
      	line++;
    		  while(currentChar != '\n' && currentChar != '\u0000')
    		    discard();
    		  if(currentChar == '\n')
    			  discard();
    		  return Token.NOTHING;
      case '+': //This lable is correct do not change.
        takeIt();
        return Token.PLUS;
        case '-': //Consider: - and ->
      	takeIt();
      return Token.MINUS;
      // case '->':
      // takeIt();
      // return Token.ARROW;
      case '*':
        takeIt();
        return Token.MULT;
      case '/':
        takeIt();
        return Token.DIV;
      case '\\':
        takeIt();
        return Token.MODULO;
      case '<':
        takeIt();
        return Token.LT;
      case '>':
        takeIt();
        return Token.GT;
      case '=':
        takeIt();
        return Token.EQ;
      case '~':
        takeIt();
        return Token.NOT;
      case '&':
        takeIt();
        return Token.AND;
      case ',':
        takeIt();
        return Token.COMMA;
      case '.':
        takeIt();
        return Token.PRIOD;
      case '(':
        takeIt();
        return Token.LPAREN;
      case ')':
        takeIt();
        return Token.RPAREN;
      case '|':
        takeIt();
        return Token.OR;
      case ';':
        takeIt();
        return Token.SEMICOLON;
        // case ':=':
        // takeIt();
        // return Token.BECOMES;                
        ////Note: In this language we do not have a token for colon (i.e. :). After a colon we should have =  
        //This means := is a token but : is not.
        
                           
      //  Consider "[]" as Token Token GUARD. There should be no should ne no character (Space, tab, character)                      
      //   between "[" and "]". If after "[" we do not have "]", then the token is:Token.LBRACKET
      	
     
      case '[':
      takeIt();
      return Token.LBRACKET; 
      
      case ']':
        takeIt();
        return Token.RBRACKET;
        // case '[]':
        // takeIt();
        // return Token.GUARD;
      
      case '\u0000':
        return Token.EOT;
      default:
        new Error("wrong token " + currentChar, line);
        return Token.EOT;
    }
  }

  private void scanSeparator(){
    switch(currentChar){
      case '$':
        discard();
        while(isGraphic(currentChar))
          discard();
        if(currentChar == '\r')
          discard();
        discard();
        line++;
        break;
      case ' ': case '\n': case '\r':
        if(currentChar == '\n')
          line++;
        discard();
    }
  }

  public Token scan(){
    currentSpelling = new StringBuffer("");
    while(currentChar == '$' || currentChar == ' ' || currentChar == '\n' ||
          currentChar == '\r')
      scanSeparator();
    currentKind = scanToken();
    return new Token(currentKind, currentSpelling.toString(), line);
  }
  
  public Token nextToken(){
    currentSpelling = new StringBuffer("");
    while(currentChar == ' ' || currentChar == '\n' || 
      currentChar == '\r' || currentChar == '\t')
      scanEscapeCharacters();
    currentKind = scanToken();
    if(currentKind == Token.NOTHING)
    	nextToken();
    return new Token(currentKind, currentSpelling.toString(), line);
  }
  
  private void scanEscapeCharacters(){
    switch(currentChar){
      case ' ': case '\n': case '\r': case '\t':
        if(currentChar == '\n')
          line++;
        discard();
    }
  }
}
