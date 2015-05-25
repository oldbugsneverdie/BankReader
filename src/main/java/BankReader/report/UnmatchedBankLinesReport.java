package BankReader.report;

import BankReader.category.FinancialCategoryLoader;
import BankReader.file.GenericBankLine;
import BankReader.util.Amount;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by jadu on 11-5-2015.
 */
@Component
@Configuration
public class UnmatchedBankLinesReport extends BaseReport implements Tasklet {

    private static Logger LOG = LoggerFactory.getLogger(UnmatchedBankLinesReport.class);

    public UnmatchedBankLinesReport() {
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("Unmatched bank lines");

        LOG.info("Found {} unmatched banklines", financialCategoryLoader.getUnMatchedGenericBankLines().size());
        int rowNumber = 0;
        Amount totalAmount = new Amount();
        for (GenericBankLine genericBankLine : financialCategoryLoader.getUnMatchedGenericBankLines()){
            LOG.debug("Found unmatched bankline {}", genericBankLine);
            rowNumber++;
            totalAmount.addAmount(genericBankLine.getAmount());
            createGenericBankLineRow(rowNumber, genericBankLine, sheet);
        }
        LOG.info("Total amount of unmatched banklines is {}", totalAmount);

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);

        // Write the excel file
        String fileName = outputDirectory + "/unmatched-banklines.xls";
        try (FileOutputStream out = new FileOutputStream(new File(fileName));) {
            LOG.info("Writing unmatched bank lines file to: {}" + fileName);
            workbook.write(out);
        }
        catch (IOException e)
        {
            LOG.error("Could not write excel file",e);
        }

        return RepeatStatus.FINISHED;

    }

    private void createGenericBankLineRow(int rowNumber, GenericBankLine bankLine, Sheet sheet) {

        Row row = sheet.createRow(rowNumber);

        int col = 0;

        Cell cell = row.createCell(col++);
        cell.setCellValue(bankLine.getDate().toString());

        cell = row.createCell(col++);
        cell.setCellValue(bankLine.getDescription());

        cell = row.createCell(col++);
        cell.setCellValue(bankLine.getAmount().getAmountInCents());

    }

}
