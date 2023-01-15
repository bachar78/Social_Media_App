package com.bachar.socialapp.project.user.vm;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateVM {
    @NotNull
    @Size(min=4, max=255)
    private String displayName;

//    private String image;
}
