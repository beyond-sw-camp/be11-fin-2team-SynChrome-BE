package com.Synchrome.user.User.Domain;

import com.Synchrome.user.Common.domain.BaseTimeEntity;
import com.Synchrome.user.User.Domain.Enum.*;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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
    private Subscribe subscribe = Subscribe.N;
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
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    @Builder.Default
    private List<Pay> pays = new ArrayList<>();

    public void subscribe(){
        this.subscribe = Subscribe.Y;
    }

    public void cancelSubscribe(){
        this.subscribe = Subscribe.N;
    }
}
