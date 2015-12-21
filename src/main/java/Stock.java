import java.util.List;

/**
 * Created by caneba on 12/20/15.
 */
public class Stock implements Comparable<Stock> {
    private String symbol;
    private long date;
    private long volume;
    private ValueObject values;

    public Stock(List<String> entry) throws Exception {
        try {
            this.symbol = entry.get(0);
            this.date = Long.parseLong(entry.get(1));
            this.values = new ValueObject(Double.parseDouble(entry.get(2)), Double.parseDouble(entry.get(3)), Double.parseDouble(entry.get(4)), Double.parseDouble(entry.get(5)));
            this.volume = Long.parseLong(entry.get(6).trim());
        } catch (Exception e) {
            throw new Exception("unable to parse entry : " + entry);
        }
    }

    public String getSymbol() { return this.symbol; }
    public long getDate() { return this.date; }
    public ValueObject getValues() { return this.values; }
    public double getVolume() { return volume; }


    public class ValueObject {
        //NOTE: these ids are placeholders until I can figure out what they mean in the data
        private double value_1;
        private double value_2;
        private double value_3;
        private double value_4;

        public ValueObject(double v1, double v2, double v3, double v4) {
            this.value_1 = v1;
            this.value_2 = v2;
            this.value_3 = v3;
            this.value_4 = v4;
        }
        public double getValue_2() {
            return value_2;
        }

        public double getValue_1() {
            return value_1;
        }

        public double getValue_3() {
            return value_3;
        }

        public double getValue_4() {
            return value_4;
        }
    }
    public int compareTo(Stock compareStock) {

        long compareDate = ((Stock) compareStock).getDate();

        //ascending order
        return (int)(this.date - compareDate);

        //descending order
        //return compareQuantity - this.quantity;

    }
}
