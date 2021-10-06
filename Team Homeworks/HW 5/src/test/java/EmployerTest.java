import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import model.Employer;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class EmployerTest {
    private final String URI = "jdbc:sqlite:./JBApp.db";

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class EmployerORMLiteDaoTest {

        private ConnectionSource connectionSource;
        private Dao<Employer, Integer> dao;

        // create a new connection to JBApp database, create "employers" table, and create a
        // new dao to be used by test cases
        @BeforeAll
        public void setUpAll() throws SQLException {
            connectionSource = new JdbcConnectionSource(URI);
            TableUtils.createTableIfNotExists(connectionSource, Employer.class);
            dao = DaoManager.createDao(connectionSource, Employer.class);
        }

        // delete all rows in the employers table before each test case
        @BeforeEach
        public void setUpEach() throws SQLException {

            TableUtils.clearTable(connectionSource, Employer.class);
        }

        // inserting a new record where name is null must fail, the reason being
        // there is a non-null constraint on the "name" column in "employers" table!
        @Test
        public void testCreateNameNull() {
            //create a new employer instance
            Employer e = new Employer(null, "Tech", "Summary");
            // try to insert into employers table. This must fail!
            Assertions.assertThrows(SQLException.class, () -> dao.create(e));
        }

        // inserting a new record where sector is an empty string must succeed!
        @Test
        public void testCreateSectorEmpty() throws SQLException {
            // create a new employer instance
            Employer e = new Employer("Company1", "", "Summary");
            // try to insert into employers table. This must succeed!
            dao.create(e);
            List<Employer> ls = dao.queryForEq("name", e.getName());
            assertEquals(ls.size(), 1);
            assertEquals("", ls.get(0).getSector());
        }

        // insert multiple employer records, and assert they were indeed added!
        @Test
        public void testReadMutipleEmployers() throws SQLException {
            // create multiple new employer instance
            List<Employer> lsCreate = new ArrayList<>();
            lsCreate.add(new Employer("Salesforce", "Tech", "An American cloud-based software company focused on customer relationship management services!"));
            lsCreate.add(new Employer("Sonos", "Tech", "Sonos is a developer and manufacturer of audio products best known for its multi-room audio products!"));
            lsCreate.add(new Employer("Fedex", "Transportation/E-Commerce", "An American multinational conglomerate holding company which focuses on transportation, e-commerce and business services!"));
            lsCreate.add(new Employer("First Solar", "Energy", "A leading global provider of comprehensive PV solar solutions!"));
            // try to insert them into employers table. This must succeed!
            dao.create(lsCreate);
            // read all employers
            List<Employer> lsRead = dao.queryForAll();
            // assert all employers in lsCreate were inserted and can be read
            assertEquals(lsCreate, lsRead);
        }

        // insert a new record, then delete it, and assert it was indeed removed!
        @Test
        public void testDeleteAllFieldsMatch() throws SQLException {
            // create a new employer instance
            Employer e = new Employer("Kraft Heinz", "Food", "A global food and beverage company!");
            // try to insert into employers table. This must succeed!
            dao.create(e);
            List<Employer> ls1 = dao.queryForEq("name", e.getName());
            assertEquals(1, ls1.size());
            assertEquals("Kraft Heinz", ls1.get(0).getName());
            dao.delete(e);
            // Assert "Karft Heinz" was removed from employers
            List<Employer> ls2 = dao.queryForEq("name", e.getName());
            assertEquals(0, ls2.size());
        }

        // insert a new employer, update its sector, then assert it was indeed updated!
        @Test
        public void testUpdateSector() throws SQLException {
            // create a new employer instance
            Employer e = new Employer("Kraft Heinz", "Food", "A global food and beverage company!");
            e.setId(22);
            // try to insert into employers table. This must succeed!
            dao.create(e);
            e.setSector("Food/Beverage");
            dao.createOrUpdate(e);
            // assert the sector is updated successfully!
            assertEquals("Food/Beverage", dao.queryForEq("name", "Kraft Heinz").get(0).getSector());
        }
    }


    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class EmployerAPITest {

        final String BASE_URL = "http://localhost:7000";
        private OkHttpClient client;

        @BeforeAll
        public void setUpAll() {
            client = new OkHttpClient();
        }

        @Test
        public void testHTTPGetEmployersEndpoint() throws IOException {
            // TODO 2: Write code to send a http get request using OkHttp to thr
            //  "employers" endpoint and assert that the received status code is OK (200)!
            //  Note: In order for this to work, you need to make sure your local sparkjava
            //  server is running, before you run the JUnit test!

            String endpoint = BASE_URL + "/employers";

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(endpoint)
                    .build();
            Response response = client.newCall(request).execute();
            assertEquals(200, response.code());

        }
    }

}
