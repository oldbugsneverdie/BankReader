package BankReader.report.category;

import BankReader.category.Category;
import BankReader.category.FinancialCategories;
import BankReader.category.FinancialCategory;
import BankReader.category.SubCategory;
import BankReader.file.GenericBankLine;
import BankReader.util.Amount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jadu on 11-5-2015.
 */
@Component
@Configuration
public class ExpensesPerCategoryReport extends FlatFileItemWriter<GenericBankLine> implements FlatFileFooterCallback {

    public static final String NEW_LINE = "%n";
    private static Logger LOG = LoggerFactory.getLogger(ExpensesPerCategoryReport.class);

    @Autowired
    private FinancialCategories financialCategories;

    @Value("${output.directory}")
    private String outputDirectory;

    public ExpensesPerCategoryReport() {
    }

    @PostConstruct
    public void init(){
        setResource(new FileSystemResource(outputDirectory + "/expenses-per-category.csv"));
        DelimitedLineAggregator<GenericBankLine> delLineAgg = new DelimitedLineAggregator<GenericBankLine>();
        delLineAgg.setDelimiter("###");
        BeanWrapperFieldExtractor<GenericBankLine> fieldExtractor = new BeanWrapperFieldExtractor<GenericBankLine>();
        fieldExtractor.setNames(new String[]{"category", "subCategory", "date", "amount", "description"});
        delLineAgg.setFieldExtractor(fieldExtractor);
        setLineAggregator(delLineAgg);
    }

    @Override
    public void write(List<? extends GenericBankLine> list) throws Exception {
        //No processing per GenericBankLine needed as we just want to know the amounts per category and sub category
    }

    @Override
    public void writeFooter(Writer writer) throws IOException {
        String separator = ";";

        writer.write("Amounts per category"+ NEW_LINE);
        writer.write(""+ NEW_LINE);
        for (Category category : financialCategories.getAllCategories()){
            String message = String.format(category.getName() + separator + category.getAmount() + NEW_LINE);
            writer.write(message);
        }

        writer.write(""+ NEW_LINE);
        writer.write(""+ NEW_LINE);

        writer.write("Amounts per subcategory"+ NEW_LINE);
        writer.write(""+ NEW_LINE);
        for (SubCategory subCategory : financialCategories.getAllSubCategories()){
            String message = String.format(subCategory.getCategory().getName() + separator + subCategory.getName() + separator + subCategory.getAmount() + "%n");
            writer.write(message);
        }


    }

}
