import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import model.Employer;
import spark.ModelAndView;
import spark.Spark;
import spark.template.velocity.VelocityTemplateEngine;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static spark.Spark.port;

public class Main {

    static int PORT = 7000;

    private static Dao getEmployerORMLiteDao() throws SQLException {
        final String URI = "jdbc:sqlite:./JBApp.db";
        ConnectionSource connectionSource = new JdbcConnectionSource(URI);
        TableUtils.createTableIfNotExists(connectionSource, Employer.class);
        return DaoManager.createDao(connectionSource, Employer.class);
    }


    //hkl
    private static int getPort() {
        String herokuPort = System.getenv("PORT");
        if (herokuPort != null) {
            PORT = Integer.parseInt(herokuPort);
        }
        return PORT;
    }


    public static void main(String[] args) {
        //Spark.port(PORT_NUM);


        port(getPort());
        Spark.staticFiles.location("/public");
        workWithDatabase();


        // render and return login/homepage
        Spark.get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            if (req.cookie("username") != null)
                model.put("username", req.cookie("username"));
            return new ModelAndView(model, "public/index.vm");
        }, new VelocityTemplateEngine());

        // used set a username cookie
        Spark.post("/", (req, res) -> {
            String username = req.queryParams("username");
            res.cookie("username", username);
            res.redirect("/");
            return null;
        });

        // show (read) all employers
        Spark.get("/employers", (req, res) -> {
            List<Employer> ls = getEmployerORMLiteDao().queryForAll();
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("employers", ls);
            return new ModelAndView(model, "public/employers.vm");
        }, new VelocityTemplateEngine());

        // create a new employer
        Spark.post("/employers", (req, res) -> {
            String name = req.queryParams("name");
            String sector = req.queryParams("sector");
            String summary = req.queryParams("summary");
            Employer em = new Employer(name, sector, summary);
            getEmployerORMLiteDao().create(em);
            res.status(201);
            res.type("application/json");
            return new Gson().toJson(em.toString());
        });

        // delete an employer
        Spark.delete("/employers", (req, res) -> {
            String name = req.queryParams("name");
            Dao emDao = getEmployerORMLiteDao();
            List<Employer> ems = emDao.queryForEq("name", name);
            int del = 0;
            if (ems != null  && !ems.isEmpty()) {
                del = emDao.deleteById(ems.get(0).getId());
            }
            res.status(200);
            res.type("application/json");
            if (del > 0) {
                return new Gson().toJson(ems.get(0).toString());
            }
            return new Gson().toJson("{}");
        });

    }


    private static Connection getConnection() throws URISyntaxException, SQLException, ClassNotFoundException {
        String databaseUrl = System.getenv("DATABASE_URL");
        if (databaseUrl == null) {
            // Not on Heroku, so use SQLite
            return DriverManager.getConnection("jdbc:sqlite:./JBApp.db");
        }
        URI dbUri = new URI(databaseUrl);
        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() ;
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection(dbUrl, username, password);
    }

    private static void workWithDatabase() {
        try (Connection conn = getConnection()) {
            String sql = "";

            //System.out.println(conn.getMetaData().getDatabaseProductName());
//            if ("SQLite".equalsIgnoreCase(conn.getMetaData().getDatabaseProductName())) { // running locally
//                sql = "CREATE TABLE IF NOT EXISTS employers (id INTEGER PRIMARY KEY, " +
//                        "name VARCHAR(100) NOT NULL UNIQUE, sector VARCHAR(100), summary VARCHAR(10000));";
//            }
//            else {
//                sql = "CREATE TABLE IF NOT EXISTS employers (id serial PRIMARY KEY, name VARCHAR(100) NOT NULL UNIQUE," +
//                        " sector VARCHAR(100), summary VARCHAR(10000));";
//            }
//
//            Statement st = conn.createStatement();
//            st.execute(sql);


        } catch (URISyntaxException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
