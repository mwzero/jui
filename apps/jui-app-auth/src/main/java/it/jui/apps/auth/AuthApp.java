package it.jui.apps.auth;

import it.jui.JuiApp;
import it.jui.UIContext;
import it.jui.app.JuiProvider;
import it.jui.auth.GoogleOAuthConfig;
import it.jui.server.JuiServer;

/** Google authentication plus per-tab application state behind an explicit access check. */
public final class AuthApp implements JuiApp {
    @Override
    public void run(UIContext ui) {
        ui.title("Area personale");
        if (!ui.authenticated()) {
            ui.text("Accedi con Google per visualizzare il tuo profilo e scrivere una nota privata.");
            ui.googleLoginButton("Accedi con Google");
            return;
        }

        var user = ui.authUser().orElseThrow();
        ui.header("Profilo");
        if (user.name() != null && !user.name().isBlank()) ui.text(user.name());
        if (user.email() != null && !user.email().isBlank()) ui.text(user.email());
        ui.caption("La nota è conservata in memoria per questa scheda e viene cancellata al logout.");
        ui.form(PrivateNote.class).ifPresent(note -> {
            ui.setValue("auth-demo:last-note", note.message());
            ui.success("Nota salvata");
        });
        String note = ui.getValue("auth-demo:last-note", "");
        if (!note.isEmpty()) {
            ui.subheader("Ultima nota");
            ui.text(note);
        }
        ui.logoutButton("Esci");
    }

    public static void main(String[] args) throws Exception {
        GoogleOAuthConfig config;
        try {
            config = GoogleOAuthConfig.fromEnv();
        } catch (IllegalArgumentException e) {
            System.err.println("Configurazione Google OAuth non valida: imposta GOOGLE_CLIENT_ID, "
                    + "GOOGLE_CLIENT_SECRET e GOOGLE_CALLBACK_URL. "
                    + "Per l'esempio locale usa http://localhost:8080/auth/google/callback.");
            System.exit(1);
            return;
        }
        new JuiServer(new JuiProvider(new AuthApp())).googleOAuth(config).start();
    }

    public record PrivateNote(String message) {}
}
