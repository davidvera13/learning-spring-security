package springboot.app.brewery.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.*;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.util.DigestUtils;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PasswordEncodingTest {
    private static final String ADMIN_PASSWORD = "adminPassword";
    private static final String USER_PASSWORD = "me";
    private static final String CUSTOMER_PASSWORD = "tiger";

    @Test
    void md5HashingPasswordEncoder() {
        // not recommended encryption method
        // value will always be the same....
        System.out.println(DigestUtils.md5DigestAsHex(ADMIN_PASSWORD.getBytes()));
        System.out.println(DigestUtils.md5DigestAsHex(ADMIN_PASSWORD.getBytes()));
        System.out.println(DigestUtils.md5DigestAsHex(ADMIN_PASSWORD.getBytes()));

        // salt
        String saltedPassword = ADMIN_PASSWORD + "mySaltValue";
        System.out.println("Adding salt");
        System.out.println(DigestUtils.md5DigestAsHex(saltedPassword.getBytes()));
        System.out.println(DigestUtils.md5DigestAsHex(saltedPassword.getBytes()));
    }

    @Test
    void noop() {
        // not recommended
        PasswordEncoder noop = NoOpPasswordEncoder.getInstance();
        System.out.println(noop.encode(ADMIN_PASSWORD));
    }

    @Test
    void ldapPasswordEncoder() {
        // not recommended too
        PasswordEncoder ldap = new LdapShaPasswordEncoder();
        System.out.println(ldap.encode(ADMIN_PASSWORD));
        System.out.println(ldap.encode(ADMIN_PASSWORD));
        String encodedPwd = ldap.encode(ADMIN_PASSWORD);
        assertTrue(ldap.matches(ADMIN_PASSWORD, encodedPwd));
    }

    @Test
    void sha256PasswordEncoder() {
        // previous default encoder
        PasswordEncoder sha = new StandardPasswordEncoder();
        System.out.println(sha.encode(ADMIN_PASSWORD));
        System.out.println(sha.encode(ADMIN_PASSWORD));
    }

    @Test
    void bCryptPasswordEncoder() {
        // previous default encoder: default strength is 10
        PasswordEncoder bCrypt = new BCryptPasswordEncoder();
        System.out.println(bCrypt.encode(USER_PASSWORD));
        System.out.println(bCrypt.encode(USER_PASSWORD));

        // 15 is very long to generate
        // bCrypt = new BCryptPasswordEncoder(15);
        // System.out.println(bCrypt.encode(PASSWORD));
        // System.out.println(bCrypt.encode(PASSWORD));
    }

    @Test
    void pbkdf2PasswordEncoder() {
        PasswordEncoder pbkdf2 = new Pbkdf2PasswordEncoder();
        System.out.println(pbkdf2.encode(CUSTOMER_PASSWORD));
        System.out.println(pbkdf2.encode(CUSTOMER_PASSWORD));

    }
    @Test
    void argon2PasswordEncoder() {
        PasswordEncoder argon2 = new Argon2PasswordEncoder();
        System.out.println(argon2.encode(CUSTOMER_PASSWORD));
        System.out.println(argon2.encode(CUSTOMER_PASSWORD));
    }

    @Test
    void scryptPasswordEncoder() {
        PasswordEncoder sCrypt = new SCryptPasswordEncoder();
        System.out.println(sCrypt.encode(CUSTOMER_PASSWORD));
        System.out.println(sCrypt.encode(CUSTOMER_PASSWORD));
    }
}