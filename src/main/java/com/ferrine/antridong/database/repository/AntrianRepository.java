package com.ferrine.antridong.database.repository;

import io.ebean.Finder;
import org.springframework.stereotype.Repository;
import com.ferrine.antridong.database.models.Antrian;
import com.ferrine.antridong.database.DbMetadata;
import java.util.List;

@Repository
public class AntrianRepository extends Finder<Long, Antrian> {
    public AntrianRepository() {
        super(Antrian.class);
    }

    public List<Antrian> findByStatus(String status) {
        return query().where().eq(DbMetadata.Antrian.COL_STATUS, status).findList();
    }
}
