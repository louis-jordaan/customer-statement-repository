package nl.jordaan.csprocessor.service.config.batch;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import nl.jordaan.csprocessor.objectmodel.statement.StatementRecord;
import nl.jordaan.csprocessor.objectmodel.validation.result.StatementRecordValidationSummary;
import nl.jordaan.csprocessor.objectmodel.validation.validator.StatementRecordDistinctTxReferenceValidator;
import nl.jordaan.csprocessor.service.statement.processing.StatementRecordProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import java.util.Optional;

@Configuration
public class ValidateStatementRecordsJobConfig {

    public static final String VALIDATE_TXS_CSV_FILE_JOB_NAME = "job.validation.customer.statement.records.csv";
    public static final String VALIDATE_TXS_XML_FILE_JOB_NAME = "job.validation.customer.statement.records.xml";

    @Bean
    public Job validateCsvFileJob(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, ItemReader<StatementRecord> csvInputReader, ItemWriter<StatementRecordValidationSummary> csvOutputWriter) {
        return jobBuilderFactory.get(VALIDATE_TXS_CSV_FILE_JOB_NAME)
                .incrementer(new RunIdIncrementer())
                .flow(validateCsvFileStep(stepBuilderFactory, csvInputReader, csvOutputWriter))
                .end()
                .build();
    }

    @Bean
    public Step validateCsvFileStep(StepBuilderFactory stepBuilderFactory, ItemReader<StatementRecord> csvInputReader, ItemWriter<StatementRecordValidationSummary> csvOutputWriter) {
        return stepBuilderFactory.get("validateCsvFileStep")
                .<StatementRecord, StatementRecordValidationSummary>chunk(10)
                .reader(csvInputReader)
                .processor(processor())
                .writer(csvOutputWriter)
                .build();
    }

    @Bean
    public Job validateXmlFileJob(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, ItemReader<StatementRecord> xmlInputReader, ItemWriter<StatementRecordValidationSummary> xmlOutputWriter) {
        return jobBuilderFactory.get(VALIDATE_TXS_XML_FILE_JOB_NAME)
                .incrementer(new RunIdIncrementer())
                .flow(validateXmlFileStep(stepBuilderFactory, xmlInputReader, xmlOutputWriter))
                .end()
                .build();
    }

    @Bean
    public Step validateXmlFileStep(StepBuilderFactory stepBuilderFactory, ItemReader<StatementRecord> xmlInputReader, ItemWriter<StatementRecordValidationSummary> xmlOutputWriter) {
        return stepBuilderFactory.get("validateXmlFileStep")
                .<StatementRecord, StatementRecordValidationSummary>chunk(1)
                .reader(xmlInputReader)
                .processor(processor())
                .writer(xmlOutputWriter)
                .build();
    }

    @Bean
    public StatementRecordProcessor processor() {
        return new StatementRecordProcessor(Lists.newArrayList(new StatementRecordDistinctTxReferenceValidator()));
    }

    @Bean
    @StepScope
    public FlatFileItemReader<StatementRecord> csvInputReader(@Value("#{jobParameters[T(nl.jordaan.csprocessor.objectmodel.constant.BatchJobParameter).INPUT_FILE_NAME.getKey()]}") String inputFileName) {

        return new FlatFileItemReaderBuilder<StatementRecord>()
                .name("csvInputReader")
                .resource(new FileSystemResource(inputFileName))
                .linesToSkip(1)
                .delimited()
                .names(new String[]{"transactionReference", "accountNumber", "description", "startBalance", "mutation", "endBalance"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(StatementRecord.class);
                }})
                .build();
    }

    @Bean
    @StepScope
    public StaxEventItemReader<StatementRecord> xmlInputReader(@Value("#{jobParameters[T(nl.jordaan.csprocessor.objectmodel.constant.BatchJobParameter).INPUT_FILE_NAME.getKey()]}") String inputFileName) {

        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(StatementRecord.class);

        return new StaxEventItemReaderBuilder<StatementRecord>()
                .name("xmlInputReader")
                .resource(new FileSystemResource(inputFileName))
                .addFragmentRootElements("record")
                .unmarshaller(marshaller)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<StatementRecordValidationSummary> csvOutputWriter(@Value("#{jobParameters[T(nl.jordaan.csprocessor.objectmodel.constant.BatchJobParameter).OUTPUT_FILE_NAME.getKey()]}") String outputFileName) {
        return new FlatFileItemWriterBuilder<StatementRecordValidationSummary>()
                .name("csvOutputWriter")
                .resource(new FileSystemResource(outputFileName))
                .headerCallback(writer -> writer.write("Reference, Account Number, Valid, Validation Failures"))
                .lineAggregator(item -> Joiner.on(",")
                        .useForNull("")
                        .join(item.getTransactionReference(),
                                item.getAccountNumber(),
                                item.isValid(),
                                Optional.ofNullable(item.getValidationErrors()).map(ve -> Joiner.on("; ").skipNulls().join(ve)).orElse(null)
                        ))
                .build();
    }

    @Bean
    @StepScope
    public StaxEventItemWriter<StatementRecordValidationSummary> xmlOutputWriter(@Value("#{jobParameters[T(nl.jordaan.csprocessor.objectmodel.constant.BatchJobParameter).OUTPUT_FILE_NAME.getKey()]}") String outputFileName) {

        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(StatementRecordValidationSummary.class);

        return new StaxEventItemWriterBuilder<StatementRecordValidationSummary>()
                .resource(new FileSystemResource(outputFileName))
                .name("xmlOutputWriter")
                .rootTagName("results")
                .marshaller(marshaller)
                .build();
    }


}
