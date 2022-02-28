package bg.sofia.uni.fmi.mjt.twitch;

import bg.sofia.uni.fmi.mjt.twitch.content.Category;
import bg.sofia.uni.fmi.mjt.twitch.content.Content;
import bg.sofia.uni.fmi.mjt.twitch.content.stream.Stream;
import bg.sofia.uni.fmi.mjt.twitch.content.stream.StreamClass;
import bg.sofia.uni.fmi.mjt.twitch.content.video.Video;
import bg.sofia.uni.fmi.mjt.twitch.user.*;
import bg.sofia.uni.fmi.mjt.twitch.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StreamingPlatformTest {
    @Mock
    private UserService userServiceMock = Mockito.mock(UserService.class);

    @InjectMocks
    private StreamingPlatform streamingPlatform = new Twitch(userServiceMock);

    void userServiceDummyImplementation() {
        when(userServiceMock.getUsers()).thenReturn(Map.ofEntries(
                Map.entry("p_varna", new UserClass("p_varna")),
                Map.entry("sonik27", new UserClass("sonik27")),
                Map.entry("marji", new UserClass("marji")),
                Map.entry("joroit", new UserClass("joroit")),
                Map.entry("sozonov", new UserClass("sozonov"))
                ));

        this.streamingPlatform = new Twitch(userServiceMock);
    }

    void makeValidContentsWithMultipleWatches() throws UserNotFoundException, UserStreamingException {
        Stream peshoStream = streamingPlatform.startStream("p_varna", "Federer vs. Dimitrov", Category.ESPORTS);

        streamingPlatform.watch("sonik27", peshoStream);
        streamingPlatform.watch("marji", peshoStream);
        streamingPlatform.watch("marji", peshoStream);
        streamingPlatform.watch("sonik27", peshoStream);
        streamingPlatform.watch("sozonov", peshoStream);
        streamingPlatform.watch("sonik27", peshoStream);
        streamingPlatform.watch("sozonov", peshoStream);
        streamingPlatform.watch("sozonov", peshoStream);

        Video peshoVideo = streamingPlatform.endStream("p_varna", peshoStream);

        streamingPlatform.watch("p_varna", peshoVideo);
        streamingPlatform.watch("sonik27", peshoVideo);
        streamingPlatform.watch("sozonov", peshoVideo);
        streamingPlatform.watch("sozonov", peshoVideo);
        streamingPlatform.watch("sozonov", peshoVideo);
        streamingPlatform.watch("sozonov", peshoVideo);

        Stream radoStream = streamingPlatform.startStream("sozonov", "Galena Party Mix", Category.MUSIC);

        streamingPlatform.watch("p_varna", radoStream);
        streamingPlatform.watch("p_varna", radoStream);
        streamingPlatform.watch("p_varna", radoStream);
        streamingPlatform.watch("p_varna", radoStream);
        streamingPlatform.watch("p_varna", radoStream);
        streamingPlatform.watch("p_varna", radoStream);
        streamingPlatform.watch("p_varna", radoStream);
        streamingPlatform.watch("p_varna", radoStream);
        streamingPlatform.watch("p_varna", radoStream);

        Video radoVideo = streamingPlatform.endStream("sozonov", radoStream);

        streamingPlatform.watch("p_varna", radoVideo);

        Stream soncheStream = streamingPlatform.startStream("sonik27", "Sims Home Tour", Category.GAMES);

        streamingPlatform.watch("p_varna", soncheStream);
        streamingPlatform.watch("p_varna", soncheStream);
        streamingPlatform.watch("p_varna", soncheStream);
        streamingPlatform.watch("p_varna", soncheStream);
        streamingPlatform.watch("p_varna", soncheStream);
        streamingPlatform.watch("p_varna", soncheStream);
        streamingPlatform.watch("p_varna", soncheStream);
        streamingPlatform.watch("p_varna", soncheStream);
        streamingPlatform.watch("p_varna", soncheStream);
        streamingPlatform.watch("p_varna", soncheStream);
        streamingPlatform.watch("p_varna", soncheStream);
        streamingPlatform.watch("p_varna", soncheStream);
        streamingPlatform.watch("p_varna", soncheStream);
    }

    void makeContentWithNoWatchers() throws UserNotFoundException, UserStreamingException {
        Stream peshoStream = streamingPlatform.startStream("p_varna", "Federer vs. Dimitrov", Category.ESPORTS);
        streamingPlatform.endStream("p_varna", peshoStream);
        Stream radoStream = streamingPlatform.startStream("sozonov", "Galena Party Mix", Category.MUSIC);
        streamingPlatform.endStream("sozonov", radoStream);
        streamingPlatform.startStream("sonik27", "Sims Home Tour", Category.GAMES);
    }

    @Test
    void testStartStreamWithNullUsername() {
        assertThrows(IllegalArgumentException.class, () -> this.streamingPlatform.startStream(null, "test", Category.MUSIC));
    }

    @Test
    void testStartStreamWithEmptyUsername() {
        assertThrows(IllegalArgumentException.class, () -> this.streamingPlatform.startStream("", "test", Category.MUSIC));
    }

    @Test
    void testStartStreamWithNullTitle() {
        assertThrows(IllegalArgumentException.class, () -> this.streamingPlatform.startStream("test", null, Category.MUSIC));
    }

    @Test
    void testStartStreamWithEmptyTitle() {
        assertThrows(IllegalArgumentException.class, () -> this.streamingPlatform.startStream("test", "", Category.MUSIC));
    }

    @Test
    void testStartStreamWithNullCategory() {
        assertThrows(IllegalArgumentException.class, () -> this.streamingPlatform.startStream("test", "test", null));
    }

    @Test
    void testStartStreamWithUserNotInService() {
        userServiceDummyImplementation();

        assertThrows(UserNotFoundException.class, () -> this.streamingPlatform.startStream("gosho", "test", Category.MUSIC));
    }

    @Test
    void testStartStreamWithUserWhoIsStreaming() throws UserNotFoundException, UserStreamingException {
        userServiceDummyImplementation();

        this.streamingPlatform.startStream("p_varna", "test", Category.ESPORTS);
        assertThrows(UserStreamingException.class, () -> this.streamingPlatform.startStream("p_varna", "test", Category.ESPORTS));
    }

    @Test
    void testStartStreamWithValidUser() throws UserNotFoundException, UserStreamingException {
        userServiceDummyImplementation();

        Stream stream = this.streamingPlatform.startStream("p_varna", "Federer vs. Dimitrov", Category.ESPORTS);
        assertEquals("p_varna", stream.getMetadata().streamer().getName());
        assertEquals("Federer vs. Dimitrov", stream.getMetadata().title());
        assertEquals(Category.ESPORTS, stream.getMetadata().category());
        assertEquals(UserStatus.STREAMING, this.userServiceMock.getUsers().get("p_varna").getStatus());
    }

    @Test
    void testEndStreamWithNullUsername() {
        assertThrows(IllegalArgumentException.class, () -> this.streamingPlatform.endStream(null, new StreamClass("test", Category.MUSIC, null)));
    }

    @Test
    void testEndStreamWithEmptyUsername() {
        assertThrows(IllegalArgumentException.class, () -> this.streamingPlatform.endStream("", new StreamClass("test", Category.MUSIC, null)));
    }

    @Test
    void testEndStreamWithNullStream() {
        assertThrows(IllegalArgumentException.class, () -> this.streamingPlatform.endStream("test", null));
    }

    @Test
    void testEndStreamWithUserNotInService() {
        userServiceDummyImplementation();

        assertThrows(UserNotFoundException.class, () -> this.streamingPlatform.endStream("gosho", new StreamClass("test", Category.MUSIC, null)));
    }

    @Test
    void testEndStreamWithUserWhoIsOffline() {
        userServiceDummyImplementation();

        assertThrows(UserStreamingException.class, () -> this.streamingPlatform.endStream("p_varna", new StreamClass("test", Category.MUSIC, null)));
    }

    @Test
    void testEndStreamWithValidUser() throws UserNotFoundException, UserStreamingException {
        userServiceDummyImplementation();

        Stream stream = this.streamingPlatform.startStream("p_varna", "Federer vs. Dimitrov", Category.ESPORTS);

        assertEquals(UserStatus.STREAMING, this.userServiceMock.getUsers().get("p_varna").getStatus());

        Video video = this.streamingPlatform.endStream("p_varna", stream);

        assertEquals("p_varna", video.getMetadata().streamer().getName());
        assertEquals("Federer vs. Dimitrov", video.getMetadata().title());
        assertEquals(Category.ESPORTS, video.getMetadata().category());
        assertEquals(UserStatus.OFFLINE, this.userServiceMock.getUsers().get("p_varna").getStatus());
    }

    @Test
    void testWatchWithNullUsername() {
        assertThrows(IllegalArgumentException.class, () -> this.streamingPlatform.watch(null, new StreamClass(null, null, null)));
    }

    @Test
    void testWatchWithEmptyUsername() {
        assertThrows(IllegalArgumentException.class, () -> this.streamingPlatform.watch("", new StreamClass(null, null, null)));
    }

    @Test
    void testWatchWithNullContent() {
        assertThrows(IllegalArgumentException.class, () -> this.streamingPlatform.watch("test", null));
    }

    @Test
    void testWatchWithUserNotInService() {
        userServiceDummyImplementation();

        assertThrows(UserNotFoundException.class, () -> this.streamingPlatform.watch("gosho", new StreamClass(null, null, null)));
    }

    @Test
    void testWatchWithUserWhoIsStreaming() throws UserNotFoundException, UserStreamingException {
        userServiceDummyImplementation();

        this.streamingPlatform.startStream("p_varna", "test", Category.ESPORTS);

        assertThrows(UserStreamingException.class, () -> this.streamingPlatform.watch("p_varna", new StreamClass(null, null, null)));
    }

    @Test
    void testGetMostWatchedStreamerWhenNobodyHasStreamed() {
        assertNull(this.streamingPlatform.getMostWatchedStreamer());
    }

    @Test
    void testGetMostWatchedStreamerWhenBestStreamerHasZeroWatchers() throws UserNotFoundException, UserStreamingException {
        userServiceDummyImplementation();

        makeContentWithNoWatchers();

        User result = this.streamingPlatform.getMostWatchedStreamer();

        assertNull(this.streamingPlatform.getMostWatchedStreamer());
    }

    @Test
    void testGetMostWatchedStreamerWithValidContentsAndWithWatches() throws UserNotFoundException, UserStreamingException {
        userServiceDummyImplementation();

        makeValidContentsWithMultipleWatches();

        User result = this.streamingPlatform.getMostWatchedStreamer();
        assertEquals("sonik27", result.getName());
    }

    @Test
    void testGetMostWatchedContentWhenThereIsNoContent() {
        assertNull(this.streamingPlatform.getMostWatchedContent());
    }

    @Test
    void testGetMostWatchedContentWhenBestContentHasZeroWatches() throws UserNotFoundException, UserStreamingException {
        userServiceDummyImplementation();

        makeContentWithNoWatchers();

        assertNull(this.streamingPlatform.getMostWatchedContent());
    }

    @Test
    void testGetMostWatchedContentWithValidContentsAndWithWatches() throws UserNotFoundException, UserStreamingException {
        userServiceDummyImplementation();

        makeValidContentsWithMultipleWatches();

        Content result = this.streamingPlatform.getMostWatchedContent();
        assertEquals("sonik27", result.getMetadata().streamer().getName());
        assertEquals("Sims Home Tour", result.getMetadata().title());
        assertEquals(Category.GAMES, result.getMetadata().category());
        assertEquals(13, result.getNumberOfViews());
    }

    @Test
    void testGetMostWatchedContentFromWithNullUsername() {
        assertThrows(IllegalArgumentException.class, () -> this.streamingPlatform.getMostWatchedContentFrom(null));
    }

    @Test
    void testGetMostWatchedContentFromWithEmptyUsername() {
        assertThrows(IllegalArgumentException.class, () -> this.streamingPlatform.getMostWatchedContentFrom(""));
    }

    @Test
    void testGetMostWatchedContentFromWhenThereIsNoContent() throws UserNotFoundException {
        userServiceDummyImplementation();

        assertNull(this.streamingPlatform.getMostWatchedContentFrom("p_varna"));
    }

    @Test
    void testGetMostWatchedContentFromWithUserNotInService() {
        userServiceDummyImplementation();

        assertThrows(UserNotFoundException.class, () -> this.streamingPlatform.getMostWatchedContentFrom("gosho"));
    }

    @Test
    void testGetMostWatchedContentFromWithUserWhoHasNeverStreamed() throws UserNotFoundException, UserStreamingException {
        userServiceDummyImplementation();

        makeValidContentsWithMultipleWatches();

        assertNull(this.streamingPlatform.getMostWatchedContentFrom("joroit"));
    }

    @Test
    void testGetMostWatchedContentFromWhenBestContentHasZeroWatches() throws UserNotFoundException, UserStreamingException {
        userServiceDummyImplementation();

        makeContentWithNoWatchers();

        assertNull(this.streamingPlatform.getMostWatchedContentFrom("p_varna"));
    }

    @Test
    void testGetMostWatchedContentFromWithValidContentsAndWithWatches() throws UserNotFoundException, UserStreamingException {
        userServiceDummyImplementation();

        makeValidContentsWithMultipleWatches();

        Content pesho = this.streamingPlatform.getMostWatchedContentFrom("p_varna");
        Content rado = this.streamingPlatform.getMostWatchedContentFrom("sozonov");
        Content sonche = this.streamingPlatform.getMostWatchedContentFrom("sonik27");

        assertEquals("p_varna", pesho.getMetadata().streamer().getName());
        assertEquals("Federer vs. Dimitrov", pesho.getMetadata().title());
        assertEquals(Category.ESPORTS, pesho.getMetadata().category());
        assertEquals(6, pesho.getNumberOfViews());

        assertEquals("sozonov", rado.getMetadata().streamer().getName());
        assertEquals("Galena Party Mix", rado.getMetadata().title());
        assertEquals(Category.MUSIC, rado.getMetadata().category());
        assertEquals(1, rado.getNumberOfViews());

        assertEquals("sonik27", sonche.getMetadata().streamer().getName());
        assertEquals("Sims Home Tour", sonche.getMetadata().title());
        assertEquals(Category.GAMES, sonche.getMetadata().category());
        assertEquals(13, sonche.getNumberOfViews());
    }

    @Test
    void testGetMostWatchedCategoriesByWithNullUsername() {
        assertThrows(IllegalArgumentException.class, () -> this.streamingPlatform.getMostWatchedCategoriesBy(null));
    }

    @Test
    void testGetMostWatchedCategoriesByWithEmptyUsername() {
        assertThrows(IllegalArgumentException.class, () -> this.streamingPlatform.getMostWatchedCategoriesBy(""));
    }

    @Test
    void testGetMostWatchedCategoriesByWithUserNotInService() {
        userServiceDummyImplementation();

        assertThrows(UserNotFoundException.class, () -> this.streamingPlatform.getMostWatchedCategoriesBy("gosho"));
    }

    @Test
    void testGetMostWatchedCategoriesByWithValidContentsAndWithWatches() throws UserNotFoundException, UserStreamingException {
        userServiceDummyImplementation();

        makeValidContentsWithMultipleWatches();

        List<Category> pesho = this.streamingPlatform.getMostWatchedCategoriesBy("p_varna");
        assertTrue(pesho.getClass().getName().contains("ImmutableCollection"));
        assertEquals(3, pesho.size());
        assertEquals(Category.GAMES, pesho.get(0));
        assertEquals(Category.MUSIC, pesho.get(1));
        assertEquals(Category.ESPORTS, pesho.get(2));

        List<Category> rado = this.streamingPlatform.getMostWatchedCategoriesBy("sozonov");
        assertTrue(rado.getClass().getName().contains("ImmutableCollection"));
        assertEquals(1, rado.size());
        assertEquals(Category.ESPORTS, rado.get(0));

        List<Category> gosho = this.streamingPlatform.getMostWatchedCategoriesBy("joroit");
        assertTrue(gosho.getClass().getName().contains("ImmutableCollection"));
        assertTrue(gosho.isEmpty());
    }
}