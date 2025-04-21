package com.Synchrome.workspace.space.service;

import com.Synchrome.workspace.common.InviteCodeGenerator;
import com.Synchrome.workspace.common.S3Uploader;
import com.Synchrome.workspace.space.domain.*;
import com.Synchrome.workspace.space.domain.ENUM.Del;
import com.Synchrome.workspace.space.domain.ENUM.Owner;
import com.Synchrome.workspace.space.dtos.channelDtos.*;
import com.Synchrome.workspace.space.dtos.sectionDtos.*;
import com.Synchrome.workspace.space.dtos.workSpaceDtos.*;
import com.Synchrome.workspace.space.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

import java.util.*;

@Service
@Transactional
public class WorkSpaceService {
    private final WorkSpaceRepository workSpaceRepository;
    private final SectionRepository sectionRepository;
    private final ChannelRepository channelRepository;
    private final WorkSpaceParticipantRepository workSpaceParticipantRepository;
    private final ChannelParticipantRepository channelParticipantRepository;
    private final S3Uploader s3Uploader;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public WorkSpaceService(WorkSpaceRepository workSpaceRepository, SectionRepository sectionRepository, ChannelRepository channelRepository, WorkSpaceParticipantRepository workSpaceParticipantRepository, ChannelParticipantRepository channelParticipantRepository, S3Uploader s3Uploader) {
        this.workSpaceRepository = workSpaceRepository;
        this.sectionRepository = sectionRepository;
        this.channelRepository = channelRepository;
        this.workSpaceParticipantRepository = workSpaceParticipantRepository;
        this.channelParticipantRepository = channelParticipantRepository;
        this.s3Uploader = s3Uploader;
    }

    public Long saveWorkSpace(WorkSpaceCreateDto dto) throws IOException {
        String logoUrl = null;
        MultipartFile logoFile = dto.getLogo();
        if (logoFile != null && !logoFile.isEmpty()) {
            logoUrl = s3Uploader.uploadFile(logoFile);
        }
        String code;
        do {
            code = InviteCodeGenerator.generate(6); // 예: 6자리 영문+숫자
        } while (workSpaceRepository.existsByInviteUrl(code));

        WorkSpace workSpace = WorkSpace.builder()
                .title(dto.getTitle())
                .userId(dto.getUserId())
                .inviteUrl(code)
                .logo(logoUrl)
                .build();

        Section commonSection = Section.builder()
                .title("공통 섹션")
                .userId(dto.getUserId())
                .workSpace(workSpace)
                .owner(Owner.C)
                .build();

        Channel channel1 = Channel.builder()
                .title("공지사항")
                .userId(dto.getUserId())
                .section(commonSection)
                .owner(Owner.C)
                .build();

        Channel channel2 = Channel.builder()
                .title("자유게시판")
                .userId(dto.getUserId())
                .section(commonSection)
                .owner(Owner.C)
                .build();

        commonSection.getChannels().add(channel1);
        commonSection.getChannels().add(channel2);

        workSpace.getSections().add(commonSection);

        WorkSpace saveWorkSpace = workSpaceRepository.save(workSpace);
        WorkSpaceParticipant participant = WorkSpaceParticipant.builder()
                .userId(dto.getUserId())
                .workSpace(saveWorkSpace)
                .build();

        workSpaceParticipantRepository.save(participant);
        return saveWorkSpace.getId();
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

        List<WorkSpace> workSpaces = optionalWorkSpaces.orElse(new ArrayList<>());

        List<WorkSpaceParticipant> participants = workSpaceParticipantRepository.findByUserIdAndDel(userId, Del.N);
        for (WorkSpaceParticipant participant : participants) {
            WorkSpace ws = participant.getWorkSpace();
            if (ws.getDel() == Del.N && !workSpaces.contains(ws)) {
                workSpaces.add(ws);
            }
        }

        return workSpaces.stream()
                .map(ws -> MyWorkSpaceResDto.builder()
                        .workSpaceId(ws.getId())
                        .workSpaceTitle(ws.getTitle())
                        .logo(ws.getLogo())
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
        section.delete();
        return section.getId(); // 삭제된 섹션 ID 반환
    }

    public List<MySectionResDto> findMySection(FindMySectionDto dto){
        List<Section> sections = sectionRepository.findByUserIdAndWorkSpaceIdAndDel(dto.getUserId(), dto.getWorkSpaceId(),Del.N);

        return sections.stream()
                .map(section -> MySectionResDto.builder()
                        .sectionId(section.getId())
                        .title(section.getTitle())
                        .build())
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
        channelParticipantRepository.save(channelParticipant);
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
                    .sectionId(channel.getSection().getId())
                    .title(channel.getTitle())
                    .owner(Owner.M)
                    .build());
        }

        for (Channel channel : joinedChannels) {
            result.add(ChannelResDto.builder()
                    .channelId(channel.getId())
                    .sectionId(channel.getSection().getId())
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


    public List<WorkSpaceInfoDto> getMyWorkspaceSectionAndChannels(GetWorkSpaceInfoDto dto) {
        Long workSpaceId = dto.getWorkSpaceId();
        Long userId = dto.getUserId();

        WorkSpace selectedWorkSpace = workSpaceRepository.findById(workSpaceId)
                .orElseThrow(() -> new EntityNotFoundException("없는 워크스페이스"));

        // ✅ 1. 내 개인 섹션 (Owner.U)만 조회
        List<Section> mySections = sectionRepository.findByWorkSpaceIdAndUserIdAndDel(workSpaceId, userId, Del.N).stream()
                .filter(section -> section.getOwner() == Owner.U)
                .toList();

        // ✅ 2. 내 섹션에서 내가 만든 채널만 매핑
        List<WorkSpaceInfoDto> mySectionList = mySections.stream()
                .map(section -> {
                    List<ChannelResDto> myChannels = section.getChannels().stream()
                            .filter(c -> c.getUserId().equals(userId) && c.getDel() == Del.N)
                            .filter(c -> c.getOwner() == Owner.U)
                            .map(channel -> ChannelResDto.builder()
                                    .channelId(channel.getId())
                                    .sectionId(section.getId())
                                    .title(channel.getTitle())
                                    .owner(Owner.U)
                                    .build())
                            .toList();

                    return WorkSpaceInfoDto.builder()
                            .workspaceId(selectedWorkSpace.getId())
                            .workspaceTitle(selectedWorkSpace.getTitle())
                            .sectionId(section.getId())
                            .title(section.getTitle())
                            .channels(myChannels)
                            .build();
                })
                .toList();

        // ✅ 3. 공통 채널
        List<ChannelResDto> commonChannelDtos = channelRepository.findBySection_WorkSpaceIdAndDel(workSpaceId, Del.N).stream()
                .filter(channel -> channel.getOwner() == Owner.C)
                .map(channel -> ChannelResDto.builder()
                        .channelId(channel.getId())
                        .sectionId(channel.getSection().getId())
                        .title(channel.getTitle())
                        .owner(Owner.C)
                        .build())
                .toList();

        // ✅ 4. 초대받은 채널
        List<ChannelResDto> invitedChannelDtos = channelParticipantRepository.findByUserId(userId).stream()
                .map(ChannelParticipant::getChannel)
                .filter(channel -> !channel.getUserId().equals(userId)) // 내가 만든 거 제외
                .filter(channel -> channel.getDel() == Del.N)
                .filter(channel -> channel.getOwner() == Owner.U) // 공통 아닌 개인 채널 중 초대받은 것만
                .filter(channel -> channel.getSection().getWorkSpace().getId().equals(workSpaceId))
                .map(channel -> ChannelResDto.builder()
                        .channelId(channel.getId())
                        .sectionId(channel.getSection().getId())
                        .title(channel.getTitle())
                        .owner(Owner.U)
                        .build())
                .toList();

        // ✅ 5. 공통 섹션 구성
        List<ChannelResDto> allSharedChannels = new ArrayList<>();
        allSharedChannels.addAll(commonChannelDtos);
        allSharedChannels.addAll(invitedChannelDtos);

        Section commonSection = sectionRepository.findByWorkSpaceIdAndOwnerAndDel(workSpaceId, Owner.C, Del.N)
                .orElse(null);

        if (!allSharedChannels.isEmpty()) {
            WorkSpaceInfoDto sharedSection = WorkSpaceInfoDto.builder()
                    .workspaceId(selectedWorkSpace.getId())
                    .workspaceTitle(selectedWorkSpace.getTitle())
                    .sectionId(commonSection != null ? commonSection.getId() : null) // ✅ 여기!
                    .title("공통")
                    .channels(allSharedChannels)
                    .build();

            mySectionList = new ArrayList<>(mySectionList);
            mySectionList.add(sharedSection);
        }


        return mySectionList;
    }

    public void inviteUsersToWorkspace(Long workspaceId, List<Long> userIds) {
        WorkSpace workspace = workSpaceRepository.findById(workspaceId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 워크스페이스입니다."));

        List<Long> existingUserIds = workSpaceParticipantRepository
                .findByWorkSpaceIdAndDel(workspaceId, Del.N).stream()
                .map(WorkSpaceParticipant::getUserId)
                .toList();

        List<Long> duplicated = userIds.stream()
                .filter(existingUserIds::contains)
                .toList();

        if (!duplicated.isEmpty()) {
            throw new IllegalArgumentException("이미 참여 중인 사용자입니다: " + duplicated);
        }

        List<WorkSpaceParticipant> newParticipants = userIds.stream()
                .map(userId -> WorkSpaceParticipant.builder()
                        .userId(userId)
                        .workSpace(workspace)
                        .build())
                .toList();

        workSpaceParticipantRepository.saveAll(newParticipants);
    }



    public Long getWorkspaceIdByInviteUrl(String inviteUrl) {
        WorkSpace workspace = workSpaceRepository.findByInviteUrlAndDel(inviteUrl, Del.N)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "초대코드가 유효하지 않습니다."));

        return workspace.getId();
    }

    public void acceptInvite(Long workspaceId, Long userId) {
        WorkSpace workspace = workSpaceRepository.findByIdAndDel(workspaceId, Del.N)
                .orElseThrow(() -> new RuntimeException("워크스페이스가 존재하지 않거나 삭제되었습니다."));

        boolean alreadyJoined = workSpaceParticipantRepository.existsByWorkSpaceIdAndUserIdAndDel(workspaceId, userId, Del.N);
        if (alreadyJoined) return;

        WorkSpaceParticipant participant = WorkSpaceParticipant.builder()
                .userId(userId)
                .workSpace(workspace)
                .build();

        workSpaceParticipantRepository.save(participant);
    }

    public void inviteUserToChannel(Long channelId, List<Long> userIds) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 채널입니다."));

        List<Long> existingUserIds = channelParticipantRepository
                .findByChannelIdAndDel(channelId, Del.N).stream()
                .map(ChannelParticipant::getUserId)
                .toList();

        List<Long> duplicated = userIds.stream()
                .filter(existingUserIds::contains)
                .toList();

        if (!duplicated.isEmpty()) {
            throw new IllegalArgumentException("이미 채널에 참여 중인 사용자입니다: " + duplicated);
        }

        List<ChannelParticipant> newParticipants = userIds.stream()
                .map(userId -> ChannelParticipant.builder()
                        .userId(userId)
                        .channel(channel)
                        .build())
                .toList();

        channelParticipantRepository.saveAll(newParticipants);
    }

}
