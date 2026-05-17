package com.ferrine.antridong.database.repository;

import io.ebean.Finder;
import org.springframework.stereotype.Repository;
import com.ferrine.antridong.database.models.User;
import com.ferrine.antridong.database.DbMetadata;

@Repository
public class UserRepository extends Finder<Long, User> {
    public UserRepository() {
        super(User.class);
    }

    public User findByUsername(String username) {
        return query().where().eq(DbMetadata.User.COL_USERNAME, username).findOne();
    }
}
