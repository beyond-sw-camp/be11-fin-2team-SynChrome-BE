package com.Synchrome.workspace.space.repository;

import com.Synchrome.workspace.space.domain.Channel;
import com.Synchrome.workspace.space.domain.ENUM.Del;
import com.Synchrome.workspace.space.domain.WorkSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChannelRepository extends JpaRepository<Channel,Long> {
    List<Channel> findBySectionIdAndDel(Long sectionId, Del del);
    List<Channel> findByUserIdAndDel(Long userId, Del del);
    List<Channel> findBySection_WorkSpaceIdAndDel(Long workSpaceId, Del del);
}
