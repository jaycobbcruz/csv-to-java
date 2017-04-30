# csv-to-java
CSV to Java. Plain and simple, no mumbo-jumbo.

Convert your csv files to list of Java objects.

## Usage
```java
final List<YourObject> items = new CsvToObject(new File("your-file.csv").toObjectList(YourObject.class);
```

## Example
Return a List of ExchangeRate objects.
```java
final List<ExchangeRate> items = new CsvToObject(new File("test-file.csv")).toObjectList(ExchangeRate.class);

Assert.assertEquals("USD", item.getFromCurrency());
Assert.assertEquals("GBP", item.getToCurrency());
Assert.assertEquals("13 Apr 2017", new SimpleDateFormat("dd MMM yyyy").format(item.getDate()));
Assert.assertEquals(BigDecimal.valueOf(0.80827675), item.getExchangeRate().setScale(8, BigDecimal.ROUND_HALF_UP));
```