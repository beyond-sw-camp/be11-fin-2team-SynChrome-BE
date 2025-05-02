package com.Synchrome.user.User.Repository;

import com.Synchrome.user.User.Domain.Enum.Del;
import com.Synchrome.user.User.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    List<User> findByIdInAndDel(List<Long> ids, Del del);
}
