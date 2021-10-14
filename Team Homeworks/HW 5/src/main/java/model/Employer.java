package model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Objects;


@DatabaseTable(tableName = "employers")
public class Employer {
    @DatabaseField(generatedId = true, columnName="id")
    private Integer id;
    @DatabaseField(canBeNull = false, unique = true)
    private String name;
    @DatabaseField(canBeNull = false)
    private String sector;
    @DatabaseField
    private String summary;

    public Employer() {
    }

    public Employer(String name, String sector, String summary) {
        this.name = name;
        this.sector = sector;
        this.summary = summary;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSector() {
        return sector;
    }

    public String getSummary() {
        return summary;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public String toString() {
        return "Employer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sector='" + sector + '\'' +
                ", summary='" + summary + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employer employer = (Employer) o;
        return id.equals(employer.id) && name.equals(employer.name) && Objects.equals(sector, employer.sector) && Objects.equals(summary, employer.summary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, sector, summary);
    }
}
