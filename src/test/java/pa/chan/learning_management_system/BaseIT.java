package pa.chan.learning_management_system;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.postgresql.PostgreSQLContainer;

import javax.sql.DataSource;
import java.io.IOException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseIT {
    protected static final String BASE_URL = "/api/v1/";

    @ServiceConnection
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:15-alpine");

    static {
        postgreSQLContainer.start();
    }

    @LocalServerPort
    protected int port;

    protected RestTemplate restTemplate;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void initClient() {
        restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(@NonNull ClientHttpResponse response) throws IOException {
                return false;
            }
        });

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(
                new ClassPathResource("/cleanDataBase.sql"),
                new ClassPathResource("/initData.sql")
        );

        populator.execute(dataSource);
    }

    @AfterEach
    void tearDown() {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(
                new ClassPathResource("/cleanDataBase.sql")
        );

        populator.execute(dataSource);
    }

    protected String getUrl(String path) {
        return "http://localhost:" + port + BASE_URL + path;
    }
}
