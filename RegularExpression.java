import java.util.*;
import java.util.regex.*;

class RegularExpression {
    private final Map<String, String> tokenPatterns;

    public RegularExpression() {
        // Define token patterns
        tokenPatterns = Map.of(
                "PUNCTUATION", "[💲]",
                "KEYWORD", "\\b💹|🔢|🔤|🏳️|🚩|🌏|🏁🏎️\\b",
                "OPERATOR", "[⏩➕➖➗❌💯🤯]",
                "IDENTIFIER", "[a-z][a-z]*",
                "NUMBER", "\\b\\d+(\\.\\d+)?\\b",
                "WHITESPACE", "\\s+",
                "UNKNOWN", "."
        );
    }
}
