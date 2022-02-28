package bg.sofia.uni.fmi.mjt.cocktail.server.storage;

import bg.sofia.uni.fmi.mjt.cocktail.server.Cocktail;
import bg.sofia.uni.fmi.mjt.cocktail.server.storage.exceptions.CocktailAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.cocktail.server.storage.exceptions.CocktailNotFoundException;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DefaultCocktailStorage implements CocktailStorage {
    private final Set<Cocktail> cocktails;

    public DefaultCocktailStorage() {
        this.cocktails = new HashSet<>();
    }

    @Override
    public void createCocktail(Cocktail cocktail) throws CocktailAlreadyExistsException {
        if (this.cocktails.contains(cocktail)) {
            throw new CocktailAlreadyExistsException("The cocktail already exists");
        }

        this.cocktails.add(cocktail);
    }

    @Override
    public Collection<Cocktail> getCocktails() {
        return Collections.unmodifiableCollection(this.cocktails);
    }

    @Override
    public Collection<Cocktail> getCocktailsWithIngredient(String ingredientName) {
        return this.cocktails.stream()
                .filter(cocktail -> cocktail.contains(ingredientName))
                .toList();
    }

    @Override
    public Cocktail getCocktail(String name) throws CocktailNotFoundException {
        List<Cocktail> result = this.cocktails.stream()
                        .filter(cocktail -> cocktail.name().equals(name))
                        .toList();

        if (result.isEmpty()) {
            throw new CocktailNotFoundException("There isn't a cocktail with such name");
        }

        assert (result.size() == 1);

        return result.get(0);
    }
}
