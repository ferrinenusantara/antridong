package com.ferrine.antridong.service;

import com.ferrine.antridong.database.DbMetadata;
import com.ferrine.antridong.database.models.Antrian;
import com.ferrine.antridong.database.repository.AntrianRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QueueStateService {

    private final AntrianRepository antrianRepository;

    public QueueStateService(AntrianRepository antrianRepository) {
        this.antrianRepository = antrianRepository;
    }

    /**
     * Mengambil daftar antrian yang sedang menunggu (PENDING) secara FIFO.
     */
    public List<Antrian> getPendingQueues() {
        return antrianRepository.query()
                .where().eq(DbMetadata.Antrian.COL_STATUS, "PENDING")
                .orderBy(DbMetadata.Antrian.COL_CREATED_AT + " asc")
                .findList();
    }

    /**
     * Mengambil antrian terakhir yang dipanggil (CALLED).
     */
    public Antrian getLastCalledQueue() {
        return antrianRepository.query()
                .where().eq(DbMetadata.Antrian.COL_STATUS, "CALLED")
                .orderBy(DbMetadata.Antrian.COL_CALLED_AT + " desc")
                .setMaxRows(1)
                .findOne();
    }
}
