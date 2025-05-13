package ureca.ureca_mini.user.validator;

import ureca.ureca_mini.user.dto.JoinDTO;

public interface UserValidator {
    void validate(JoinDTO dto);
}
