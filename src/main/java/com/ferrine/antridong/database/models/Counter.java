package com.ferrine.antridong.database.models;

import io.ebean.Model;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import java.util.List;
import com.ferrine.antridong.database.DbMetadata;

@Entity
@Table(name = DbMetadata.Counter.TABLE_NAME)
public class Counter extends Model {
    @Id
    @Column(name = DbMetadata.Counter.COL_ID)
    private Long id;

    @Column(name = DbMetadata.Counter.COL_NAME, nullable = false)
    private String name;

    @Column(name = DbMetadata.Counter.COL_STATUS, nullable = false)
    private String status;

    @ManyToMany
    @JoinTable(
        name = DbMetadata.CounterKategori.TABLE_NAME,
        joinColumns = @JoinColumn(name = DbMetadata.CounterKategori.COL_COUNTER_ID),
        inverseJoinColumns = @JoinColumn(name = DbMetadata.CounterKategori.COL_KATEGORI_ID)
    )
    private List<KategoriAntrian> kategoriList;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<KategoriAntrian> getKategoriList() { return kategoriList; }
    public void setKategoriList(List<KategoriAntrian> kategoriList) { this.kategoriList = kategoriList; }
}
