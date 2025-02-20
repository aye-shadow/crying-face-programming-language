package States;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DFA {
    private State startState;
    private Set<State> states;
    private Set<State> acceptStates;
    private Map<Set<State>, State> nfaStatesToDfaState;
    private NFA nfa;

    public DFA() {
        this.nfa = NFA.getInstance();
        nfa.regularExpressionToNFA();
        nfa.printNFA();

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
        Set<State> initialStates = getEpsilonClosure(Collections.singleton(nfa.getStartState()));
        State dfaStartState = new State();
        startState = dfaStartState;
        states.add(dfaStartState);
        nfaStatesToDfaState.put(initialStates, dfaStartState);

        // Mark as accepting if any NFA state in the set is accepting
        if (initialStates.stream().anyMatch(s -> nfa.getAcceptStates().contains(s))) {
            acceptStates.add(dfaStartState);
        }

        Queue<Set<State>> unprocessedStates = new LinkedList<>();
        unprocessedStates.add(initialStates);

        while (!unprocessedStates.isEmpty()) {
            Set<State> currentStates = unprocessedStates.poll();
            State currentDFAState = nfaStatesToDfaState.get(currentStates);

            Set<Pattern> symbols = currentStates.stream()
                    .flatMap(state -> state.getTransitions().keySet().stream())
                    .filter(pattern -> !pattern.pattern().equals("ε"))
                    .collect(Collectors.toSet());

            for (Pattern symbol : symbols) {
                Set<State> nextStates = getEpsilonClosure(move(currentStates, symbol));

                if (nextStates.isEmpty()) continue;

                State dfaState = nfaStatesToDfaState.get(nextStates);
                if (dfaState == null) {
                    dfaState = new State();
                    states.add(dfaState);
                    nfaStatesToDfaState.put(nextStates, dfaState);
                    unprocessedStates.add(nextStates);

                    // Mark new state as accepting if it contains any accepting NFA states
                    if (nextStates.stream().anyMatch(s -> nfa.getAcceptStates().contains(s))) {
                        acceptStates.add(dfaState);
                    }
                }

                currentDFAState.addTransition(symbol, dfaState);
            }
        }
    }

    public void printDFA() {
        try (PrintWriter writer = new PrintWriter("States/transition_table.txt")) {
            writer.println("DFA Start State: " + startState.getId());

            // print accepting states
            writer.print("DFA Accept States:");
            for (State state : states) {
                if (state.getTransitions().isEmpty()) {
                    acceptStates.add(state);
                    writer.print(" " + state.getId());
                }
            }

            writer.println("\nDFA States and Transitions:");
            for (State state : states) {
                writer.println("\tState " + state.getId() + ":");
                for (Map.Entry<Pattern, Set<State>> entry : state.getTransitions().entrySet())
                {
                    for (State targetState : entry.getValue())
                    {
                        writer.println("  " + entry.getKey().pattern() + " -> " + targetState.getId());
                    }
                }
            }

            writer.println("\n");
            System.out.println("DFA transition table has been saved to transition_table.txt");
        } catch (FileNotFoundException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}