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

    public FinancialCategory(String key, String categoryName) {
        this.key = key;
        this.categoryName = categoryName;
    }

    public void addAmount(Amount amount) {
        this.amount.addAmount(amount);
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

}
