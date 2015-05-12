package BankReader.category;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jan on 3-5-15.
 */
public class FinancialCategories {

    private static final Logger LOG = LoggerFactory.getLogger(FinancialCategories.class);

    List<FinancialCategory> financialCategories = new ArrayList<>();

    public FinancialCategories() {
    }

    public void addFinancialCategory(String key, String categoryName, String subCategoryName){

        if (keyIsUnique(key)){
            addNewFinancialCategory(key, categoryName, subCategoryName);
        } else {
            LOG.error("Will ignore category with key '{}', as it already exists", key);
        }
    }

    private boolean keyIsUnique(String key) {
        for(FinancialCategory financialCategory: financialCategories){
            if (financialCategory.getKey().equals(key)){
                return false;
            }
        }
        return true;
    }

    private void addNewFinancialCategory(String key, String categoryName, String subCategoryName) {
        FinancialCategory financialCategory = new FinancialCategory(key, categoryName, subCategoryName);
        financialCategories.add(financialCategory);
    }

    public List<FinancialCategory> getAll() {
        return financialCategories;
    }

    public FinancialCategory getFinancialCategory(String banklineDescription) {

        for(FinancialCategory financialCategory: financialCategories){
            if (banklineDescription.toLowerCase().contains(financialCategory.getKey().toLowerCase())){
                return financialCategory;
            }
        }
        return null;

    }
}
