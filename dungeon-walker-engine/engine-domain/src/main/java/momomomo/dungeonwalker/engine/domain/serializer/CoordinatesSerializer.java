package momomomo.dungeonwalker.engine.domain.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import momomomo.dungeonwalker.engine.domain.model.coordinates.Coordinates;

import java.io.IOException;

public class CoordinatesSerializer extends JsonSerializer<Coordinates> {

    @Override
    public void serialize(
            final Coordinates coordinates,
            final JsonGenerator jsonGenerator,
            final SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeFieldName(coordinates.toString());
    }

}
