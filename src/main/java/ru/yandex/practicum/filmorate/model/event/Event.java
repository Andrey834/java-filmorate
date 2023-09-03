package ru.yandex.practicum.filmorate.model.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
public class Event {
    @JsonFormat(shape = JsonFormat.Shape.NUMBER, without = JsonFormat.Feature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
    @JsonProperty(value = "timestamp")
    private Instant createTime;
    private int userId;
    private EventType eventType;
    private Operation operation;
    private int eventId;
    private int entityId;
}
