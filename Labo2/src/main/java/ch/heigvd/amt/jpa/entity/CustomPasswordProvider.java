package ch.heigvd.amt.jpa.entity;

import jakarta.xml.bind.DatatypeConverter;

import org.wildfly.security.password.Password;
import org.wildfly.security.password.interfaces.SimpleDigestPassword;

import io.quarkus.security.jpa.PasswordProvider;

public class CustomPasswordProvider implements PasswordProvider {
    @Override
    public Password getPassword(String passwordInDatabase) {
        byte[] digest = DatatypeConverter.parseHexBinary(passwordInDatabase);

        // Let the security runtime know that this passwordInDatabase is hashed by using the SHA256 hashing algorithm
        return SimpleDigestPassword.createRaw(SimpleDigestPassword.ALGORITHM_SIMPLE_DIGEST_SHA_1, digest);
    }
}