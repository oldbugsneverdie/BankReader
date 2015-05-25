package BankReader.file.ING;

import BankReader.category.FinancialCategoryLoader;
import BankReader.category.SubCategory;
import BankReader.file.GenericBankLine;
import BankReader.file.BankProcessor;
import BankReader.util.Amount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;

/**
 * Created by jan on 3-5-15.
 */
public class INGProcessor extends BankProcessor implements ItemProcessor<INGBankLine, GenericBankLine> {

    public static final Logger LOG = LoggerFactory.getLogger(INGProcessor.class);

    @Autowired
    FinancialCategoryLoader financialCategories;

    @Override
    public GenericBankLine process(final INGBankLine ingBankLine) throws Exception {

        GenericBankLine genericBankLine =  new GenericBankLine();

        String amountAsString = ingBankLine.getBedrag();
        if (amountAsString!=null && !amountAsString.isEmpty() && ingBankLine.getAfBij().toLowerCase().equals("af")){
            amountAsString = "-" + ingBankLine.getBedrag();
        }
        genericBankLine.setAmount(new Amount(amountAsString));
        genericBankLine.setDescription(ingBankLine.getOmschrijving() + " - " + ingBankLine.getMededelingen() );


        String dateAsString = ingBankLine.getDatum();
        String year = dateAsString.substring(0, 4);
        String month = dateAsString.substring(4, 6);
        String day = dateAsString.substring(6,8);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(day));
        genericBankLine.setDate(calendar.getTime());

        super.process(genericBankLine);

        return genericBankLine;
    }

}
