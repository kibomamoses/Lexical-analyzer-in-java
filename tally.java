// tally.java
//   Implementes a simple LL(1) Recursive Descent Parser and calculator for simple expressions
//   Uses  1. lexical analysis (simple calss) using java.io.StreamTokenizer
//         2. eunmeration for tokentypes
//         3. an LL(1) recursive descent parsing algorithm
//         4. a simpel stack to convert to prefixed (cambriagde) notation
//         5. binary tree (simple node and tree classes) to build a ascii art tree
//
//   Uses an attribute grammar to calculate the value of the given expression.
//   Synthesized value attributes are returned as integer values from the methods
//   that correspond to nonterminals. Inherited subtotal attributes are passed to
//   the methods as arguments
//
//
//   Compile:
//   javac tally.java
//
//   Execute:
//   java tally [-parse] [-tree] [<filename>]
//

import java.io.*;
import java.util.*;

// *************************************************
//  Recursive Descent Parser and calculator for simple expressions
//  Uses an attribute grammar to calculate the value of the given expression.
//   Synthesized value attributes are returned as integer values from the methods
//   that correspond to nonterminals. Inherited subtotal attributes are passed to
//   the methods as arguments
//
// grammar:
//
//   <expr>        -> <term> { <add_op> <expr> }
//
//   <term>        -> <factor> { mult_op> <term> }
//
//   <factor>      -> '(' <expr> ')'
//                  | identifier
//                  | number
//
//   <add_op>      -> '+' | '-'
//   <mult_op>     -> '*' | '/'
//

// ****************************************************************************************
//          TALLY CLASS
// ****************************************************************************************

public class tally {
    private static int token; // enum:  Identifer, Number, Operator
    private static Lexical lex; // scanner - gets next token
    private static aStack s; // used to convert to prefixed (cambriagde) notation
    private static boolean print = false; // show parsing (debug) inof
    private static boolean art = false; // show ascii art parsing tree (abstract)

    // ************************************************
    //           MAIN
    // ************************************************
    public static void main(String argv[]) {
        // deal with any commandline arguments
        if (argv.length > 0) {
            if (argv[0].equals("-parse")) { // "-parse" is there show the parsing
                for (int i = 1; i < argv.length; ++i) argv[i - 1] = argv[i];
                print = true;
            }
            if (argv[0].equals("-tree")) { // "-tree" is there show the ascii art parsing tree
                for (int i = 1; i < argv.length; ++i) argv[i - 1] = argv[i];
                art = true;
            }
        }
        // create the stack - for converting to postfix
        s = new aStack();
        // create the lexigraphical analysier
        lex = new Lexical();
        if (print) lex.print = true;
        // advance to the first token on the input:
        token = lex.getToken();
        // parse expression and get calculated value:
        int value = expr();
        // check if expression ends with ';' and print value
        if (token == (int) ';') {
            System.out.println("\nValue = " + value);
            String cambridge = s.printStack();
            if (art) { // printout ascii art parser tree
                cambridge = cambridge.replaceAll("\\]", "");
                cambridge = cambridge.replaceAll("\\[", "");
                cambridge = cambridge.replaceAll("\\)", "");
                cambridge = cambridge.replaceAll("\\(", "");
                cambridge = cambridge.replaceAll("   ", " ");
                cambridge = cambridge.replaceAll("  ", " ");
                // System.out.println(cambridge);
                String[] splitted = cambridge.split(" ");
                pTree t = new pTree();
                Node root = t.constructTree(splitted);
                System.out.println("abstract parse tree: ");
                t.print("", t.root, false);
            }
        } else System.out.println("Syntax error");
    }

    // ************************************************
    //           EXPR
    // <expr>   -> <term> { <add_op> <expr> }
    // ************************************************
    private static int expr() {
        s.indentPrint(1, "expr", print);
        int subtotal = term();
        while (token == (int) '+' || token == (int) '-') {
            int saveOp = token;
            token = lex.getToken();
            if ((int) saveOp == '+') subtotal += expr();
            else if ((int) saveOp == '-') subtotal -= expr();
            else {
                System.out.println("error: " + (char) saveOp + "  expected '+'  or '-' ");
            }
            s.push_op(saveOp);
        }
        s.indentPrint(-1, "expr", print);
        return subtotal;
    }

    // ************************************************
    //           TERM
    // <term>   -> <factor> { mult_op> <term> }
    // ************************************************
    private static int term() {
        s.indentPrint(1, "term", print);
        int subtotal = factor();
        while (token == (int) '*' || token == (int) '/') {
            int saveOp = token;
            token = lex.getToken();
            if ((int) saveOp == '*') subtotal *= term();
            else if ((int) saveOp == '/') subtotal = subtotal / term();
            else {
                System.out.println("error: " + (char) saveOp + "  expected '*'  or '/' ");
            }
            s.push_op(saveOp);
        }
        s.indentPrint(-1, "term", print);
        return subtotal;
    }

    // ************************************************
    //           FACTOR
    //    <factor>   -> '(' <expr> ')' | '-' <factor>
    //                     | identifier     | number
    // ************************************************
    private static int factor() {
        s.indentPrint(1, "factor", print);
        int subtotal = 0;
        if (token == (int) '(') {
            token = lex.getToken();
            // if(print) System.out.println("Lexeem: "+lex.type()+":"+lex.tokenStr());
            subtotal = expr();
            if (token == (int) ')') {
                token = lex.getToken();
                // if(print) System.out.println("Lexeem: "+lex.type()+":"+lex.tokenStr());
            } else {
                System.out.println("closing ')' expected");
            }
        } else if (lex.type() == TokenType.Identifer) {
            token = lex.getToken();
            // if(print) System.out.println("Lexeem: "+lex.type()+":"+lex.tokenStr());
            // ignore variable names for now
            // t.stack.push(lex.word());
        } else if (lex.type() == TokenType.Number) {
            token = lex.getToken();
            // if(print) System.out.println("Lexeem: "+lex.type()+":"+ lex.tokenStr());
            subtotal = (int) lex.value();
            s.stack.push(" " + (int) lex.value());
        } else {
            System.out.println("factor expected");
        }
        s.indentPrint(-1, "factor", print);
        return subtotal;
    }
}

// ***************************************************************************
//    ENUMERATIONS
//   eunmeration for types of tokens
// ***************************************************************************
enum TokenType {
    Identifer,
    Number,
    Operator
}

// ***************************************************************************
//    LEXIGRAPHICAL ANALYSIS
//   Lexigraphical Analysiser
//   reads input and determines what are the tokens
//   and pass then to the parse
// ***************************************************************************
class Lexical {
    private static StreamTokenizer tokenstream;
    private static int token;
    private static TokenType type;
    private static double num;
    private static String word;
    public static boolean print = false;

    // ************************************************
    //   constructors
    // ************************************************
    public Lexical() {
        this(new String[] {});
    }

    // ************************************************
    //   constructors
    // ************************************************
    public Lexical(String argv[]) {
        try {
            InputStreamReader reader;
            if (argv.length > 0) reader = new InputStreamReader(new FileInputStream(argv[0]));
            else reader = new InputStreamReader(System.in);
            // create the tokenizer:
            tokenstream = new StreamTokenizer(reader);
            tokenstream.ordinaryChar('.');
            tokenstream.ordinaryChar('-');
            tokenstream.ordinaryChar('/');
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    // ************************************************
    // getToken - advance to the next token on the input
    // ************************************************
    public static int getToken() {
        try {
            token = tokenstream.nextToken();
            switch (token) {
                case StreamTokenizer.TT_NUMBER:
                    type = TokenType.Number;
                    num = tokenstream.nval;
                    if (print) aStack.indentPrint(0, ("Number found: " + num), true);
                    break;
                case StreamTokenizer.TT_WORD:
                    type = TokenType.Identifer;
                    word = tokenstream.sval;
                    if (print) aStack.indentPrint(0, ("Word found: " + word), true);
                    break;
                case '+':
                case '-':
                case '/':
                case '*':
                case '(':
                case ')':
                case ';':
                    type = TokenType.Operator;
                    if (print) aStack.indentPrint(0, ("Operator found: " + (char) token), true);
                    break;
                default:
                    System.out.println("Scanner found niether Identifer, Number, nor Operator");
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return token;
    }

    // ********************************************
    // getter
    // ********************************************
    public static TokenType type() {
        return type;
    }

    public static double value() {
        return num;
    }

    public static String word() {
        return word;
    }

    // ********************************************
    // getter
    // ********************************************
    public static String tokenStr() {
        switch (type) {
            case Number:
                return "" + (int) value();
            case Identifer:
                return word();
            case Operator:
                return word();
            default:
                return "unknown";
        }
    }
}

// ***************************************************************************
//     BINARY TREE
// binary tree for building simple binary tree
//   so we can do a nice ASCII art printout
//   only implemented the few needed methods
// ***************************************************************************
class Node {
    static int count = 0;
    String value;
    Node left, right;

    Node() {
        this(null);
    }

    Node(String item) {
        value = item;
        left = right = null;
    }

    @Override
    public String toString() {
        return String.format(
                "Data: " + value + " left: " + left + " right: " + right + " count: " + count);
    }

    // function to insert element in binary tree
    // expression elemenmts are assumed to be in a String array in prefix order
    Node insert(String prefix[]) {
        Node temp = this;
        if (count >= prefix.length) {
            System.out.println("walked off array " + count);
            System.exit(1);
        }
        value = prefix[count];
        left = right = null;
        count++;
        if (isOperator(prefix[count - 1])) {
            left = new Node();
            temp = left.insert(prefix);
            right = new Node();
            temp = right.insert(prefix);
        }
        return temp;
    }

    // is this element an operator ?
    static boolean isOperator(String s) {
        if (s.contains("+")) return true;
        if (s.contains("-")) return true;
        if (s.contains("*")) return true;
        if (s.contains("/")) return true;
        if (s.contains("^")) return true;
        return false;
    }
}

// ***************************************************************************
//    BINARY TREE
//   only implemented the few needed methods
// ***************************************************************************
class pTree {
    Node root;

    pTree() {
        root = null;
    }

    pTree(String data) {
        root = new Node(data);
    }

    // Returns root of constructed tree for given postfix expression
    Node constructTree(String postfix[]) {
        // Traverse through every character of input expression
        root = new Node(postfix[0]);
        Node temp = root;
        while (root.count < postfix.length) {
            temp = temp.insert(postfix);
        }
        return root;
    }

    public void print(String prefix, Node n, boolean isLeft) {
        if (n != null) {
            print(prefix + "     ", n.right, false);
            System.out.println(prefix + ("|-- ") + n.value);
            print(prefix + "     ", n.left, true);
        }
    }
}

// ***************************************************************************
//       STACK
//    Java code for building cambriagde notation
//    show how operation are grouped
// ***************************************************************************

class aStack {
    static int level = 0; // indentation level
    public static Stack<String> stack = new Stack<String>();

    public static String printStack() {
        String str = new String(Arrays.toString(stack.toArray()));
        System.out.println(str);
        return str;
    }

    //      tokens get pushed onto the stack in <FACTOR> (later could be <Primary>)
    //      here we pop them off and then
    //      push the OPERATION and its 2 operands (in correct order)
    public static void push_op(int tok) {
        String rside = stack.pop();
        String lside = stack.pop();
        String tmp = "" + (char) tok;
        String line = " ( " + tmp + lside + rside + " )"; // build
        stack.push(new String(line)); // save sub on stack - it will be needed later
    }

    //    printout user info but kepp track of indentation level
    //    'amount' could be -1, 0 , or 1   -1 decrements indetation level
    //                                      1 increamants indetation
    //    'str' is string to print
    public static void indentPrint(int amount, String str, Boolean print) {
        if (amount > 0) level += amount;
        if (print)
            for (int i = 0; i < level; i++) {
                System.out.print("\t");
            }
        if (print) { // show parsing info in detail
            System.out.print("" + level + ": ");
            System.out.println(str);
        }
        if (amount < 0) level += amount;
    }
}