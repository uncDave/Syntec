package com.Synctec.Synctec.domains;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "reply")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Reply extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")  // Foreign key to the parent comment
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")     // Foreign key to the user who made the reply
    private BaseUser user;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")     // Foreign key to the post to which the reply belongs
    private Post post;

    private int likeCount = 0;

    @OneToMany(mappedBy = "reply", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Like> likes;

}
