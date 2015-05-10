package BankReader.file;

import BankReader.category.FinancialCategories;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by jadu on 10-5-2015.
 */
public class BankProcessor{

    public static final Logger LOG = LoggerFactory.getLogger(BankProcessor.class);

    @Value("${input.directory}")
    protected String inputDirectory;

    public static final String CATEGORIES_FILE_NAME = "categories.txt";

    protected FinancialCategories financialCategories = new FinancialCategories();


    @PostConstruct
    public void init() throws IOException {
        Path path = FileSystems.getDefault().getPath(inputDirectory, CATEGORIES_FILE_NAME);
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

            financialCategories.addFinancialCategory(key, cat, subCat);

        }
    }


}
