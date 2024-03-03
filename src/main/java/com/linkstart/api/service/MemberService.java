package com.linkstart.api.service;

import com.linkstart.api.exception.NotFoundException;
import com.linkstart.api.model.dto.PlaylistDto;
import com.linkstart.api.model.entity.Guild;
import com.linkstart.api.model.entity.Member;
import com.linkstart.api.model.dto.MemberDto;
import com.linkstart.api.repo.GuildRepo;
import com.linkstart.api.repo.MemberRepo;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class MemberService {
    private final MemberRepo memberRepo;
    private final PlaylistService playlistService;
    private final GuildRepo guildRepo;
    private final ModelMapper modelMapper;

    public MemberService(
            MemberRepo memberRepo,
            PlaylistService playlistService,
            ModelMapper modelMapper,
            GuildRepo guildRepo) {
        this.memberRepo = memberRepo;
        this.playlistService = playlistService;
        this.modelMapper = modelMapper;
        this.guildRepo = guildRepo;
    }

    public List<MemberDto> getMember() {
        List<Member> members = memberRepo.findAll();
        return members
                .stream()
                .map(user -> modelMapper.map(user, MemberDto.class))
                .toList();
    }

    public List<MemberDto> birthdayIsToday() {
        List<MemberDto> members = getMember();
        LocalDate today = LocalDate.now();

        List<MemberDto> discordUsersBirthdayIsNow = new ArrayList<>();
        for (MemberDto member : members) {
            if (member.getBirthday() == null) continue;

            LocalDate userBirthday = member.getBirthday();
            // To compare dates, we set them to the same year
            userBirthday = userBirthday.withYear(today.getYear());

            if (userBirthday.equals(today)) {
                discordUsersBirthdayIsNow.add(member);
            }
        }
        return discordUsersBirthdayIsNow;
    }

    public MemberDto getMemberById(String id) {
        Member member = memberRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(id, Member.class));

        return modelMapper.map(member, MemberDto.class);
    }

    public MemberDto createMember(MemberDto memberDto) {
        Optional<Guild> guild = guildRepo.findById(memberDto.getGuildId());
        if (guild.isEmpty())
            throw new NotFoundException(memberDto.getGuildId(), Guild.class);

        Member member = modelMapper.map(memberDto, Member.class);

        memberRepo.save(member);
        return modelMapper.map(member, MemberDto.class);
    }

    public MemberDto updateMember(String id, MemberDto memberDto) {
        memberRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(id, Member.class));

        Member updatedMember = memberRepo.save(modelMapper.map(memberDto, Member.class));
        return modelMapper.map(updatedMember, MemberDto.class);
    }

    public void deleteMember(String id) {
        Member member = memberRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(id, Member.class));

        memberRepo.delete(member);
    }

    public List<PlaylistDto> getPlaylistsByMember(String id) {
        MemberDto memberDto = this.getMemberById(id);
        Member member = modelMapper.map(memberDto, Member.class);
        return playlistService.getPlaylistsByDiscordUser(member);
    }
}