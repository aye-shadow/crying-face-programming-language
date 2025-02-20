import java.util.*;
import java.util.regex.Pattern;

public class NFA {
    private State startState;
    private Set<State> states;
    private Set<State> acceptStates;
    private Set<State> rejectStates;
    private final RegularExpression regularExpression;
    private static final Pattern EPSILON = Pattern.compile("E");

    NFA() {
        regularExpression = new RegularExpression();
        states = new HashSet<>();
        acceptStates = new HashSet<>();
        rejectStates = new HashSet<>();
    }

    public void addState(State state) {
        states.add(state);
    }

    public void addEpsilonTransition(State fromState, State toState) {
        fromState.addTransition(EPSILON, toState);
    }

    public void setStartState(State state) {
        startState = state;
    }

    public void addAcceptState(State state) {
        acceptStates.add(state);
    }

    public void regularExpressionToNFA() {
        // create inital state
        State initialState = new State(), endState = new State();
        setStartState(initialState);
        addState(initialState);

        Pattern numPattern = regularExpression.TOKENPATTERNS.get("NUMBER");
        String patternStr = numPattern.pattern();
        patternStr = patternStr.replaceAll("\\s+", "");
        System.out.println(patternStr);

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

    public void printNFA() {

        System.out.println("Start State: " + startState.getId());
        System.out.println("\nAccept States: " + acceptStates.stream().map(State::getId).toList());
        System.out.println("\nStates and Transitions:");
        for (State state : states) {
            System.out.println("\tState " + state.getId() + ":");
            for (Map.Entry<Pattern, Set<State>> entry : state.getTransitions().entrySet()) {
                for (State targetState : entry.getValue()) {
                    System.out.println("\t  " + entry.getKey() + " -> State " + targetState.getId());
                }
            }
        }
    }
}