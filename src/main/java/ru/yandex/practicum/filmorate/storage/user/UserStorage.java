package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.entity.User;

import java.util.List;

public interface UserStorage {
    User save(User user);

    User findUserById(Long id);

    List<User> findUsers();

    User update(User user);

    boolean addFriend(Long idUser, Long idFriend);

    boolean removeFriend(Long idUser, Long idFriend);
}
