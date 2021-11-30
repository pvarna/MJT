package bg.sofia.uni.fmi.mjt.gifts.person;

import bg.sofia.uni.fmi.mjt.gifts.exception.WrongReceiverException;
import bg.sofia.uni.fmi.mjt.gifts.gift.Gift;

import java.util.*;

public class DefaultPerson<I> implements Person<I> {
    private final I id;
    private final Queue<Gift<?>> gifts;

    public DefaultPerson(I id) {
        this.id = id;
        this.gifts = new PriorityQueue<>();
    }

    @Override
    public Collection<Gift<?>> getNMostExpensiveReceivedGifts(int n) {
        if (n == 0) {
            return Collections.emptySet();
        }
        if (n < 0) {
            throw new IllegalArgumentException();
        }
        int size = this.gifts.size();
        if (n > size) {
            return List.copyOf(this.gifts);
        }

        List<Gift<?>> result = new ArrayList<>();

        for (int i = 0; i < n; ++i) {
            result.add(this.gifts.poll());
        }

        this.gifts.addAll(result);

        return List.copyOf(result);
    }

    @Override
    public Collection<Gift<?>> getGiftsBy(Person<?> person) {
        if (person == null) {
            throw new IllegalArgumentException();
        }

        List<Gift<?>> result = new ArrayList<>();

        for (Gift<?> current : this.gifts) {
            if (current.getSender().equals(person)) {
                result.add(current);
            }
        }

        if (result.isEmpty()) {
            return Collections.emptyList();
        }

        return List.copyOf(result);
    }

    @Override
    public I getId() {
        return this.id;
    }

    @Override
    public void receiveGift(Gift<?> gift) {
        if (gift == null) {
            throw new IllegalArgumentException();
        }

        if (!gift.getReceiver().equals(this)) {
            throw new WrongReceiverException();
        }

        this.gifts.add(gift);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultPerson<?> that = (DefaultPerson<?>) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
