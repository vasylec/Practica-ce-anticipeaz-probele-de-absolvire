package com.example.ui.view;

import com.example.service.AppUserService;
import com.example.ui.CustomNotification;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(value = "/forgot-password", autoLayout = false)
@AnonymousAllowed
public class ForgotPasswordView extends VerticalLayout {
    private final AppUserService appUserService;

    private final EmailField email = new EmailField("Email");
    private final TextField resetCode = new TextField("Reset code");
    private final PasswordField newPassword = new PasswordField("New password");
    private final PasswordField confirmPassword = new PasswordField("Confirm new password");
    private final Span error = new Span();

    public ForgotPasswordView(AppUserService appUserService) {
        this.appUserService = appUserService;

        configureLayout();
        configureFields();

        add(getForm());
    }

    private VerticalLayout getForm() {
        Button sendCode = new Button("Send code");
        sendCode.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        sendCode.addClickListener(event -> sendResetCode());

        Button resetPassword = new Button("Reset password");
        resetPassword.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        resetPassword.addClickListener(event -> resetPassword());
        resetPassword.addClickShortcut(Key.ENTER);

        Button backToLogin = new Button("Back to login");
        backToLogin.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        backToLogin.addClickListener(event -> UI.getCurrent().navigate(LoginView.class));

        VerticalLayout form = new VerticalLayout(
                new H1("Reset password"),
                email,
                resetCode,
                newPassword,
                confirmPassword,
                error,
                new HorizontalLayout(sendCode, resetPassword, backToLogin)
        );

        form.addClassNames("glass-layout", "forgot-password-view-layout");
        form.getChildren().forEach(component -> component.addClassName("full-width"));

        return form;
    }

    private void configureLayout() {
        addClassName("forgot-password-view");
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
    }

    private void configureFields() {
        email.setRequiredIndicatorVisible(true);
        email.setClearButtonVisible(true);
        email.setErrorMessage("Please enter a valid email address.");
        resetCode.setPlaceholder("6 digit code");
        error.setVisible(false);
        error.getStyle().set("color", "red");
    }

    private void sendResetCode() {
        error.setVisible(false);
        if (isBlank(email.getValue()) || email.isInvalid()) {
            showError("Please enter a valid email address.");
            return;
        }

        try {
            appUserService.createPasswordResetCode(email.getValue().trim());
            CustomNotification.showSuccessMessage("Password reset code sent to your email.");
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        } catch (Exception exception) {
            showError("The reset code could not be sent.");
        }
    }

    private void resetPassword() {
        error.setVisible(false);
        if (hasEmptyRequiredFields()) {
            showError("Please complete all required fields.");
            return;
        }
        if (email.isInvalid()) {
            showError("Please enter a valid email address.");
            return;
        }
        if (!newPassword.getValue().equals(confirmPassword.getValue())) {
            showError("Passwords do not match.");
            return;
        }

        try {
            appUserService.resetPassword(
                    email.getValue().trim(),
                    resetCode.getValue().trim(),
                    newPassword.getValue()
            );

            CustomNotification.showSuccessMessage("Password successfully reset. You can log in now.");
            UI.getCurrent().navigate(LoginView.class);
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        } catch (Exception exception) {
            showError("Password could not be reset.");
        }
    }

    private boolean hasEmptyRequiredFields() {
        return isBlank(email.getValue())
                || isBlank(resetCode.getValue())
                || isBlank(newPassword.getValue())
                || isBlank(confirmPassword.getValue());
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private void showError(String message) {
        error.setText(message);
        error.setVisible(true);
    }
}
