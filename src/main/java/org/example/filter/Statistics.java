package org.example.filter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

/**
 * @author Dmitrii Taranenko
 */
public class Statistics {
    private long count = 0;
    private BigDecimal sum = BigDecimal.ZERO;
    private BigDecimal min = null;
    private BigDecimal max = null;
    private int minLength = Integer.MAX_VALUE;
    private int maxLength = 0;

    public void addInteger(BigInteger value) {
        addFloat(new BigDecimal(value));
    }

    public void addFloat(BigDecimal value) {
        count++;
        sum = sum.add(value);
        if (min == null || value.compareTo(min) < 0) min = value;
        if (max == null || value.compareTo(max) > 0) max = value;
    }

    public void addString(String s) {
        count++;
        int len = s.length();
        if (len < minLength) minLength = len;
        if (len > maxLength) maxLength = len;
    }

    public long getCount() { return count; }
    public Optional<BigDecimal> getMin() { return Optional.ofNullable(min); }
    public Optional<BigDecimal> getMax() { return Optional.ofNullable(max); }
    public BigDecimal getSum() { return sum; }
    public BigDecimal getAverage() {
        return count > 0 ? sum.divide(BigDecimal.valueOf(count), BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO; }
    public Optional<Integer> getMinLength() { return count > 0 ? Optional.of(minLength) : Optional.empty(); }
    public Optional<Integer> getMaxLength() { return count > 0 ? Optional.of(maxLength) : Optional.empty(); }
}
