package BankReader.report;

import BankReader.account.AccountLoader;
import BankReader.category.FinancialCategoryLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * Base class for reports.
 */
public class BaseReport {

    public static final String NEW_LINE = "%n";
    public static final String EMPTY_CELL = "-";

    @Autowired
    protected FinancialCategoryLoader financialCategoryLoader;

    @Autowired
    protected AccountLoader accountLoader;

    @Value("${output.directory}")
    protected String outputDirectory;


}
