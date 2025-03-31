package com.Synchrome.user.User.Domain;

import com.Synchrome.user.Common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Entity
@Builder
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String profile;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Active active = Active.Y;
    @Enumerated(EnumType.STRING)
    private ChannelManager channelManager;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Del del = Del.N;
    @Enumerated(EnumType.STRING)
    private Mas mas;
}
