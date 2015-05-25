package BankReader.account;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jan on 3-5-15.
 */
@Component
public class AccountLoader {

    private static final Logger LOG = LoggerFactory.getLogger(AccountLoader.class);
    public static final String ACCOUNTS_FILE_NAME = "accounts.txt";


    @Value("${settings.directory}")
    protected String settingsDirectory;

    @Value("${input.directory}")
    protected String inputDirectory;

    private List<Account> accounts = new ArrayList<Account>();

    @PostConstruct
    public void init() throws IOException {

        Path path = FileSystems.getDefault().getPath(settingsDirectory, ACCOUNTS_FILE_NAME);
        readAccountFile(path);

    }

    private void readAccountFile(Path path) throws IOException {
        LOG.info("Read accounts from: {}", path);
        List<String> accountLines = Files.readAllLines(path, Charset.defaultCharset());
        int lineNumber = 0;
        String currentComment = "";
        for (String line : accountLines){
            lineNumber++;
            LOG.info("reading line {},  {}", lineNumber, line);

            if (line.trim().isEmpty()){
                //skip empty lines
                continue;
            }

            if (line.startsWith("#")){
                currentComment = line;
                continue;
            }

            String[] values = line.split("=");
            if (values.length<2){
                LOG.error("Skipping invalid account: {}. It should be in format accountName=accountFileName,accountType");
                continue;
            }
            String accountName = values[0];

            String[] xxxx = values[1].split(",");
            if (xxxx.length<2){
                LOG.error("Skipping invalid account: {}. It should be in format accountName=accountFileName,accountType");
                continue;
            }
            String accountFileName = xxxx[0];
            String accountType = xxxx[1];

            addAccount(accountName, accountFileName, accountType, currentComment);

        }
    }

    private void addAccount(String accountName, String accountFileName, String accountType, String comment) {

        if (accountIsUnique(accountName)){
            LOG.info("Account {} does not yet exist.", accountName);
            Account account = new Account(accountName, accountFileName, accountType);
            account.setComment(comment);
            accounts.add(account);
            LOG.info("Created account {}", account);
        } else {
            LOG.error("Will ignore account {} / {} , as the an account with that name already exists", accountName, accountFileName);
        }
    }

    private boolean accountIsUnique(String accountName) {
        for(Account account: accounts){
            if (account.getAccountName().toLowerCase().equals(accountName.toLowerCase())){
                return false;
            }
        }
        return true;
    }

    public Account getAccount(String accountName) {
        for(Account account: accounts){
            if (account.getAccountName().toLowerCase().equals(accountName.toLowerCase())){
                return account;
            }
        }
        LOG.error("Could not find account {}", accountName);
        LOG.info("Currently {} accounts are known", accounts.size());
        for(Account account: accounts){
            LOG.info("- {}", account);
        }
        throw new IllegalArgumentException("Account " + accountName + " does not exist");
    }

    public List<Account> getAccounts() {
        return accounts;
    }
}
