package BankReader.category;

import BankReader.file.GenericBankLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    private List<Category> categories = new ArrayList<Category>();
    private List<SubCategory> subCategories = new ArrayList<SubCategory>();
    private List<GenericBankLine> unMatchedGenericBankLines = new ArrayList<GenericBankLine>();

    public FinancialCategories() {
    }

    @PostConstruct
    public void init() throws IOException {
        Path path = FileSystems.getDefault().getPath(settingsDirectory, CATEGORIES_FILE_NAME);
        List<String> categoryLines = Files.readAllLines(path, Charset.defaultCharset());
        int lineNumber = 0;
        for (String line : categoryLines){
            lineNumber++;
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
            } else {
                throw new RuntimeException("Missing sub category for category: " + cat + " on line " + lineNumber);
            }

            addFinancialCategory(key, cat, subCat);

        }
    }

    private void addFinancialCategory(String key, String categoryName, String subCategoryName){

        if (keyIsUnique(key)){
            addNewSubCategory(key, categoryName, subCategoryName);
        } else {
            LOG.error("Will ignore category {} / {} with key '{}', as the key already exists", categoryName, subCategoryName, key);
        }
    }

    private boolean keyIsUnique(String key) {
        for(SubCategory subCategory: subCategories){
            if (subCategory.getKey().equals(key)){
                return false;
            }
        }
        return true;
    }

    private void addNewSubCategory(String key, String categoryName, String subCategoryName) {

        Category category = getOrCreateCategory(categoryName);

        SubCategory subCategory = getOrCreateSubCategory(category, subCategoryName, key);

    }

    private Category getOrCreateCategory(String categoryName) {
        Assert.notNull(categoryName);
        for(Category category: categories){
            if (category.getName().toLowerCase().equals(categoryName.toLowerCase())){
                return category;
            }
        }
        Category category = new Category(categoryName.toLowerCase());
        categories.add(category);
        return category;
    }

    private SubCategory getOrCreateSubCategory(Category category, String subCategoryName, String key) {
        Assert.notNull(subCategoryName);
        for(SubCategory subCategory: subCategories){
            if (subCategory.getName().toLowerCase().equals(subCategoryName.toLowerCase())
                    &&
                    (subCategory.getCategory().getName().toLowerCase().equals(category.getName().toLowerCase()))){
                return subCategory;
            }
        }
        SubCategory subCategory = new SubCategory(category, subCategoryName.toLowerCase(), key);
        subCategories.add(subCategory);
        return subCategory;
    }

    public List<SubCategory> getAllSubCategories() {
        Collections.sort(subCategories, new Comparator<SubCategory>() {
            @Override
            public int compare(SubCategory subCategory1, SubCategory subCategory2) {

                return subCategory1.compareTo(subCategory2);
            }
        });
        return subCategories;
    }

    public List<Category> getAllCategories() {

        Collections.sort(categories, new Comparator<Category>() {
            @Override
            public int compare(Category category1, Category category2) {

                return category1.compareTo(category2);
            }
        });
        return categories;

    }

    public SubCategory getSubCategory(String banklineDescription) {

        for(SubCategory subCategory: subCategories){
            if (banklineDescription.toLowerCase().contains(subCategory.getKey().toLowerCase())){
                return subCategory;
            }
        }
        return null;

    }

    public void addUnmatchedGenericBankLine(GenericBankLine genericBankLine) {
        unMatchedGenericBankLines.add(genericBankLine);
    }

    public List<GenericBankLine> getUnMatchedGenericBankLines() {
        return unMatchedGenericBankLines;
    }
}
