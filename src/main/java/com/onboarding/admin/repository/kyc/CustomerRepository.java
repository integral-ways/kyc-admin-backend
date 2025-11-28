package com.onboarding.admin.repository.kyc;

import com.onboarding.admin.entity.kyc.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
    Page<Customer> findByPartyStatus(String partyStatus, Pageable pageable);
}
