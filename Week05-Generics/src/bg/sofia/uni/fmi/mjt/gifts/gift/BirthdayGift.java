package bg.sofia.uni.fmi.mjt.gifts.gift;

import bg.sofia.uni.fmi.mjt.gifts.person.Person;

import java.util.*;

public class BirthdayGift<T extends Priceable> implements Gift<T>, Comparable<BirthdayGift<T>> {
    private final Person<?> sender;
    private final Person<?> receiver;
    private final List<T> items;


    public BirthdayGift(Person<?> sender, Person<?> receiver, Collection<T> items) {
        this.sender = sender;
        this.receiver = receiver;
        this.items = new ArrayList<>();

        if (items != null) {
            this.items.addAll(items);
        }
    }

    @Override
    public int compareTo(BirthdayGift<T> o) {
        return Double.compare(o.getPrice(), this.getPrice());
    }

    @Override
    public Person<?> getSender() {
        return this.sender;
    }

    @Override
    public Person<?> getReceiver() {
        return this.receiver;
    }

    @Override
    public double getPrice() {
        double result = 0.0;
        for (T current : this.items) {
            result += current.getPrice();
        }

        return result;
    }

    @Override
    public void addItem(T t) {
        if (t == null) {
            throw new IllegalArgumentException();
        }

        this.items.add(t);
    }

    @Override
    public boolean removeItem(T t) {
        if (t == null || !this.items.contains(t)) {
            return false;
        }

        this.items.remove(t);
        return true;
    }

    @Override
    public Collection<T> getItems() {
        return List.copyOf(this.items);
    }

    @Override
    public T getMostExpensiveItem() {
        if (this.items.isEmpty()) {
            return null;
        }
        T result = this.items.get(0);
        int size = this.items.size();

        for (int i = 1; i < size; ++i) {
            if (this.items.get(i).getPrice() > result.getPrice()) {
                result = this.items.get(i);
            }
        }
        return result;
    }
}
