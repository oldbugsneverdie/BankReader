package BankReader.file;

import BankReader.account.Account;
import BankReader.account.AccountLoader;
import BankReader.category.FinancialCategoryLoader;
import BankReader.category.SubCategory;
import BankReader.util.Amount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by jadu on 10-5-2015.
 */
public class BankProcessor{

    public static final Logger LOG = LoggerFactory.getLogger(BankProcessor.class);

    @Autowired
    private FinancialCategoryLoader financialCategoryLoader;

    @Autowired
    private AccountLoader accountLoader;


    public GenericBankLine process(GenericBankLine genericBankLine) {
        SubCategory subCategory = financialCategoryLoader.getSubCategory(genericBankLine.getDescription());

        if (subCategory==null){
            genericBankLine.setCategory(null);
            genericBankLine.setSubCategory(null);
            financialCategoryLoader.addUnmatchedGenericBankLine(genericBankLine);
            LOG.warn("Could not find category for amount {}, {}", genericBankLine.getAmount(), genericBankLine.getDescription());
        } else {
            genericBankLine.setCategory(subCategory.getCategory());
            genericBankLine.setSubCategory(subCategory);
            if (subCategory.getCategory().isInternalTransfer()){
                Account account = accountLoader.getAccount(subCategory.getName());
                if (account.isVirtualAccount()){
                    Amount reversedAmount = genericBankLine.getAmount().reversedAmount();
                    GenericBankLine reversedGenericBankLine = new GenericBankLine(genericBankLine);
                    reversedGenericBankLine.setAmount(reversedAmount);
                    account.addInternalTransfer(reversedGenericBankLine);
                }
                account.addInternalTransfer(genericBankLine);
            } else {
                subCategory.addGenericBankLine(genericBankLine);
            }
        }
        return genericBankLine;

    }
}
