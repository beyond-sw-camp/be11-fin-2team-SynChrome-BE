package com.Synchrome.workspace.space.domain;

import com.Synchrome.workspace.common.BaseTimeEntity;
import com.Synchrome.workspace.space.domain.ENUM.Del;
import com.Synchrome.workspace.space.domain.ENUM.Owner;
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
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Owner owner = Owner.U;
    @OneToMany(mappedBy = "workSpace",cascade = CascadeType.ALL)
    @Builder.Default
    private List<Section> sections = new ArrayList<>();
    @OneToMany(mappedBy = "workSpace",cascade = CascadeType.ALL)
    @Builder.Default
    private List<WorkSpaceParticipant> participants = new ArrayList<>();

    public void delete(){
        this.del = Del.Y;
    }

    public void updateLogo(String logoUrl){
        this.logo = logoUrl;
    }

    public void update(WorkSpaceUpdateDto dto){
        if (dto.getTitle() != null) {
            this.title = dto.getTitle();
        }
        if (dto.getChangeMasterId() != null) {
            this.userId = dto.getChangeMasterId();
        }
    }
}
