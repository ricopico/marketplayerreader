import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by caneba on 12/25/15.
 */
public class RuleObject {

    private Set<Reference> references;
    private Action action;

    public RuleObject() {
        this.references = new HashSet<Reference>();
        this.action = new Action();
    }

    public JSONObject toJSON() {

        JSONObject toReturn = new JSONObject();

        return toReturn;
    }
    public String stringify() {
        return "";
    }

    public class Reference {

        Set<Stock> stockReferences;
        TimeOffset timeOffset;
        double averageValueOfStockReferences;

        public Reference(Stock stockReference, TimeOffset timeOffset, double averageValueOfStockReferences) {
            this.stockReferences = new HashSet<>();
            this.stockReferences.add(stockReference);

            this.timeOffset = timeOffset;

            this.averageValueOfStockReferences = averageValueOfStockReferences;
        }
        public Reference(Set<Stock> stockReferences, TimeOffset timeOffset, double averageValueOfStockReferences) {
            this.stockReferences = stockReferences;

            this.timeOffset = timeOffset;

            this.averageValueOfStockReferences = averageValueOfStockReferences;
        }



        public JSONObject toJSON() throws JSONException {
            JSONObject toReturn = new JSONObject();

            JSONArray stockReferencesJSONArray = new JSONArray();
            for(Stock stockReference : this.stockReferences) {
                String symbol = stockReference.getSymbol();
                stockReferencesJSONArray.put(symbol);
            }

            toReturn.put("REFERENCES", stockReferencesJSONArray);

            toReturn.put("TIME_OFFSET(DAYS)", this.timeOffset.stringify());

            toReturn.put("VALUE OF REFERENCES", this.averageValueOfStockReferences);

            return toReturn;
        }

        public String stringify() throws JSONException {
            return this.toJSON().toString();
        }
    }

    public class Action {

        private Constants.ACTION action;

        public Action() {
            this.action = null;
        }
        public Action(Constants.ACTION action) {
            this.action = action;
        }

        public void setAction(Constants.ACTION action) {
            this.action = action;
        }
        public Constants.ACTION getAction() {
            return this.action;
        }

        public String stringify() {
            return this.action.toString();
        }
    }

    public class TimeOffset {
        private int value;      //by day by default
        private boolean past;

        public TimeOffset(int value) {
            this.value = value;
            this.past = true;
        }
        public TimeOffset(int value, boolean isPast) {
            this.value = value;
            this.past = isPast;
        }

        public int getValue() {
            return this.value;
        }

        public boolean getIsPast() {
            return this.past;
        }

        public String stringify() {
            String toReturn = "";
            if(past) {
                toReturn += value + " days in the past";
            } else {
                toReturn += "today";
            }
            return toReturn;
        }
    }
}
