package States;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class State {
    private static int id_counter = 0;
    private int id;
    private Map<Pattern, Set<State>> transitions;

    public State() {
        id = id_counter++;
        transitions = new HashMap<Pattern, Set<State>>();
    }

    public int getId() {
        return id;
    }

    public void addTransition(Pattern symbol, State state) {
        transitions.computeIfAbsent(symbol, k -> new HashSet<>()).add(state);
    }

    public Map<Pattern, Set<State>> getTransitions() {
        return transitions;
    }
}