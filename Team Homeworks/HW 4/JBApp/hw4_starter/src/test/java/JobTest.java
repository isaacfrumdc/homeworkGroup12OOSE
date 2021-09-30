import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import model.Employer;
import model.Job;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


public class JobTest {

    private final String URI = "jdbc:sqlite:./JBApp.db";

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class JobORMLiteDaoTest {
        // TODO 5: Similar to what was done in EmployerTest.EmployerORMLiteDaoTest class, write JUnit tests
        //  to test basic CRUD operations on the jobs table! Think of interesting test cases and
        //  write at least four different test cases for each of the C(reate)/U(pdate)/D(elete)
        //  operations!
        //  Note: You need to (write code to) create the "jobs" table before writing your test cases!
        private ConnectionSource connectionSource;
        private Dao<Job, Integer> dao;
        private Dao<Employer, Integer> daoE;

        // create a new connection to JBApp database, create "jobs" table, and create a
        // new dao to be used by test cases
        @BeforeAll
        public void setUpAll() throws SQLException {
            connectionSource = new JdbcConnectionSource(URI);
            TableUtils.createTableIfNotExists(connectionSource, Job.class);
            dao = DaoManager.createDao(connectionSource, Job.class);
        }

        // delete all rows in the jobs table before each test case
        @BeforeEach
        public void setUpEach() throws SQLException {
            TableUtils.clearTable(connectionSource, Job.class);
        }

        //CREATE TESTS:

        //inserting job succeeds
        @Test
        public void testCreateJob() throws SQLException {
            Employer e1 = new Employer("Employee1", "Tech", "Summary1");
            e1.setId(1);
            Job j1 = new Job("teacher", new Date(2021, 1, 1), new Date(2021, 2, 1), "education", "Baltimore", true, true, "college degree", 100000, e1);
            dao.create(j1);
            List<Job> ls = dao.queryForEq("id", j1.getId());
            assertEquals(ls.size(), 1);
        }

        //inserting job with empty title fails
        @Test
        public void testCreateTitleEmpty() throws SQLException {
            Employer e1 = new Employer("Employee1", "Tech", "Summary1");
            e1.setId(1);
            Job j1 = new Job(null, new Date(2021, 1, 1), new Date(2021, 2, 1), "education", "Baltimore", true, true, "college degree", 100000, e1);
            // Try to insert job. This must fail!
            Assertions.assertThrows(SQLException.class, () -> dao.create(j1));
        }

        //inserting job with empty datePosted fails
        @Test
        public void testCreateDatePostedEmpty() throws SQLException {
            Employer e1 = new Employer("Employee1", "Tech", "Summary1");
            e1.setId(1);
            Job j1 = new Job("teacher", null, new Date(2021, 2, 1), "education", "Baltimore", true, true, "college degree", 100000, e1);
            // Try to insert job. This must fail!
            Assertions.assertThrows(SQLException.class, () -> dao.create(j1));
        }

        //inserting job with empty employer fails
        @Test
        public void testCreateEmployerEmpty() throws SQLException {
            Job j1 = new Job("teacher", new Date(2021, 1, 1), new Date(2021, 2, 1), "education", "Baltimore", true, true, "college degree", 100000, null);
            // Try to insert job. This must fail!
            Assertions.assertThrows(SQLException.class, () -> dao.create(j1));
        }

        //inserting two jobs with same exact domain passes
        @Test
        public void testCreateSameDomain() throws SQLException {
            Employer e1 = new Employer("Employee1", "Tech", "Summary1");
            e1.setId(1);
            Job j1 = new Job("teacher", new Date(2021, 1, 1), new Date(2021, 2, 1), "education", "Baltimore", true, true, "college degree", 100000, e1);
            Employer e2 = new Employer("Employee1", "Tech", "Summary1");
            e2.setId(2);
            Job j2 = new Job("professor", new Date(2021, 1, 1), new Date(2021, 2, 1), "education", "Baltimore", true, true, "college degree", 100000, e1);
            dao.create(j1);
            dao.create(j2);
        }

        //set id is not necessarily id in table
        @Test
        public void testSetIdNotInsertedId() throws SQLException {
            Employer e1 = new Employer("Offset", "Offset", "Have this employee get a certain index first");
            e1.setId(1);
            Job j1 = new Job("teacher", new Date(2021, 1, 1), new Date(2021, 2, 1), "education", "Baltimore", true, true, "college degree", 100000, e1);
            dao.create(j1);
            Integer firstId = dao.queryForEq("title", "teacher").get(0).getId();

            Employer e2 = new Employer("Test Employee", "Food", "A global food and beverage company!");
            e2.setId(2);
            Job j2 = new Job("professor", new Date(2021, 1, 1), new Date(2021, 2, 1), "education", "Baltimore", true, true, "college degree", 100000, e2);
            j2.setId(firstId); //Attempt to set it to an id already taken
            dao.create(j2);
            // assert the actual id is not what we tried to set it as
            assertNotEquals(dao.queryForEq("title", "professor").get(0).getId(), firstId);
        }

        //UPDATE TESTS:


        //updating a job's id to an id value that does NOT exist in the table succeeds
        @Test
        public void testUpdateIdNotInserted() throws SQLException {
            Employer e1 = new Employer("Test Employee", "Food", "A global food and beverage company!");
            e1.setId(1);
            Job j1 = new Job("teacher", new Date(2021, 1, 1), new Date(2021, 2, 1), "education", "Baltimore", true, true, "college degree", 100000, e1);
            dao.create(j1);
            Integer id = dao.queryForEq("title", "teacher").get(0).getId();

            dao.updateId(j1, id + 1);
            assertEquals(dao.queryForEq("title", "teacher").get(0).getId(), id + 1);
        }

        //updating a job's id to an id value that exists in the table fails
        @Test
        public void testSetIdAlreadyInserted() throws SQLException {
            Employer first = new Employer("First", "First", "First");
            first.setId(1);
            Job j1 = new Job("teacher", new Date(2021, 1, 1), new Date(2021, 2, 1), "education", "Baltimore", true, true, "college degree", 100000, first);
            dao.create(j1);
            Integer firstId = dao.queryForEq("title", "teacher").get(0).getId();

            Employer second = new Employer("Second", "Second", "Second");
            second.setId(2);
            Job j2 = new Job("teacher", new Date(2021, 1, 1), new Date(2021, 2, 1), "education", "Baltimore", true, true, "college degree", 100000, second);
            dao.create(j2);
            Assertions.assertThrows(SQLException.class, () -> dao.updateId(j2, firstId)); //This should fail
        }

        //updating a job's title that is already exists in the table succeeds
        @Test
        public void testUpdateName() throws SQLException {
            Employer first = new Employer("First", "First", "First");
            first.setId(1);
            Job j1 = new Job("teacher", new Date(2021, 1, 1), new Date(2021, 2, 1), "education", "Baltimore", true, true, "college degree", 100000, first);
            dao.create(j1);
            j1.setTitle("professor");
            dao.update(j1);
            assertEquals(dao.queryForAll().get(0).getTitle(), "professor");
        }

        //updating a job's employer that is already exists in the table succeeds
        @Test
        public void testUpdateEmployer() throws SQLException {
            Employer first = new Employer("First", "First", "First");
            first.setId(1);
            Job j1 = new Job("teacher", new Date(2021, 1, 1), new Date(2021, 2, 1), "education", "Baltimore", true, true, "college degree", 100000, first);
            dao.create(j1);
            // create new employer and replace in job
            Employer second = new Employer("Second", "Second", "Second");
            j1.setEmployer(second);
            dao.update(j1);
            assertEquals(dao.queryForAll().get(0).getEmployer(), second);
        }

        //updating a job's fullTime status in the table succeeds
        @Test
        public void testUpdateFullTime() throws SQLException {
            Employer first = new Employer("First", "First", "First");
            first.setId(1);
            Job j1 = new Job("teacher", new Date(2021, 1, 1), new Date(2021, 2, 1), "education", "Baltimore", true, true, "college degree", 100000, first);
            dao.create(j1);
            j1.setFullTime(false);
            dao.update(j1);
            assertEquals(dao.queryForAll().get(0).isFullTime(), false);
        }

        //DELETE TESTS:


        //Deleting a job record (using the "delete" function of ORMLite) based on an id that does not exist does not delete any rows even if a row with the same exact title exists
        @Test
        public void testDeleteNonexistentId() throws SQLException {
            Employer e = new Employer("Kraft Heinz", "Food", "A global food and beverage company!");
            e.setId(1);
            Job j1 = new Job("teacher", new Date(2021, 1, 1), new Date(2021, 2, 1), "education", "Baltimore", true, true, "college degree", 100000, e);
            dao.create(j1);
            Integer id = dao.queryForEq("title", "teacher").get(0).getId();
            List<Job> ls1 = dao.queryForAll();
            assertEquals(1, ls1.size());
            // create another job and assign them id
            Employer e2 = new Employer("Kraft Heinz", "Food", "A global food and beverage company!");
            e2.setId(2);
            Job j2 = new Job("teacher", new Date(2021, 1, 1), new Date(2021, 2, 1), "education", "Baltimore", true, true, "college degree", 100000, e2);
            Integer id2 = j2.getId();
            //ensure j1 and j2 have same title
            assertEquals(j1.getTitle(), j2.getTitle());
            // ensure id and id2 are not equal
            assertNotEquals(id, id2);
            // Try to delete id2 from table
            dao.deleteById(id2);
            // Check no rows were deleted
            List<Job> ls2 = dao.queryForAll();
            assertEquals(1, ls2.size());
        }

        // Deleting an job record (using the "delete" function of ORMLite) based on an id that exists succeeds even if the title are different
        @Test
        public void testDeleteExistentIdWithDifferentName() throws SQLException {
            Employer e = new Employer("Kraft Heinz", "Food", "A global food and beverage company!");
            e.setId(1);
            Job j1 = new Job("teacher", new Date(2021, 1, 1), new Date(2021, 2, 1), "education", "Baltimore", true, true, "college degree", 100000, e);
            dao.create(j1);
            Integer id = dao.queryForEq("title", "teacher").get(0).getId();
            List<Job> ls1 = dao.queryForAll();
            assertEquals(1, ls1.size());
            // Create another job with same id
            Employer e2 = new Employer("Kraft Heinz", "Food", "A global food and beverage company!");
            e2.setId(2);
            Job j2 = new Job("professor", new Date(2021, 1, 1), new Date(2021, 2, 1), "education", "Baltimore", true, true, "college degree", 100000, e);
            j2.setId(id);
            assertEquals(j1.getId(), j2.getId());
            // Try to delete employee from table. This must succeed!
            dao.delete(j2);
            List<Job> ls2 = dao.queryForAll();
            assertEquals(0, ls2.size());
        }

        // Deleting a collection of jobs at once (using the "delete" function of ORMLite) removes all those jobs that exist from the table
        @Test
        public void testDeleteExistingMultipleEmployees() throws SQLException {
            Employer e = new Employer("Kraft Heinz", "Food", "A global food and beverage company!");
            e.setId(1);
            // create multiple new job instance
            List<Job> lsCreate = new ArrayList<>();
            lsCreate.add(new Job("teacher", new Date(2021, 1, 1), new Date(2021, 2, 1), "education", "Baltimore", true, true, "college degree", 100000, e));
            lsCreate.add(new Job("professor", new Date(2021, 1, 1), new Date(2021, 2, 1), "education", "Baltimore", true, true, "college degree", 100000, e));
            lsCreate.add(new Job("intern", new Date(2021, 1, 1), new Date(2021, 2, 1), "education", "Baltimore", true, true, "college degree", 100000, e));
            lsCreate.add(new Job("dean", new Date(2021, 1, 1), new Date(2021, 2, 1), "education", "Baltimore", true, true, "college degree", 100000, e));
            // try to insert them into job table. This must succeed!
            dao.create(lsCreate);
            List<Job> ls1 = dao.queryForAll();
            assertEquals(4, ls1.size());
            // try to delete the inserted values
            dao.delete(lsCreate);
            List<Job> ls2 = dao.queryForAll();
            assertEquals(0, ls2.size());
        }

        // Deleting a collection of jobs at once (using the "delete" function of ORMLite) where none of them exists does not remove any rows from the table
        @Test
        public void testDeleteForeignMultipleEmployees() throws SQLException {
            Employer e = new Employer("Kraft Heinz", "Food", "A global food and beverage company!");
            e.setId(1);
            // create multiple new job instance
            List<Job> lsCreate = new ArrayList<>();
            lsCreate.add(new Job("teacher", new Date(2021, 1, 1), new Date(2021, 2, 1), "education", "Baltimore", true, true, "college degree", 100000, e));
            lsCreate.add(new Job("professor", new Date(2021, 1, 1), new Date(2021, 2, 1), "education", "Baltimore", true, true, "college degree", 100000, e));
            // try to insert them into employers table. This must succeed!
            dao.create(lsCreate);
            List<Job> ls1 = dao.queryForAll();
            assertEquals(2, ls1.size());
            // create collection of other employees
            List<Job> lsCreate2 = new ArrayList<>();
            lsCreate.add(new Job("intern", new Date(2021, 1, 1), new Date(2021, 2, 1), "education", "Baltimore", true, true, "college degree", 100000, e));
            lsCreate.add(new Job("dean", new Date(2021, 1, 1), new Date(2021, 2, 1), "education", "Baltimore", true, true, "college degree", 100000, e));
            // try to delete the the other employees from the first list of employees.
            dao.delete(lsCreate2);
            // Check that no rows were deleted
            List<Job> ls2 = dao.queryForAll();
            assertEquals(2, ls2.size());
        }
   }


    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class JobAPITest {

        final String BASE_URL = "http://localhost:7000";
        private OkHttpClient client;

        @BeforeAll
        public void setUpAll() {
            client = new OkHttpClient();
        }

        @Test
        public void testHTTPGetJobsEndPoint() throws IOException {
            String ENDPOINT = "/jobs";

            Request request = new Request.Builder().url(BASE_URL + ENDPOINT).build();
            Response response = client.newCall(request).execute();
            String responseText = response.body().string();

            assertEquals(200, response.code());
            List<Job> list = new Gson().fromJson(responseText, new TypeToken<List<Job>>(){}.getType());
            // TODO 6: Write code to send a http get request using OkHttp to the
            //  "jobs" endpoint and assert that the received status code is OK (200)!
            //  Note: In order for this to work, you need to make sure your local sparkjava
            //  server is running, before you run the JUnit test!
        }
    }

}
