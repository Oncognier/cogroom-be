package oncog.cogroom.domain.auth.service;



import oncog.cogroom.domain.member.enums.Provider;

import static oncog.cogroom.domain.auth.dto.request.AuthRequestDTO.*;
import static oncog.cogroom.domain.auth.dto.response.AuthResponseDTO.*;

public interface AuthService {

    LoginResponseDTO login(LoginRequestDTO request);

    SignupResponseDTO signup(SignupRequestDTO request);

    Provider getProvider();
}
