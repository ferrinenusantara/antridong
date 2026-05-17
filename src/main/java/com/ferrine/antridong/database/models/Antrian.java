package com.ferrine.antridong.database.models;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import java.time.LocalDateTime;
import com.ferrine.antridong.database.DbMetadata;

@Entity
@Table(name = DbMetadata.Antrian.TABLE_NAME)
public class Antrian extends Model {
    @Id
    @Column(name = DbMetadata.Antrian.COL_ID)
    private Long id;

    @Column(name = DbMetadata.Antrian.COL_TICKET_NUMBER, nullable = false)
    private String ticketNumber;

    @Column(name = DbMetadata.Antrian.COL_VISITOR_NAME)
    private String visitorName;

    @Column(name = DbMetadata.Antrian.COL_STATUS, nullable = false)
    private String status;

    @WhenCreated
    @Column(name = DbMetadata.Antrian.COL_CREATED_AT)
    private LocalDateTime createdAt;

    @Column(name = DbMetadata.Antrian.COL_CALLED_AT)
    private LocalDateTime calledAt;

    @ManyToOne
    @JoinColumn(name = DbMetadata.Antrian.COL_KATEGORI_ID)
    private KategoriAntrian kategori;

    @ManyToOne
    @JoinColumn(name = DbMetadata.Antrian.COL_COUNTER_ID)
    private Counter counter;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }

    public String getVisitorName() { return visitorName; }
    public void setVisitorName(String visitorName) { this.visitorName = visitorName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getCalledAt() { return calledAt; }
    public void setCalledAt(LocalDateTime calledAt) { this.calledAt = calledAt; }

    public KategoriAntrian getKategori() { return kategori; }
    public void setKategori(KategoriAntrian kategori) { this.kategori = kategori; }

    public Counter getCounter() { return counter; }
    public void setCounter(Counter counter) { this.counter = counter; }
}
