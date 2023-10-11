package io.custom.eventlistners;

import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
public class SessionManagementListenerProviderFactory implements EventListenerProviderFactory {
    public static final String SESSION_REVOCATION = "session-revocation";

    @Override
    public EventListenerProvider create(KeycloakSession session) {
        return new SessionManagementListener(session);
    }

    @Override
    public void init(Config.Scope config) {
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
    }

    @Override
    public void close() {
    }

    @Override
    public String getId() {
        return SESSION_REVOCATION;
    }

    @Override
    public int order() {
        return Integer.MAX_VALUE;
    }
}
