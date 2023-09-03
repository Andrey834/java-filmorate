package ru.yandex.practicum.filmorate.storage.event;


import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.model.event.Operation;

import java.util.List;

public interface EventStorage {
    void writeEvent(int userId, EventType eventType, Operation operation, int entityId);

    List<Event> getEvents(int userId);
}
