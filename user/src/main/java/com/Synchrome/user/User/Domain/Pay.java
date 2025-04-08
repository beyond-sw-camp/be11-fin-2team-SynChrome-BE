package com.Synchrome.user.User.Domain;


import com.Synchrome.user.Common.domain.BaseTimeEntity;
import com.Synchrome.user.User.Domain.Enum.Paystatus;
import com.Synchrome.user.User.Domain.Enum.Subscribe;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Entity
@Builder
public class Pay extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @Column(nullable = false)
    private String impUid;
    @Column(nullable = false)
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Paystatus paystatus = Paystatus.PAY;


    public void cancelPaymentStatus(){
        this.paystatus = Paystatus.CANCEL;
    }
}
