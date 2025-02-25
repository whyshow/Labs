package club.ccit.view.widget.utils;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * File Name：DecimalPointFilter
 *
 * @author : swzhang3
 * date：2025/02/25 14:00
 * Description：DecimalPointFilter 类用于过滤输入的小数位数，构建DecimalPointFilter时传入保留的小数位数
 * Version：1.0
 */
public class DecimalPointFilter implements InputFilter {
    private final int maxDecimalLength;

    // 构造函数 maxDecimalLength 为保留小数位数
    public DecimalPointFilter(int maxDecimalLength) {
        this.maxDecimalLength = maxDecimalLength;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end,
                               Spanned dest, int dstart, int dend) {
        String newString = dest.toString().substring(0, dstart)
                + source.subSequence(start, end)
                + dest.toString().substring(dend);

        int dotIndex = newString.indexOf('.');

        // 仅保留小数点相关校验
        if (newString.indexOf('.', dotIndex + 1) > 0) { // 禁止多个小数点
            return "";
        }

        if (dotIndex >= 0 && (newString.length() - dotIndex - 1) > maxDecimalLength) {
            return ""; // 限制小数位数
        }
        return null;
    }
}

