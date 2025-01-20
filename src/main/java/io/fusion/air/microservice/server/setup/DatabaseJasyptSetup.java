/**
 * Copyright (c) 2025 Araf Karsh Hamid
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * <p>
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 * <p>
 * or (per the licensee's choosing)
 * <p>
 * under the terms of the Apache 2 License version 2.0
 * as published by the Apache Software Foundation.
 */
package io.fusion.air.microservice.server.setup;

// Jasypt
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
// Spring
import org.jasypt.iv.RandomIvGenerator;
import org.jasypt.salt.RandomSaltGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Base64;
/**
 * ms-springboot-334-vanilla / DatabaseJasyptSetup
 *
 * @author: Araf Karsh Hamid
 * @version: 0.1
 * @date: 2025-01-20T3:38 PM
 */
@Configuration
public class DatabaseJasyptSetup {

    @Value("${jasypt.encryptor.algorithm:PBEWithHmacSHA512AndAES_256}")
    private String encryptAlgo;

    @Bean("jasyptStringEncryptor")
    public StringEncryptor stringEncryptor() {
        // Get the Encryption Key
        String rawKey = validateKey(System.getenv("JASYPT_ENCRYPTOR_PASSWORD"));
        // Check if the key is Base64-encoded
        String plainKey = isBase64(rawKey) ? new String(Base64.getDecoder().decode(rawKey)) : rawKey;
        // Create and configure the encryptor
        System.setProperty("jasypt.encryptor.password", plainKey);
        StandardPBEStringEncryptor decryptor = new StandardPBEStringEncryptor();
        decryptor.setPassword(plainKey);         // Use the decoded key
        decryptor.setAlgorithm(encryptAlgo);   // Adjust algorithm as needed
        decryptor.setIvGenerator(new RandomIvGenerator());
        decryptor.setSaltGenerator(new RandomSaltGenerator());
        return decryptor;
    }

    /**
     * Validate the RawKey
     * This method should throw an Exception if the  Key is NULL.
     * Default value is only for Demo Purpose
     * @param rawKey
     * @return
     */
    private String validateKey(String rawKey) {
        return (rawKey != null) ? rawKey : "RnVzaW9uLldhdGVyQDIwMzA=";
    }

    /**
     * Check if the Incoming Value is Base 64 or Not
     * @param value
     * @return
     */
    private boolean isBase64(String value) {
        try {
            // Try decoding; if it succeeds and the decoded bytes re-encoded match, it is Base64
            byte[] decoded = Base64.getDecoder().decode(value);
            return Base64.getEncoder().encodeToString(decoded).equals(value);
        } catch (IllegalArgumentException e) {
            // If decoding fails, it's not Base64
            return false;
        }
    }
}
