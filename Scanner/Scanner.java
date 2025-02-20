package Scanner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class Token {
    private String type;
    private String value;

    public Token(String type, String value) {
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
    private Set<Integer> acceptStates;

    public Scanner(String filePath) {
        transitionTable = new HashMap<>();
        acceptStates = new HashSet<>();
        loadTransitionTable(filePath);
    }

    private void loadTransitionTable(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean startStateFound = false;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("#") || line.isEmpty()) continue; // Skip comments and empty lines

                if (line.startsWith("DFA Start State:")) {
                    // Extract the start state
                    startState = Integer.parseInt(line.split(":")[1].trim());
                    startStateFound = true;
                } else if (line.startsWith("DFA Accept States:")) {
                    // Extract accept states
                    String[] states = line.split(":")[1].trim().split("\\s+"); // Split by whitespace
                    for (String state : states) {
                        acceptStates.add(Integer.parseInt(state.trim()));
                    }
                } else if (line.startsWith("DFA States and Transitions:")) {
                    // Start processing states and transitions
                    continue; // Skip this line
                } else if (line.startsWith("State ")) {
                    // Extract current state
                    String[] stateParts = line.split(":");
                    int currentState = Integer.parseInt(stateParts[0].replace("State ", "").trim());

                    // Process transitions
                    while ((line = br.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty() || line.startsWith("State ")) {
                            // End of the current state's transitions
                            break;
                        }

                        // Split transitions on "->"
                        String[] transitionParts = line.trim().split("->");
                        if (transitionParts.length == 2) {
                            String inputSymbol = transitionParts[0].trim();
                            int nextState = Integer.parseInt(transitionParts[1].trim());

                            // Convert special patterns to regex if needed
                            if (inputSymbol.equals("\\d")) {
                                inputSymbol = "\\d"; // Match a single digit
                            } else if (inputSymbol.equals("\\d*")) {
                                inputSymbol = "\\d*"; // Match zero or more digits
                            } else if (inputSymbol.equals("[a-z]*")) {
                                inputSymbol = "[a-z]*"; // Match zero or more lowercase letters
                            }

                            transitionTable.putIfAbsent(currentState, new HashMap<>());
                            transitionTable.get(currentState).put(inputSymbol, nextState);
                        }
                    }
                }
            }

            if (!startStateFound) {
                throw new IllegalArgumentException("Start state not found in the transition table.");
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
        List<Token> tokens = new ArrayList<>();

        String symbol;
        for (int i = 0; i < input.length(); )
        {
            int codePoint = input.codePointAt(i);
             symbol = new String(Character.toChars(codePoint));

            if (symbol.isBlank() || symbol.equals(" "))
            {
                i += Character.charCount(codePoint);
                continue;
            }

         //   System.out.println(symbol);

            Integer nextState = transitionTable.getOrDefault(currentState, new HashMap<>()).get(symbol);

            if (nextState != null)
            {
                currentToken.append(symbol);
                currentState = nextState;
            } else
            {
                // READ THE NEXT LETTER
//                if (acceptStates.contains(currentState))
//                {
//                    String tokenType = getTokenType(currentState);
//                    tokens.add(new Token(tokenType, currentToken.toString()));
//                }

                // Reset for the next token
             //   currentState = startState;
            //    currentToken.setLength(0); // Clear the current token

                // Check if the character itself can start a new token
                nextState = transitionTable.getOrDefault(currentState, new HashMap<>()).get(symbol);
                if (nextState != null)
                {
                    currentToken.append(symbol);
                    currentState = nextState;
                }
                else
                {
                    if (acceptStates.contains(currentState))
                {
                    String tokenType = getTokenType(currentState);
                    tokens.add(new Token(tokenType, currentToken.toString()));
                }
                    else {
                        tokens.add(new Token("UNKNOWN", String.valueOf(symbol)));
                        System.out.println();
                    }
                }
                   currentState = startState;
                    currentToken.setLength(0);
            }
            i += Character.charCount(codePoint);
        }

        if (acceptStates.contains(currentState))
        {
            String tokenType = getTokenType(currentState);
            tokens.add(new Token(tokenType, currentToken.toString()));

        }

        return tokens.toArray(new Token[0]);
    }


    private String readInputFromFile(String inputFilePath)
    {
        if (!inputFilePath.endsWith(".\uD83D\uDE2D"))
        {
            throw new IllegalArgumentException("Input file must be of type .\uD83D\uDE2D");
        }

        StringBuilder inputBuilder = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath)))
        {
            int character;
            while ((character = br.read()) != -1)
            {
                inputBuilder.append((char) character);
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return inputBuilder.toString().trim();
    }
    private String getTokenType(int state)
    {
        switch (state)  // 19 18 21 31 26  || 18 20 31 19 26
        {
            case 1:
                return "IDENTIFIER"; // Matches [a-z][a-z]*

            case 31:
                return "NUMBER"; // Matches ^\\d+(\\.\\d{0,1})?$
            case 18:
                return "OPERATOR"; // Matches [⏩➕➖➗❌💯🤯🌏]
            case 19:
                return "KEYWORD"; // Matches \\b💹|🔢|🚗|🏳️|🚩|🏁🏎\\b
            case 26:
                return "DELIMITER"; // Matches 🔕(?s).*?🔕
            case 21:
                return "PUNCTUATION"; // Matches [💲]

            case 7:
                return "WHITESPACE"; // Matches \\s+
            default:
                return "UNKNOWN"; // Matches .
        }
    }

    public void printTransitionTable() {
        StringBuilder sb = new StringBuilder();

        sb.append("DFA Start State: ").append(startState).append("\n");
        sb.append("DFA Accept States: ").append(String.join(" ", acceptStates.stream().map(String::valueOf).toArray(String[]::new))).append("\n");
        sb.append("DFA States and Transitions:\n");

        for (Map.Entry<Integer, Map<String, Integer>> entry : transitionTable.entrySet()) {
            int state = entry.getKey();
            sb.append("\tState ").append(state).append(":\n");

            for (Map.Entry<String, Integer> transition : entry.getValue().entrySet()) {
                String inputSymbol = transition.getKey();
                int nextState = transition.getValue();
                sb.append("\t").append(inputSymbol).append(" -> ").append(nextState).append("\n");
            }
        }

        System.out.println(sb.toString());
    }
    public static void main(String[] args)
    {
        Scanner tokenizer = new Scanner("D:\\projects\\New folder\\States\\transition_table.txt");
        tokenizer.printTransitionTable();

        String inputFilePath = "D:\\projects\\New folder\\InputFiles\\test1.\uD83D\uDE2D";
        String input = tokenizer.readInputFromFile(inputFilePath);
       // System.out.println(input);
        Token[] tokens = tokenizer.getTokens(input);
        for (Token token : tokens)
        {
            System.out.println(token);
        }
    }
}