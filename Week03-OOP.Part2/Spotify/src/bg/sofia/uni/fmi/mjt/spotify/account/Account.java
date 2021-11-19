package bg.sofia.uni.fmi.mjt.spotify.account;

import bg.sofia.uni.fmi.mjt.spotify.library.Library;
import bg.sofia.uni.fmi.mjt.spotify.playable.Playable;

import java.util.Objects;

public abstract class Account {
    private final String email;
    private final Library library;
    private double totalListenTime;
    private int numberOfContentPlayed;

    public Account(String email, Library library) {
        this.email = email;
        this.library = library;
    }

    /**
     * Returns the number of ads listened to.
     * - Free accounts get one ad after every 5 pieces of content played
     * - Premium accounts get no ads
     */
    public abstract int getAdsListenedTo();

    /**
     * Returns the account type as an enum with possible values FREE and PREMIUM
     */
    public abstract AccountType getType();

    /**
     * Simulates listening of the specified content.
     * This should increment the total number of content listened and the total listen time for this account.
     *
     * @param playable
     */
    public void listen(Playable playable) {
        ++this.numberOfContentPlayed;
        this.totalListenTime += playable.getDuration();
        playable.play();
    }

    /**
     * Returns the library for this account.
     */
    public Library getLibrary() {
        return library;
    }

    /**
     * Returns the total listen time for this account. The time for any ads listened is not included.
     */
    public double getTotalListenTime() {
        return totalListenTime;
    }

    public int getNumberOfContentPlayed() {
        return numberOfContentPlayed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Double.compare(account.totalListenTime, totalListenTime) == 0 && numberOfContentPlayed == account.numberOfContentPlayed && email.equals(account.email) && library.equals(account.library);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, library, totalListenTime, numberOfContentPlayed);
    }
}
