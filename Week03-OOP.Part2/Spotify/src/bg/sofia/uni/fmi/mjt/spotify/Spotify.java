package bg.sofia.uni.fmi.mjt.spotify;

import bg.sofia.uni.fmi.mjt.spotify.account.Account;
import bg.sofia.uni.fmi.mjt.spotify.account.AccountType;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.AccountNotFoundException;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.PlayableNotFoundException;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.PlaylistCapacityExceededException;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.StreamingServiceException;
import bg.sofia.uni.fmi.mjt.spotify.playable.Playable;

import java.util.ServiceConfigurationError;

public class Spotify implements StreamingService {
    private final Account[] accounts;
    private final int accountsSize;

    private final Playable[] playableContent;
    private final int playableContentSize;

    public Spotify(Account[] accounts, Playable[] playableContent) {
        this.accountsSize = accounts.length;
        this.accounts = new Account[this.accountsSize];
        System.arraycopy(accounts, 0, this.accounts, 0, this.accountsSize);

        this.playableContentSize = playableContent.length;
        this.playableContent = new Playable[this.playableContentSize];
        System.arraycopy(playableContent, 0, this.playableContent, 0, this.playableContentSize);
    }

    @Override
    public void play(Account account, String title) throws AccountNotFoundException, PlayableNotFoundException {
        if (account == null || title == null || title.isEmpty()) {
            throw new IllegalArgumentException();
        }

        int searchedIndexAccount = this.getAccountIndex(account);

        if (searchedIndexAccount == -1) {
            throw new AccountNotFoundException();
        }

        int searchedIndexPlayable = this.getPlayableContentIndex(title);

        if (searchedIndexPlayable == -1) {
            throw new PlayableNotFoundException();
        }

        this.accounts[searchedIndexAccount].listen(this.playableContent[searchedIndexPlayable]);
    }

    @Override
    public void like(Account account, String title) throws AccountNotFoundException, PlayableNotFoundException, StreamingServiceException {
        if (account == null || title == null || title.isEmpty()) {
            throw new IllegalArgumentException();
        }

        int searchedIndexAccount = this.getAccountIndex(account);

        if (searchedIndexAccount == -1) {
            throw new AccountNotFoundException();
        }

        int searchedIndexPlayable = this.getPlayableContentIndex(title);

        if (searchedIndexPlayable == -1) {
            throw new PlayableNotFoundException();
        }

        try {
            this.accounts[searchedIndexAccount].getLibrary().getLiked().add(this.playableContent[searchedIndexPlayable]);
        } catch (PlaylistCapacityExceededException e) {
            throw new StreamingServiceException();
        }
    }

    @Override
    public Playable findByTitle(String title) throws PlayableNotFoundException {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException();
        }

        int searchedIndexPlayable = this.getPlayableContentIndex(title);

        if (searchedIndexPlayable == -1) {
            throw new PlayableNotFoundException();
        }

        return this.playableContent[searchedIndexPlayable];
    }

    @Override
    public Playable getMostPlayed() {
        if (playableContentSize == 0) {
            return null;
        }
        int bestIndex = 0;
        int mostPlays = this.playableContent[0].getTotalPlays();

        for (int i = 1; i < playableContentSize; ++i) {
            int currentPlays = this.playableContent[i].getTotalPlays();
            if (currentPlays > mostPlays) {
                bestIndex = i;
                mostPlays = currentPlays;
            }
        }

        return (this.playableContent[bestIndex].getTotalPlays() == 0) ? null : this.playableContent[bestIndex];
    }

    @Override
    public double getTotalListenTime() {
        double result = 0.0;
        for (int i = 0; i < accountsSize; ++i) {
            result += this.accounts[i].getTotalListenTime();
        }

        return result;
    }

    @Override
    public double getTotalPlatformRevenue() {
        double result = 0.0;
        for (int i = 0; i < accountsSize; ++i) {
            if (this.accounts[i].getType() == AccountType.FREE) {
                result += this.accounts[i].getAdsListenedTo()*0.10;
            } else {
                result += 25.00;
            }
        }

        return result;
    }

    private int getAccountIndex(Account account) {
        int searchedIndex = -1;
        for (int i = 0; i < accountsSize; ++i) {
            if (account.equals(this.accounts[i])) {
                searchedIndex = i;
                break;
            }
        }

        return searchedIndex;
    }

    private int getPlayableContentIndex(String title) {
        int searchedIndex = -1;
        for (int i = 0; i < playableContentSize; ++i) {
            if (title.equals(this.playableContent[i].getTitle())) {
                searchedIndex = i;
                break;
            }
        }

        return searchedIndex;
    }
}
