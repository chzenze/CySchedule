package edu.iastate.coms309.cyschedulebackend.persistence.model;


import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.Set;
import javax.persistence.*;

@Data
@Entity
@Table(name ="user_role")
public class Permission implements GrantedAuthority {
    @Id
    @Column(name = "role_id")
    private String roleName;

    private String description;

    @Override
    public String toString(){ return roleName;}

    @Override
    public String getAuthority() { return roleName; }
}