package com;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private WebService ws = new WebService();

    // regex to identify a multi-char delimiter
    private static final String MULTI_CHAR_DELIMITER = "(?s)^//\\[(.+)\\].*";
    private int callCount = 0;

    public int add(String numbers) {
        callCount++;
        if(numbers.isEmpty()) {
            return 0;
        }

        List<String> delimiters = new ArrayList<>(List.of(",", "\n"));
        String customDelimiter = getCustomDelimiter(numbers);
        if(!customDelimiter.isEmpty()) {
            numbers = numbers.substring(numbers.indexOf("\n") + 1);
            delimiters.add(customDelimiter);
        }

        List<String> extractedNums = extractNums(delimiters, List.of(numbers));

        final StringBuilder error = new StringBuilder("negatives not allowed:");
        boolean errors = extractedNums.stream()
                .mapToInt(Integer::parseInt)
                .filter(i -> i < 0)
                .peek(i -> error.append(" " + i))
                .count() > 0;
        if(errors) {
            throw new IllegalStateException(error.toString());
        }

        int result = extractedNums.stream()
                .mapToInt(Integer::parseInt)
                .filter(i -> i <= 1000)
                .sum();
        try {
            log.info(Integer.toString(result));
        } catch(RuntimeException e) {
            ws.errorInLogging(e.getMessage());
        }

        System.out.println(result);
        return result;
    }

    private String getCustomDelimiter(String numbers) {
        if(numbers.matches(MULTI_CHAR_DELIMITER)) {
            Pattern pattern = Pattern.compile(MULTI_CHAR_DELIMITER);
            Matcher matcher = pattern.matcher(numbers);
            matcher.find();
            return matcher.group(1);
        } else if(numbers.startsWith("//")) { // Single char delimiter
            return Character.toString(numbers.charAt(2));
        }

        return "";
    }

    List<String> extractNums(List<String> delimiters, List<String> string) {
        if(delimiters.isEmpty()) {
            return string;
        }

        List<String> newResults = new ArrayList<String>();

        for(String s : string) {
            String[] split = s.split(Pattern.quote(delimiters.get(0)));
            newResults.addAll(Arrays.asList(split));
        }

        delimiters = delimiters.subList(1, delimiters.size());

        if(!delimiters.isEmpty()) {
            newResults = extractNums(delimiters, newResults);
        }

        return newResults;
    }

    public int getCallCount() {
        return callCount;
    }
}
