package BankReader.category;

import BankReader.util.Amount;

/**
 * Created by jadu on 17-5-2015.
 */
public class Category implements Comparable {

    private String name;
    private Amount amount = new Amount();

    public Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Amount getAmount() {
        return amount;
    }

    public void addAmount(Amount amount) {
        this.amount.addAmount(amount);
    }

    @Override
    public int compareTo(Object o) {
        Category otherCategory = (Category)o;
        return this.getName().compareTo(otherCategory.getName());
    }
}
