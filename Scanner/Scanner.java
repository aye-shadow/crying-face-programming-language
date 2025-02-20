package Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Token {
    private String type;
    private String value;

    public Token(String type, String value)
    {
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("Token[type=%s, value=%s]", type, value);
    }
}

public class Scanner
{
    private Map<Integer, Map<String, Integer>> transitionTable;
    private int startState;
    private int acceptState;
    public FileProcessor fileProcessor = null;

    public Scanner(String filePath)
    {
        transitionTable = new HashMap<>();
        loadTransitionTable(filePath);
        startState = 0;
        acceptState = 4; // Assuming 4 is the error state (non-accepting)
        fileProcessor = new FileProcessor();
    }

    private void loadTransitionTable(String filePath)
    {
        // this is to be set up with our Dfa-table
        try (BufferedReader br = new BufferedReader(new FileReader(filePath)))
        {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#")) continue; // Skip comments
                String[] parts = line.split(", ");
                if (parts.length == 3) {
                    int currentState = Integer.parseInt(parts[0]);
                    String inputSymbol = parts[1].trim();
                    int nextState = Integer.parseInt(parts[2]);

                    transitionTable.putIfAbsent(currentState, new HashMap<>());
                    transitionTable.get(currentState).put(inputSymbol, nextState);
                }
            }
            System.out.println("Transition table loaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Token[] getTokens(String input)
    {
        int currentState = startState;
        StringBuilder currentToken = new StringBuilder();
        boolean isTokenFound = false;
        int tokenStartState = startState;

        List<Token> tokens = new ArrayList<>();

        for (char ch : input.toCharArray())
        {
            String symbol = String.valueOf(ch);
            currentState = transitionTable.getOrDefault(currentState, new HashMap<>()).getOrDefault(symbol, acceptState);

            if (currentState != acceptState)
            {
                currentToken.append(ch);
                isTokenFound = true;
            } else
            {
                // If we hit the error state, we need to determine the token type
                if (isTokenFound)
                {
                    // Create a token based on the last accepted state
                    String tokenType = getTokenType(tokenStartState);
                    tokens.add(new Token(tokenType, currentToken.toString()));
                    currentToken.setLength(0); // Reset for next token
                    isTokenFound = false;
                }
                // Reset to starting state if we hit an error
                currentState = startState;
                currentToken.setLength(0);
            }
            tokenStartState = currentState;
        }

        // Check if there's a token left at the end
        if (isTokenFound)
        {
            String tokenType = getTokenType(tokenStartState);
            tokens.add(new Token(tokenType, currentToken.toString()));
        }

        return tokens.toArray(new Token[0]);
    }

    private String getTokenType(int state)
    {
        // Define token types based on the state
        switch (state)
        {
            case 1:
                return "IDENTIFIER"; // Matches [a-z][a-z]*
            case 2:
                return "NUMBER"; // Matches ^\\d+(\\.\\d{0,1})?$
            case 3:
                return "OPERATOR"; // Matches [⏩➕➖➗❌💯🤯🌏]
            case 4:
                return "KEYWORD"; // Matches \\b💹|🔢|🚗|🏳️|🚩|🏁🏎\\b
            case 5:
                return "DELIMITER"; // Matches 🔕(?s).*?🔕
            case 6:
                return "PUNCTUATION"; // Matches [💲]
            case 7:
                return "WHITESPACE"; // Matches \\s+
            default:
                return "UNKNOWN"; // Matches .
        }
    }

    public static void main(String[] args)
    {
        Scanner tokenizer = new Scanner("transition_table.txt");
        String[] input = {
                "🔢", "a", "=", "5",
                "🔢", "b", "=", "6",
                "🔢", "c", "=", "a", "➕", "b",
                "return", "🚩"
        };
        Token[] tokens = tokenizer.getTokens(input[0]);

        for (Token token : tokens)
        {
            System.out.println(token);
        }
    }
}