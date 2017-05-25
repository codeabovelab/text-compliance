package com.codeabovelab.tpc.util;

/**
 */
public class AvgAggregator {
    private double summ;
    private double count;

    public void append(double item) {
        this.summ += item;
        this.count++;
    }

    public double get() {
        return summ / count;
    }
}
