package vc.dark.minecraft.reflection.mappings.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** @noinspection Duplicates*/
public class YarnParser implements DataParser {

    private static Pattern yarnClassPattern = Pattern.compile("([a-z0-9A-Z\\-\\._$]+) -> ([a-z0-9A-Z\\-\\._$]+):", Pattern.MULTILINE);
    private static Pattern yarnFieldPattern = Pattern.compile("([a-zA-Z0-9_$]+) -> ([$a-zA-Z0-9_]+)", Pattern.MULTILINE);
    private static Pattern yarnMethodPattern = Pattern.compile("([<>a-zA-Z0-9_$]+)[\\(\\)].+ -> ([_a-zA-Z0-9$]+)", Pattern.MULTILINE);

    @Override
    public void parse(String[] lines, DataWriter out) {
        // Read data line by line
        String currentClass = null;
        String currentObfClass = null;
        for (String line : lines) {
            if (line.startsWith("#")) {
                continue;
            }
            // Check class
            Matcher classMatcher = yarnClassPattern.matcher(line);
            Matcher fieldMatcher = yarnFieldPattern.matcher(line);
            Matcher methodMatcher = yarnMethodPattern.matcher(line);
            if (classMatcher.find()) {
                currentClass = classMatcher.group(1);
                currentObfClass = classMatcher.group(2);
//                System.out.println(Strings.join(new String[] {currentClass, currentObfClass}, ","));
                out.clazz(currentClass, currentObfClass);
            } else if (currentClass == null || currentObfClass == null) {
                throw new IllegalArgumentException("Could not find current class while parsing " + line);
            } else if (fieldMatcher.find()) {
                String original = fieldMatcher.group(1);
                String obfuscated = fieldMatcher.group(2);
                //System.out.println(Strings.join(new String[] {currentClass, currentObfClass, original, obfuscated}, ","));
                out.field(currentClass, currentObfClass, original, obfuscated);
            } else if (methodMatcher.find()) {
                String original = methodMatcher.group(1);
                String obfuscated = methodMatcher.group(2);
                //System.out.println(Strings.join(new String[] {currentClass, currentObfClass, original, obfuscated}, ","));
                out.method(currentClass, currentObfClass, original, obfuscated);
            }
        }
    }
}
