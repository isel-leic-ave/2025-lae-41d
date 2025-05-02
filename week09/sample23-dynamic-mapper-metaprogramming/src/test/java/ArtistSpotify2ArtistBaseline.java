import kotlin.Pair;
import pt.isel.Artist;
import pt.isel.ArtistSpotify;
import pt.isel.Mapper;
import pt.isel.State;
import pt.isel.Country;
import java.util.Map;

import static java.util.Map.entry;

public class ArtistSpotify2ArtistBaseline implements Mapper<ArtistSpotify, Artist> {
    private static Map< Pair< Class<?>, Class<?> >, Mapper<?, ?> > mappers = Map.ofEntries(
            entry(new Pair<>(Country.class, State.class), new Country2StateBaseline())
    );

    private static <T, R> Mapper<T, R> loadMapper(Class<T> srcType, Class<R> destType) {
        return (Mapper<T, R>) mappers.get(new Pair<>(srcType, destType));
    }

    @Override
    public Artist mapFrom(ArtistSpotify src) {
        return new Artist(
                src.getName(),
                src.getKind(),
                src.getId(),
                loadMapper(Country.class, State.class).mapFrom(src.getCountry())
        );
        /*
        // 1. cast src to ArtistSpotify
        ArtistSpotify from = src;

        // 2. For each property read its value from srcArtist
        String name = from.getName();
        String type = from.getKind();
        int id = from.getId();
        State state = loadMapper(Country.class, State.class).mapFrom(src.getCountry());

        // 3. Instantiate Artist with the values of src properties
        return new Artist(type, name, id, state);
        */
    }
}

class Country2StateBaseline implements Mapper<Country, State> {

    @Override
    public State mapFrom(Country src) {
        return new State(src.getName(), src.getIdiom());
    }
}