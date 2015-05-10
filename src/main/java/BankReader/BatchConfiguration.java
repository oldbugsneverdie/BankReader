package BankReader
        ;

import BankReader.file.ABN.ABNBankLine;
import BankReader.file.ABN.ABNProcessor;
import BankReader.file.GenericBankLine;
import BankReader.file.GenericProcessor;
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
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.mapping.PassThroughFieldSetMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import sun.net.www.content.text.Generic;

import java.io.File;
import java.io.IOException;

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


    // Job definition
    @Bean
    public Job getJob() {
        return jobs.get("bankProcessingJob")
                .start(abnStep())
                .next(ingStep())
                .next(resultFilesStep())
                .build();
    }


    // Bank ABN
    @Bean
    public ItemReader<ABNBankLine> abnReader() {

        FlatFileItemReader<ABNBankLine> reader = new FlatFileItemReader<ABNBankLine>();
        reader.setResource(new FileSystemResource(inputDirectory + "/abn.csv"));
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
        writer.setResource(new FileSystemResource(inputDirectory + "/abn-output.csv"));
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
        return new ABNProcessor();
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
        writer.setResource(new FileSystemResource(inputDirectory + "/ing-output.csv"));
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
        return new INGProcessor();
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


    // Join result files
    @Bean
    public ItemReader<GenericBankLine> genericReader() {

        FlatFileItemReader<GenericBankLine> reader = new FlatFileItemReader<GenericBankLine>();
        reader.setLineMapper(new DefaultLineMapper<GenericBankLine>() {{
            setLineTokenizer(new DelimitedLineTokenizer("###") {{
                setNames(new String[]{"category", "subCategory", "date", "amount", "description"});
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<GenericBankLine>() {{
                setTargetType(GenericBankLine.class);
            }});
        }});

        MultiResourceItemReader multiResourceItemReader = new MultiResourceItemReader();
        Resource[] resources;
        try {
            String pattern = "file:" + inputDirectory + "/*output.csv";
            resources = resourcePatternResolver.getResources(pattern);
        } catch (IOException e) {
            throw new RuntimeException("I/O problems when resolving the input file pattern.",e);
        }
        multiResourceItemReader.setResources(resources);
        multiResourceItemReader.setDelegate(reader);

        return multiResourceItemReader;
    }

    @Bean
    public ItemProcessor<GenericBankLine, GenericBankLine> genericProcessor() {
        return new GenericProcessor();
    }

    @Bean
    public ItemWriter<GenericBankLine> genericWriter() {
        FlatFileItemWriter<GenericBankLine> writer = new FlatFileItemWriter<GenericBankLine>();
        writer.setResource(new FileSystemResource(inputDirectory + "/total.csv"));
        DelimitedLineAggregator<GenericBankLine> delLineAgg = new DelimitedLineAggregator<GenericBankLine>();
        delLineAgg.setDelimiter("###");
        BeanWrapperFieldExtractor<GenericBankLine> fieldExtractor = new BeanWrapperFieldExtractor<GenericBankLine>();
        fieldExtractor.setNames(new String[] {"category", "subCategory", "date", "amount", "description"});
        delLineAgg.setFieldExtractor(fieldExtractor);
        writer.setLineAggregator(delLineAgg);
        return writer;
    }

    @Bean
    public Step resultFilesStep() {
        return steps.get("joinResultFilesStep")
                .<GenericBankLine, GenericBankLine> chunk(5)
                .reader(genericReader())
                .processor(genericProcessor())
                .writer(genericWriter())
                .build();
    }

    // Process total.csv to get useful reports


}
