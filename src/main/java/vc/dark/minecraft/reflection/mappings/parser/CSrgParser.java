package vc.dark.minecraft.reflection.mappings.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CSrgParser implements DataParser {

    private static Pattern csrgClassPattern = Pattern.compile("([a-zA-Z0-9/$]+) ([a-zA-Z0-9/$_]+)", Pattern.MULTILINE);

    @Override
    public void parse(String[] lines, DataWriter out) {
        // This will both parse -cl.csrg and -members.csrg files.
        String firstLine = "";
        for (String data : lines) {
            if (data.startsWith("#")) {
                continue;
            }
            firstLine = data;
            break;
        }
        String[] check = firstLine.split(" ");
        if (check.length == 2) {
            parseBukkitClasses(lines, out);
        } else {
            parseBukkitMembers(lines, out);
        }
    }

    private void parseBukkitClasses(String[] lines, DataWriter out) {
        for (String data : lines) {
            if (data.startsWith("#")) {
                continue;
            }
            Matcher m = csrgClassPattern.matcher(data);
            while (m.find()) {
                String original = m.group(2).replace("/", ".");
                String obfuscated = m.group(1).replace("/", ".");
                out.clazz(original, obfuscated);
            }
        }
    }

    private void parseBukkitMembers(String[] lines, DataWriter out) {
        // Read data line by line
        for (String line : lines) {
            if (line.startsWith("#")) {
                continue;
            }
            // Split here
            String[] whitespace = line.split(" ");
            String className = whitespace[0].replace("/", ".");
            String original = whitespace[3];
            String obfuscated = whitespace[1];
            out.method(className, "", original, obfuscated);
        }
    }
}
