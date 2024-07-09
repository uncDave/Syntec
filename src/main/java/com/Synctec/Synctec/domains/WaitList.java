package com.Synctec.Synctec.domains;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "waitlist")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WaitList extends BaseEntity{
    private String fullName;
    private String email;
    private String phoneNo;

}
