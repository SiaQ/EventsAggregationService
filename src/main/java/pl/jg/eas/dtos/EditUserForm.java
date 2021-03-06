package pl.jg.eas.dtos;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class EditUserForm {

    @Size(min = 8, max = 30, message = "Password length should be at least 8 and max 30")
    private String password;

    @NotBlank(message = "Nickname cannot be empty or contains only white spaces")
    @Size(max = 50)
    private String nickname;

    @Override
    public String toString() {
        return "EditUserForm{" +
                "nickname='" + nickname + '\'' +
                '}';
    }
}
