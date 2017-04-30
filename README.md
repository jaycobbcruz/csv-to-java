# csv-to-java
CSV to Java. Plain and simple, no mumbo-jumbo.

Convert your csv files to list of Java objects.

## Example
```java
final List<YourObject> items = new CsvToObject(new File("your-file.csv").toObjectList(YourObject.class);
```