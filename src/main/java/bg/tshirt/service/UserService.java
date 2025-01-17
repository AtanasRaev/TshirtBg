package bg.tshirt.service;

import bg.tshirt.database.dto.UserDTO;
import bg.tshirt.database.dto.UserRegistrationDTO;

public interface UserService {
    void registerUser(UserRegistrationDTO registrationDTO);

    UserDTO findByEmail(String email);
}
