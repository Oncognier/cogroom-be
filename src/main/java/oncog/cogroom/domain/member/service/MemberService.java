package oncog.cogroom.domain.member.service;

import lombok.RequiredArgsConstructor;
import oncog.cogroom.domain.member.dto.MemberRequestDTO;
import oncog.cogroom.domain.member.entity.Member;
import oncog.cogroom.domain.member.exception.MemberErrorCode;
import oncog.cogroom.domain.member.exception.MemberException;
import oncog.cogroom.domain.member.repository.MemberRepository;
import oncog.cogroom.global.common.service.BaseService;
import oncog.cogroom.global.exception.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static oncog.cogroom.domain.member.dto.MemberResponseDTO.*;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService extends BaseService {

    private final MemberRepository memberRepository;

    public MemberInfoDTO findMemberInfo() {
        Member member = getMember();

        return MemberInfoDTO.builder()
                .email(member.getEmail())
                .description(member.getDescription())
                .imageUrl(member.getProfileImageUrl())
                .nickname(member.getNickname())
                .phoneNumber(member.getPhoneNumber())
                .build();
    }

    public MemberSummaryDTO findMemberSummary() {
        Member member = getMember();

        return MemberSummaryDTO.builder()
                .imageUrl(member.getProfileImageUrl())
                .nickname(member.getNickname())
                .build();
    }

    public void updateMemberInfo(MemberRequestDTO.MemberInfoUpdateDTO request){
        Member member = getMember();

        member.updateMemberInfo(request);
    }

    public boolean existNickname(MemberRequestDTO.ExistNicknameDTO request) {

        if(memberRepository.existsByNickname(request.getNickname())){
            throw new MemberException(MemberErrorCode.DUPLICATE_USER_NICKNAME);
        }

        return false;
    }

    public boolean isExist(String memberId) {
        return memberRepository.existsById(Long.valueOf(memberId));
    }


    }
