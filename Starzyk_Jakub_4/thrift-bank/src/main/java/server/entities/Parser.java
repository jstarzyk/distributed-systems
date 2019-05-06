package server.entities;

import server.ThriftServer;

import javax.money.CurrencyUnit;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public abstract class Parser {

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

    public static String join(Collection<String> tokens) {
        return "[" + String.join(", ", tokens) + "]";
    }

    public static boolean validateID(String s) {
        // TODO
//        return s.length() == 11 && s.chars().allMatch(Character::isDigit);
        return true;
    }

    public static boolean validateFirstName(String s) {
        return !s.isEmpty();
    }

    public static boolean validateLastName(String s) {
        return !s.isEmpty();
    }

    public static BigDecimal parseBigDecimal(String s) {
        try {
            return new BigDecimal(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static CurrencyUnit parseCurrencyUnit(String s) {
        return ThriftServer.currencies.stream().filter(c -> c.getCurrencyCode().equals(s.toUpperCase()))
                .findAny()
                .orElse(null);
    }

    public static Date parseDate(String s) {
        try {
            return sdf.parse(s);
        } catch (ParseException e) {
            return null;
        }
    }

}
