package bg.sofia.uni.fmi.mjt.cocktail.server.command;

import java.util.ArrayList;
import java.util.List;

public class CommandParser {

    private static List<String> getCommandArguments(String input) {
        List<String> result = new ArrayList<>();

        StringBuilder builder = new StringBuilder();

        boolean insideQuote = false;

        for (char ch : input.toCharArray()) {
            if (ch == '"') {
                insideQuote = !insideQuote;
            }

            if (ch == ' ' && !insideQuote) {
                if (!builder.isEmpty()) {
                    result.add(builder.toString().replace("\"", ""));
                }
                builder.delete(0, builder.length());
            } else {
                builder.append(ch);
            }
        }

        result.add(builder.toString().replace("\"", ""));

        return result;
    }

    public static Command newCommand(String input) {
        List<String> tokens = CommandParser.getCommandArguments(input);
        List<String> arguments = tokens.subList(1, tokens.size());

        System.out.println("Main command: " + tokens.get(0));
        System.out.println("Arguments: " + arguments);
        return new Command(tokens.get(0), arguments);
    }
}
