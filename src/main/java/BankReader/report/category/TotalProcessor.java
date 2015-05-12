package BankReader.report.category;

import BankReader.file.BankProcessor;
import BankReader.file.GenericBankLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by jadu on 10-5-2015.
 */
public class TotalProcessor extends BankProcessor implements ItemProcessor<GenericBankLine, GenericBankLine> {

    public static final Logger LOG = LoggerFactory.getLogger(TotalProcessor.class);

    @Override
    public GenericBankLine process(final GenericBankLine genericBankLine) throws Exception {

        return genericBankLine;
    }

}
