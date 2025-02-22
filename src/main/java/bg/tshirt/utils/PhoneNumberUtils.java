package bg.tshirt.utils;

import bg.tshirt.exceptions.InvalidPhoneNumberException;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.springframework.stereotype.Component;

@Component
public class PhoneNumberUtils {
    private final PhoneNumberUtil phoneNumberUtil;

    public PhoneNumberUtils(PhoneNumberUtil phoneNumberUtil) {
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

    public String formatPhoneNumber(String phoneNumber) {
        return phoneNumber.replaceAll("(\\+\\d{3})(\\d{3})(\\d{3})(\\d{3})", "$1 $2 $3 $4");
    }
}
