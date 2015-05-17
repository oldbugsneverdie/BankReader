package BankReader.file.ING;

import BankReader.category.FinancialCategories;
import BankReader.category.FinancialCategory;
import BankReader.category.SubCategory;
import BankReader.file.GenericBankLine;
import BankReader.file.BankProcessor;
import BankReader.util.Amount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

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

    @Autowired
    FinancialCategories financialCategories;

    @Override
    public GenericBankLine process(final INGBankLine ingBankLine) throws Exception {

        GenericBankLine genericBankLine =  new GenericBankLine();

        String amountAsString = ingBankLine.getBedrag();
        if (amountAsString!=null && !amountAsString.isEmpty() && ingBankLine.getAfBij().toLowerCase().equals("af")){
            amountAsString = "-" + ingBankLine.getBedrag();
        }
        genericBankLine.setAmount(new Amount(amountAsString));
        genericBankLine.setDescription(ingBankLine.getOmschrijving() + " - " + ingBankLine.getMededelingen() );

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
