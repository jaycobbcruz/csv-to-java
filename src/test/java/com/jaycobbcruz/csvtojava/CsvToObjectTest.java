package com.jaycobbcruz.csvtojava;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.List;

public class CsvToObjectTest {

    private Path testFile;

    @Before
    public void init() throws URISyntaxException {
        this.testFile = new File(this.getClass().getResource("/test-file.csv").toURI()).toPath();
        Assert.assertNotNull(testFile);
    }

    @Test
    public void testToObjectList() {
        final List<ExchangeRate> items = new CsvToObject(testFile).toObjectList(ExchangeRate.class);
        Assert.assertTrue(items != null && !items.isEmpty());
        final ExchangeRate item = items.stream().findFirst().orElse(null);
        Assert.assertNotNull(item);
        Assert.assertEquals("USD", item.getFromCurrency());
        Assert.assertEquals("GBP", item.getToCurrency());
        Assert.assertEquals("13 Apr 2017", new SimpleDateFormat("dd MMM yyyy").format(item.getDate()));
        Assert.assertEquals(BigDecimal.valueOf(0.80827675), item.getExchangeRate().setScale(8, BigDecimal.ROUND_HALF_UP));
    }

}