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
    private List<SubCategoryKey> keys = new ArrayList<SubCategoryKey>();
    private List<GenericBankLine> genericBankLines = new ArrayList<GenericBankLine>();
    private String comment = null;

    public SubCategory(Category category, String name, SubCategoryKey subCategoryKey) {
        Assert.notNull(name, "Could not create sub category, name is null");
        Assert.notNull(subCategoryKey, "Could not create sub category, key is null");
        Assert.notNull(category, "Could not create sub category for '"+name+"', category is null");
        this.name = name;
        this.category = category;
        this.keys.add(subCategoryKey);
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

    public List<GenericBankLine> getGenericBankLines() {
        return genericBankLines;
    }

    public void addGenericBankLine(GenericBankLine genericBankLine){
        genericBankLines.add(genericBankLine);
        this.amount.addAmount(genericBankLine.getAmount());
        this.category.addGenericBankLine(genericBankLine);
    }

    public void addKey(SubCategoryKey subCategoryKey) {
        keys.add(subCategoryKey);
    }

    public List<SubCategoryKey> getKeys() {
        return keys;
    }

    @Override
    public int compareTo(Object o) {
        SubCategory othersSubCategory = (SubCategory)o;
        if (this.getCategory().equals(othersSubCategory.getCategory())){
            return this.getName().compareTo(othersSubCategory.getName());
        } else {
            return+ this.getCategory().compareTo(othersSubCategory.getCategory());
        }
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        String result = "Subcategory " + this.getCategory().getName() + ", " + this.getName() + " for key " + this.getKeys();
        if (comment !=null){
            result += ". Comment: " + comment;
        }
        return result;
    }

}
