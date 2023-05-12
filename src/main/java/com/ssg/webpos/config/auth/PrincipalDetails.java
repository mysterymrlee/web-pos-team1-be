package com.ssg.webpos.config.auth;

import com.ssg.webpos.domain.BranchAdmin;
import com.ssg.webpos.domain.HQAdmin;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Data
public class PrincipalDetails implements UserDetails {

    private Object admin;
    private Map<String, Object> attributes;
    public PrincipalDetails(Object admin) {
        this.admin = admin;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                String retRoleStr = "";
                if (admin instanceof HQAdmin) {
                    retRoleStr = ((HQAdmin) admin).getRole().toString();
                } else if (admin instanceof BranchAdmin) {
                    retRoleStr = ((BranchAdmin) admin).getRole().toString();
                }
                return retRoleStr;
            }
        });
        return collection;
    }

    @Override
    public String getPassword() {
        String retPassword = "";
        if (admin instanceof HQAdmin) {
            retPassword = ((HQAdmin) admin).getPassword();
        } else if (admin instanceof BranchAdmin) {
            retPassword = ((BranchAdmin) admin).getPassword();
        }
        return retPassword;
    }

    @Override
    public String getUsername() {
        return (String) attributes.get("name");
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
