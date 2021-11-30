package bg.sofia.uni.fmi.mjt.gifts.service;

import bg.sofia.uni.fmi.mjt.gifts.gift.BirthdayGift;
import bg.sofia.uni.fmi.mjt.gifts.gift.Gift;
import bg.sofia.uni.fmi.mjt.gifts.gift.Priceable;
import bg.sofia.uni.fmi.mjt.gifts.person.Person;

import java.util.Collection;
import java.util.List;

public class DefaultPackingService<T extends Priceable> implements PackingService<T> {
    @Override
    public Gift<T> pack(Person<?> sender, Person<?> receiver, T item) {
        if (sender == null || receiver == null || item == null) {
            throw new IllegalArgumentException();
        }

        return new BirthdayGift<>(sender, receiver, List.of(item));
    }

    @SafeVarargs
    @Override
    public final Gift<T> pack(Person<?> sender, Person<?> receiver, T... items) {
        if (sender == null || receiver == null || items == null) {
            throw new IllegalArgumentException();
        }

        for (T current : items) {
            if (current == null) {
                throw new IllegalArgumentException();
            }
        }

        return new BirthdayGift<>(sender, receiver, List.of(items));
    }

    @Override
    public Collection<T> unpack(Gift<T> gift) {
        if (gift == null) {
            throw new IllegalArgumentException();
        }

        return gift.getItems();
    }
}
