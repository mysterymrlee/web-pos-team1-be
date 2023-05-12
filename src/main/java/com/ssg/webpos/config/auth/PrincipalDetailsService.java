package com.ssg.webpos.config.auth;

import com.ssg.webpos.domain.BranchAdmin;
import com.ssg.webpos.domain.HQAdmin;
import com.ssg.webpos.repository.BranchAdminRepository;
import com.ssg.webpos.repository.HQAdminRepository;
import com.ssg.webpos.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final HQAdminRepository hqAdminRepository;
    private final BranchAdminRepository branchAdminRepository;

    @Override
    public UserDetails loadUserByUsername(String adminNumber) throws UsernameNotFoundException {
        Object admin = null;
        if (adminNumber.startsWith("0")) {
            admin = (HQAdmin) hqAdminRepository.findByAdminNumber(adminNumber).get();
        } else if (adminNumber.startsWith("1") || adminNumber.startsWith("2")) {
            admin = (BranchAdmin) branchAdminRepository.findByAdminNumber(adminNumber).get();
        }
        if (admin == null) {
            return new PrincipalDetails(admin);
        }
        return null;
    }
}
