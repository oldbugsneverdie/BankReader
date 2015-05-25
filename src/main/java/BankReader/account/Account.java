package BankReader.account;

import BankReader.file.GenericBankLine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jadu on 24-5-2015.
 */
public class Account {

    public static final String ACCOUNT_TYPE_REAL = "real";
    public static final String ACCOUNT_TYPE_VIRTUAL = "virtual";

    private String accountName;
    private String accountFileName;
    private String accountType;
    private String comment;
    private boolean isVirtualAccount = false;
    private List<GenericBankLine> internalTransfers = new ArrayList<GenericBankLine>();
    private List<GenericBankLine> externalTransfers = new ArrayList<GenericBankLine>();

    public Account(String accountName, String accountFileName, String accountType) {
        this.accountName = accountName;
        this.accountFileName = accountFileName;
        this.accountType = accountType;
        if (ACCOUNT_TYPE_REAL.equalsIgnoreCase(accountType) || ACCOUNT_TYPE_VIRTUAL.equalsIgnoreCase(accountType)){
            //OK
        } else {
            throw new IllegalArgumentException("Invalid account type: " + accountType + ". Valid types are: " + ACCOUNT_TYPE_REAL + ", or " + ACCOUNT_TYPE_VIRTUAL) ;
        }
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountFileName() {
        return accountFileName;
    }

    public void setAccountFileName(String accountFileName) {
        this.accountFileName = accountFileName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAccountType() {
        return accountType;
    }

    public boolean isVirtualAccount() {
        return ACCOUNT_TYPE_VIRTUAL.equalsIgnoreCase(this.getAccountType());
    }

    public void addInternalTransfer(GenericBankLine genericBankLine) {
        internalTransfers.add(genericBankLine);
    }

    public List<GenericBankLine> getInternalTransfers() {
        return internalTransfers;
    }

    public void addExternalTransfer(GenericBankLine genericBankLine) {
        externalTransfers.add(genericBankLine);
    }

    public List<GenericBankLine> getExternalTransfers() {
        return externalTransfers;
    }

    @Override
    public String toString() {
        return "Account '"+this.accountName+"', file: " + this.accountFileName + ", " + this.getAccountType()+ " ("+this.comment+")";
    }

}
