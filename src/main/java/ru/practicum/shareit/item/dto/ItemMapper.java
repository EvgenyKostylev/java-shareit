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
                .id(itemDto.id())
                .name(itemDto.name())
                .description(itemDto.description())
                .available(itemDto.available())
                .owner(itemDto.owner())
                .request(itemDto.request())
                .build();
    }

    public static Item updateItemFields(Item item, UpdatedItem updatedItem) {
        if (updatedItem.hasName()) {
            item.setName(updatedItem.name());
        }
        if (updatedItem.hasDescription()) {
            item.setDescription(updatedItem.description());
        }
        if (updatedItem.hasOwner()) {
            item.setOwner(updatedItem.owner());
        }
        if (updatedItem.hasAvailable()) {
            item.setAvailable(updatedItem.available());
        }
        if (updatedItem.hasRequest()) {
            item.setRequest(updatedItem.request());
        }

        return item;
    }
}