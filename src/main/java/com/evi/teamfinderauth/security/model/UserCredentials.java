package com.evi.teamfinderauth.security.model;

import com.sun.istack.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class UserCredentials {

    @NotNull
    private String username;

    @NotNull
    private String password;
}