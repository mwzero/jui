# Google authentication example

`jui-app-auth` demonstrates a Google login, a protected profile and note form, and logout. The application checks `ui.authenticated()` before rendering or processing any private content. The note lives in server-owned state for the current tab; it is removed at logout or session expiry.

## Configure Google

1. Create a Google OAuth client of type **Web application**, following the [Google server-side OAuth guide](https://developers.google.com/identity/protocols/oauth2/web-server).
2. Configure the consent screen and, if the project is in testing mode, add your Google account as a test user.
3. Register this exact authorized redirect URI for local use:

   ```text
   http://localhost:8080/auth/google/callback
   ```

4. Provide the client ID and secret through environment variables. Keep the secret outside source control.

```bash
export GOOGLE_CLIENT_ID='your-client-id.apps.googleusercontent.com'
export GOOGLE_CLIENT_SECRET='your-client-secret'
export GOOGLE_CALLBACK_URL='http://localhost:8080/auth/google/callback'

mvn -B -pl apps/jui-app-auth -am package
java -jar apps/jui-app-auth/target/jui-app-auth-0.0.1-SNAPSHOT.jar
```

Open **http://localhost:8080**, choose **Accedi con Google**, then save a note in the protected form. **Esci** invalidates the authenticated session before rendering the anonymous page. Missing or invalid configuration stops startup with an explanation and does not print credentials.

If `PORT` is set, use the matching local port in both the registered redirect URI and `GOOGLE_CALLBACK_URL`. Outside loopback, use HTTPS for the entire public application and an HTTPS callback. The callback scheme configures the cookie's `Secure` attribute even when TLS terminates at a reverse proxy; incoming forwarded headers do not change this policy.

## Session behavior

- The server creates `JUI_SESSION`, an HttpOnly, SameSite=Lax, host-only cookie. The identifier is not exposed to JavaScript or placed in URLs.
- Login is shared by tabs in the same browser. Each tab uses a separate `viewId` for its widgets and internal state. On focus, a tab refreshes its view to observe login/logout in another tab.
- Login and logout rotate the cookie and CSRF token and clear all previous tab state. The old session immediately stops accepting requests.
- Sessions expire after 30 minutes idle or 8 hours total. Server restarts also remove them. This example uses memory only.
- Google `state` is random, bound to the initiating cookie, expires after 10 minutes and is consumed once. Starting another login replaces the previous pending attempt.
- The example demonstrates authentication, not roles or account allowlists: any Google account accepted by your OAuth client can sign in. Notes are scoped to a tab, not stored in a user database.

## API example

```java
public void run(UIContext ui) {
    if (!ui.authenticated()) {
        ui.googleLoginButton("Accedi con Google");
        return;
    }

    var user = ui.authUser().orElseThrow();
    ui.text("Ciao " + user.name());
    ui.form(PrivateNote.class).ifPresent(note ->
        ui.setValue("last-note", note.message()));
    ui.text(ui.getValue("last-note", ""));
    ui.logoutButton("Esci");
}

public record PrivateNote(String message) {}
```

Register OAuth at startup with `new JuiServer(new JuiProvider(new AuthApp())).googleOAuth(GoogleOAuthConfig.fromEnv()).start()`.

## Tests

The normal Maven build checks the anonymous and authenticated views, isolation of notes and logout cleanup. Core integration tests exercise actual HTTP requests on ephemeral loopback ports and replace the Google exchange with a deterministic test client. They need no Google credentials or external network access. A real browser login requires your configured Google client.
