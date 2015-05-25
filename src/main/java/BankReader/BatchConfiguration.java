package BankReader;

import BankReader.account.Account;
import BankReader.account.AccountLoader;
import BankReader.file.ABN.ABNBankLine;
import BankReader.file.ABN.ABNProcessor;
import BankReader.file.GenericBankLine;
import BankReader.file.ING.INGBankLine;
import BankReader.file.ING.INGProcessor;
import BankReader.report.AccountReport;
import BankReader.report.ExpensesPerCategoryReport;
import BankReader.report.UnmatchedBankLinesReport;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.ResourcePatternResolver;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {


    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

    @Autowired
    private ResourcePatternResolver resourcePatternResolver;

    @Value("${input.directory}")
    protected String inputDirectory;

    @Value("${output.directory}")
    protected String outputDirectory;

    @Value("${settings.directory}")
    protected String settingsDirectory;

    @Autowired
    AccountLoader accountLoader;

    // Job definition
    @Bean
    public Job getJob() {
        return jobs.get("bankProcessingJob")
                .start(abnStep())
                .next(ingStep())
                .next(unmatchedBankLinesStep())
                .next(expensesPerCategoryStep())
                .next(accountReportStep())
                .build();
    }

    //        use a multi resource item reader to readmultiple files and delegate it to the e.g. abnReader
//        MultiResourceItemReader<ABNBankLine> multiResourceItemReader = new MultiResourceItemReader<ABNBankLine>();
//        multiResourceItemReader.setDelegate(abnReader())


    // Bank ABN
    @Bean
    public ItemReader<ABNBankLine> abnReader() {

        FlatFileItemReader<ABNBankLine> reader = new FlatFileItemReader<ABNBankLine>();
        reader.setResource(new FileSystemResource(inputDirectory + "/abn.csv"));
        reader.setLinesToSkip(1);
        reader.setLineMapper(new DefaultLineMapper<ABNBankLine>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames(new String[]{"Rekeningnummer", "Muntsoort", "Transactiedatum", "Rentedatum", "Beginsaldo", "Eindsaldo", "Transactiebedrag", "Omschrijving"});
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<ABNBankLine>() {{
                setTargetType(ABNBankLine.class);
            }});
        }});
        return reader;
    }
   

    @Bean
    public ItemWriter<GenericBankLine> abnWriter() {
    	FlatFileItemWriter<GenericBankLine> writer = new FlatFileItemWriter<GenericBankLine>();
        writer.setResource(new FileSystemResource(inputDirectory + "/temp/abn-output.csv"));
    	DelimitedLineAggregator<GenericBankLine> delLineAgg = new DelimitedLineAggregator<GenericBankLine>();
    	delLineAgg.setDelimiter("###");
    	BeanWrapperFieldExtractor<GenericBankLine> fieldExtractor = new BeanWrapperFieldExtractor<GenericBankLine>();
    	fieldExtractor.setNames(new String[] {"category", "subCategory", "date", "amount", "description"});
    	delLineAgg.setFieldExtractor(fieldExtractor);
    	writer.setLineAggregator(delLineAgg);
        return writer;
    }
    
    @Bean
    public ItemProcessor<ABNBankLine, GenericBankLine> abnProcessor() {
        Account account = accountLoader.getAccount("abn");
        return new ABNProcessor(account);
    }

    @Bean
    public Step abnStep() {
        return steps.get("abnProcessingStep")
                .<ABNBankLine, GenericBankLine> chunk(5)
                .reader(abnReader())
                .processor(abnProcessor())
                .writer(abnWriter())
                .build();
    }


    // Bank ING

    @Bean
    public ItemReader<INGBankLine> ingReader() {

        FlatFileItemReader<INGBankLine> reader = new FlatFileItemReader<INGBankLine>();
        reader.setLinesToSkip(1);
        reader.setResource(new FileSystemResource(inputDirectory + "/ing.csv"));
        reader.setLineMapper(new DefaultLineMapper<INGBankLine>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames(new String[]{"Datum", "Omschrijving", "Rekening", "Tegenrekening", "Code", "Af Bij", "Bedrag", "Mutatiesoort", "Mededelingen"});
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<INGBankLine>() {{
                setTargetType(INGBankLine.class);
            }});
        }});
        return reader;
    }


    @Bean
    public ItemWriter<GenericBankLine> ingWriter() {
        FlatFileItemWriter<GenericBankLine> writer = new FlatFileItemWriter<GenericBankLine>();
        writer.setResource(new FileSystemResource(inputDirectory + "/temp/ing-output.csv"));
        DelimitedLineAggregator<GenericBankLine> delLineAgg = new DelimitedLineAggregator<GenericBankLine>();
        delLineAgg.setDelimiter("###");
        BeanWrapperFieldExtractor<GenericBankLine> fieldExtractor = new BeanWrapperFieldExtractor<GenericBankLine>();
        fieldExtractor.setNames(new String[] {"category", "subCategory", "date", "amount", "description"});
        delLineAgg.setFieldExtractor(fieldExtractor);
        writer.setLineAggregator(delLineAgg);
        return writer;
    }

    @Bean
    public ItemProcessor<INGBankLine, GenericBankLine> ingProcessor() {
        Account account = accountLoader.getAccount("ing");
        return new INGProcessor(account);
    }

    @Bean
    public Step ingStep() {
        return steps.get("ingProcessingStep")
                .<INGBankLine, GenericBankLine> chunk(5)
                .reader(ingReader())
                .processor(ingProcessor())
                .writer(ingWriter())
                .build();
    }


    // Unmatched bank lines report

    @Bean
    public Tasklet unmatchedBankLinesWriter() {
        UnmatchedBankLinesReport unmatchedBankLinesReport = new UnmatchedBankLinesReport();
        return unmatchedBankLinesReport;

    }

    @Bean
    public TaskletStep unmatchedBankLinesStep() {
        return steps.get("unmatched bank lines")
                .tasklet(unmatchedBankLinesWriter())
                .build();
    }


    // Expenses per category report

    @Bean
    public Tasklet expensesPerCategoryWriter() {
        ExpensesPerCategoryReport expensesPerCategoryReport = new ExpensesPerCategoryReport();
        return expensesPerCategoryReport;

    }

    @Bean
    public TaskletStep expensesPerCategoryStep() {
        return steps.get("expensesPerCategory")
                .tasklet(expensesPerCategoryWriter())
                .build();
    }

    // Expenses per category report

    @Bean
    public Tasklet accountReportWriter() {
        AccountReport accountReport= new AccountReport();
        return accountReport;

    }

    @Bean
    public TaskletStep accountReportStep() {
        return steps.get("accountReport")
                .tasklet(accountReportWriter())
                .build();
    }


}
