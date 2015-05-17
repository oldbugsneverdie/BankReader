package BankReader.file.ABN;

import BankReader.category.FinancialCategories;
import BankReader.category.FinancialCategory;
import BankReader.category.SubCategory;
import BankReader.file.BankProcessor;
import BankReader.file.GenericBankLine;
import BankReader.util.Amount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by jan on 3-5-15.
 */
public class ABNProcessor extends BankProcessor implements ItemProcessor<ABNBankLine, GenericBankLine> {

    public static final Logger LOG = LoggerFactory.getLogger(ABNProcessor.class);

    @Autowired
    FinancialCategories financialCategories;

    @Override
    public GenericBankLine process(final ABNBankLine abnBankLine) throws Exception {
        //        return marksheet;
        GenericBankLine genericBankLine =  new GenericBankLine();

        genericBankLine.setAmount(new Amount(abnBankLine.getTransactiebedrag()));
        genericBankLine.setDescription(abnBankLine.getOmschrijving());

        String dateAsString = abnBankLine.getTransactiedatum();
        String year = dateAsString.substring(0, 4);
        String month = dateAsString.substring(4, 6);
        String day = dateAsString.substring(6,8);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(day));
        genericBankLine.setDate(calendar.getTime());

        SubCategory subCategory = financialCategories.getSubCategory(genericBankLine.getDescription());

        if (subCategory==null){
            genericBankLine.setCategory(null);
            genericBankLine.setSubCategory(null);
            financialCategories.addUnmatchedGenericBankLine(genericBankLine);
            LOG.warn("Could not find category for amount {}, {}", genericBankLine.getAmount(), genericBankLine.getDescription());
        } else {
            genericBankLine.setCategory(subCategory.getCategory());
            genericBankLine.setSubCategory(subCategory);
            subCategory.addGenericBankLine(genericBankLine);
        }

        return genericBankLine;
    }

}
