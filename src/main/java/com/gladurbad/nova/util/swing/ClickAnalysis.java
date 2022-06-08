package com.gladurbad.nova.util.swing;

import com.gladurbad.nova.util.math.MathUtil;
import com.gladurbad.nova.util.tuple.Pair;
import lombok.Getter;

import java.util.Collection;
import java.util.List;

@Getter
public class ClickAnalysis {

    private final Collection<Integer> samples;
    private final Pair<List<Double>, List<Double>> outliers;
    private final double deviation, skewness, kurtosis, max, min, average, cps;

    public ClickAnalysis(Collection<Integer> samples) {
        this.samples = samples;

        this.deviation = MathUtil.deviation(samples);
        this.skewness = MathUtil.skewness(samples);
        this.kurtosis = MathUtil.kurtosis(samples);

        this.max = samples.stream()
                .mapToInt(val -> val)
                .max()
                .orElseThrow(() -> new IllegalArgumentException("Samples cannot be empty."));

        this.min = samples.stream()
                .mapToInt(val -> val)
                .min()
                .orElseThrow(() -> new IllegalArgumentException("Samples cannot be empty."));

        this.average = MathUtil.mean(samples);
        this.cps = 20 / average;
        this.outliers = MathUtil.outliers(samples);
    }
}
