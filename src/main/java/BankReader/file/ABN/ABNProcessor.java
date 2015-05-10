package BankReader.file.ABN;

import BankReader.category.FinancialCategories;
import BankReader.category.FinancialCategory;
import BankReader.file.BankProcessor;
import BankReader.file.GenericBankLine;
import BankReader.util.Amount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by jan on 3-5-15.
 */
public class ABNProcessor extends BankProcessor implements ItemProcessor<ABNBankLine, GenericBankLine> {

    public static final Logger LOG = LoggerFactory.getLogger(ABNProcessor.class);
    public static final String CATEGORIES_FILE_NAME = "categories.txt";

    private FinancialCategories financialCategories = new FinancialCategories();

    @Override
    public GenericBankLine process(final ABNBankLine abnBankLine) throws Exception {
        //        return marksheet;
        GenericBankLine genericBankLine =  new GenericBankLine();

        genericBankLine.setAmount(abnBankLine.getTransactiebedrag());
        genericBankLine.setDescription(abnBankLine.getOmschrijving());

        FinancialCategory financialCategory = financialCategories.getFinancialCategory(genericBankLine.getDescription());

        if (financialCategory==null){
            genericBankLine.setCategory("Unknown");
            genericBankLine.setSubCategory("Unknown");
            LOG.warn("Could not find category for amount {}, {}", genericBankLine.getAmount(), genericBankLine.getDescription());
        } else {
            genericBankLine.setCategory(financialCategory.getCategoryName());
            genericBankLine.setSubCategory(financialCategory.getSubCategoryName());
            Amount amount = new Amount(genericBankLine.getAmount());
            financialCategory.addAmount(amount);
        }

        return genericBankLine;
    }

    @PostConstruct
    public void init() throws IOException {
        //TODO fix path
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
