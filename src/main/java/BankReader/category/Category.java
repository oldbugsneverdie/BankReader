package BankReader.category;

import BankReader.file.GenericBankLine;
import BankReader.util.Amount;

import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by jadu on 17-5-2015.
 */
public class Category implements Comparable {

    private String name;
    private Amount amount = new Amount();
    private List<GenericBankLine> genericBankLines = new ArrayList<GenericBankLine>();

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

    @Override
    public int compareTo(Object o) {
        // Sort by amount being positive or not, then by name
        Category otherCategory = (Category)o;
        int compared = Integer.compare(this.getAmount().getAmountInCents(), otherCategory.getAmount().getAmountInCents());
        if (compared != 0){
            return compared;
        }

        return this.getName().compareTo(otherCategory.getName());

    }

    public void addGenericBankLine(GenericBankLine genericBankLine) {
        genericBankLines.add(genericBankLine);
        this.amount.addAmount(genericBankLine.getAmount());

    }

    public List<GenericBankLine> getGenericBankLines() {
        return genericBankLines;
    }

    public Amount getAmountByMonth(Month month) {
        Amount amount = new Amount();
        for (GenericBankLine genericBankLine: genericBankLines){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(genericBankLine.getDate());
            int monthFromDate = calendar.get(Calendar.MONTH);
            if (monthFromDate == month.getValue()){
                amount.addAmount(genericBankLine.getAmount());
            }
        }
        return amount;
    }
}
