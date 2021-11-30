package nl.jordaan.csprocessor.application;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"nl.jordaan.csprocessor"})
@EnableScheduling
@EnableAsync
public class CustomerStatementProcessorApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerStatementProcessorApplication.class);

	public static void main(String[] args) {

		// create command line options
		Options options = new Options();
		options.addOption(Option.builder("cm").longOpt("console-mode").hasArg(false).required(false).desc("Run as console application.").build());

		try {
			CommandLineParser parser = new DefaultParser();

			// Validate the arguments that were provided.
			CommandLine cl = parser.parse(options, args, true);

			SpringApplicationBuilder app = new SpringApplicationBuilder(CustomerStatementProcessorApplication.class);
			if (cl.hasOption("cm")) {
				app.web(WebApplicationType.NONE);
			} else {
				app.web(WebApplicationType.SERVLET);
			}
			app.run(args);

		} catch (ParseException e) {
			LOGGER.error("Failed to start due to invalid/missing arguments.", e);
			new HelpFormatter().printHelp("customer-statement-processor", options);
			System.exit(1);
		}


	}
}
