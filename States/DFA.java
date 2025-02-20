package States;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DFA {
    private State startState;
    private Set<State> states;
    private Set<State> acceptStates;
    private Map<Set<State>, State> nfaStatesToDfaState;
    private NFA nfa;

    public DFA(NFA nfa) {
        this.nfa = nfa;
        this.states = new HashSet<>();
        this.acceptStates = new HashSet<>();
        this.nfaStatesToDfaState = new HashMap<>();
        convertNFAtoDFA();
    }

    private Set<State> getEpsilonClosure(Set<State> states) {
        Set<State> closure = new HashSet<>(states);
        Stack<State> stack = new Stack<>();
        stack.addAll(states);

        while (!stack.isEmpty()) {
            State current = stack.pop();
            Pattern epsilon = Pattern.compile("ε");
            Set<State> epsilonTransitions = current.getTransitions().getOrDefault(epsilon, new HashSet<>());

            for (State nextState : epsilonTransitions) {
                if (closure.add(nextState)) {
                    stack.push(nextState);
                }
            }
        }
        return closure;
    }

    private Set<State> move(Set<State> states, Pattern symbol) {
        Set<State> result = new HashSet<>();
        for (State state : states) {
            Set<State> transitions = state.getTransitions().getOrDefault(symbol, new HashSet<>());
            result.addAll(transitions);
        }
        return result;
    }

    private void convertNFAtoDFA() {
        // Create initial state from epsilon closure of NFA start state
        Set<State> initialStates = getEpsilonClosure(Collections.singleton(nfa.getStartState()));
        State dfaStartState = new State();
        startState = dfaStartState;
        states.add(dfaStartState);
        nfaStatesToDfaState.put(initialStates, dfaStartState);

        // Process states queue
        Queue<Set<State>> unprocessedStates = new LinkedList<>();
        unprocessedStates.add(initialStates);

        while (!unprocessedStates.isEmpty()) {
            Set<State> currentStates = unprocessedStates.poll();
            State currentDFAState = nfaStatesToDfaState.get(currentStates);

            // Get all possible input symbols from current states
            Set<Pattern> symbols = currentStates.stream()
                    .flatMap(state -> state.getTransitions().keySet().stream())
                    .filter(pattern -> !pattern.pattern().equals("ε"))
                    .collect(Collectors.toSet());

            // Process each symbol
            for (Pattern symbol : symbols) {
                Set<State> nextStates = getEpsilonClosure(move(currentStates, symbol));

                if (nextStates.isEmpty()) continue;

                State dfaState = nfaStatesToDfaState.get(nextStates);
                if (dfaState == null) {
                    dfaState = new State();
                    states.add(dfaState);
                    nfaStatesToDfaState.put(nextStates, dfaState);
                    unprocessedStates.add(nextStates);
                }

                currentDFAState.addTransition(symbol, dfaState);
            }
        }
    }

    public void printDFA() {
        // print starting state
        System.out.println("DFA Start State: " + startState.getId());

        // print accepting states
        System.out.println("\nDFA Accept States:");
        for (State state : states) {
            if (state.getTransitions().isEmpty()) {
                acceptStates.add(state);
                System.out.println("\tState " + state.getId());
            }
        }

        System.out.println("\nDFA States and Transitions:");
        for (State state : states) {
            System.out.println("\tState " + state.getId() + ":");
            for (Map.Entry<Pattern, Set<State>> entry : state.getTransitions().entrySet()) {
                for (State targetState : entry.getValue()) {
                    System.out.println("\t  " + entry.getKey().pattern() + " -> State " + targetState.getId());
                }
            }
        }
    }
}