package com.Synchrome.workspace.space.domain;

import com.Synchrome.workspace.space.common.BaseTimeEntity;
import com.Synchrome.workspace.space.domain.ENUM.Del;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Entity
@Builder
public class WokrSpaceParticipant extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Del del = Del.N;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_space_id")
    private WorkSpace workSpace;
}
