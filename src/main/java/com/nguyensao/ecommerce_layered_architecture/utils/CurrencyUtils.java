package com.nguyensao.ecommerce_layered_architecture.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyUtils {
    public static String formatVndCurrency(BigDecimal value) {
        if (value == null) {
            return "Số tiền không hợp lệ";
        }

        if (value.compareTo(BigDecimal.ZERO) < 0) {
            return "Số tiền không hợp lệ";
        }

        @SuppressWarnings("deprecation")
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return formatter.format(value);
    }

    public static String formatAmount(double amount) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(amount) + " VND"; // Adding VND symbol at the end
    }
}
