package bg.sofia.uni.fmi.mjt.cocktail.server.command;

import bg.sofia.uni.fmi.mjt.cocktail.server.Cocktail;
import bg.sofia.uni.fmi.mjt.cocktail.server.Ingredient;
import bg.sofia.uni.fmi.mjt.cocktail.server.storage.CocktailStorage;
import bg.sofia.uni.fmi.mjt.cocktail.server.storage.exceptions.CocktailAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.cocktail.server.storage.exceptions.CocktailNotFoundException;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommandHandler {
    private static final String INVALID_ARGS_COUNT_MESSAGE_FORMAT =
            "Invalid count of arguments: \"%s\" expects %s argument%s. Example: \"%s\"";

    private static final String CREATE = "create";
    private static final String GET = "get";
    private static final String ALL = "all";
    private static final String BY_NAME = "by-name";
    private static final String BY_INGREDIENT = "by-ingredient";
    private static final String DISCONNECT = "disconnect";

    private CocktailStorage cocktailStorage;

    public CommandHandler(CocktailStorage cocktailStorage) {
        this.cocktailStorage = cocktailStorage;
    }

    public String execute(Command cmd) {
        return switch (cmd.mainCommand()) {
            case GET -> switch (cmd.arguments().get(0)) {
                case ALL -> this.getAll(cmd.arguments());
                case BY_NAME -> this.getByName(cmd.arguments());
                case BY_INGREDIENT -> this.getByIngredient(cmd.arguments());
                default -> "Unknown command";
            };
            case CREATE -> this.create(cmd.arguments());
            case DISCONNECT -> "Disconnecting client...";
            default -> "Unknown command";
        };
    }

    private String create(List<String> arguments) {
        if (arguments.size() < 2) {
            return String.format(INVALID_ARGS_COUNT_MESSAGE_FORMAT, GET + " " + ALL, "at least 2", "s", CREATE + "<coctailName> <ingredient1> ... <ingredientN>");
        }

        String cocktailName = arguments.get(0);
        Set<Ingredient> ingredients = new HashSet<>();

        for (int i = 1; i < arguments.size(); ++i) {
            String[] currentIngredient = arguments.get(i).split("=");

            ingredients.add(new Ingredient(currentIngredient[0], currentIngredient[1]));
        }

        try {
            this.cocktailStorage.createCocktail(new Cocktail(cocktailName, ingredients));
        } catch (CocktailAlreadyExistsException e) {
            return e.getMessage();
        }

        return "Cocktail successfully created";
    }

    private String getAll(List<String> arguments) {
        if (arguments.size() != 1) {
            return String.format(INVALID_ARGS_COUNT_MESSAGE_FORMAT, GET + " " + ALL, 0, "s", GET + " " + ALL);
        }

        Collection<Cocktail> cocktails = this.cocktailStorage.getCocktails();

        return this.getCocktailsString(cocktails);
    }

    private String getByName(List<String> arguments) {
        if (arguments.size() != 2) {
            return String.format(INVALID_ARGS_COUNT_MESSAGE_FORMAT, GET + " " + BY_NAME, 1, "", GET + " " + BY_NAME + " <cocktailName>");
        }

        Cocktail cocktail = null;

        try {
            cocktail = this.cocktailStorage.getCocktail(arguments.get(1));
        } catch (CocktailNotFoundException e) {
            return e.getMessage();
        }

        return this.getSingleCocktailString(cocktail);
    }

    private String getByIngredient(List<String> arguments) {
        if (arguments.size() != 2) {
            return String.format(INVALID_ARGS_COUNT_MESSAGE_FORMAT, GET + " " + BY_INGREDIENT, 1, "", GET + " " + BY_INGREDIENT + " <ingredientName>");
        }

        Collection<Cocktail> cocktails = this.cocktailStorage.getCocktailsWithIngredient(arguments.get(1));

        return this.getCocktailsString(cocktails);
    }

    private String getCocktailsString(Collection<Cocktail> cocktails) {
        StringBuilder result = new StringBuilder();

        for (Cocktail cocktail : cocktails) {
            result.append(this.getSingleCocktailString(cocktail));
        }

        return result.toString();
    }

    private String getSingleCocktailString(Cocktail cocktail) {
        StringBuilder result = new StringBuilder();

        result.append("Name: ").append(cocktail.name()).append(System.lineSeparator());
        result.append("Ingredients: ").append(System.lineSeparator());
        result.append("{").append(System.lineSeparator());
        for (Ingredient ingredient : cocktail.ingredients()) {
            result.append('\t' + "Name: ").append(ingredient.name())
                    .append(", Amount: ").append(ingredient.amount()).append(System.lineSeparator());
        }
        result.append("}").append(System.lineSeparator());

        return result.toString();
    }
}
