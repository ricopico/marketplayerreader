import java.util.List;

/**
 * Created by caneba on 12/20/15.
 */
public class Stock implements Comparable<Stock> {
    private String symbol;
    private long date;
    private long volume;
    private ValueObject values;
    private ValueTransitionObject previousValueTransition = null;
    private ValueTransitionObject nextValueTransition = null;

    private Stock() {}
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
    public ValueTransitionObject getPreviousValueTransition() { return this.previousValueTransition; }
    public ValueTransitionObject getNextValueTransition() { return this.nextValueTransition; }

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

        public double getAverageOfAllValues() {
            return ((this.value_1 + this.value_2 + this.value_3 + this.value_4)/4);
        }

        public ValueObject clone() {
            ValueObject clone = new ValueObject(this.value_1, this.value_2, this.value_3, this.value_4);
            return clone;
        }
    }

    public class ValueTransitionObject {
        private double percentValueDiff_1;
        private double percentValueDiff_2;
        private double percentValueDiff_3;
        private double percentValueDiff_4;
        private double averagePercentValueDiff;

        public ValueTransitionObject(ValueObject first, ValueObject second) {
            this.percentValueDiff_1 = ((second.getValue_1() - first.getValue_1())/first.getValue_1()) * 100;
            this.percentValueDiff_2 = ((second.getValue_2() - first.getValue_2())/first.getValue_2()) * 100;
            this.percentValueDiff_3 = ((second.getValue_3() - first.getValue_3())/first.getValue_3()) * 100;
            this.percentValueDiff_4 = ((second.getValue_4() - first.getValue_4())/first.getValue_4()) * 100;
            this.averagePercentValueDiff = (percentValueDiff_1 + percentValueDiff_2 + percentValueDiff_3 + percentValueDiff_4)/4;
        }
        public double getPercentValueDiff_3() {
            return percentValueDiff_3;
        }

        public double getPercentValueDiff_1() {
            return percentValueDiff_1;
        }

        public double getPercentValueDiff_2() {
            return percentValueDiff_2;
        }

        public double getPercentValueDiff_4() {
            return percentValueDiff_4;
        }

        public double getAveragePercentValueDiff() {
            return averagePercentValueDiff;
        }

    }

    public void setValueTransition(Stock next) {
        ValueTransitionObject valueTransitionObject = new ValueTransitionObject(this.values, next.getValues());
        this.nextValueTransition = valueTransitionObject;
        next.setPreviousValueTransition(valueTransitionObject);
    }
    public void clearValueTransitions() {
        this.nextValueTransition = null;
        this.previousValueTransition = null;
    }
    public void setPreviousValueTransition(ValueTransitionObject valueTransitionObject) {
        this.previousValueTransition = valueTransitionObject;
    }
    public int compareTo(Stock compareStock) {

        long compareDate = ((Stock) compareStock).getDate();

        //ascending order
        return (int)(this.date - compareDate);

        //descending order
        //return compareQuantity - this.quantity;

    }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public void setDate(long date) { this.date = date; }
    public void setVolume(long volume) { this.volume = volume; }
    public void setValueObject(ValueObject values) { this.values = values; }

    public Stock clone() {

        Stock cloneStock = new Stock();
        cloneStock.setSymbol(this.symbol);
        cloneStock.setDate(this.date);
        cloneStock.setVolume(this.volume);
        cloneStock.setValueObject(this.values.clone());
        cloneStock.previousValueTransition = null;
        cloneStock.nextValueTransition = null;

        return cloneStock;
    }

}
