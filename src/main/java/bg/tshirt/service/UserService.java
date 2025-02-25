package bg.tshirt.service;

import bg.tshirt.database.dto.UserDTO;
import bg.tshirt.database.dto.UserProfileDTO;
import bg.tshirt.database.dto.UserRegistrationDTO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface UserService {
    void registerUser(UserRegistrationDTO registrationDTO);

    UserDTO findByEmail(String email);

    UserDTO validateAdmin(HttpServletRequest request);

    UserDTO validateUser(HttpServletRequest request);

    UserProfileDTO getUserProfile(HttpServletRequest request);

    List<String> getUserRoles(String accessToken);

    boolean resetUserPassword(UserDTO userDTO);
}
