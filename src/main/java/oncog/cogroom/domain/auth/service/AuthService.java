package oncog.cogroom.domain.auth.service;



import lombok.With;
import oncog.cogroom.domain.member.enums.Provider;

import static oncog.cogroom.domain.auth.dto.request.AuthRequest.*;
import static oncog.cogroom.domain.auth.dto.response.AuthResponse.*;

public interface AuthService {

    LoginResultDTO login(LoginDTO request);

    SignupResultDTO signup(SignupDTO request);

    void withdraw(WithdrawDTO request, String accessToken);
    Provider getProvider();
}
