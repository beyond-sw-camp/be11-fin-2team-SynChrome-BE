package com.Synchrome.workspace.space.domain;

import com.Synchrome.workspace.common.BaseTimeEntity;
import com.Synchrome.workspace.space.domain.ENUM.Del;
import com.Synchrome.workspace.space.domain.ENUM.Owner;
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
public class Channel extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private Long userId;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Del del = Del.N;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Owner owner = Owner.U;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id")
    private Section section;
    @OneToMany(mappedBy = "channel",cascade = CascadeType.ALL)
    @Builder.Default
    private List<ChannelParticipant> participants = new ArrayList<>();


    public void delete(){
        this.del = Del.Y;
    }

    public void update(Section section,String title){
        if (title != null) {
            this.title = title;
        }
        if (section != null) {
            this.section = section;
        }
    }
}
