package bg.sofia.uni.fmi.mjt.cocktail.server;

import bg.sofia.uni.fmi.mjt.cocktail.server.command.CommandHandler;
import bg.sofia.uni.fmi.mjt.cocktail.server.storage.DefaultCocktailStorage;

public class Main {
    public static void main(String[] args) {
        Server server = new Server(3945, new CommandHandler(new DefaultCocktailStorage()));

        server.start();

        //server.stop();
    }
}
