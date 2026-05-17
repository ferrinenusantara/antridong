package com.ferrine.antridong.database.repository;

import io.ebean.Finder;
import org.springframework.stereotype.Repository;
import com.ferrine.antridong.database.models.Counter;
import com.ferrine.antridong.database.DbMetadata;
import java.util.List;

@Repository
public class CounterRepository extends Finder<Long, Counter> {
    public CounterRepository() {
        super(Counter.class);
    }

    public List<Counter> findActiveCounters() {
        return query().where().eq(DbMetadata.Counter.COL_STATUS, "active").findList();
    }
}
