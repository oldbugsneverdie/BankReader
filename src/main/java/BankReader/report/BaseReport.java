package BankReader.report;

import BankReader.account.AccountLoader;
import BankReader.category.FinancialCategoryLoader;
import BankReader.util.Amount;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;

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

    protected CellStyle euroCellStyle;

    protected Workbook createWorkbook(){
        Workbook workbook = new HSSFWorkbook();
        DataFormat cf = workbook.createDataFormat();
        euroCellStyle = workbook.createCellStyle();
        euroCellStyle.setDataFormat(cf.getFormat("€#,##0;€-#,##0"));
        return workbook;
    }

    protected Cell createEuroCell(Row row, int columnNumber, Amount amount){

        Cell cell = row.createCell(columnNumber);
        cell.setCellStyle(euroCellStyle);
        cell.setCellValue(amount.getAmountInCents()/100);
        return cell;
    }

}
