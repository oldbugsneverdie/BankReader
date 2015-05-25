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

    protected Account account;

    public BankProcessor(Account account) {
        this.account = account;
    }

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
                // Add the bank line as an internal transfer to the account
                account.addInternalTransfer(genericBankLine);
                // Get the target account for this internal transfer
                Account targetAccount = accountLoader.getAccount(subCategory.getName());
                if (targetAccount.isVirtualAccount()){
                    // If the target account is a virtual account (like 'cash') we also create a new generic bank line with the reversed amount for it.
                    Amount reversedAmount = genericBankLine.getAmount().reversedAmount();
                    GenericBankLine reversedGenericBankLine = new GenericBankLine(genericBankLine);
                    reversedGenericBankLine.setAmount(reversedAmount);
                    targetAccount.addInternalTransfer(reversedGenericBankLine);
                } else {
                    // Do nothing.
                    // If the target account is not a virtual account we do nothing as the transfer will appear automatically when
                    // processing the bank lines of that target account.
                }
            } else {
                // If a bank line is not an internal transfer, but an actual expense, we link it to the sub category.
                subCategory.addGenericBankLine(genericBankLine);
                // And as an external transfer to the account
                account.addExternalTransfer(genericBankLine);
            }
        }
        return genericBankLine;

    }
}
