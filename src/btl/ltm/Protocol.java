package btl.ltm;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.ArrayList;

public class Protocol {

    public static final String LIST_THUOC = "LIST_THUOC";
    public static final String LIST_CONGDUNG = "LIST_CONGDUNG";
    public static final String XOA_THUOC = "XOA_THUOC";
    public static final String XOA_CONGDUNG = "XOA_CONGDUNG";

    public static String encode(String value) {
        if (value == null) {
            value = "";
        }
        return Base64.getEncoder().encodeToString(
            value.getBytes(StandardCharsets.UTF_8)
        );
    }

    public static String decode(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        try {
            return new String(
                Base64.getDecoder().decode(value),
                StandardCharsets.UTF_8
            );
        } catch (IllegalArgumentException e) {
            return value;
        }
    }

    public static String encodeRows(List<String[]> rows) {
        if (rows == null || rows.isEmpty()) {
            return "";
        }
        StringBuilder result = new StringBuilder();

        for (String[] row : rows) {
            if (result.length() > 0) {
                result.append(";");
            }

            for (int i = 0; i < row.length; i++) {
                if (i > 0) {
                    result.append(".");
                }

                result.append(encode(row[i]));
            }
        }

        return result.toString();
    }

    public static List<String[]> decodeRows(String data) {
        List<String[]> result = new ArrayList<>();

        if (data == null || data.trim().isEmpty()) {
            return result;
        }

        String[] rows = data.split(";", -1);

        for (String row : rows) {
            if (row.trim().isEmpty()) {
                continue;
            }
            String[] fields = row.split("\\.", -1);
            String[] decoded = new String[fields.length];

            for (int i = 0; i < fields.length; i++) {
                decoded[i] = decode(fields[i]);
            }

            result.add(decoded);
        }

        return result;
    }

    public static String success(String message) {
        return "OK|" + (message == null ? "" : message);
    }

    public static String error(String message) {
        return "ERROR|" + (message == null ? "" : message);
    }
}
