package bookstore.bookstore.service;

import bookstore.bookstore.dto.converter.UserDtoConvertor;
import bookstore.bookstore.dto.request.LoginRequest;
import bookstore.bookstore.dto.request.SignUpRequest;
import bookstore.bookstore.dto.response.TokenResponseDto;
import bookstore.bookstore.dto.response.UserResponseDto;
import bookstore.bookstore.exception.GeneralException;
import bookstore.bookstore.model.Role;
import bookstore.bookstore.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder encoder;
    private final UserService userService;
    private final TokenService tokenService;

    public TokenResponseDto login(LoginRequest loginRequest) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
            return TokenResponseDto.builder()
                    .token(tokenService.generateToken(authentication))
                    .user(UserDtoConvertor.convertToUserResponseDto(userService.findUserByUsername(loginRequest.getUsername())))
                    .build();
        }catch (final BadCredentialsException e) {
            throw new GeneralException("Incorrect username or password", HttpStatus.UNAUTHORIZED);
        }
    }

    public UserResponseDto signup(SignUpRequest signUpRequest) {

        var isAllReadyRegistired= userService.existsByUsername(signUpRequest.getUsername());

        if(isAllReadyRegistired){
            throw new GeneralException("Username is already used",HttpStatus.FOUND);
        }

        var user = User.builder()
                .username(signUpRequest.getUsername())
                .password(encoder.encode(signUpRequest.getPassword()))
                .role(Role.USER)
                .email(signUpRequest.getEmail())
                .build();

        return UserDtoConvertor.convertToUserResponseDto(userService.saveUser(user));
    }
}