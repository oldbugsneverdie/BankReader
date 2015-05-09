package BankReader
        ;

import BankReader.file.ABN.GenericBankLine;
import BankReader.file.ING.INGBankLine;
import BankReader.file.ING.INGProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
@EnableBatchProcessing
public class INGBatchConfiguration {

    @Bean
    public ItemReader<INGBankLine> reader() {

        FlatFileItemReader<INGBankLine> reader = new FlatFileItemReader<INGBankLine>();
        //reader.setResource(new ClassPathResource("uitgaven-ing-abn-2015.csv"));
        reader.setResource(new FileSystemResource("/home/jan/Documenten/projecten/BankReader/src/test/resources/ing.csv"));
        reader.setLineMapper(new DefaultLineMapper<INGBankLine>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames(new String[] {"Datum", "Omschrijving", "Rekening", "Tegenrekening", "Code", "Af Bij", "Bedrag", "Mutatiesoort", "Mededelingen" });
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<INGBankLine>() {{
                setTargetType(INGBankLine.class);
            }});
        }});
        return reader;
    }
   

    @Bean
    public ItemWriter<GenericBankLine> writer() {
    	FlatFileItemWriter<GenericBankLine> writer = new FlatFileItemWriter<GenericBankLine>();
    	//writer.setResource(new ClassPathResource("student-marksheet.csv"));
        writer.setResource(new FileSystemResource("/home/jan/Documenten/projecten/BankReader/src/test/resources/ing-output.csv"));
    	DelimitedLineAggregator<GenericBankLine> delLineAgg = new DelimitedLineAggregator<GenericBankLine>();
    	delLineAgg.setDelimiter(",");
    	BeanWrapperFieldExtractor<GenericBankLine> fieldExtractor = new BeanWrapperFieldExtractor<GenericBankLine>();
    	fieldExtractor.setNames(new String[] {"category", "subCategory", "date", "amount", "description"});
    	delLineAgg.setFieldExtractor(fieldExtractor);
    	writer.setLineAggregator(delLineAgg);
        return writer;
    }
    
    @Bean
    public ItemProcessor<INGBankLine, GenericBankLine> processor() {
        return new INGProcessor();
    }

    @Bean
    public Job createMarkSheet(JobBuilderFactory jobs, Step step) {
        return jobs.get("createGenericFromING")
                .flow(step)
                .end()
                .build();
    }

    @Bean
    public Step step(StepBuilderFactory stepBuilderFactory, ItemReader<INGBankLine> reader,
            ItemWriter<GenericBankLine> writer, ItemProcessor<INGBankLine, GenericBankLine> processor) {
        return stepBuilderFactory.get("step")
                .<INGBankLine, GenericBankLine> chunk(5)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
 
}
