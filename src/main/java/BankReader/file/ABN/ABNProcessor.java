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

    @Override
    public GenericBankLine process(final ABNBankLine abnBankLine) throws Exception {
        //        return marksheet;
        GenericBankLine genericBankLine =  new GenericBankLine();

        genericBankLine.setAmount(abnBankLine.getTransactiebedrag());
        genericBankLine.setDescription(abnBankLine.getOmschrijving());
        genericBankLine.setDate(abnBankLine.getTransactiedatum());

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
