import java.util.*;
import java.util.regex.*;

class RegularExpression {
    public final Map<String, Pattern> TOKENPATTERNS = new HashMap<>();

    RegularExpression() {
        TOKENPATTERNS.put("PUNCTUATION", Pattern.compile("[💲]"));
        TOKENPATTERNS.put("DELIMITER", Pattern.compile("🔕(?s).*?🔕"));
        TOKENPATTERNS.put("KEYWORD", Pattern.compile("\\b💹|🔢|🚗|🏳️|🚩|🏁🏎\\b"));
        TOKENPATTERNS.put("OPERATOR", Pattern.compile("[⏩➕➖➗❌💯🤯🌏]"));
        TOKENPATTERNS.put("IDENTIFIER", Pattern.compile("[a-z][a-z]*"));
        TOKENPATTERNS.put("NUMBER", Pattern.compile("^\\d+(\\.\\d{0,1})?$"));
        TOKENPATTERNS.put("WHITESPACE", Pattern.compile("\\s+"));
        TOKENPATTERNS.put("UNKNOWN", Pattern.compile("."));
    }

    public String getPattern(String type) {
        Pattern punctPattern = TOKENPATTERNS.get(type);
        String patternStr = punctPattern.pattern();
        patternStr = patternStr.replaceAll("\\s+", "");
        return patternStr;
    }
}
