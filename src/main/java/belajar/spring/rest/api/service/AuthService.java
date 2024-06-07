package belajar.spring.rest.api.service;

import belajar.spring.rest.api.entity.User;
import belajar.spring.rest.api.model.LoginUserRequest;
import belajar.spring.rest.api.model.TokenResponse;
import belajar.spring.rest.api.repository.UserRepository;
import belajar.spring.rest.api.security.BCrypt;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationService validationService;

    @Transactional
    public TokenResponse login(LoginUserRequest request){
        validationService.validate(request);

        User user = userRepository.findById(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or password wrong"));

        if(BCrypt.checkpw(request.getPassword(), user.getPassword())){
            user.setToken(UUID.randomUUID().toString());
            user.setTokenExpiredAt(next30Days());

            userRepository.save(user);

            return TokenResponse.builder()
                    .token(user.getToken()).
                    expiredAt(user.getTokenExpiredAt())
                    .build();
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,  "Username or password wrong");
        }
    }

    private Long next30Days(){
        return System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 30);
    }
}
