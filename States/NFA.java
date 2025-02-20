package States;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class NFA {
    private State startState;
    private Set<State> states;
    private Set<State> acceptStates;
    private Set<State> rejectStates;
    private final RegularExpression regularExpression;
    private static final Pattern EPSILON = Pattern.compile("ε");
    private static NFA instance = null;

    NFA() {
        regularExpression = new RegularExpression();
        states = new HashSet<>();
        acceptStates = new HashSet<>();
        rejectStates = new HashSet<>();
    }

    public static NFA getInstance() {
        if (instance == null) {
            instance = new NFA();
        }
        return instance;
    }

    public void addState(State state) {
        states.add(state);
    }

    public void setStartState(State state) {
        startState = state;
    }

    public void addAcceptState(State state) {
        acceptStates.add(state);
    }

    public void addOperatorRegex(State initialState) {
        String patternStr = regularExpression.getPattern("OPERATOR");

        State prevState = null, currentState = initialState;

        Boolean firstDone = false;
        for (int i = 0; i < patternStr.length(); ++i) {
            if (patternStr.charAt(i) == '[' || patternStr.charAt(i) == ']')
                continue;

            char currentChar = patternStr.charAt(i);
            String currentEmoji = patternStr.substring(i, i + 2);
            if (currentChar == '⏩' || currentChar == '➕' || currentChar == '➖' || currentChar == '➗' || currentChar == '❌' || currentEmoji.equals("💯") || currentEmoji.equals("🤯") || currentEmoji.equals("🌏")) {
                if (firstDone) {
                    if (currentEmoji.equals("💯") || currentEmoji.equals("🤯") || currentEmoji.equals("🌏")) {
                        prevState.addTransition(Pattern.compile(currentEmoji), currentState);
                    }
                    else {
                        prevState.addTransition(Pattern.compile(String.valueOf(currentChar)), currentState);
                    }
                    continue;
                }

                State keywordState = new State();
                addState(keywordState);

                prevState = currentState;
                currentState = keywordState;

                if (currentEmoji.equals("💯") || currentEmoji.equals("🤯") || currentEmoji.equals("🌏")) {
                    prevState.addTransition(Pattern.compile(currentEmoji), keywordState);
                }
                else {
                    prevState.addTransition(Pattern.compile(String.valueOf(currentChar)), keywordState);
                }

                firstDone = true;
            }
        }

        addAcceptState(currentState);
    }

    public void addKeywordRegex(State initialState) {
        String patternStr = regularExpression.getPattern("KEYWORD");

        State prevState = null, currentState = initialState;

        Boolean editPrevTransition = false;
        for (int i = 0; i < patternStr.length(); ++i) {
            if (patternStr.charAt(i) == '\\' && patternStr.charAt(i + 1) == 'b') {
                ++i;
                continue;
            }

            String currentEmoji = patternStr.substring(i, i + 2);
            if (currentEmoji.equals("💹") || currentEmoji.equals("🔢") || currentEmoji.equals("🚗") || currentEmoji.equals("🏳️") || currentEmoji.equals("🚩") || (currentEmoji.equals("🏁"))) {
                if (editPrevTransition) {
                    if (currentEmoji.equals("🏁")) {
                        prevState.addTransition(Pattern.compile("🏁🏎️"), currentState);
                    }
                    else {
                        prevState.addTransition(Pattern.compile(currentEmoji), currentState);
                    }
                    continue;
                }

                State keywordState = new State();
                addState(keywordState);

                prevState = currentState;
                currentState = keywordState;

                if (currentEmoji.equals("🏁")) {
                    prevState.addTransition(Pattern.compile("🏁🏎️"), keywordState);
                }
                else {
                    prevState.addTransition(Pattern.compile(currentEmoji), keywordState);
                }
            }
            else if (patternStr.charAt(i) == '|') {
                editPrevTransition = true;
            }
        }

        addAcceptState(currentState);
    }

    public void addPunctuationRegex(State initialState) {
        String patternStr = regularExpression.getPattern("PUNCTUATION");

        State prevState = null, currentState = initialState;

        for (int i = 0; i < patternStr.length(); ++i) {
            if (patternStr.charAt(i) == '💲') {
                State punctState = new State();
                addState(punctState);

                prevState = currentState;
                currentState = punctState;

                prevState.addTransition(Pattern.compile("💲"), punctState);
            }
        }

        addAcceptState(currentState);
    }

    public void addCommentRegex(State initialState) {
        String patternStr = regularExpression.getPattern("DELIMITER");

        State prevState = null, currentState = initialState;

        for (int i = 0; i < patternStr.length(); ++i) {
            if (patternStr.charAt(i) == '🔕') {
                State startCommentState = new State();
                addState(startCommentState);

                prevState = currentState;
                currentState = startCommentState;

                prevState.addTransition(Pattern.compile("🔕"), startCommentState);
            }
            else if (patternStr.charAt(i) == '(' && patternStr.charAt(i + 1) == '?' && patternStr.charAt(i + 2) == 's' && patternStr.charAt(i + 3) == ')') {
                // flag that enables "dot all" mode
                i += 4;
                if (patternStr.charAt(i) == '.') {
                    // match anything

                    currentState.addTransition(Pattern.compile("."), currentState);
                }
            }
            else if (patternStr.charAt(i) == '?' && patternStr.charAt(i + 1) == '🔕') {
                State endCommentState = new State();
                addState(endCommentState);

                prevState = currentState;
                currentState = endCommentState;

                prevState.addTransition(Pattern.compile("🔕"), endCommentState);
            }
        }

        addAcceptState(currentState);
    }

    public void addNumberRegex(State initialState) {
        String patternStr = regularExpression.getPattern("NUMBER");

        State prevState, currentState = initialState;
        Stack<State> paranthesis = new Stack<>();

        for (int i = 0; i < patternStr.length(); ++i) {
            Boolean connectTopStack = false;
            if (i - 2 >= 0 && patternStr.charAt(i - 1) == '?' && patternStr.charAt(i - 2) == ')') {
                // before making the state, check if prev pattern was optional
                // if it was, remember to connect currentState to state at top of stack
                connectTopStack = true;
            }
            else if (patternStr.charAt(i) == '^' || patternStr.charAt(i) == '$')
                continue;

            if (patternStr.charAt(i) == '\\') {
                if (patternStr.charAt(i + 1) == 'd') {
                    if (patternStr.charAt(i + 2) == '+') {
                        // 1 compulsory digit, rest optional

                        State digitState = new State();
                        addState(digitState);

                        prevState = currentState;
                        currentState = digitState;

                        prevState.addTransition(Pattern.compile("\\d"), currentState);

                        digitState = new State();

                        currentState.addTransition(Pattern.compile("\\d*"), currentState);

                        i += 2;
                    }
                    else if (patternStr.charAt(i + 2) == '{') {
                        // range of digits

                        String minDigits = "", maxDigits = "";
                        for (i = i + 3; patternStr.charAt(i) != '}'; ++i) {
                            if (patternStr.charAt(i) == ',') {
                                minDigits = maxDigits;
                                maxDigits = "";
                            } else {
                                maxDigits += patternStr.charAt(i);
                            }
                        }
                        --i;

                        int minDigitsInt = Integer.parseInt(minDigits), maxDigitsInt = Integer.parseInt(maxDigits);

                        // add min states
                        for (int j = 0; j < minDigitsInt; ++j) {
                            State rangeStates = new State();
                            addState(rangeStates);

                            prevState = currentState;
                            currentState = rangeStates;

                            prevState.addTransition(Pattern.compile("\\d"), rangeStates);
                        }

                        // add max states
                        for (int j = minDigitsInt; j < maxDigitsInt; ++j) {
                            State rangeStates = new State();
                            addState(rangeStates);

                            prevState = currentState;
                            currentState = rangeStates;

                            prevState.addTransition(Pattern.compile("\\d"), rangeStates);
                        }
                    }
                }
                else if (patternStr.charAt(i + 1) == '.') {
                    // decimal point

                    State decimalPointState = new State();
                    addState(decimalPointState);

                    prevState = currentState;
                    currentState = decimalPointState;

                    prevState.addTransition(Pattern.compile("\\."), decimalPointState);

                    ++i;
                }
            }
            else if (patternStr.charAt(i) == '(') {
                // there might be a ')?' eventually somewhere
                // which means that current state should go to the "NEXT" one
                // so store its address just in case there's a ')?' somewhere
                paranthesis.push(currentState);
            }

            if (connectTopStack) {
                paranthesis.peek().addTransition(EPSILON, currentState);
            }
        }

        addAcceptState(currentState);
    }

    public void addCharactersRegex(State initialState) {
        String patternStr = regularExpression.getPattern("IDENTIFIER");

        State prevState = null, currentState = initialState;

        for (int i = 0; i < patternStr.length(); ++i) {
            if (patternStr.charAt(i) == '[') {
                String charClass = "";
                for (i = i + 1; patternStr.charAt(i) != ']'; ++i) {
                    charClass += patternStr.charAt(i);
                }

                State singleCharState = new State();
                addState(singleCharState);

                prevState = currentState;
                currentState = singleCharState;

                prevState.addTransition(Pattern.compile("[" + charClass + "]"), singleCharState);
            }
            else if (patternStr.charAt(i) == '*') {
                Pattern pattern = prevState.getTransitions().keySet().iterator().next();
                currentState.addTransition(pattern, currentState);
            }
        }

        addAcceptState(currentState);
    }

    public void regularExpressionToNFA() {
        // create inital state
        State initialState = new State(), endState = new State();
        setStartState(initialState);
        addState(initialState);

        addNumberRegex(initialState);
        addCharactersRegex(initialState);
        addCommentRegex(initialState);
        addPunctuationRegex(initialState);
        addKeywordRegex(initialState);
        addOperatorRegex(initialState);
    }

    public void printNFA() {
        System.out.println("Start States.State: " + startState.getId());
        System.out.println("\nAccept States: " + acceptStates.stream().map(State::getId).toList());
        System.out.println("\nStates and Transitions:");
        for (State state : states) {
            System.out.println("\tStates.State " + state.getId() + ":");
            for (Map.Entry<Pattern, Set<State>> entry : state.getTransitions().entrySet()) {
                for (State targetState : entry.getValue()) {
                    System.out.println("\t  " + entry.getKey() + " -> States.State " + targetState.getId());
                }
            }
        }
        System.out.println('\n');
    }

    public void nfaGraphToTable() {
        System.out.println("NFA Transition Table:");
        System.out.println("State\tTransitions");

        for (State state : states) {
            StringBuilder transitions = new StringBuilder();
            Map<Pattern, Set<State>> stateTransitions = state.getTransitions();

            if (!stateTransitions.isEmpty()) {
                for (Map.Entry<Pattern, Set<State>> entry : stateTransitions.entrySet()) {
                    String symbol = entry.getKey().pattern();
                    String nextStates = entry.getValue().stream()
                            .map(State::getId)
                            .map(String::valueOf)
                            .collect(Collectors.joining(","));

                    transitions.append(symbol).append("->").append(nextStates).append("; ");
                }
                // Remove trailing separator
                transitions.setLength(transitions.length() - 2);
            }

            System.out.println(state.getId() + "\t" + transitions);
        }
    }
}