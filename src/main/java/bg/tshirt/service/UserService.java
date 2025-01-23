package bg.tshirt.service;

import bg.tshirt.database.dto.UserDTO;
import bg.tshirt.database.dto.UserRegistrationDTO;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {
    void registerUser(UserRegistrationDTO registrationDTO);

    UserDTO findByEmail(String email);

    UserDTO validateAdmin(HttpServletRequest request);

    UserDTO validateUser(HttpServletRequest request);
}
