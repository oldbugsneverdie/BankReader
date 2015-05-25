package BankReader.util;

/**
 * Created by jan on 3-5-15.
 */
public class Amount {

    private String amountAsString = null;
    private int amountInCents = 0;

    public Amount(){
        amountAsString = "0";
        amountInCents = 0;
    }

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

    public Amount(int amountInCents){
        this.amountInCents = amountInCents;
        this.amountAsString = String.valueOf(amountInCents);
    }

    public int getAmountInCents() {
        return amountInCents;
    }

    public void addAmount(Amount amount) {
        this.amountInCents += amount.getAmountInCents();
    }

    public Amount reversedAmount(){
        Amount reversedAmount = new Amount(this.amountInCents*-1);
        return reversedAmount;
    }

    @Override
    public String toString() {
        return String.valueOf(amountInCents);
    }
}
