package com;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringCalculator {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private WebService ws = new WebService();

    // regex to identify a multi-char delimiter
    private static final String MULTI_CHAR_DELIMITER = "(?s)^//\\[(.+)\\].*";
    private int callCount = 0;

    public int add(String numbers) {
        callCount++;

        List<String> delimiters = getDelimiters(numbers);
        if(delimiters.size() > 2) { // custom delimiter used, need to skip header
            numbers = numbers.substring(numbers.indexOf("\n") + 1);
        }

        List<Integer> ints = tokenize(delimiters, List.of(numbers))
                .stream()
                .filter(Predicate.not(String::isEmpty))
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        // Throw exception if any numbers are negative
        if(ints.stream().anyMatch(i -> i < 0)) {
            throw new IllegalStateException("negatives not allowed:" +
                    ints.stream()
                            .filter(i -> i < 0)
                            .map(i -> " " + i)
                            .collect(Collectors.joining()));
        }

        int result = ints.stream()
                .filter(i -> i <= 1000)
                .collect(Collectors.summingInt(i->i));
        try {
            log.info(Integer.toString(result));
        } catch(RuntimeException e) {
            ws.errorInLogging(e.getMessage());
        }
        System.out.println(result);
        return result;
    }

    private List<String> getDelimiters(String numbers) {
        Matcher matcher = Pattern.compile(MULTI_CHAR_DELIMITER).matcher(numbers);
        List<String> delimiters = new ArrayList<>(List.of(",", "\n"));
        if(matcher.find()) { // Multi-char custom delimiter
            delimiters.add(matcher.group(1));
        } else if(numbers.startsWith("//")) { // Single char custom delimiter
            delimiters.add(Character.toString(numbers.charAt(2)));
        }
        return delimiters;
    }

    /**
     * Split string by delimiters
     * @param delimiters
     * @param string
     * @return List of split tokens
     */
    List<String> tokenize(List<String> delimiters, List<String> string) {
        for (String d : delimiters) {
            List<String> newResults = new ArrayList<>();
            for(String s : string) {
                String[] split = s.split(Pattern.quote(d));
                Collections.addAll(newResults, split);
            }
            string = newResults;
        }
        return string;
    }

    public int getCallCount() {
        return callCount;
    }
}
