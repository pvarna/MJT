package bg.sofia.uni.fmi.mjt.spotify.playlist;

import bg.sofia.uni.fmi.mjt.spotify.exceptions.PlaylistCapacityExceededException;
import bg.sofia.uni.fmi.mjt.spotify.playable.Playable;

public class UserPlaylist implements Playlist {
    private final String name;
    private final Playable[] playableContent;
    private int size;

    public UserPlaylist(String name) {
        this.name = name;
        this.playableContent = new Playable[20];
        this.size = 0;
    }

    @Override
    public void add(Playable playable) throws PlaylistCapacityExceededException {
        if (this.size == 20) {
            throw new PlaylistCapacityExceededException();
        }
        this.playableContent[this.size++] = playable;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
