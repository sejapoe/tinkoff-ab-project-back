package edu.tinkoff.ninjamireaclone.service;

import edu.tinkoff.ninjamireaclone.utils.jwt.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;

    /**
     * Generate JWT based on user information
     * @param userDetails core info of user
     * @param username entered name
     * @param password entered password
     * @return token
     */
    public String createAuthToken(UserDetails userDetails, String username, String password) {
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username, password));
        return jwtTokenUtils.generateToken(userDetails);
    }
}
