package ru.sejapoe.digitalhotelserver.hotel.info.room;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import lombok.SneakyThrows;

import java.util.List;

public class RoomClassConverter implements AttributeConverter<List<RoomClass>, String> {
    final ObjectMapper mapper = new ObjectMapper();

    @SneakyThrows
    @Override
    public String convertToDatabaseColumn(List<RoomClass> attribute) {
        return mapper.writeValueAsString(attribute);
    }

    @SneakyThrows
    @Override
    public List<RoomClass> convertToEntityAttribute(String dbData) {
        return mapper.readValue(dbData, new TypeReference<>() {
        });
    }
}
