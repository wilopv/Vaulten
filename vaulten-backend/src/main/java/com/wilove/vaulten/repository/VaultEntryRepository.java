package com.wilove.vaulten.repository;

import com.wilove.vaulten.model.User;
import com.wilove.vaulten.model.VaultEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaultEntryRepository extends JpaRepository<VaultEntry, Long> {
    List<VaultEntry> findAllByUser(User user);

    List<VaultEntry> findByUserAndNameContainingIgnoreCase(User user, String name);
}
