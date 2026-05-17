package com.ferrine.antridong.database.models;

import io.ebean.Model;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToMany;
import java.time.LocalTime;
import java.util.List;
import com.ferrine.antridong.database.DbMetadata;

@Entity
@Table(name = DbMetadata.KategoriAntrian.TABLE_NAME)
public class KategoriAntrian extends Model {
    @Id
    @Column(name = DbMetadata.KategoriAntrian.COL_ID)
    private Long id;

    @Column(name = DbMetadata.KategoriAntrian.COL_CODE, length = 3, unique = true, nullable = false)
    private String code;

    @Column(name = DbMetadata.KategoriAntrian.COL_NAME, nullable = false)
    private String name;

    @Column(name = DbMetadata.KategoriAntrian.COL_START_TIME, nullable = false)
    private LocalTime startTime;

    @Column(name = DbMetadata.KategoriAntrian.COL_END_TIME, nullable = false)
    private LocalTime endTime;

    @ManyToMany(mappedBy = "kategoriList")
    private List<Counter> counterList;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public List<Counter> getCounterList() { return counterList; }
    public void setCounterList(List<Counter> counterList) { this.counterList = counterList; }
}
