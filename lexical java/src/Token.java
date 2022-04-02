public class Token{
  public int kind;
  public String spelling;
  public int line;

  private final static String[] keywords = {
      "<Place holder>", "<Place holder>", 
      "begin",
      "end",
      "const",
    	"array",
    	"integer",
    	"boolean",
    	"proc",
    	"skip",
    	"read",
    	"write",
    	"call",
    	"if",
    	"fi",
    	"false",
    	"true",
    	"do",
    	"od"
   };
  
  public Token(int kind, String spelling, int line){
    this.kind = kind;
    this.spelling = spelling;
    this.line = line;
    if(kind==IDENTIFIER)
    	for(int k = 0; k < keywords.length; k++)
    		if(spelling.equals(keywords[k])){
    			this.kind = k;
    			return;
    		}
  }

  public final static int
 
    IDENTIFIER	= 0,		//Identifier
    LITERAL			= 1,		//Literal
    BEGIN 			= 2,		//begin
    END 				= 3,		//end
    CONST 			= 4,		//const
  	ARRAY 			= 5,		//array
  	INTEGER 		= 6,		//integer
  	BOOLEAN 		= 7,		//boolean
  	PROC 				= 8,		//proc
  	SKIP 				= 9,		//skip
  	READ 				= 10,		//read
  	WRITE 			= 11,		//write
  	CALL 				= 12,		//call
  	IF 					= 13,		//if
  	FI 					= 14,		//fi
  	FALSE 			= 15,		//false
  	TRUE 				= 16,		//true
  	DO 					= 17,		//do
  	OD 					= 18,		//od
  	
    SEMICOLON 	= 19,		//;
  	LBRACKET 		= 20,		//[
  	RBRACKET 		= 21,		//]
  	COMMA 			= 22,		//,
  	BECOMES 		= 23,		//:=
    GUARD 			= 24,		//[]
    ARROW 			= 25,		//->
    AND 				= 26,		//&
    OR 					= 27,		//|
    LT 					= 28,		//<
    EQ 					= 29,		//=
    GT 					= 30,		//>
    MINUS 			= 31,		//-
    PLUS 				= 32,		//+
    MULT 				= 33,		//*
    DIV 				= 34,		//     /
    MODULO 			= 35,		//     \
    LPAREN 			= 36,		//(
    RPAREN 			= 37,		//)
    NOT 				= 38,		//~
    PRIOD 			= 39,		//.
    NOTHING    	= 40,   //Never happen but we need to return something 
    EOT        	= 41,		//End of file character
    UNDEFINED 	= 42;		//Note: I added this for the next phases of the compiler. We do not need it for the parser
}
