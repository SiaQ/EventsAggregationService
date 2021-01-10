package pl.jg.eas.dtos;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
public class NewCommentForm {

    @Size(max = 500, message = "Comment size max 500")
    private String comment;

    @Override
    public String toString() {
        return "NewCommentForm{" +
                "comment='" + comment + '\'' +
                '}';
    }
}
