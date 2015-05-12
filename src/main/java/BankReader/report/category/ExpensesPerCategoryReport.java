package BankReader.report.category;

import BankReader.category.FinancialCategory;
import BankReader.file.GenericBankLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
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

    private static Logger LOG = LoggerFactory.getLogger(ExpensesPerCategoryReport.class);

    private List<FinancialCategory> financialCategories = new ArrayList<FinancialCategory>();

    @Value("${input.directory}")
    private String inputDirectory;

    public ExpensesPerCategoryReport() {
    }

    @PostConstruct
    public void init(){
        setResource(new FileSystemResource(inputDirectory + "/expenses-per-category.csv"));
        DelimitedLineAggregator<GenericBankLine> delLineAgg = new DelimitedLineAggregator<GenericBankLine>();
        delLineAgg.setDelimiter("###");
        BeanWrapperFieldExtractor<GenericBankLine> fieldExtractor = new BeanWrapperFieldExtractor<GenericBankLine>();
        fieldExtractor.setNames(new String[]{"category", "subCategory", "date", "amount", "description"});
        delLineAgg.setFieldExtractor(fieldExtractor);
        setLineAggregator(delLineAgg);
    }

    @Override
    public void write(List<? extends GenericBankLine> list) throws Exception {
        for (GenericBankLine genericBankLine : list) {
            addAmount(genericBankLine);
        }
        //super.write(list);
    }

    @Override
    public void writeFooter(Writer writer) throws IOException {
        String separator = ";";
        for (FinancialCategory financialCategory : financialCategories){
            String message = String.format(financialCategory.getCategoryName() + separator + financialCategory.getSubCategoryName() + separator + financialCategory.getAmount() + "%n");
            writer.write(message);
        }

    }

    public void addAmount(GenericBankLine genericBankLine){

        FinancialCategory financialCategory = getFinancialCategory(genericBankLine.getCategory(), genericBankLine.getSubCategory());
        financialCategory.addAmount(genericBankLine.getAmount());

    }

    private FinancialCategory getFinancialCategory(String category, String subCategory) {
        LOG.debug("Looking for category {} / {}", category, subCategory);
        for (FinancialCategory financialCategory: financialCategories){
            if (financialCategory.categoriesMatch(category, subCategory)){
                LOG.debug("Found financial category {} ", financialCategory);
                return financialCategory;
            } else {
                LOG.debug("Category {} / {} does not match {}", category, subCategory, financialCategory);
            }
        }
        LOG.debug("No financial category found, create new one for {} / {}", category, subCategory);
        FinancialCategory financialCategory = new FinancialCategory("", category, subCategory);
        financialCategories.add(financialCategory);
        return financialCategory;

    }


    public List<FinancialCategory> getFinancialCategories() {
        return financialCategories;
    }

}
