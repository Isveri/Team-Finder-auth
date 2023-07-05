package com.evi.teamfinderauth.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
public class EmailDTO {
    @NotBlank
    @Email
    private String email;
}

