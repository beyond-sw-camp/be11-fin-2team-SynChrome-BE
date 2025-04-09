package com.Synchrome.user.User.Repository;

import com.Synchrome.user.User.Domain.Enum.Paystatus;
import com.Synchrome.user.User.Domain.Pay;
import com.Synchrome.user.User.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface PaymentRepository extends JpaRepository<Pay,Long> {
    Pay findByImpUid(String impUid);
    Optional<Pay> findByUserAndPaystatus(User user, Paystatus paystatus);
    Optional<List<Pay>> findByUserId(Long userId);
}
