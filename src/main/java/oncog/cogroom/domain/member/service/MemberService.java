package oncog.cogroom.domain.member.service;

import lombok.RequiredArgsConstructor;
import oncog.cogroom.domain.member.dto.MemberRequestDTO;
import oncog.cogroom.domain.member.entity.Member;
import oncog.cogroom.domain.member.repository.MemberRepository;
import oncog.cogroom.global.common.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static oncog.cogroom.domain.member.dto.MemberResponseDTO.*;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService extends BaseService {

    private final MemberRepository memberRepository;

    public MemberInfoDTO findMemberInfo() {
        Long memberId = getMemberId();

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
        Long memberId = getMemberId();

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("member not found"));

        if(!request.getPhoneNumber().matches("^01[016789]-\\d{3,4}-\\d{4}$")){
            throw new IllegalArgumentException("전화번호 형식 오류");
        }

        member.updateMemberInfo(request);
    }

    public boolean existNickname(MemberRequestDTO.ExistNicknameDTO request) {

        if(memberRepository.existsByNickname(request.getNickname())){
            throw new IllegalArgumentException("이미 존재하는 닉네임");
        }

        return false;
    }

    public boolean isExist(String memberId) {
        return memberRepository.existsById(Long.valueOf(memberId));
    }


    }
