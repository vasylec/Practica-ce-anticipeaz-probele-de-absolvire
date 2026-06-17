package com.example.security;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.VaadinServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class SecurityService {
    public void logout() {
        UI.getCurrent().getPage().setLocation("/");
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(VaadinServletRequest.getCurrent().getHttpServletRequest(),
                VaadinServletResponse.getCurrent().getHttpServletResponse(), null);
    }

    public boolean hasRole(String role) {
        return Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication())
                .getAuthorities().stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_" + role));
    }
}
