package BankReader.report;

import BankReader.account.Account;
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
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Report on the bank lines and amounts per Account
 */
@Component
@Configuration
public class AccountReport extends BaseReport implements Tasklet {

    private static Logger LOG = LoggerFactory.getLogger(AccountReport.class);


    public AccountReport() {
    }


    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        Workbook workbook = createWorkbook();

        // Totals per account

        Sheet sheet = workbook.createSheet("Overview");

        int rownum = 0;
        for (Account account: accountLoader.getAccounts()) {

            Amount totalInternalTransferAmount = new Amount();
            Amount totalExternalTransferAmount = new Amount();

            for (GenericBankLine genericBankLine : account.getInternalTransfers()){
                totalInternalTransferAmount.addAmount(genericBankLine.getAmount());
            }

            for (GenericBankLine genericBankLine : account.getExternalTransfers()){
                totalExternalTransferAmount.addAmount(genericBankLine.getAmount());
            }

            createAccountRow(rownum++, account, totalInternalTransferAmount, totalExternalTransferAmount, sheet);

        }

        // All transfers per account

        sheet = workbook.createSheet("Account transfers");

        rownum = 0;
        for (Account account: accountLoader.getAccounts()) {
            Amount totalInternalTransferAmount = new Amount();
            for (GenericBankLine genericBankLine : account.getInternalTransfers()){
                totalInternalTransferAmount.addAmount(genericBankLine.getAmount());
            }
            createAccountRow(rownum++, account, totalInternalTransferAmount, null, sheet);
            for (GenericBankLine genericBankLine : account.getInternalTransfers()){
                createAccountTransferRow(rownum++, account, genericBankLine, sheet);
            }

            Amount totalExternalTransferAmount = new Amount();
            for (GenericBankLine genericBankLine : account.getExternalTransfers()){
                totalExternalTransferAmount.addAmount(genericBankLine.getAmount());
            }
            createAccountRow(rownum++, account, null, totalExternalTransferAmount, sheet);
            for (GenericBankLine genericBankLine : account.getExternalTransfers()){
                createAccountTransferRow(rownum++, account, genericBankLine, sheet);
            }


        }

        // Write the excel file
        try (FileOutputStream out = new FileOutputStream(new File(outputDirectory + "/accounts.xls"));) {
            workbook.write(out);
        }
        catch (IOException e)
        {
            LOG.error("Could not write excel file",e);
        }

        return RepeatStatus.FINISHED;

    }

    private void createAccountRow(int rowNumber, Account account, Amount totalInternalTransferAmount, Amount totalExternalTransferAmount, Sheet sheet) {

        Row row = sheet.createRow(rowNumber);
        int cellNumber = 0;

        Cell accountNameCell = row.createCell(cellNumber);
        accountNameCell.setCellValue(account.getAccountName());

        cellNumber++;
        if (totalInternalTransferAmount != null){
            createEuroCell(row, cellNumber, totalInternalTransferAmount);
        }

        cellNumber++;
        if (totalExternalTransferAmount != null){
            createEuroCell(row, cellNumber, totalExternalTransferAmount);
        }

    }

    private void createAccountTransferRow(int rowNumber, Account account, GenericBankLine genericBankLine, Sheet sheet) {
        Row row = sheet.createRow(rowNumber);
        int cellNumber = 0;

        Cell accountNameCell = row.createCell(cellNumber++);
        accountNameCell.setCellValue(account.getAccountName());

        Cell transferInformationCell = row.createCell(cellNumber++);
        transferInformationCell.setCellValue(genericBankLine.toString());
    }


}
