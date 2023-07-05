package com.evi.teamfinderauth.model;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class ChangePasswordDTO {
    public String oldPassword;
    public String newPassword;
}