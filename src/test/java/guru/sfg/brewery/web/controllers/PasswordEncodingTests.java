package guru.sfg.brewery.web.controllers;

import jdk.jfr.Description;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

public class PasswordEncodingTests {

    static final String PASSWORD = "password";

    @Test
    @Description("NoOp Password Encoder")
    void testNoOp(){
        PasswordEncoder noOp = NoOpPasswordEncoder.getInstance();
        System.out.println(noOp.encode(PASSWORD));
    }



    @Test
    @Description("MD5 Hash and Password Salt")
    void hashingExample(){
        System.out.println(DigestUtils.md5DigestAsHex(PASSWORD.getBytes()));
        System.out.println(DigestUtils.md5DigestAsHex(PASSWORD.getBytes(StandardCharsets.UTF_8)));
        String salted = PASSWORD + "ThisIsMySaltValue";
        System.out.println(DigestUtils.md5DigestAsHex(salted.getBytes(StandardCharsets.UTF_8)));
    }
}
