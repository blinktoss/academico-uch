package net.uch.i18n;

import org.junit.Test;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.Assert.assertEquals;

/**
 * Pins the phase-0 i18n foundation: the bundle registered in faces-config.xml
 * ({@code <application><resource-bundle>}) must keep today's exact rendered text.
 */
public class MessagesBundleTest {

    private static final String BASE_NAME = "net.uch.i18n.messages";

    @Test
    public void loginUsernameKeepsCurrentSpanishText() {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME);

        assertEquals("Usuario", bundle.getString("login.username"));
    }

    @Test
    public void bundleIsReadAsUtf8() {
        // JEP 226: the Java 11 runtime reads .properties bundles as UTF-8; a broken
        // encoding chain (editor, build copy) would corrupt these characters.
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME);

        assertEquals("áéíóúñÑ ¿¡", bundle.getString("i18n.encoding.probe"));
    }

    @Test
    public void everyLocaleResolvesToTheBaseBundle() {
        // Phase 0 ships only the base file: any client locale must fall back to it
        // so the rendered output stays identical for every user.
        assertEquals("Usuario",
                ResourceBundle.getBundle(BASE_NAME, new Locale("es")).getString("login.username"));
        assertEquals("Usuario",
                ResourceBundle.getBundle(BASE_NAME, Locale.ENGLISH).getString("login.username"));
    }
}
