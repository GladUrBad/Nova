package com.gladurbad.nova.util.math;

import com.gladurbad.nova.util.tuple.Pair;
import lombok.experimental.UtilityClass;

import java.util.*;
import java.util.stream.Collectors;

@UtilityClass
public class MathUtil {

    public final double EXPANDER = Math.pow(2, 24);

    public double hypot(final double x, final double z) {
        return Math.sqrt(x * x + z * z);
    }

    public double clamp(double num, double min, double max) {
        return num < min ? min : Math.min(num, max);
    }

    private long getGcd(final long current, final long previous) {
        return (previous <= 16384L) ? current : getGcd(previous, current % previous);
    }

    public double gcd(double a, double b) {
        long expansionA = (long) (Math.abs(a) * EXPANDER);
        long expansionB = (long) (Math.abs(b) * EXPANDER);

        long gcd = getGcd(expansionA, expansionB);

        return gcd / EXPANDER;
    }


    public double mean(Collection<? extends Number> samples) {
        double sum = 0D;

        for (Number val : samples) sum += val.doubleValue();

        return sum / samples.size();
    }

    public double variance(Collection<? extends Number> samples) {
        double sum = 0D, size = samples.size() - 1, mean = mean(samples);

        for (Number i : samples) sum += Math.pow(i.doubleValue() - mean, 2D);

        return sum / size;
    }

    public double deviation(Collection<? extends Number> samples) {
        return Math.sqrt(variance(samples));
    }

    public double skewness(Collection<? extends Number> samples) {
        double n = samples.size(), mean = mean(samples), deviation = deviation(samples),
                thirdMoment = 0D;

        for (Number i : samples) thirdMoment += Math.pow(i.doubleValue() - mean, 3D);

        return thirdMoment / (n - 1) * Math.pow(deviation, 3D);
    }

    public int duplicates(final Collection<? extends Number> data) {
        return (int) (data.size() - data.stream().distinct().count());
    }

    public int distinct(final Collection<? extends Number> data) {
        return (int) data.stream().distinct().count();
    }

    public double kurtosis(Collection<? extends Number> samples) {
        double sum = 0, variance = variance(samples), n = samples.size(), mean = mean(samples);

        double factor = (n * (n + 1)) / ((n - 1) * (n - 2) * (n - 3));
        double subtrahend = (3 * Math.pow(n - 1, 2.0D)) / ((n - 2) * (n - 3));

        for (Number i : samples) sum += Math.pow(i.doubleValue() - mean, 4.0D);

        sum /= Math.pow(variance, 2.0D);

        return factor * sum - subtrahend;
    }

    public double cps(Collection<? extends Number> samples) {
        return 20 / mean(samples);
    }

    public double median(Collection<? extends Number> samples) {
        List<Double> converted = samples.stream()
                .map(Number::doubleValue)
                .sorted()
                .collect(Collectors.toList());

        return converted.size() % 2 == 0
                ? converted.get(converted.size() / 2)
                : (converted.get((converted.size() - 1) / 2) + converted.get((converted.size() + 1) / 2)) / 2;
    }

    public Pair<List<Double>, List<Double>> outliers(Collection<? extends Number> samples) {
        List<Double> values = new ArrayList<>();

        for (Number i : samples) values.add(i.doubleValue());

        double q1 = median(values.subList(0, values.size() / 2));
        double q3 = median(values.subList(values.size() / 2, values.size()));

        double iqr = Math.abs(q1 - q3);
        double lowThreshold = q1 - 1.5 * iqr, highThreshold = q3 + 1.5 * iqr;

        Pair<List<Double>, List<Double>> tuple = new Pair<>(new ArrayList<>(), new ArrayList<>());

        for (Double v : values) {
            if (v < lowThreshold) {
                tuple.getX().add(v);
            } else if (v > highThreshold) {
                tuple.getY().add(v);
            }
        }

        return tuple;
    }

    public Number getMode(Collection<? extends Number> samples) {
        Map<Number, Integer> frequencies = new HashMap<>();

        samples.forEach(i -> frequencies.put(i, frequencies.getOrDefault(i, 0) + 1));

        Number mode = null;
        int highest = 0;

        for (Map.Entry<Number, Integer> entry : frequencies.entrySet()) {
            if (entry.getValue() > highest) {
                mode = entry.getKey();
                highest = entry.getValue();
            }
        }

        return mode;
    }

    public double round(double value, double nearest) {
        return Math.round(value / nearest) * nearest;
    }

    public float yawTo180F(float flub) {
        if ((flub %= 360.0f) >= 180.0f) {
            flub -= 360.0f;
        }
        if (flub < -180.0f) {
            flub += 360.0f;
        }
        return flub;
    }

}