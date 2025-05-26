package oncog.cogroom.domain.auth.service;


import oncog.cogroom.domain.auth.dto.response.SocialResponseDTO;
import oncog.cogroom.domain.member.enums.Provider;

public abstract class AbstractSocialAuthService {

    protected abstract SocialResponseDTO.LoginResponseDTO login(String code);
    protected abstract Provider getProvider();

}
