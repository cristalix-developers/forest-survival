package me.reidj.forest.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : Рейдж
 * @project : forest
 **/

@AllArgsConstructor
@Getter
public enum Knowledge {
    APPLE("apple", "Новый предмет"),
    FLINT("flint", "Новый предмет"),
    HEAL("heal", "Новый предмет"),
    TOXIC("red_mushroom", "Новый предмет"),
    STONE("stone", "Новый предмет"),
    COLD("cold", "Новая земля"),
    HOT("hot", "Новая земля"),
    ;

    private final String picture;
    private final String message;
}
