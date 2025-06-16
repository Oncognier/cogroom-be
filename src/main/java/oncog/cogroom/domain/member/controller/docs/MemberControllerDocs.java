package oncog.cogroom.domain.member.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import oncog.cogroom.domain.auth.exception.AuthErrorCode;
import oncog.cogroom.domain.daily.dto.response.DailyQuestionResponseDTO;
import oncog.cogroom.domain.daily.exception.DailyErrorCode;
import oncog.cogroom.domain.member.dto.MemberRequestDTO;
import oncog.cogroom.domain.member.dto.MemberResponseDTO;
import oncog.cogroom.domain.member.exception.MemberErrorCode;
import oncog.cogroom.global.common.response.ApiResponse;
import oncog.cogroom.global.common.response.code.ApiErrorCode;
import oncog.cogroom.global.exception.swagger.ApiErrorCodeExamples;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Member", description = "Member 관련 API")
public interface MemberControllerDocs {

    @ApiErrorCodeExamples(
            value = {MemberErrorCode.class, AuthErrorCode.class},
            include = {"TOKEN_INVALID_ERROR","TOKEN_EXPIRED_ERROR", "MEMBER_NOT_FOUND_ERROR"})
    @Operation(summary = "사용자 정보 조회 API", description = "사용자 정보 수정을 위해 사용자 정보를 조회합니다. ")
    ResponseEntity<ApiResponse<MemberResponseDTO.MemberInfoDTO>> getMemberInfo();

    @ApiErrorCodeExamples(
            value = {MemberErrorCode.class, AuthErrorCode.class, ApiErrorCode.class},
            include = {"TOKEN_INVALID_ERROR","TOKEN_EXPIRED_ERROR", "MEMBER_NOT_FOUND_ERROR",
                    "PHONENUMBER_PATTERN_ERROR", "PASSWORD_PATTERN_ERROR" ,"NICKNAME_DUPLICATE_ERROR",
            "EMPTY_FILED_ERROR"})
    @Operation(summary = "사용자 정보 수정 API", description = "사용자 정보를 수정합니다. ")
    ResponseEntity<ApiResponse<Void>> updateMemberInfo(@RequestBody @Valid MemberRequestDTO.MemberInfoUpdateDTO request);

    @ApiErrorCodeExamples(
            value = {ApiErrorCode.class, MemberErrorCode.class},
            include = {"NICKNAME_DUPLICATE_ERROR", "EMPTY_FILED_ERROR"})
    @Operation(summary = "닉네임 중복검사 API", description = "닉네임 중복 검사를 진행합니다.")
    ResponseEntity<ApiResponse<Boolean>> existNickname(@RequestBody @Valid MemberRequestDTO.ExistNicknameDTO request);

    @ApiErrorCodeExamples(
            value = {AuthErrorCode.class, MemberErrorCode.class},
            include = {"TOKEN_INVALID_ERROR","TOKEN_EXPIRED_ERROR", "MEMBER_NOT_FOUND_ERROR"}
    )
    @Operation(summary = "사용자 정보 조회 API(닉네임,사진)", description = "사용자의 닉네임과 이미지 조회를 진행합니다.")
    ResponseEntity<ApiResponse<MemberResponseDTO.MemberSummaryDTO>> getMemberSummary();


    @ApiErrorCodeExamples(
            value = {AuthErrorCode.class, MemberErrorCode.class},
            include = {"TOKEN_INVALID_ERROR","TOKEN_EXPIRED_ERROR", "MEMBER_NOT_FOUND_ERROR"}
    )
    @Operation(summary = "사용자 정보 조회 API(마이페이지)", description = "마이페이지 홈에서 필요한 사용자의 정보 조회를 진행합니다.")
    ResponseEntity<ApiResponse<MemberResponseDTO.MemberMyPageInfoDTO>> getMemberInfoForMyPage();

    @ApiErrorCodeExamples(
            value = {AuthErrorCode.class, MemberErrorCode.class, DailyErrorCode.class},
            include = {"TOKEN_INVALID_ERROR","TOKEN_EXPIRED_ERROR", "MEMBER_NOT_FOUND_ERROR",
             "QNA_NOT_FOUND_ERROR"}
    )
    @Operation(summary = "데일리 질문 및 답변 조회", description = "마이페이지 내에서 사용자에게 할당된 데일리 질문과 그에 대한 답변을 조회합니다.")
    public ResponseEntity<ApiResponse<List<DailyQuestionResponseDTO.AssignedQuestionWithAnswerDTO>>> getDailyQuestionAndAnswer();
}
