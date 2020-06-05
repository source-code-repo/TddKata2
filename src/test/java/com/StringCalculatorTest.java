package com;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;

public class StringCalculatorTest {
    private StringCalculator sc = new StringCalculator();

    @Test
    public void zero() {
        assertThat(sc.add(""), is(0));
    }

    @Test
    public void oneNum() {
        assertThat(sc.add("0"), is(0));
        assertThat(sc.add("1"), is(1));
        assertThat(sc.add("2"), is(2));
        assertThat(sc.add("1000"), is(1000));
    }

    @Test
    public void doubleDigit() {
        assertThat(sc.add("10"), is(10));
    }

    @Test
    public void twoNums() {
        assertThat(sc.add("1,2"), is(3));
        assertThat(sc.add("7,9"), is(16));
    }

    @Test
    public void threeDigitsTwoNums() {
        assertThat(sc.add("100,201"), is(301));
    }

    @Test
    public void manyNums() {
        assertThat(sc.add("1,1,1,1,1"), is(5));
        assertThat(sc.add("1,4,5"), is(10));
        assertThat(sc.add("1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1"), is(19));
    }

    @Test
    public void newLines() {
        assertThat(sc.add("1\n1\n1"), is(3));
    }

    @Test
    public void newLinesCommasMixed() {
        assertThat(sc.add("1\n1,1,2\n5"), is(10));
    }

    @Test
    public void customDelimiter() {
        assertThat(sc.add("//;\n1;2"), is(3));
    }

    @Test
    public void threeDigitsMixedDelimiters() {
        assertThat(sc.add("1,400\n5,500"), is(906));
    }

    @Test
    public void customAndDefaultDelimiters() {
        assertThat(sc.add("//;\n1;2,3\n4"), is(10));
    }

    @Test
    public void negative() {
        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> sc.add("-1"));
        assertThat(e.getMessage(), is(equalTo("negatives not allowed: -1")));
    }

    @Test
    public void manyNegative() {
        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> sc.add("-1,-2,-3"));
        assertThat(e.getMessage(), is(equalTo("negatives not allowed: -1 -2 -3")));
    }

    @Test
    public void negativeDelimiter() {
        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> sc.add("-1,-2\n-3"));
        assertThat(e.getMessage(), is(equalTo("negatives not allowed: -1 -2 -3")));
    }

    @Test
    public void negativeCustomDelimiter() {
        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> sc.add("//;\n-1;2,-3\n4"));
        assertThat(e.getMessage(), is(equalTo("negatives not allowed: -1 -3")));
    }

    @Test
    public void callCount() {
        sc = new StringCalculator();
        sc.add("1,2,3");
        sc.add("1,2,3");
        sc.add("1,2,3");
        assertThat(sc.getCallCount(), is(equalTo(3)));
    }

    @Test
    public void ignoreOver1000() {
        assertThat(sc.add("1,2,1001,3"), is(equalTo(6)));
    }

    @Test
    public void multiCharDelimiter() {
        assertThat(sc.add("//[***];\n1***2***3"), is(6));
    }
}
