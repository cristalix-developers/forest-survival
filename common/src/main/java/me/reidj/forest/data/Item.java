package me.reidj.forest.data;

import lombok.Data;

/**
 * @author : Рейдж
 * @project : forest
 **/

@Data
public class Item {
    private final ItemList itemList;
    private final int amount;
    private final int slot;
}
