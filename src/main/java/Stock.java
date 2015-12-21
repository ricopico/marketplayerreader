import java.util.Date;

/**
 * Created by caneba on 12/20/15.
 */
public class Stock {
    private String symbol;
    private Date date;
    private double value;

    public Stock(String symbol, Date date, double value) {
        this.symbol = symbol;
        this.date = date;
        this.value = value;
    }

    public String getSymbol() { return this.symbol; }
    public Date getDate() { return this.date; }
    public double getValue() { return this.value; }
}
