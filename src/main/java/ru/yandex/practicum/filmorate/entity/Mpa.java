package ru.yandex.practicum.filmorate.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.entity.enums.MpaName;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mpa {
    private Long id;
    private String name;

    public Mpa(Long id) {
        this.id = id;
        this.name = setMpaName(String.valueOf(id));
    }

    public String setMpaName(String mpa) {
        switch (mpa) {
            case "0":
                return "UNKNOWN";
            case "1":
                return MpaName.G.getVale();
            case "2":
                return MpaName.PG.getVale();
            case "3":
                return MpaName.PG13.getVale();
            case "4":
                return MpaName.R.getVale();
            case "5":
                return MpaName.NC17.getVale();
            default:
                throw new MpaNotFoundException("Mpa not found: " + mpa);
        }
    }
}
