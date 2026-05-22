package model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Random;

public class Util {

    public static String generateCode() {
        int i = (int) (Math.random() * 1000000);
        return String.format("%06d", i);
    }

    public static boolean isEmailValid(String email) {
        return email.matches("^[a-zA-Z0-9._%+-]+@gmail\\.com$");
    }

    public static boolean isPasswordValid(String password) {
        return password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$");
    }

    public static boolean isCodeValid(String code) {
        return code.matches("^\\d{5}$");
    }

    public static boolean isInteger(String value) {
        return value.matches("^\\d+$");
    }

    public static boolean isDouble(String value) {
        return value.matches("^\\d+(\\.\\d{2})?$");
    }

    public static boolean isValidYear(String yearStr) {
        try {
            return Integer.parseInt(yearStr) >= 1000 && Integer.parseInt(yearStr) <= 9999;
        } catch (NumberFormatException e) {
            return false; // Not a valid integer
        }
    }

    public static boolean isValidDate(String dateStr) {
        try {
            LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE); // "yyyy-MM-dd"
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static Date getDateObject(int year) {
        return Date.from(LocalDate.of(year, 12, 31).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static Date getFirstDateOfYear(String year) {
        return Date.from(LocalDate.of(Integer.parseInt(year), 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static Date getLastDateOfYear(String year) {
        return Date.from(LocalDate.of(Integer.parseInt(year), 12, 31).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static int generateUniqueId() {
        return new Random().nextInt((999999 - 100000) + 1) + 100000;
    }

    public static Date getReleaseDateObject(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (ParseException ex) {
            return null;
        }
    }

    public static String getConvertedLink(String youtubeLink) {
        return youtubeLink.replace("watch?v=", "embed/");
    }

    public static Date getFirstDateOfCurrentMonth() {
        return Date.from(LocalDate.now().withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

}
