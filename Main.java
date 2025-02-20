//public class Main
//{
//    public static void main(String[] args)
//    {
//        States.NFA nfa = new States.NFA();
//        nfa.regularExpressionToNFA();
//        nfa.printNFA();
//    }
//}

import States.DFA;
import States.NFA;
import Symbols.SymbolTable;

public class Main
{
    public static void main(String[] args)
    {
        NFA nfa = NFA.getInstance();
        nfa.regularExpressionToNFA();
        // nfa.printNFA();
//         nfa.nfaGraphToTable();

        DFA dfa = new DFA(nfa);
        dfa.printDFA();

        // Sample tokens parsed from the provided code
        String[] tokens = {
                "🔢", "a", "=", "5",
                "🔢", "b", "=", "6",
                "🔢", "c", "=", "a", "➕", "b",
                "return", "🚩"
        };

        SymbolTable symbolTable = new SymbolTable();

        // Simulate adding tokens to the symbol table
        for (String token : tokens) {
            if (token.matches("[a-z]")) { // Identifiers (e.g., a, b, c)
                symbolTable.addSymbol(token, "identifier", "local");
            } else if (token.matches("^\\d+(\\.\\d+)?$")) { // Numbers (e.g., 5, 6)
                symbolTable.addSymbol(token, "number", "local");
            }
            // Add more conditions if needed for different token types
        }

        // Display the symbol table
        symbolTable.display();
    }
}