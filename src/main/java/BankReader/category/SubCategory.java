package BankReader.category;

import BankReader.file.GenericBankLine;
import BankReader.util.Amount;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jadu on 17-5-2015.
 */
public class SubCategory implements Comparable{

    private String name;
    private Amount amount = new Amount();
    private Category category;
    private String key;
    private List<GenericBankLine> genericBankLines = new ArrayList<GenericBankLine>();

    public SubCategory(Category category, String name, String key) {
        Assert.notNull(name, "Could not create sub category, name is null");
        Assert.notNull(key, "Could not create sub category, key is null");
        Assert.notNull(category, "Could not create sub category for '"+name+"', category is null");
        this.name = name;
        this.category = category;
        this.key = key;
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

    public Category getCategory() {
        return category;
    }

    public String getKey() {
        return key;
    }

    public List<GenericBankLine> getGenericBankLines() {
        return genericBankLines;
    }

    public void addGenericBankLine(GenericBankLine genericBankLine){
        genericBankLines.add(genericBankLine);
        this.amount.addAmount(genericBankLine.getAmount());
        this.category.addGenericBankLine(genericBankLine);
    }

    @Override
    public int compareTo(Object o) {
        SubCategory othersSubCategory = (SubCategory)o;
        if (this.getCategory().equals(othersSubCategory.getCategory())){
            return this.getName().compareTo(othersSubCategory.getName());
        } else {
            return this.getCategory().compareTo(othersSubCategory.getCategory());
        }
    }
}
