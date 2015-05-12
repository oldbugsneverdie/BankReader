package BankReader.category;

import BankReader.util.Amount;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jan on 3-5-15.
 */
public class FinancialCategory {

    private String categoryName;
    private String subCategoryName;
    private String key;

    private Amount amount = new Amount(null);

    public FinancialCategory(String key, String categoryName, String subCategoryName) {
        this.key = key;
        this.categoryName = categoryName;
        this.subCategoryName = subCategoryName;
    }

    public void addAmount(Amount amount) {
        this.amount.addAmount(amount);
    }

    public boolean categoriesMatch(String categoryName, String subCategoryName){
        return  nameMatches(this.categoryName, categoryName) && nameMatches(this.subCategoryName, subCategoryName);
    }

    private boolean nameMatches(String name1, String name2) {
        if (name1 == null && name2 == null){
            return true;
        }
        if (name1 != null && name1.equals(name2)){
            return true;
        }
        return false;
    }


    public Amount getAmount() {
        return amount;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "Financial category: " + categoryName + " / " + subCategoryName + ": " + amount;
    }
}
