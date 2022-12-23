package springboot.app.brewery.repositories.security;

import com.warrenstrange.googleauth.ICredentialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import springboot.app.brewery.domain.security.UserEntity;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class GoogleCredentialRepository implements ICredentialRepository {

    private final UserRepository userRepository;

    @Override
    public String getSecretKey(String userName) {
        UserEntity user = userRepository.findByUsername(userName)
                .orElseThrow();

        return user.getGoogle2FaSecret();
    }

    @Override
    public void saveUserCredentials(String userName,
                                    String secretKey,
                                    int validationCode,
                                    List<Integer> scratchCodes) {
        UserEntity user = userRepository.findByUsername(userName)
                .orElseThrow();
        user.setGoogle2FaSecret(secretKey);
        user.setUseGoogle2fa(true);
        userRepository.save(user);
    }
}
