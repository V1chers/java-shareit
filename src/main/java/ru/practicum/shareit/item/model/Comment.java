package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.Instant;

@Data
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String text;

    @Column(name = "item_id")
    private int itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @Column
    private Instant created;

    public Comment(String text, int authorId, int itemId) {
        this.text = text;
        this.itemId = itemId;
        this.created = Instant.now();

        author = new User();
        author.setId(authorId);
    }

    public Comment() {
        super();
    }
}
