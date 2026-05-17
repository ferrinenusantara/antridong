package com.ferrine.antridong.database.repository;

import io.ebean.Finder;
import org.springframework.stereotype.Repository;
import com.ferrine.antridong.database.models.KategoriAntrian;
import com.ferrine.antridong.database.DbMetadata;

@Repository
public class KategoriAntrianRepository extends Finder<Long, KategoriAntrian> {
    public KategoriAntrianRepository() {
        super(KategoriAntrian.class);
    }

    public KategoriAntrian findByCode(String code) {
        return query().where().eq(DbMetadata.KategoriAntrian.COL_CODE, code).findOne();
    }
}
