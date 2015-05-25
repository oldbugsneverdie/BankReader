package BankReader.report;

import BankReader.category.Category;
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

        // Per sub category per month

        Sheet subCategoryPerMonthSheet = workbook.createSheet("Per sub category per month");
        rownum = 0;

        createSubCategoryPerMonthHeaderRow(rownum++, subCategoryPerMonthSheet);

        for (SubCategory subCategory : financialCategoryLoader.getAllSubCategories()){

            int percentage = 0;
            if (subCategory.getAmount().getAmountInCents() < 0 ){
                //TODO percentage = (subCategory.getAmount().getAmountInCents()*100) / totalAmountMinusCategories.getAmountInCents();
                createSubCategoryPerMonthRow(rownum++, subCategory, percentage, subCategoryPerMonthSheet);
            } else{
                //TODO percentage = (subCategory.getAmount().getAmountInCents()*100) / totalAmountPlusCategories.getAmountInCents();
                createSubCategoryPerMonthRow(rownum++, subCategory, percentage, subCategoryPerMonthSheet);
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

        createSubCategoryMonthCell(row, colNumber++, category, Month.JANUARY);
        createSubCategoryMonthCell(row, colNumber++, category, Month.FEBRUARY);
        createSubCategoryMonthCell(row, colNumber++, category, Month.MARCH);
        createSubCategoryMonthCell(row, colNumber++, category, Month.APRIL);
        createSubCategoryMonthCell(row, colNumber++, category, Month.MAY);
        createSubCategoryMonthCell(row, colNumber++, category, Month.JUNE);
        createSubCategoryMonthCell(row, colNumber++, category, Month.JULY);
        createSubCategoryMonthCell(row, colNumber++, category, Month.AUGUST);
        createSubCategoryMonthCell(row, colNumber++, category, Month.SEPTEMBER);
        createSubCategoryMonthCell(row, colNumber++, category, Month.OCTOBER);
        createSubCategoryMonthCell(row, colNumber++, category, Month.NOVEMBER);
        createSubCategoryMonthCell(row, colNumber++, category, Month.DECEMBER);
    }

    private void createSubCategoryMonthCell(Row row, int columnNumber, Category category, Month month) {
        Cell cell = row.createCell(columnNumber);
        Amount amount = category.getAmountByMonth(month);
        cell.setCellValue(new Double(amount.toString()));
    }

    private void createSubCategoryMonthCell(Row row, int columnNumber, SubCategory subCategory, Month month) {
        Cell cell = row.createCell(columnNumber);
        Amount amount = subCategory.getAmountByMonth(month);
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


    private void createSubCategoryPerMonthHeaderRow(int rowNumber, Sheet sheet) {

        // Header row
        Row row = sheet.createRow(rowNumber++);
        int colNumber = 0;

        Cell categoryNameHeaderCell = row.createCell(colNumber++);
        categoryNameHeaderCell.setCellValue("Category");

        Cell subCategoryNameHeaderCell = row.createCell(colNumber++);
        subCategoryNameHeaderCell.setCellValue("Subcategory");

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

    private void createSubCategoryPerMonthRow(int rowNumber, SubCategory subCategory, int percentage, Sheet sheet) {

        Row row = sheet.createRow(rowNumber++);
        int colNumber = 0;

        Cell categoryNameCell = row.createCell(colNumber++);
        categoryNameCell.setCellValue(subCategory.getCategory().getName());

        Cell subCategoryNameCell = row.createCell(colNumber++);
        subCategoryNameCell.setCellValue(subCategory.getName());

        Cell categoryPercentageCell = row.createCell(colNumber++);
        categoryPercentageCell.setCellValue(percentage);

        createSubCategoryMonthCell(row, colNumber++, subCategory, Month.JANUARY);
        createSubCategoryMonthCell(row, colNumber++, subCategory, Month.FEBRUARY);
        createSubCategoryMonthCell(row, colNumber++, subCategory, Month.MARCH);
        createSubCategoryMonthCell(row, colNumber++, subCategory, Month.APRIL);
        createSubCategoryMonthCell(row, colNumber++, subCategory, Month.MAY);
        createSubCategoryMonthCell(row, colNumber++, subCategory, Month.JUNE);
        createSubCategoryMonthCell(row, colNumber++, subCategory, Month.JULY);
        createSubCategoryMonthCell(row, colNumber++, subCategory, Month.AUGUST);
        createSubCategoryMonthCell(row, colNumber++, subCategory, Month.SEPTEMBER);
        createSubCategoryMonthCell(row, colNumber++, subCategory, Month.OCTOBER);
        createSubCategoryMonthCell(row, colNumber++, subCategory, Month.NOVEMBER);
        createSubCategoryMonthCell(row, colNumber++, subCategory, Month.DECEMBER);
    }


}
