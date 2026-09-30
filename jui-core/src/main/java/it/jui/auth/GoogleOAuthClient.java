package it.jui.auth;

import java.io.IOException;

/** Replaceable server-side Google exchange, kept package-private for deterministic tests. */
@FunctionalInterface
interface GoogleOAuthClient {
    AuthUser authenticate(String code, GoogleOAuthConfig config) throws IOException, InterruptedException;
}
