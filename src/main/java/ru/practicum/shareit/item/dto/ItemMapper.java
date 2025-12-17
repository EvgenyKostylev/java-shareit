package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                item.getRequest());
    }

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(itemDto.getOwner())
                .request(itemDto.getRequest())
                .build();
    }

    public static Item updateItemFields(Item item, ItemDto updatedItem) {
        if (updatedItem.hasName()) {
            item.setName(updatedItem.getName());
        }
        if (updatedItem.hasDescription()) {
            item.setDescription(updatedItem.getDescription());
        }
        if (updatedItem.hasOwner()) {
            item.setOwner(updatedItem.getOwner());
        }
        if (updatedItem.hasAvailable()) {
            item.setAvailable(updatedItem.getAvailable());
        }
        if (updatedItem.hasRequest()) {
            item.setRequest(updatedItem.getRequest());
        }

        return item;
    }
}