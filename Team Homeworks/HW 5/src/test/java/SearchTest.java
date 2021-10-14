import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import model.Employer;
import model.Job;
import okhttp3.*;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class SearchTest {
    private final String URI = "jdbc:sqlite:./JBApp.db";

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class SearchTestClass {
        private final String ENDPOINT = "http://localhost:7000/search";
        private OkHttpClient client;
        private ConnectionSource connectionSource;
        private Dao<Job, Integer> jobDao;
        private Dao<Employer, Integer> employerDao;

        private Employer emp1;
        private Employer emp2;

        private Job job1;
        private Job job2;
        private Job job3;
        private Job job4;
        private Job job5;

        @BeforeAll
        public void setUpAll() throws SQLException {
            connectionSource = new JdbcConnectionSource(URI);
            TableUtils.createTableIfNotExists(connectionSource, Employer.class);
            TableUtils.createTableIfNotExists(connectionSource, Job.class);
            employerDao = DaoManager.createDao(connectionSource, Employer.class);
            jobDao = DaoManager.createDao(connectionSource, Job.class);
            client = new OkHttpClient();

            emp1 = new Employer("Google Inc.", "Tech", "Google Summary");
            emp2 = new Employer("The Microsoft Company", "Software", "Microsoft Summary");

            job1 = new Job("Google Dev", new Date(), new Date(), "Tech", "Location1", true, false, "Requirements", 5000, emp1);
            job2 = new Job("Google CEO", new Date(), new Date(), "Management", "Location2", true, false, "None", 50000000, emp1);
            job3 = new Job("Microsoft Dev", new Date(), new Date(), "Tech", "Location3", true, false, "Requirements", 5000, emp2);
            job4 = new Job("Researcher", new Date(), new Date(), "R&D", "Location4", true, false, "CS Skills", 500000, emp2);
            job5 = new Job("Microsoft Security", new Date(), new Date(), "Safety", "Location5", true, false, "Requirements", 50000, emp2);
        }

        @BeforeEach
        public void setUpEach() throws SQLException {
            TableUtils.clearTable(connectionSource, Job.class);
            TableUtils.clearTable(connectionSource, Employer.class);
        }

        private void addData() throws SQLException{
            employerDao.create(emp1);
            employerDao.create(emp2);
            jobDao.create(job1);
            jobDao.create(job2);
            jobDao.create(job3);
            jobDao.create(job4);
            jobDao.create(job5);
        }

        private List<Job> query(String keyword) throws IOException {
            RequestBody body = new FormBody.Builder().add("query", keyword).build();

            Request request = new Request.Builder()
                .url(ENDPOINT)
                .post(body)
                .build();

            Response response = client.newCall(request).execute();
            assertEquals(200, response.code());
            return new Gson().fromJson(response.body().string(), new TypeToken<List<Job>>(){}.getType());
        }

        @Test
        public void testEmptyDatabase() throws IOException{
            assertEquals(0, query("Microsoft").size());
        }

        @Test
        public void testEmptyStringDatabase() throws IOException , SQLException{
            addData();
            assertEquals(5, query("").size());
        }

        @Test
        public void testIncompleteTitle() throws IOException, SQLException {
            addData();
            assertEquals(2, query("De").size());
        }

        @Test
        public void testTitleTooMuchInfo() throws IOException, SQLException{
            addData();
            assertEquals(0, query("Developer").size());
        }

        @Test
        public void testTitleCaseInsensitive() throws IOException, SQLException{
            addData();
            List<Job> jobs = query("rESEARCHER");
            assertEquals(1, jobs.size());
            assertEquals("Researcher", jobs.get(0).getTitle());
        }

        @Test
        public void testDomainSubstring() throws IOException, SQLException{
            addData();
            List<Job> jobs = query("age");
            assertEquals(1, jobs.size());
            assertEquals("Management", jobs.get(0).getDomain());
        }

        @Test
        public void testDomainExtraSpaces() throws IOException, SQLException{
            addData();
            assertEquals(0, query("T e c h").size());
        }

        @Test
        public void testDomainSurroundingSpaces() throws IOException, SQLException {
            addData();
            assertEquals(0, query("   Tech   ").size());
        }

        @Test
        public void testDomainCorrect() throws IOException, SQLException {
            addData();
            assertEquals(2, query("Tech").size());
        }

        @Test
        public void testGeneralString() throws IOException, SQLException {
            addData();
            assertEquals(5, query("e").size()); //Every job has some sort of match
        }

        @Test
        public void testCompanySubstring() throws IOException, SQLException{
            addData();
            assertEquals(0, query("Inc.").size());
        }

        @Test
        public void testCompanyTooMuch() throws IOException, SQLException{
            addData();
            assertEquals(0, query("The Microsoft Company Inc.").size());
        }

        @Test
        public void testCompanyCaseInsensitive() throws IOException, SQLException{
            addData();
            assertEquals(3, query("The Microsoft Company").size());
        }

        @Test
        public void sqlInjection() throws IOException, SQLException{
            addData();
            assertEquals(0, query("Google; DROP TABLE jobs").size());
            assertEquals(5, query("e").size());
        }


    }
}
