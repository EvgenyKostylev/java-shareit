package ru.practicum.shareit.request.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Entity
@Table(name = "item_requests")
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requestor_id")
    @ToString.Exclude
    private User requestor;

    @Column
    private String description;

    @Column
    private LocalDateTime created = LocalDateTime.now();
}