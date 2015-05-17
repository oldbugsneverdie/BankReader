package BankReader.category;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jan on 3-5-15.
 */
@Component
public class FinancialCategories {

    private static final Logger LOG = LoggerFactory.getLogger(FinancialCategories.class);
    public static final String CATEGORIES_FILE_NAME = "categories.txt";

    @Value("${settings.directory}")
    protected String settingsDirectory;

    private List<FinancialCategory> financialCategories = new ArrayList<FinancialCategory>();

    public FinancialCategories() {
    }

    @PostConstruct
    public void init() throws IOException {
        Path path = FileSystems.getDefault().getPath(settingsDirectory, CATEGORIES_FILE_NAME);
        List<String> categoryLines = Files.readAllLines(path, Charset.defaultCharset());
        for (String line : categoryLines){
            LOG.info("reading category {}", line);

            if (line.trim().isEmpty()){
                //skip empty lines
                continue;
            }

            String[] keyValue = line.split("=");
            if (keyValue.length==0){
                LOG.error("Skipping invalid category: {}. It should be in format key=category,subcategory");
                continue;
            }

            String key = keyValue[0];
            String value = keyValue[1];

            String[] categoryAndSubCategory = value.split(",");
            if (categoryAndSubCategory.length==0){
                LOG.error("Skipping invalid category: {}. It should be in format key=category,subcategory");
                continue;
            }
            String cat = categoryAndSubCategory[0];
            String subCat = "";
            if (categoryAndSubCategory.length>1){
                subCat = categoryAndSubCategory[1];
            }

            addFinancialCategory(key, cat, subCat);

        }
    }

    private void addFinancialCategory(String key, String categoryName, String subCategoryName){

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
