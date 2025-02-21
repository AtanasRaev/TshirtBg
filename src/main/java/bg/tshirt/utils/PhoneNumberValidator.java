package bg.tshirt.utils;

import bg.tshirt.exceptions.InvalidPhoneNumberException;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.springframework.stereotype.Component;

@Component
public class PhoneNumberValidator {
    private final PhoneNumberUtil phoneNumberUtil;

    public PhoneNumberValidator(PhoneNumberUtil phoneNumberUtil) {
        this.phoneNumberUtil = phoneNumberUtil;
    }

    public void validateBulgarianPhoneNumber(String phone) {
        try {
            Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.parse(phone, "BG");
            if (!phoneNumberUtil.isValidNumber(phoneNumber)) {
                throw new InvalidPhoneNumberException("Invalid Bulgarian phone number: " + phone);
            }
        } catch (NumberParseException e) {
            throw new InvalidPhoneNumberException("Invalid phone number format: " + phone);
        }
    }
}
