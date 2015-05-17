package BankReader.report.category;

import BankReader.category.Category;
import BankReader.category.FinancialCategories;
import BankReader.category.SubCategory;
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

import java.io.*;

/**
 * Created by jadu on 11-5-2015.
 */
@Component
@Configuration
public class ExpensesPerCategoryReport implements Tasklet {

    public static final String NEW_LINE = "%n";
    private static final String EMPTY_CELL = "-";
    private static Logger LOG = LoggerFactory.getLogger(ExpensesPerCategoryReport.class);

    @Autowired
    private FinancialCategories financialCategories;

    @Value("${output.directory}")
    private String outputDirectory;
    public static final String SEPARATOR = ";";

    public ExpensesPerCategoryReport() {
    }


    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        Amount totalAmountCategories = new Amount();
        Amount totalAmountPlusCategories = new Amount();
        Amount totalAmountMinusCategories = new Amount();
        for (Category category : financialCategories.getAllCategories()){
            totalAmountCategories.addAmount(category.getAmount());
            if (category.getAmount().getAmountInCents() < 0 ){
                LOG.info("Category " + category.getName() + " add " + category.getAmount() + " to minus");
                totalAmountMinusCategories.addAmount(category.getAmount());
            } else {
                LOG.info("Category " + category.getName() + " add " + category.getAmount() + " to plus");
                totalAmountPlusCategories.addAmount(category.getAmount());
            }
        }
        LOG.info("Total " + totalAmountCategories.getAmountInCents());
        LOG.info("Plus " + totalAmountPlusCategories.getAmountInCents());
        LOG.info("Minus " + totalAmountMinusCategories.getAmountInCents());

        Workbook workbook = new HSSFWorkbook();

        Sheet sheet = workbook.createSheet("Per category");

        int rownum = 0;
        for (Category category : financialCategories.getAllCategories()){

            int percentage = 0;
            if (category.getAmount().getAmountInCents() < 0 ){
                percentage = (category.getAmount().getAmountInCents()*100) / totalAmountMinusCategories.getAmountInCents();
                createCategoryRow(rownum++, category, percentage, sheet);
            } else{
                percentage = (category.getAmount().getAmountInCents()*100) / totalAmountPlusCategories.getAmountInCents();
                createCategoryRow(rownum++, category, percentage, sheet);
            }
        }


        Sheet subcategorySheet = workbook.createSheet("Per sub category");
        rownum = 0;

        for (SubCategory subCategory : financialCategories.getAllSubCategories()){
            int percentage = 0;
            if (subCategory.getAmount().getAmountInCents() < 0 ){
                percentage = (subCategory.getAmount().getAmountInCents()*100) / totalAmountMinusCategories.getAmountInCents();
                createSubCategoryRow(rownum++, subCategory, percentage, subcategorySheet);
            } else{
                percentage = (subCategory.getAmount().getAmountInCents()*100) / totalAmountPlusCategories.getAmountInCents();
                createSubCategoryRow(rownum++, subCategory, percentage, subcategorySheet);
            }
        }

        try (FileOutputStream out = new FileOutputStream(new File(outputDirectory + "/expenses-per-category.xls"));) {
            workbook.write(out);
        }
        catch (IOException e)
        {
            LOG.error("Could not write excel file",e);
        }

        return RepeatStatus.FINISHED;

    }

    private void createSubCategoryRow(int rowNumber, SubCategory subCategory, int percentage, Sheet sheet) {

        Row row = sheet.createRow(rowNumber);

        Cell categoryNameCell = row.createCell(1);
        categoryNameCell.setCellValue(subCategory.getCategory().getName());

        Cell subCategoryNameCell = row.createCell(2);
        subCategoryNameCell.setCellValue(subCategory.getName());

        Cell subCategoryAmountCell = row.createCell(3);
        subCategoryAmountCell.setCellValue(subCategory.getAmount().toString());

        Cell subCategoryPercentageCell = row.createCell(4);
        subCategoryPercentageCell.setCellValue(percentage);

    }

    private void createCategoryRow(int rowNumber, Category category, int percentage, Sheet sheet) {

        Row row = sheet.createRow(rowNumber);

        Cell categoryNameCell = row.createCell(1);
        categoryNameCell.setCellValue(category.getName());

        Cell categoryAmountCell = row.createCell(2);
        categoryAmountCell.setCellValue(category.getAmount().toString());

        Cell categoryPercentageCell = row.createCell(3);
        categoryPercentageCell.setCellValue(percentage);

    }

}
