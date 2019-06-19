package club.mydlq.swagger.kubernetes.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;
import java.util.regex.Pattern;

public class ValidationUtils {

    private ValidationUtils() {
    }

    static final String REGEX_URL = "^(?:https?://)?[\\w]{1,}(?:\\.?[\\w]{1,})+[\\w-_/?&=#%:]*$";
    static final String REGEX_URL_PORT = "^\\S*:[0-9]+$";

    public static boolean validateUrl(String str) {
        return Pattern.matches(REGEX_URL, str);
    }

    public static boolean validatePort(String str) {
        return validateUrl(str) && Pattern.matches(REGEX_URL_PORT, str);
    }

    /**
     * 验证 String 是否为 Swagger Api
     * Validate string is Swagger Api.
     *
     * @param jsonStr
     * @return
     */
    public static boolean isSwagger(String jsonStr) {
        try {
            JsonElement jsonElement = new JsonParser().parse(jsonStr);
            String swaggerStr = jsonElement.getAsJsonObject().get("swagger").toString();
            return StringUtils.isNotEmpty(swaggerStr);
        } catch (Exception e) {
            return false;
        }
    }

}
