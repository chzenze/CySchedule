package edu.iastate.coms309.cyschedulebackend.persistence.model;


import lombok.Data;

import java.util.List;
import java.util.Set;
import javax.persistence.*;

@Data
@Entity
@Table(name ="user_role")
public class UserRole{
    @Id
    @Column(name = "role_id")
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer roleID;

    private String roleName;

    private String description;

    @OneToMany
    private Set<Permission> rolePermissions;

    @Override
    public String toString(){ return roleName;}
}
