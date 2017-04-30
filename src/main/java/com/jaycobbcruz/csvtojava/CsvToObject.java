package com.jaycobbcruz.csvtojava;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CsvToObject {

    private static final Logger log = Logger.getLogger(CsvToObject.class.getName());

    private static final String DEFAULT_DATA_DELIMITER = ",";
    private static final String DELIMITER_REPLACEMENT = "<#replaceme#>";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private String headerString;
    private List<String> records;
    private final String dataDelimiter;
    private final Function<String, String> columnNameConverter;

    public CsvToObject(final Path file) {
        this(file, DEFAULT_DATA_DELIMITER, true, CaseFormat.UPPER_UNDERSCORE);
    }

    public CsvToObject(final Path file, final String dataDelimiter, final boolean hasHeader,
                       final CaseFormat caseFormat) {
        this(file, dataDelimiter, hasHeader, s-> caseFormat.to(CaseFormat.LOWER_CAMEL, s));
    }

    public CsvToObject(final Path file, final String dataDelimiter, final boolean hasHeader,
                        final Function<String, String> columnNameConverter) {
        this.dataDelimiter = dataDelimiter;
        this.columnNameConverter = columnNameConverter;
        try {
            final List<String> lines = Files.readAllLines(file);
            if (lines.isEmpty() || (hasHeader && lines.size() <= 1)) {
                throw new RuntimeException(file.getFileName() + " file is empty.");
            }
            if (hasHeader) {
                this.headerString = lines.stream().findFirst().orElse("");
                this.records = lines.subList(1, lines.size());
            }
        } catch (IOException e) {
            log.warning(e.getMessage());
        }
    }

    private List<String> getColumnNames() {
        return Arrays.asList(headerString.split(dataDelimiter));
    }

    private List<String> camelCaseColumnNames() {
        return getColumnNames().stream().map(columnNameConverter).collect(Collectors.toList());
    }

    public <T> List<T> toObjectList(Class<T> classType) {
        return toObjectList(classType, false);
    }

    public <T> List<T> toObjectList(Class<T> classType, boolean escaped) {
        final List<T> rows = new ArrayList<>();
        final List<String> convertedPropertyNames = camelCaseColumnNames();

        try {
            for (String record : records) {
                if (StringUtils.isBlank(record)) { continue; }
                final Map<String, Object> propertyValuesMap = new HashMap<>();
                if (escaped) {
                    record = replaceDelimitersInsideQuotes(record);
                }
                final List<String> recordData = Arrays.asList(record.split(dataDelimiter));
                final AtomicInteger index = new AtomicInteger();
                recordData.forEach(data -> {
                    if (escaped) {
                        data = data.replaceAll(DELIMITER_REPLACEMENT, dataDelimiter);
                        data = StringUtils.removeStart(data, "\"");
                        data = StringUtils.removeEnd(data, "\"");
                    }
                    propertyValuesMap.put(convertedPropertyNames.get(index.getAndIncrement()), data);
                });
                rows.add(objectMapper.convertValue(propertyValuesMap, classType));
            }
        } catch (Exception e) {
            throw new RuntimeException("Problem encountered while parsing the file", e);
        }

        return rows;
    }

    /**
     * Replace delimiter characters inside quoted strings.
     * Ex. One, Two, "Three, a string", Four will be changed to One, Two, "Three<#replaceme#> a string", Four.
     *
     * This will be used prevent unintentional splitting of data by delimiter.
     * @param csv csv String
     * @return escaped csv
     */
    private String replaceDelimitersInsideQuotes(final String csv) {

        final Pattern pattern = Pattern.compile("\"(.*?)\"");
        final Matcher m = pattern.matcher(csv);
        String clean = csv;
        while (m.find()) {
            final String token = m.group(0);
            final String replacement = token.replaceAll(dataDelimiter, DELIMITER_REPLACEMENT);
            clean = clean.replaceAll(token, replacement);
        }
        return clean;
    }

}
