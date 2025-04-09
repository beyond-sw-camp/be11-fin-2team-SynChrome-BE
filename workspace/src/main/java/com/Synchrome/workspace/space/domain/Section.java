package com.Synchrome.workspace.space.domain;

import com.Synchrome.workspace.space.common.BaseTimeEntity;
import com.Synchrome.workspace.space.domain.ENUM.Del;
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
public class Section extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private Long userId;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Del del = Del.N;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_space_id")
    private WorkSpace workSpace;
    @OneToMany(mappedBy = "section",cascade = CascadeType.ALL)
    @Builder.Default
    private List<Channel> channels = new ArrayList<>();

    public void delete(){
        this.del = Del.Y;
    }

    public void update(String title){
        this.title = title;
    }
}
