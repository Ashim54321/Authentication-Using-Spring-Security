package com.example.test.config;

import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
@Service
public class JwtService {

    private static final String SECRET_KEY ="3777217A25432646294A404E635266556A586E3272357538782F413F4428472D";
    public String extractUserName(String token) {
        return extractClaim(token,Claims::getSubject);
    }

    // allow single claim which is passed
    public <T> T extractClaim( String token , Function<Claims,T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return  claimsResolver.apply(claims);
    }

    // generate token without claims
    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(),userDetails);
    }

    public String generateToken(
            Map<String,Object> extraClaims,
            UserDetails userDetails
    ){
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject( userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis())) // to check either the token is expired or to calculate token expiration date
                .setExpiration(new Date(System.currentTimeMillis()+ 100000 * 60 * 24)) // expiration time
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();

    }

    // check weather the token is valid or not && the username in the token is equal to usename in the details
    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUserName(token);
        return (username.equals(userDetails.getUsername()) &&  !isTokenExpired(token)  ); // weather the username is equals to userDetails && check weather the token is expired or not
    }

    //check weather the token is expired or not
    public boolean isTokenExpired( String token){
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }
    //  check the jwt token and decode th token
    private Claims  extractAllClaims(String token){
        return Jwts
                .parserBuilder() //
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // decoading the secreate key
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
