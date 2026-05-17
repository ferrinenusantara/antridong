package com.ferrine.antridong.database.models;

import io.ebean.Model;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import com.ferrine.antridong.database.DbMetadata;

@Entity
@Table(name = DbMetadata.User.TABLE_NAME)
public class User extends Model {
    @Id
    @Column(name = DbMetadata.User.COL_ID)
    private Long id;

    @Column(name = DbMetadata.User.COL_USERNAME, unique = true, nullable = false)
    private String username;

    @Column(name = DbMetadata.User.COL_PASSWORD, nullable = false)
    private String password;

    @Column(name = DbMetadata.User.COL_NAME, nullable = false)
    private String name;

    @Column(name = DbMetadata.User.COL_ROLE, nullable = false)
    private String role;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
