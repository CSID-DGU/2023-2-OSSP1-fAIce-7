package com.example.cokkiri.model;


import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class MatchingAgree {
    @Id
    @Column
    private int matchingId;

    @Column
    public String matchingType;

    @Column
    @ElementCollection
    private List<String> email;
}
