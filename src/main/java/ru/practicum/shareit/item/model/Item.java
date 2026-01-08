package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

/**
 * TODO Sprint add-controllers.
 */
@Entity
@Table(name = "items")
@Getter
@Setter
@ToString
@EqualsAndHashCode(exclude = {"owner", "request"})
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private Boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    @ToString.Exclude
    private User owner;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    @ToString.Exclude
    private ItemRequest request;
}