package com.Synchrome.collabcontent.User.Domain;

import com.Synchrome.collabcontent.Common.domain.BaseTimeEntity;
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
    private Delete delete = Delete.N;
    @Enumerated(EnumType.STRING)
    private Master master;
}
