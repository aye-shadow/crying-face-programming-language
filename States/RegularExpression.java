package States;

import java.util.*;
import java.util.regex.*;

class RegularExpression {
    public final Map<String, Pattern> TOKENPATTERNS = new HashMap<>();

    RegularExpression() {
        TOKENPATTERNS.put("PUNCTUATION", Pattern.compile("[💲:]"));  //1
        TOKENPATTERNS.put("DELIMITER", Pattern.compile("🔕(?s).*?🔕"));   //2
        TOKENPATTERNS.put("KEYWORD", Pattern.compile("\\b💹|🔢|🚗|🏳️|🚩|🏁🏎\\b"));  //3
        TOKENPATTERNS.put("OPERATOR", Pattern.compile("[⏩➕➖➗❌💯🤯🌏]"));  //4
        TOKENPATTERNS.put("IDENTIFIER", Pattern.compile("[a-z][a-z]*"));   //5
        TOKENPATTERNS.put("NUMBER", Pattern.compile("^\\d+(\\.\\d{0,5})?$"));  //6
        TOKENPATTERNS.put("UNKNOWN", Pattern.compile("."));
    }

    public String getPattern(String type) {
        Pattern punctPattern = TOKENPATTERNS.get(type);
        String patternStr = punctPattern.pattern();
        patternStr = patternStr.replaceAll("\\s+", "");
        return patternStr;
    }
}
