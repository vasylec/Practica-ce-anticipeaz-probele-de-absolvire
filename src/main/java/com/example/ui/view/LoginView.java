package com.example.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(value = "/login", autoLayout = false)
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {
    private final LoginForm loginForm = new LoginForm();
    private final Button forgotPasswordButton = new Button("Forgot password");
    private final Button registerButton = new Button("Create account");

    public LoginView()
    {
        configureLoginForm();
        configureForgotPasswordButton();
        configureRegisterButton();
        configurePageLayout();

        add(
                getLoginPanel()

        );
    }

    private void configureLoginForm() {
        loginForm.setAction("login");
        loginForm.setForgotPasswordButtonVisible(false);
    }

    private void configureForgotPasswordButton() {
        forgotPasswordButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        forgotPasswordButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(ForgotPasswordView.class)));
        forgotPasswordButton.addClassName("forgot-password-button");
    }

    private void configureRegisterButton() {
        registerButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        registerButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(RegisterView.class)));
        registerButton.addClassName("register-button");
    }

    private void configurePageLayout() {
        addClassName("login-view");
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
    }

    private VerticalLayout getLoginPanel() {
        VerticalLayout layout = new VerticalLayout();
        layout.addClassNames("login-view-layout", "glass-layout");
        layout.add(loginForm, getLoginActions());
        return layout;
    }

    private HorizontalLayout getLoginActions() {
        HorizontalLayout actions = new HorizontalLayout(forgotPasswordButton, registerButton);
        actions.addClassName("login-actions");
        actions.setWidthFull();
        actions.setJustifyContentMode(JustifyContentMode.BETWEEN);
        return actions;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (event.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            loginForm.setError(true);
        }
    }
}
