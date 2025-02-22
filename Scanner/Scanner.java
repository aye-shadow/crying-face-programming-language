package Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

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
                            if (inputSymbol.equals("\\d*"))
                            {
                                inputSymbol = "\\d*";
                              //  System.out.println("Integer is matched");
                            } else if (inputSymbol.equals("\\d"))
                            {
                                inputSymbol = "\\d";
                              //  System.out.println("IntegerSS iare  matched");
                            } else if (inputSymbol.equals("[a-z]*"))
                            {
                                inputSymbol = "[a-z]*";
                              //  System.out.println("LowerCase Letters  matched");
                            }
                            else if (inputSymbol.equals("[a-z]"))
                            {
                                inputSymbol = "[a-z]";
                              //  System.out.println("LowerCase  matched");
                            }
                            else if (inputSymbol.equals("\\."))
                            {
                                inputSymbol = "\\.";
                                  System.out.println("LowerCase  matched");
                            }
                            else if (inputSymbol.equals("."))
                            {
                                inputSymbol = ".";
                                //  System.out.println("LowerCase  matched");
                            }
                            else
                            {
                              //  System.out.println(inputSymbol + "Length of Input Symbol: " + inputSymbol.length());
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

    for (int i = 0; i < input.length(); )
    {
        int codePoint = input.codePointAt(i);
        String symbol = new String(Character.toChars(codePoint));
        int[] codePoints = input.codePoints().toArray();
        if (i < codePoints.length)
        {
            codePoint = codePoints[i];
            symbol = new String(Character.toChars(codePoint));
        }


        if (symbol.isBlank() || symbol.equals(" "))
        {
            i += Character.charCount(codePoint);
            continue;
        }

        Integer nextState = transitionTable.getOrDefault(currentState, new HashMap<>()).get(symbol);
        Map<String, Integer> transitions = transitionTable.getOrDefault(currentState, new HashMap<>());

        // Check regex patterns if direct transition fails
        if (nextState == null)
        {
            for (String pattern : transitions.keySet()) {
                if (symbol.matches(pattern)) {
                    nextState = transitions.get(pattern);
                    break;
                }
            }
        }

        if (nextState != null)
        {
            // Continue building token
            currentToken.append(symbol);
            currentState = nextState;
            i += Character.charCount(codePoint);
        } else
        {
            // Finalize the current token if in an accepting state
            if (acceptStates.contains(currentState))
            {
                if (!currentToken.toString().equals(" ") && !currentToken.isEmpty())
                tokens.add(new Token(getTokenType(currentState), currentToken.toString()));
                currentState = startState;
                currentToken.setLength(0);
                currentToken.append(symbol);
            }
            else
            {
                if (!currentToken.toString().equals(" ") && !currentToken.isEmpty())
                tokens.add(new Token(getTokenType(0), currentToken.toString()));
                currentState = startState;
                currentToken.setLength(0);
                currentToken.append(symbol);
            }
            i += Character.charCount(codePoint);



            nextState = transitionTable.getOrDefault(currentState, new HashMap<>()).get(symbol);
            transitions = transitionTable.getOrDefault(currentState, new HashMap<>());

            if (nextState ==null) // either skip or regex
            {
                // Check regex patterns if direct transition fails
                    for (String pattern : transitions.keySet()) {
                        if (symbol.matches(pattern)) {
                            nextState = transitions.get(pattern);
                            break;
                        }
                    }
                if (nextState == null) // invalid
                    // push it as unknown and empty token
                {
                if (!currentToken.toString().equals(" ") && !currentToken.isEmpty())
                tokens.add(new Token(getTokenType(0), currentToken.toString() ));
                currentState = startState;
                currentToken.setLength(0);}
            }
            if (nextState != null) // it was a valid token & push it if the nextState is null
            {
                if (i >= input.length()) {
                    break;
                }
                String symbol2 ;
                if (i < codePoints.length)
                {
                    codePoint = codePoints[i];
                    symbol2 = new String(Character.toChars(codePoint));
                }
                else
                {
                    break;
                }
                if (symbol2.isBlank() || symbol2.equals(" ")) // check and push
                {
                    if (acceptStates.contains(nextState))
                    {
                        if (!currentToken.toString().equals(" ") && !currentToken.isEmpty())
                        tokens.add(new Token(getTokenType(nextState), currentToken.toString() ));
                        currentState = startState;
                        currentToken.setLength(0);
                    }
                }
                else
                {   currentState = nextState;
                    nextState = transitionTable.getOrDefault(currentState, new HashMap<>()).get(symbol2);
                    if(nextState == null) // push it coz its a token, else continue
                    {
                        for (String pattern : transitions.keySet())
                        { //regex
                            if (symbol.matches(pattern))
                            {
                                nextState = transitions.get(pattern);
                                break;
                            }
                        }
                        if (nextState == null )
                        if (acceptStates.contains(currentState))
                        {

                            if (!currentToken.toString().equals(" ") && !currentToken.isEmpty())
                            tokens.add(new Token(getTokenType(currentState), currentToken.toString() ));
                            currentState = startState;
                            currentToken.setLength(0);
                        }
                    }
                }
            }


        }
    }

    // Add any remaining token if it's valid.
    if (acceptStates.contains(currentState) && !currentToken.isEmpty())
    {
        tokens.add(new Token(getTokenType(currentState), currentToken.toString()));
    }

    return tokens.toArray(new Token[0]);
}


//public Token[] getTokens(String input)
//{
//    int currentState = startState;
//    StringBuilder currentToken = new StringBuilder();
//    List<Token> tokens = new ArrayList<>();
//
//    for (int i = 0; i < input.length(); )
//    {
//        int codePoint = input.codePointAt(i);
//        String symbol = new String(Character.toChars(codePoint));
//
//        if (symbol.isBlank() || symbol.equals(" "))
//        {
//            i += Character.charCount(codePoint);
//            continue;
//        }
//
//        Integer nextState = transitionTable.getOrDefault(currentState, new HashMap<>()).get(symbol);
//        Map<String, Integer> transitions = transitionTable.getOrDefault(currentState, new HashMap<>());
//
//        // Check regex patterns if direct transition fails
//        if (nextState == null)
//        {
//            for (String pattern : transitions.keySet()) {
//                if (symbol.matches(pattern)) {
//                    nextState = transitions.get(pattern);
//                    break;
//                }
//            }
//        }
//
//        if (nextState != null)
//        {
//            // Continue building token
//            currentToken.append(symbol);
//            currentState = nextState;
//            i += Character.charCount(codePoint);
//        } else
//        {
//            // Finalize the current token if in an accepting state
//            if (acceptStates.contains(currentState))
//            {
//                if (currentToken.length() > 0 && !currentToken.toString().equals(" "))
//                    tokens.add(new Token(getTokenType(currentState), currentToken.toString()));
//                currentState = startState;
//                currentToken.setLength(0);
//                currentToken.append(symbol);
//
//            }
//            else
//            {
//                if (currentToken.length() > 0 && !currentToken.toString().equals(" "))
//                    tokens.add(new Token(getTokenType(0), currentToken.toString()));
//                currentState = startState;
//                currentToken.setLength(0);
//                currentToken.append(symbol);
//            }
//            i += Character.charCount(codePoint);
//            nextState = transitionTable.getOrDefault(currentState, new HashMap<>()).get(symbol);
//            if (nextState ==null) // either skip or regex
//            {
//                for (String pattern : transitions.keySet()) { //regex
//                    if (symbol.matches(pattern)) {
//                        nextState = transitions.get(pattern);
//                        break;
//                    }
//                }
//                if (nextState == null) // invalid
//                {
//                    if (currentToken.length() > 0 && !currentToken.toString().equals(" "))
//                        tokens.add(new Token(getTokenType(0), currentToken.toString()));
//                    currentState = startState;
//                    currentToken.setLength(0);
//                }
//            }
//            if (nextState != null) // it was a valid token & push it if the nextState is null
//            {
//                codePoint = input.codePointAt(i);
//                String symbol2 = new String(Character.toChars(codePoint)); // next symbol
//                if (symbol2.isBlank() || symbol2.equals(" ")) // check and push
//                {
//                    if (acceptStates.contains(nextState))
//                    {
//                        if (currentToken.length() > 0 && !currentToken.toString().equals(" "))
//                            tokens.add(new Token(getTokenType(nextState), currentToken.toString()));
//                        currentState = startState;
//                        currentToken.setLength(0);
//                    }
//                }
//                else
//                {
//                    currentState = nextState;
//                    nextState = transitionTable.getOrDefault(currentState, new HashMap<>()).get(symbol2);
//                    if(nextState == null) // push it coz its a token, else continue
//                    {
//                        for (String pattern : transitions.keySet())
//                        { //regex
//                            if (symbol.matches(pattern))
//                            {
//                                nextState = transitions.get(pattern);
//                                break;
//                            }
//                        }
//                        if (nextState == null )
//                        {
//                            if (acceptStates.contains(currentState))
//                            {
//                                if (currentToken.length() > 0 && !currentToken.toString().equals(" "))
//                                    tokens.add(new Token(getTokenType(currentState), currentToken.toString()));
//                                currentState = startState;
//                                currentToken.setLength(0);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    // Add any remaining token if it's valid
//    if (acceptStates.contains(currentState) && currentToken.length() > 0)
//    {
//        tokens.add(new Token(getTokenType(currentState), currentToken.toString()));
//    }
//
//    return tokens.toArray(new Token[0]);
//}


    public String readInputFromFile(String inputFilePath)
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
        switch (state)
        {
            case 21:
                return "IDENTIFIER"; // Matches [a-z][a-z]*
            case 31,23,27,30,28:
                return "NUMBER"; // Matches ^\\d+(\\.\\d{0,1})?$
            case 18:
                return "OPERATOR"; // Matches [⏩➕➖➗❌💯🤯🌏]
            case 19:
                return "KEYWORD"; // Matches \\b💹|🔢|🚗|🏳️|🚩|🏁🏎\\b
            case 26:
                return "DELIMITER"; // Matches 🔕(?s).*?🔕
            case 20:
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