package ru.sejapoe.digitalhotelserver.core.security;

import ru.sejapoe.digitalhotelserver.user.session.SessionService;

public interface SessionServiceHolder {
    SessionService getSessionService();
}
