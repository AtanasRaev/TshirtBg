package bg.tshirt.service;

import bg.tshirt.database.dto.UserRegistrationDTO;

public interface UserService {
    void registerUser(UserRegistrationDTO registrationDTO);
}
