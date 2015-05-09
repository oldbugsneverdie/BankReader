package BankReader.util;

/**
 * Created by jan on 3-5-15.
 */
public class Amount {

    private String amountAsString = null;
    private int amountInCents = 0;

    public Amount(String amountAsString) {
        this.amountAsString = amountAsString;
        if (amountAsString == null || amountAsString.trim().isEmpty()){
            amountInCents = 0;
        } else {
            String cents = amountAsString.replace(".","");
            cents = amountAsString.replace(",","");
            amountInCents = new Integer(cents).intValue();
        }
    }

    public int getAmountInCents() {
        return amountInCents;
    }

    public void addAmount(Amount amount) {
        this.amountInCents += amount.getAmountInCents();
    }
}
