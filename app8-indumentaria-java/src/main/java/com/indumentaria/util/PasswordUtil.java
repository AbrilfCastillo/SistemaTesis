package com.indumentaria.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilidad para hashear contraseñas antes de guardarlas.
 * Se usa SHA-256 sobre el texto de la contraseña directamente.
 */
public class PasswordUtil {

    public static String hashear(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    sb.append('0');
                }
                sb.append(hex);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 siempre esta disponible en el JDK estandar
            throw new RuntimeException(e);
        }
    }

    public static boolean coincide(String passwordIngresada, String hashGuardado) {
        return hashear(passwordIngresada).equals(hashGuardado);
    }
}
