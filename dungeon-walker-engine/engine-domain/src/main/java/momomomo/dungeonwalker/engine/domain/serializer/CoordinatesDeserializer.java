package momomomo.dungeonwalker.engine.domain.serializer;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import momomomo.dungeonwalker.engine.domain.model.coordinates.Coordinates;

import java.io.IOException;

public class CoordinatesDeserializer extends KeyDeserializer {

    @Override
    public Coordinates deserializeKey(
            final String key,
            final DeserializationContext deserializationContext) throws IOException {
        final var xy = key.substring(1, key.length() - 1).split(",");
        return Coordinates.of(
                Integer.parseInt(xy[0]),
                Integer.parseInt(xy[1]));
    }

}
