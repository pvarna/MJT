package bg.sofia.uni.fmi.mjt.cocktail.server.command;

import java.util.List;

public record Command(String mainCommand, List<String> arguments) {
}
