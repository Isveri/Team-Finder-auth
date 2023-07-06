package com.evi.teamfinderauth.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Entity
public class Platform {
    public enum PlatformType {
        STEAM,
        DISCORD,
        RIOTGAMES,
        FACEBOOK
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private PlatformType platformType;

    @NotBlank
    private String username;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    private User user;

    @NotBlank
    private String avatar;

    @NotBlank
    private String discriminator;

}
