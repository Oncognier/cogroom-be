package oncog.cogroom.domain.member.service;

import lombok.RequiredArgsConstructor;
import oncog.cogroom.domain.member.dto.MemberRequestDTO;
import oncog.cogroom.domain.member.entity.Member;
import oncog.cogroom.domain.member.repository.MemberRepository;
import oncog.cogroom.global.security.jwt.JwtProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static oncog.cogroom.domain.member.dto.MemberResponseDTO.*;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    public MemberInfoDTO findMemberInfo() {
        Long memberId = jwtProvider.extractMemberId();

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("member not found"));

        return MemberInfoDTO.builder()
                .email(member.getEmail())
                .description(member.getDescription())
                .imgUrl(member.getProfileImageUrl()) // preSignedUrl 방식 적용 필요
                .nickname(member.getNickname())
                .phoneNumber(member.getPhoneNumber())
                .build();
    }

    public void updateMemberInfo(MemberRequestDTO.MemberInfoUpdateDTO request){
        Long memberId = jwtProvider.extractMemberId();

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("member not found"));

        member.updateMemberInfo(request);
    }

    public boolean isExist(String memberId) {
        return memberRepository.existsById(Long.valueOf(memberId));
    }


    }
