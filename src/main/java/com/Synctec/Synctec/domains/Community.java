package com.Synctec.Synctec.domains;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "community")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Community extends BaseEntity {
    private static  final long serialVersionUID=1l;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private String id;
    private String communityName;
    @ManyToOne
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private BaseUser createdBy;
    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private boolean isActive;
    private LocalDateTime disabledAt;
    @ManyToMany(mappedBy = "communities")
    private Set<BaseUser> users;





}
