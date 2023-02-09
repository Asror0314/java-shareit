package ru.practicum.shareit.item.model;

import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.type.BooleanType;
import org.hibernate.type.TrueFalseType;
import ru.practicum.shareit.user.User;

import javax.persistence.*;

/**
 * TODO Sprint add-controllers.
 */
@Entity
@Table(name = "item")
@Getter
@Setter
@ToString
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "itemname")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "available")
    @Type(type="boolean")
    private boolean available;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
}
