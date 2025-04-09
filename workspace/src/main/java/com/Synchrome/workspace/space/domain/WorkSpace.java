package com.Synchrome.workspace.space.domain;

import com.Synchrome.workspace.space.common.BaseTimeEntity;
import com.Synchrome.workspace.space.domain.ENUM.Del;
import com.Synchrome.workspace.space.dtos.workSpaceDtos.WorkSpaceUpdateDto;
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
public class WorkSpace extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String logo;
    private String inviteUrl;
    private Long userId;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Del del = Del.N;
    @OneToMany(mappedBy = "workSpace",cascade = CascadeType.ALL)
    @Builder.Default
    private List<Section> sections = new ArrayList<>();
    @OneToMany(mappedBy = "workSpace",cascade = CascadeType.ALL)
    @Builder.Default
    private List<WokrSpaceParticipant> participants = new ArrayList<>();

    public void delete(){
        this.del = Del.Y;
    }

    public void update(WorkSpaceUpdateDto dto){
        this.title = dto.getTitle();
        this.userId = dto.getUserId();
    }
}
