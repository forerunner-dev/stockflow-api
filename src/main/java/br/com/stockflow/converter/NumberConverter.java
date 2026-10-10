package br.com.stockflow.converter;

public class NumberConverter {

    public static boolean isNumeric(String number) {

        if (number ==null || number.isEmpty()) return false;
        String numberReplace = number.replace(",",".");
        return numberReplace.matches("[-+]?[0-9]*\\.?[0-9]+");

    }

    public static Double convertToDouble(String StrNumber) throws IllegalArgumentException {

        if (StrNumber ==null || StrNumber.isEmpty())  throw new IllegalArgumentException();
        String numberReplace = StrNumber.replace(",",".");
        return Double.parseDouble(numberReplace);

    }
}
