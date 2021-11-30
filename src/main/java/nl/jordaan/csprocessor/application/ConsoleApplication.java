package nl.jordaan.csprocessor.application;

import nl.jordaan.csprocessor.service.statement.processing.IStatementBatchProcessingService;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
@ConditionalOnNotWebApplication
public class ConsoleApplication implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleApplication.class);

    @Autowired
    private IStatementBatchProcessingService statementProcessingService;

    @Override
    public void run(String... args) {
        LOGGER.info("*********************** Customer Statement Processor ***********************");

        // create command line options
        Options options = new Options();
        options.addOption(Option.builder("cm").longOpt("console-mode").hasArg(false).required(false).desc("Run as console application.").build());
        options.addOption(Option.builder("i").longOpt("input-file").hasArg(true).required(true).desc("Path to the input file.").build());
        options.addOption(Option.builder("o").longOpt("output-file").hasArg(true).required(false).desc("Path for the output file.").build());
        options.addOption(Option.builder("a").longOpt("async").hasArg(false).required(false).desc("Do the processing asynchronously.").build());

        try {
            CommandLineParser parser = new DefaultParser();

            // Validate the arguments that were provided.
            CommandLine cl = parser.parse(options, args, true);

            String inputFilePath = cl.getOptionValue("i");
            String outputFilePath = cl.getOptionValue("o");
            boolean async = Boolean.parseBoolean(cl.getOptionValue("a", "false"));

            Long referenceNumber = StringUtils.isNotBlank(outputFilePath)
                    ? statementProcessingService.processStatement(Path.of(inputFilePath), Path.of(outputFilePath), async)
                    : statementProcessingService.processStatement(Path.of(inputFilePath), async);
            LOGGER.info("Reference number: {}", referenceNumber);

        } catch (ParseException e) {
            LOGGER.error("Failed to start due to invalid/missing arguments.", e);
            new HelpFormatter().printHelp("customer-statement-processor", options);
            System.exit(1);
        }
    }
}
