package BankReader.file.ING;

import BankReader.category.FinancialCategories;
import BankReader.category.FinancialCategory;
import BankReader.file.GenericBankLine;
import BankReader.file.BankProcessor;
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
public class INGProcessor extends BankProcessor implements ItemProcessor<INGBankLine, GenericBankLine> {

    public static final Logger LOG = LoggerFactory.getLogger(INGProcessor.class);

    @Override
    public GenericBankLine process(final INGBankLine ingBankLine) throws Exception {

        GenericBankLine genericBankLine =  new GenericBankLine();

        String amountAsString = ingBankLine.getBedrag();
        if (amountAsString!=null && !amountAsString.isEmpty() && ingBankLine.getAfBij().toLowerCase().equals("af")){
            amountAsString = "-" + ingBankLine.getBedrag();
        }
        genericBankLine.setAmount(amountAsString);
        genericBankLine.setDescription(ingBankLine.getOmschrijving() + " - " + ingBankLine.getMededelingen() );

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

}
