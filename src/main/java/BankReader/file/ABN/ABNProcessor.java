package BankReader.file.ABN;

import BankReader.account.Account;
import BankReader.account.AccountLoader;
import BankReader.category.FinancialCategoryLoader;
import BankReader.category.SubCategory;
import BankReader.file.BankProcessor;
import BankReader.file.GenericBankLine;
import BankReader.util.Amount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;

/**
 * Created by jan on 3-5-15.
 */
public class ABNProcessor extends BankProcessor implements ItemProcessor<ABNBankLine, GenericBankLine> {

    public static final Logger LOG = LoggerFactory.getLogger(ABNProcessor.class);


    @Override
    public GenericBankLine process(final ABNBankLine abnBankLine) throws Exception {

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

        super.process(genericBankLine);

        return genericBankLine;

    }

}
