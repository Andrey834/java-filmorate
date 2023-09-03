package ru.yandex.practicum.filmorate.storage.like;

public interface LikeStorage {
    void addLike(int userId, int filmId);

    void removeLike(int userId, int filmId);

    Integer getUserIdWithCommonLikes(int userId);
}
