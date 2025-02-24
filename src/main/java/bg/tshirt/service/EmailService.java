package bg.tshirt.service;

import bg.tshirt.database.entity.Order;

public interface EmailService {
    void sendOrderEmail(Order order);

    void sendPasswordResetEmail(String email, String resetLink);
}
