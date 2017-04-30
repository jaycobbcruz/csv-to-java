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

```java
class ExchangeRate {

    private String fromCurrency;

    private String toCurrency;

    private BigDecimal exchangeRate;


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MMM-yy")
    private Date date;

    public String getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(String fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

```