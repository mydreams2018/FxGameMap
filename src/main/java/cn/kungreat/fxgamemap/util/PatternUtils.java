package cn.kungreat.fxgamemap.util;


import java.util.regex.Pattern;

public class PatternUtils {

    public static final String NumberRegex = "^[1-9][0-9]*";

    public static final Pattern findFileName =  Pattern.compile("/[^/]+\\.json");
}
