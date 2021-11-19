package bg.sofia.uni.fmi.mjt.spotify.library;

import bg.sofia.uni.fmi.mjt.spotify.exceptions.EmptyLibraryException;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.LibraryCapacityExceededException;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.PlaylistNotFoundException;
import bg.sofia.uni.fmi.mjt.spotify.playlist.Playlist;
import bg.sofia.uni.fmi.mjt.spotify.playlist.UserPlaylist;

public class UserLibrary implements Library {
    private Playlist[] playlists;
    private int size;

    public UserLibrary() {
        this.playlists = new Playlist[21];
        this.playlists[0] = new UserPlaylist("Liked Content");
        this.size = 1;
    }

    @Override
    public void add(Playlist playlist) throws LibraryCapacityExceededException {
        if (this.size == 21) {
            throw new LibraryCapacityExceededException();
        }

        this.playlists[this.size++] = playlist;
    }

    @Override
    public void remove(String name) throws EmptyLibraryException, PlaylistNotFoundException {
        if (name == null || name.equals("Liked Content")) {
            throw new IllegalArgumentException();
        }
        if (this.size == 0) {
            throw new EmptyLibraryException();
        }

        int searchedIndex = -1;
        for (int i = 0; i < this.size; ++i) {
            if (name.equals(this.playlists[i].getName())) {
                searchedIndex = i;
                break;
            }
        }

        if (searchedIndex == -1) {
            throw new PlaylistNotFoundException();
        }

        for (int i = searchedIndex; i < this.size-1; ++i) {
            this.playlists[i] = this.playlists[i+1];
        }

        this.playlists[--this.size] = null;
    }

    @Override
    public Playlist getLiked() {
        return this.playlists[0];
    }
}
