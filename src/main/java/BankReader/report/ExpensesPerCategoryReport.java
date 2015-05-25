package BankReader.report;

import BankReader.account.AccountLoader;
import BankReader.category.Category;
import BankReader.category.FinancialCategoryLoader;
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
import java.time.Month;

/**
 * Created by jadu on 11-5-2015.
 */
@Component
@Configuration
public class ExpensesPerCategoryReport extends BaseReport implements Tasklet {

    private static Logger LOG = LoggerFactory.getLogger(ExpensesPerCategoryReport.class);

    public ExpensesPerCategoryReport() {
    }


    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        Amount totalAmountCategories = new Amount();
        Amount totalAmountPlusCategories = new Amount();
        Amount totalAmountMinusCategories = new Amount();
        for (Category category : financialCategoryLoader.getAllCategories()){
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

        // Per category

        Sheet sheet = workbook.createSheet("Per category");

        int rownum = 0;
        for (Category category : financialCategoryLoader.getAllCategories()){

            int percentage = 0;
            if (category.getAmount().getAmountInCents() < 0 ){
                percentage = (category.getAmount().getAmountInCents()*100) / totalAmountMinusCategories.getAmountInCents();
                createCategoryRow(rownum++, category, percentage, sheet);
            } else{
                percentage = (category.getAmount().getAmountInCents()*100) / totalAmountPlusCategories.getAmountInCents();
                createCategoryRow(rownum++, category, percentage, sheet);
            }
        }

        //Per subcategory

        Sheet subcategorySheet = workbook.createSheet("Per sub category");
        rownum = 0;

        for (SubCategory subCategory : financialCategoryLoader.getAllSubCategories()){
            int percentage = 0;
            if (subCategory.getAmount().getAmountInCents() < 0 ){
                percentage = (subCategory.getAmount().getAmountInCents()*100) / totalAmountMinusCategories.getAmountInCents();
                createSubCategoryRow(rownum++, subCategory, percentage, subcategorySheet);
            } else{
                percentage = (subCategory.getAmount().getAmountInCents()*100) / totalAmountPlusCategories.getAmountInCents();
                createSubCategoryRow(rownum++, subCategory, percentage, subcategorySheet);
            }
        }


        // Per category per month

        Sheet categoryPerMonthSheet = workbook.createSheet("Per category per month");
        rownum = 0;

        createCategoryPerMonthHeaderRow(rownum++, categoryPerMonthSheet);

        for (Category category : financialCategoryLoader.getAllCategories()){

            int percentage = 0;
            if (category.getAmount().getAmountInCents() < 0 ){
                percentage = (category.getAmount().getAmountInCents()*100) / totalAmountMinusCategories.getAmountInCents();
                createCategoryPerMonthRow(rownum++, category, percentage, categoryPerMonthSheet);
            } else{
                percentage = (category.getAmount().getAmountInCents()*100) / totalAmountPlusCategories.getAmountInCents();
                createCategoryPerMonthRow(rownum++, category, percentage, categoryPerMonthSheet);
            }
        }

        // Write the excel file
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
        int colNumber = 0;

        Cell categoryNameCell = row.createCell(colNumber++);
        categoryNameCell.setCellValue(subCategory.getCategory().getName());

        Cell subCategoryNameCell = row.createCell(colNumber++);
        subCategoryNameCell.setCellValue(subCategory.getName());

        Cell subCategoryAmountCell = row.createCell(colNumber++);
        subCategoryAmountCell.setCellValue(subCategory.getAmount().toString());

        Cell subCategoryPercentageCell = row.createCell(colNumber++);
        subCategoryPercentageCell.setCellValue(percentage);

    }

    private void createCategoryRow(int rowNumber, Category category, int percentage, Sheet sheet) {

        Row row = sheet.createRow(rowNumber);
        int colNumber = 0;

        Cell categoryNameCell = row.createCell(colNumber++);
        categoryNameCell.setCellValue(category.getName());

        Cell categoryAmountCell = row.createCell(colNumber++);
        categoryAmountCell.setCellValue(category.getAmount().toString());

        Cell categoryPercentageCell = row.createCell(colNumber++);
        categoryPercentageCell.setCellValue(percentage);

    }

    private void createCategoryPerMonthRow(int rowNumber, Category category, int percentage, Sheet sheet) {

        Row row = sheet.createRow(rowNumber++);
        int colNumber = 0;

        Cell categoryNameCell = row.createCell(colNumber++);
        categoryNameCell.setCellValue(category.getName());

        Cell categoryPercentageCell = row.createCell(colNumber++);
        categoryPercentageCell.setCellValue(percentage);

        createMonthCell(row, colNumber++, category, Month.JANUARY);
        createMonthCell(row, colNumber++, category, Month.FEBRUARY);
        createMonthCell(row, colNumber++, category, Month.MARCH);
        createMonthCell(row, colNumber++, category, Month.APRIL);
        createMonthCell(row, colNumber++, category, Month.MAY);
        createMonthCell(row, colNumber++, category, Month.JUNE);
        createMonthCell(row, colNumber++, category, Month.JULY);
        createMonthCell(row, colNumber++, category, Month.AUGUST);
        createMonthCell(row, colNumber++, category, Month.SEPTEMBER);
        createMonthCell(row, colNumber++, category, Month.OCTOBER);
        createMonthCell(row, colNumber++, category, Month.NOVEMBER);
        createMonthCell(row, colNumber++, category, Month.DECEMBER);
    }

    private void createMonthCell(Row row, int columnNumber, Category category, Month month) {
        Cell cell = row.createCell(columnNumber);
        Amount amount = category.getAmountByMonth(month);
        cell.setCellValue(new Double(amount.toString()));
    }

    private void createCategoryPerMonthHeaderRow(int rowNumber, Sheet sheet) {

        // Header row
        Row row = sheet.createRow(rowNumber++);
        int colNumber = 0;

        Cell categoryNameHeaderCell = row.createCell(colNumber++);
        categoryNameHeaderCell.setCellValue("Category");

        Cell categoryPercentageHeaderCell = row.createCell(colNumber++);
        categoryPercentageHeaderCell.setCellValue("Percentage");

        createMonthHeaderCell(row, colNumber++, "Jan");
        createMonthHeaderCell(row, colNumber++, "Feb");
        createMonthHeaderCell(row, colNumber++, "Mar");
        createMonthHeaderCell(row, colNumber++, "Apr");
        createMonthHeaderCell(row, colNumber++, "May");
        createMonthHeaderCell(row, colNumber++, "Jun");
        createMonthHeaderCell(row, colNumber++, "Jul");
        createMonthHeaderCell(row, colNumber++, "Aug");
        createMonthHeaderCell(row, colNumber++, "Sep");
        createMonthHeaderCell(row, colNumber++, "Oct");
        createMonthHeaderCell(row, colNumber++, "Nov");
        createMonthHeaderCell(row, colNumber++, "Dec");

    }

    private void createMonthHeaderCell(Row row, int columnNumber, String nameOfMonth) {
        Cell cell = row.createCell(columnNumber);
        cell.setCellValue(nameOfMonth);
    }




}
