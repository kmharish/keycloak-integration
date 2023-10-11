package io.custom.eventlistners;

import org.jboss.logging.Logger;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserSessionModel;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SessionManagementListener implements EventListenerProvider{

    private static final Logger log = Logger.getLogger(SessionManagementListener.class);
    private final KeycloakSession session;

    public SessionManagementListener(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public void onEvent(Event event) {
        EventType type = event.getType();
        if (type.equals(EventType.UPDATE_PASSWORD)) {
            String realmId = event.getRealmId();
            log.infov("Update Password event for realmId: {0}, userId: {1}", realmId, event.getUserId());
            RealmModel realm = session.realms().getRealm(realmId);

            List<String> offlineSessionList = Optional.ofNullable(event.getUserId())
                    .map(id -> session.users().getUserById(id, realm))
                    .map(user -> session.sessions().getOfflineUserSessionsStream(realm, user)).orElse(Stream.empty())
                    .peek(offlineSession -> session.sessions().removeOfflineUserSession(realm, offlineSession))
                    .map(UserSessionModel::getId)
                    .collect(Collectors.toList());


            log.infov("Removed offline sessions for user: {0}, sessions: {1}", event.getUserId(), offlineSessionList);

            List<String> onlineSessionList = Optional.ofNullable(event.getUserId())
                    .map(id -> session.users().getUserById(id, realm))
                    .map(user -> session.sessions().getUserSessionsStream(realm, user)).orElse(Stream.empty())
                    .peek(onlineSession -> session.sessions().removeUserSession(realm, onlineSession))
                    .map(UserSessionModel::getId)
                    .collect(Collectors.toList());

            log.infov("Removed online sessions for user: {0}, sessions: {1}", event.getUserId(), onlineSessionList);

        }
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
    }

    @Override
    public void close() {
    }


}
