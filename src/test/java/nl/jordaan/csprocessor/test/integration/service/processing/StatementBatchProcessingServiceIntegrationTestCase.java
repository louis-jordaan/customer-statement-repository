package nl.jordaan.csprocessor.test.integration.service.processing;

import nl.jordaan.csprocessor.objectmodel.constant.ExecutionStatus;
import nl.jordaan.csprocessor.objectmodel.execution.ExecutionDetails;
import nl.jordaan.csprocessor.service.statement.processing.IStatementBatchProcessingService;
import nl.jordaan.csprocessor.test.integration.BaseIntegrationTestCase;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.UUID;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StatementBatchProcessingServiceIntegrationTestCase extends BaseIntegrationTestCase {

    @Autowired
    private IStatementBatchProcessingService processingService;

    @Test
    @Order(1)
    public void testProcessStatement() throws URISyntaxException {

        Path outputFile = Path.of("/tmp/" + UUID.randomUUID().toString() + "/output.xml");

        Long executionId =
                processingService.processStatement(Path.of(getClass().getClassLoader().getResource("records.xml").toURI()), outputFile, false);

        Assertions.assertEquals(0, (long) executionId);
        Assertions.assertTrue(Files.exists(outputFile));
    }

    @Test
    @Order(2)
    public void testGetExecutionStatus() {
        LocalDateTime start = LocalDateTime.now();
        Awaitility.await().until(() -> LocalDateTime.now().isAfter(start.plusSeconds(5)));

        ExecutionStatus executionStatus = processingService.getExecutionStatus(0);
        Assertions.assertSame(executionStatus, ExecutionStatus.COMPLETED);
    }

    @Test
    @Order(3)
    public void testGetExecutionDetails() {
        ExecutionDetails executionDetails = processingService.getExecutionDetails(0);
        Assertions.assertNotNull(executionDetails);
        Assertions.assertEquals(0, executionDetails.getExecutionId());
        Assertions.assertSame(executionDetails.getStatus(), ExecutionStatus.COMPLETED);
    }
}
