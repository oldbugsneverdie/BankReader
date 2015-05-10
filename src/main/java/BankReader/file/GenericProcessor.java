package BankReader.file;

import BankReader.category.FinancialCategory;
import BankReader.file.ABN.ABNBankLine;
import BankReader.util.Amount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import sun.net.www.content.text.Generic;

/**
 * Created by jadu on 10-5-2015.
 */
public class GenericProcessor extends BankProcessor implements ItemProcessor<GenericBankLine, GenericBankLine> {

    public static final Logger LOG = LoggerFactory.getLogger(GenericProcessor.class);

    @Override
    public GenericBankLine process(final GenericBankLine genericBankLine) throws Exception {
        return genericBankLine;
    }

}
