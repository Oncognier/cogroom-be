package oncog.cogroom.domain.auth.service;



import oncog.cogroom.domain.member.enums.Provider;

import static oncog.cogroom.domain.auth.dto.request.AuthRequest.*;
import static oncog.cogroom.domain.auth.dto.response.AuthResponse.*;

public interface AuthService {

    LoginResultDTO login(LoginRequestDTO request);

    SignupResultDTO signup(SignupDTO request);

    Provider getProvider();
}
