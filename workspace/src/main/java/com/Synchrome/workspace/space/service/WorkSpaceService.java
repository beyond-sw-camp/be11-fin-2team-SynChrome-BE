package com.Synchrome.workspace.space.service;

import com.Synchrome.workspace.space.domain.Channel;
import com.Synchrome.workspace.space.domain.ChannelParticipant;
import com.Synchrome.workspace.space.domain.ENUM.Del;
import com.Synchrome.workspace.space.domain.ENUM.Owner;
import com.Synchrome.workspace.space.domain.Section;
import com.Synchrome.workspace.space.domain.WorkSpace;
import com.Synchrome.workspace.space.dtos.channelDtos.*;
import com.Synchrome.workspace.space.dtos.sectionDtos.FindMySectionDto;
import com.Synchrome.workspace.space.dtos.sectionDtos.SectionCreateDto;
import com.Synchrome.workspace.space.dtos.sectionDtos.SectionDeleteDto;
import com.Synchrome.workspace.space.dtos.sectionDtos.SectionupdateDto;
import com.Synchrome.workspace.space.dtos.workSpaceDtos.MyWorkSpaceResDto;
import com.Synchrome.workspace.space.dtos.workSpaceDtos.WorkSpaceCreateDto;
import com.Synchrome.workspace.space.dtos.workSpaceDtos.WorkSpaceUpdateDto;
import com.Synchrome.workspace.space.repository.ChannelParticipantRepository;
import com.Synchrome.workspace.space.repository.ChannelRepository;
import com.Synchrome.workspace.space.repository.SectionRepository;
import com.Synchrome.workspace.space.repository.WorkSpaceRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class WorkSpaceService {
    private final WorkSpaceRepository workSpaceRepository;
    private final SectionRepository sectionRepository;
    private final ChannelRepository channelRepository;
    private final ChannelParticipantRepository channelParticipantRepository;

    public WorkSpaceService(WorkSpaceRepository workSpaceRepository, SectionRepository sectionRepository, ChannelRepository channelRepository, ChannelParticipantRepository channelParticipantRepository) {
        this.workSpaceRepository = workSpaceRepository;
        this.sectionRepository = sectionRepository;
        this.channelRepository = channelRepository;
        this.channelParticipantRepository = channelParticipantRepository;
    }

    public Long saveWorkSpace(WorkSpaceCreateDto dto){
        WorkSpace workSpace = WorkSpace.builder()
                .title(dto.getTitle())
                .userId(dto.getUserId())
                .inviteUrl(dto.getInviteUrl())
                .build();
        workSpaceRepository.save(workSpace);
        return workSpace.getId();
    }

    public Long deleteMyWorkSpace(Long workSpaceId, Long userId){
        WorkSpace workSpace = workSpaceRepository.findById(workSpaceId).orElseThrow(()->new EntityNotFoundException("없는 워크스페이스"));
        if (!workSpace.getUserId().equals(userId)) {
            throw new IllegalArgumentException("해당 워크스페이스를 삭제할 권한이 없습니다.");
        }
        List<Section> sections = sectionRepository.findByWorkSpaceIdAndDel(workSpaceId, Del.N);

        for (Section section : sections) {
            List<Channel> channels = channelRepository.findBySectionIdAndDel(section.getId(), Del.N);
            for (Channel channel : channels) {
                channel.delete();
            }
            section.delete();
        }

        for (Section section : sections) {
            List<Channel> channels = channelRepository.findBySectionIdAndDel(section.getId(), Del.N);
            for (Channel channel : channels) {
                channel.delete();
            }
            section.delete();
        }

        workSpace.delete();
        return workSpace.getId();
    }

    public List<MyWorkSpaceResDto> findMyWorkSpace(Long userId){
        Optional<List<WorkSpace>> optionalWorkSpaces = workSpaceRepository.findByUserIdAndDel(userId, Del.N);

        if (optionalWorkSpaces.isEmpty() || optionalWorkSpaces.get().isEmpty()) {
            return new ArrayList<>();
        }

        List<WorkSpace> workSpaces = optionalWorkSpaces.get();

        return workSpaces.stream()
                .map(ws -> MyWorkSpaceResDto.builder()
                        .workSpaceId(ws.getId())
                        .workSpaceTitle(ws.getTitle())
                        .build())
                .toList();
    }

    public Long updateMyWorkSpace(WorkSpaceUpdateDto dto){
        WorkSpace tgWorkSpace = workSpaceRepository.findById(dto.getWorkSpaceId()).orElseThrow(()->new EntityNotFoundException("없는 워크스페이스"));
        tgWorkSpace.update(dto);
        return tgWorkSpace.getId();
    }

    public Long saveSection(SectionCreateDto dto){
        WorkSpace workSpace = workSpaceRepository.findById(dto.getWorkSpaceId()).orElseThrow(()->new EntityNotFoundException("없는 워크스페이스"));
        Section section = Section.builder().title(dto.getTitle()).userId(dto.getUserId()).workSpace(workSpace).build();
        Section savedSection = sectionRepository.save(section);
        return savedSection.getId();
    }

    public Long deleteMySection(SectionDeleteDto dto){
        Section section = sectionRepository.findById(dto.getSectionId()).orElseThrow(()->new EntityNotFoundException("없는 섹션"));
        if (!section.getUserId().equals(dto.getUserId())) {
            throw new IllegalArgumentException("본인의 섹션이 아닙니다.");
        }
        List<Channel> channels = channelRepository.findBySectionIdAndDel(section.getId(), Del.N);
        for (Channel channel : channels) {
            channel.delete();
        }
        sectionRepository.delete(section);
        return section.getId(); // 삭제된 섹션 ID 반환
    }

    public List<String> findMySection(FindMySectionDto dto){
        List<Section> sections = sectionRepository.findByUserIdAndWorkSpaceIdAndDel(dto.getUserId(), dto.getWorkSpaceId(),Del.N);

        return sections.stream()
                .map(Section::getTitle)
                .toList();
    }

    public Long updateSection(SectionupdateDto dto){
        Section section = sectionRepository.findById(dto.getSectionId()).orElseThrow(()-> new EntityNotFoundException("없는 섹션"));
        section.update(dto.getTitle());
        return section.getId();
    }

    public Long createChannel(ChannelCreateDto dto){
        Section section = sectionRepository.findById(dto.getSectionId()).orElseThrow(()->new EntityNotFoundException("없는 섹션"));
        Channel myChannel = Channel.builder().title(dto.getTitle()).userId(dto.getUserId()).section(section).build();
        Channel saveChannel = channelRepository.save(myChannel);
        ChannelParticipant channelParticipant = ChannelParticipant.builder().userId(dto.getUserId()).channel(saveChannel).build();
        return saveChannel.getId();
    }

    public Long deleteChannel(ChannelDeleteDto dto){
        Channel channel = channelRepository.findById(dto.getChannelId())
                .orElseThrow(() -> new NoSuchElementException("해당 채널이 존재하지 않습니다."));

        if (!channel.getUserId().equals(dto.getUserId())) {
            throw new SecurityException("채널을 삭제할 권한이 없습니다.");
        }

        channel.delete();
        return channel.getId();
    }

    public List<ChannelResDto> findChannel(FindMyChannelDto dto){
        Long userId = dto.getUserId();

        List<Channel> myChannels = channelRepository.findByUserIdAndDel(userId, Del.N);

        List<ChannelParticipant> joined = channelParticipantRepository.findByUserId(userId);
        List<Channel> joinedChannels = joined.stream()
                .map(ChannelParticipant::getChannel)
                .filter(channel -> !channel.getUserId().equals(userId)) // 내가 만든 채널과 중복 방지
                .filter(channel -> channel.getDel() == Del.N)
                .toList();

        List<ChannelResDto> result = new ArrayList<>();

        for (Channel channel : myChannels) {
            result.add(ChannelResDto.builder()
                    .channelId(channel.getId())
                    .title(channel.getTitle())
                    .owner(Owner.M)
                    .build());
        }

        for (Channel channel : joinedChannels) {
            result.add(ChannelResDto.builder()
                    .channelId(channel.getId())
                    .title(channel.getTitle())
                    .owner(Owner.Y)
                    .build());
        }
        return result;
    }

    public Long updateChannel(ChannelUpdateDto dto){
        Channel channel = channelRepository.findById(dto.getChannelId()).orElseThrow(()->new EntityNotFoundException("없는 채널"));
        Section section = sectionRepository.findById(dto.getSectionId()).orElseThrow(()->new EntityNotFoundException("없는 섹션"));
        channel.update(section,dto.getTitle());
        return channel.getId();
    }
}
