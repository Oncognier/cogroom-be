package oncog.cogroom.domain.auth.service;



import oncog.cogroom.domain.member.enums.Provider;

import static oncog.cogroom.domain.auth.dto.request.AuthRequestDTO.*;
import static oncog.cogroom.domain.auth.dto.response.AuthResponseDTO.*;

public interface AuthService {

    LoginResultDTO login(LoginRequestDTO request);

    SignupResultDTO signup(SignupDTO request);

    Provider getProvider();
}
