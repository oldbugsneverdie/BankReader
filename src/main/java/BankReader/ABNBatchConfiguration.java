package BankReader
        ;

import BankReader.file.ABN.ABNBankLine;
import BankReader.file.ABN.ABNProcessor;
import BankReader.file.ABN.GenericBankLine;
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
public class ABNBatchConfiguration {

    @Bean
    public ItemReader<ABNBankLine> reader() {

        FlatFileItemReader<ABNBankLine> reader = new FlatFileItemReader<ABNBankLine>();
        //reader.setResource(new ClassPathResource("uitgaven-ing-abn-2015.csv"));
        reader.setResource(new FileSystemResource("/home/jan/Documenten/projecten/BankReader/src/test/resources/abn.csv"));
        reader.setLineMapper(new DefaultLineMapper<ABNBankLine>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames(new String[] {"Rekeningnummer", "Muntsoort", "Transactiedatum", "Rentedatum", "Beginsaldo", "Eindsaldo", "Transactiebedrag", "Omschrijving" });
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<ABNBankLine>() {{
                setTargetType(ABNBankLine.class);
            }});
        }});
        return reader;
    }
   

    @Bean
    public ItemWriter<GenericBankLine> writer() {
    	FlatFileItemWriter<GenericBankLine> writer = new FlatFileItemWriter<GenericBankLine>();
    	//writer.setResource(new ClassPathResource("student-marksheet.csv"));
        writer.setResource(new FileSystemResource("/home/jan/Documenten/projecten/BankReader/src/test/resources/abn-output.csv"));
    	DelimitedLineAggregator<GenericBankLine> delLineAgg = new DelimitedLineAggregator<GenericBankLine>();
    	delLineAgg.setDelimiter(",");
    	BeanWrapperFieldExtractor<GenericBankLine> fieldExtractor = new BeanWrapperFieldExtractor<GenericBankLine>();
    	fieldExtractor.setNames(new String[] {"category", "subCategory", "date", "amount", "description"});
    	delLineAgg.setFieldExtractor(fieldExtractor);
    	writer.setLineAggregator(delLineAgg);
        return writer;
    }
    
    @Bean
    public ItemProcessor<ABNBankLine, GenericBankLine> processor() {
        return new ABNProcessor();
    }

    @Bean
    public Job createMarkSheet(JobBuilderFactory jobs, Step step) {
        return jobs.get("createGenericFromABN")
                .flow(step)
                .end()
                .build();
    }

    @Bean
    public Step step(StepBuilderFactory stepBuilderFactory, ItemReader<ABNBankLine> reader,
            ItemWriter<GenericBankLine> writer, ItemProcessor<ABNBankLine, GenericBankLine> processor) {
        return stepBuilderFactory.get("step")
                .<ABNBankLine, GenericBankLine> chunk(5)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
 
}
