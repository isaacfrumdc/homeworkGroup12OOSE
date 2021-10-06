import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import model.Employer;
import model.Job;
import spark.ModelAndView;
import spark.Spark;
import spark.template.velocity.VelocityTemplateEngine;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {

    private static Dao getEmployerORMLiteDao() throws SQLException {
        final String URI = "jdbc:sqlite:./JBApp.db";
        ConnectionSource connectionSource = new JdbcConnectionSource(URI);
        TableUtils.createTableIfNotExists(connectionSource, Employer.class);
        return DaoManager.createDao(connectionSource, Employer.class);
    }

    private static Dao getJobORMLiteDao() throws SQLException {
        final String URI = "jdbc:sqlite:./JBApp.db";
        ConnectionSource connectionSource = new JdbcConnectionSource(URI);
        TableUtils.createTableIfNotExists(connectionSource, Job.class);
        return DaoManager.createDao(connectionSource, Job.class);
    }

    public static void main(String[] args) throws SQLException{

        final int PORT_NUM = 7000;
        Spark.port(PORT_NUM);

        Spark.get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            if (req.cookie("username") != null)
                model.put("username", req.cookie("username"));
            return new ModelAndView(model, "public/index.vm");
        }, new VelocityTemplateEngine());

        Spark.post("/", (req, res) -> {
            String username = req.queryParams("username");
            String color = req.queryParams("color");
            res.cookie("username", username);
            res.redirect("/");
            return null;
        });

        Spark.get("/employers", (req, res) -> {
            List<Employer> ls = getEmployerORMLiteDao().queryForAll();
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("employers", ls);
            return new ModelAndView(model, "public/employers.vm");
        }, new VelocityTemplateEngine());

        Spark.post("/employers", (req, res) -> {
            String name = req.queryParams("name");
            String sector = req.queryParams("sector");
            String summary = req.queryParams("summary");

            List<Employer> empList = getEmployerORMLiteDao().queryForEq("name", name);
            if (empList.size() != 0) {
                res.status(400);
                return "Error - duplicate employer";
            }

            Employer em = new Employer(name, sector, summary);
            getEmployerORMLiteDao().create(em);
            res.status(201);
            res.type("application/json");
            return new Gson().toJson(em.toString());
        });

        Spark.get("/jobs", (req, res) -> {
            List<Employer> ls = getJobORMLiteDao().queryForAll();
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("jobs", ls);
            return new ModelAndView(model, "public/jobs.vm");
        }, new VelocityTemplateEngine());

        Spark.post("/jobs", (req, res) -> {
            String title = req.queryParams("title");
            Date datePosted = new Date(req.queryParams("datePosted"));
            Date deadline = new Date(req.queryParams("deadline"));
            String domain = req.queryParams("domain");
            String location = req.queryParams("location");

            boolean fullTime = true;
            String fullTimeStr = req.queryParams("fullTime");

            if (fullTimeStr.equals("No")){
                fullTime = false;
            }

            boolean salaryBased = true;
            String salaryBasedStr = req.queryParams("wage");
            if (salaryBasedStr.equals("No")){
                salaryBased = false;
            }

            String requirements = req.queryParams("requirements");
            int payAmount = Integer.parseInt(req.queryParams("payAmount"));

            String employerName = req.queryParams("employer");
            List<Employer> employerList = getEmployerORMLiteDao().queryForEq("name", employerName);
            Employer employer = employerList.get(0);

            Job jo = new Job(title, datePosted, deadline, domain, location, fullTime, salaryBased, requirements, payAmount, employer);

            getJobORMLiteDao().create(jo);
            res.status(201);
            res.type("application/json");
            return new Gson().toJson(jo.toString());
        });

        Spark.get("/addjobs", (req, res) -> {
            List<Employer> ls = getEmployerORMLiteDao().queryForAll();
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("employers", ls);
            return new ModelAndView(model, "public/addjobs.vm");
        }, new VelocityTemplateEngine());


        Spark.get("/addemployers", (req, res) -> {
            Map<String, Object> model = new HashMap<String, Object>();
            return new ModelAndView(model, "public/addemployers.vm");
        }, new VelocityTemplateEngine());

        Spark.get("/search", (req, res) -> {
            Map<String, Object> model = new HashMap<String, Object>();
            return new ModelAndView(model, "public/search.vm");
        }, new VelocityTemplateEngine());

        Spark.post("/search", (req, res) -> {
            String query = req.queryParams("query");

            GenericRawResults<Employer> empResults = getEmployerORMLiteDao().queryRaw("SELECT * FROM " +
                    "employers WHERE LOWER(name) = LOWER(\"" + query + "\")", getEmployerORMLiteDao().getRawRowMapper());
            List<Employer> empLs = empResults.getResults();

            GenericRawResults<Job> jobResults = getJobORMLiteDao().queryRaw("SELECT id FROM jobs WHERE LOWER(title)" +
                    "LIKE LOWER(\"%" + query + "%\")  OR LOWER(domain) LIKE LOWER(\"%" + query + "%\")", getJobORMLiteDao().getRawRowMapper());
            List<Job> jobLs = jobResults.getResults();

            for (int ind = 0; ind < empLs.size(); ind++) {
                jobLs.addAll(getJobORMLiteDao().queryForEq("employer_id", empLs.get(ind).getId()));
            }

            res.status(201);
            res.type("application/json");
            return new Gson().toJson(jobLs.toString());
        });
    }
}
