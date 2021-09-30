package model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;
import java.util.Objects;

// TODO 3: Similar to what was done in Employer class, annotate Job class using
//  ORMLite annotations so that you can easily create the "jobs" table in Main.java
//  using ORMLite's TableUtil class.

@DatabaseTable(tableName = "jobs")
public class Job {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(canBeNull = false)
    private String title;
    @DatabaseField(canBeNull = false)
    private Date datePosted;
    @DatabaseField()
    private Date deadline;
    @DatabaseField(canBeNull = false)
    private String domain;
    @DatabaseField(canBeNull = false)
    private String location;
    @DatabaseField(canBeNull = false)
    private boolean fullTime;
    @DatabaseField(canBeNull = false)
    private boolean salaryBased;
    @DatabaseField()
    private String requirements;
    @DatabaseField(canBeNull = false)
    private int payAmount;
    @DatabaseField(canBeNull = false, foreign = true)
    private Employer employer;

    public Job() {
    }
    public Job(String title, Date datePosted, Date deadline, String domain, String location, boolean fullTime, boolean salaryBased, String requirements, int payAmount, Employer employer) {
        this.title = title;
        this.datePosted = datePosted;
        this.deadline = deadline;
        this.domain = domain;
        this.location = location;
        this.fullTime = fullTime;
        this.salaryBased = salaryBased;
        this.requirements = requirements;
        this.payAmount = payAmount;
        this.employer = employer;
    }

    public int getId() {
        return id;
    }



    public String getTitle() {
        return title;
    }

    public Date getDatePosted() {
        return datePosted;
    }

    public Date getDeadline() {
        return deadline;
    }

    public String getDomain() {
        return domain;
    }

    public String getLocation() {
        return location;
    }

    public boolean isFullTime() {
        return fullTime;
    }

    public boolean isSalaryBased() {
        return salaryBased;
    }

    public String getRequirements() {
        return requirements;
    }

    public int getPayAmount() {
        return payAmount;
    }

    public Employer getEmployer() {
        return employer;
    }

    public void setId(int id) { this.id = id; }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDatePosted(Date datePosted) {
        this.datePosted = datePosted;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setFullTime(boolean fullTime) {
        this.fullTime = fullTime;
    }

    public void setSalaryBased(boolean salaryBased) {
        this.salaryBased = salaryBased;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public void setPayAmount(int payAmount) {
        this.payAmount = payAmount;
    }

    public void setEmployer(Employer employer) {
        this.employer = employer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Job job = (Job) o;
        return id == job.id && fullTime == job.fullTime && salaryBased == job.salaryBased && payAmount == job.payAmount && title.equals(job.title) && Objects.equals(datePosted, job.datePosted) && Objects.equals(deadline, job.deadline) && Objects.equals(domain, job.domain) && Objects.equals(location, job.location) && Objects.equals(requirements, job.requirements) && Objects.equals(employer, job.employer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, datePosted, deadline, domain, location, fullTime, salaryBased, requirements, payAmount, employer);
    }
}
