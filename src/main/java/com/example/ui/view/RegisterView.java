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

@Route(value = "/register", autoLayout = false)
@AnonymousAllowed
public class RegisterView extends VerticalLayout {
    private final AppUserService appUserService;

    private final TextField username = new TextField("Username");
    private final EmailField email = new EmailField("Email");
    private final PasswordField password = new PasswordField("Password");
    private final PasswordField confirmPassword = new PasswordField("Confirm password");
    private final TextField customerName = new TextField("First name");
    private final TextField customerSecondName = new TextField("Last name");
    private final TextField phone = new TextField("Phone");
    private final Span error = new Span();

    public RegisterView(AppUserService appUserService) {
        this.appUserService = appUserService;

        configureLayout();
        configureFields();

        add(getForm());
    }

    private VerticalLayout getForm() {
        Button register = new Button("Register");
        register.addClickListener(e -> register());
        register.addClickShortcut(Key.ENTER);
        register.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button backToLogin = new Button("Back to login");
        backToLogin.addClickListener(e -> UI.getCurrent().navigate(LoginView.class));
        backToLogin.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        VerticalLayout form = new VerticalLayout();
        form.add(
                new H1("Create account"),
                username,
                email,
                password,
                confirmPassword,
                customerName,
                customerSecondName,
                phone,
                error,
                new HorizontalLayout(register, backToLogin));

        form.addClassNames("glass-layout", "register-view-layout");

        form.getChildren().forEach(component -> {
            component.addClassName("full-width");
        });

        return form;
    }

    private void configureLayout() {
        addClassName("register-view");
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
    }

    private void configureFields() {
        username.setRequired(true);
        email.setRequiredIndicatorVisible(true);
        email.setClearButtonVisible(true);
        email.setErrorMessage("Please enter a valid email address.");
        password.setRequired(true);
        confirmPassword.setRequired(true);
        customerName.setRequired(true);
        customerSecondName.setRequired(true);
        phone.setRequired(true);

        error.setVisible(false);
        error.getStyle().set("color", "red");
    }

    private void register() {
        error.setVisible(false);

        if (hasEmptyRequiredFields()) {
            showError("Please complete all required fields.");
            return;
        }

        if (!password.getValue().equals(confirmPassword.getValue())) {
            showError("Passwords do not match.");
            return;
        }
        if (email.isInvalid()) {
            showError("Please enter a valid email address.");
            return;
        }

        try {
            appUserService.registerCustomerUser(
                    username.getValue().trim(),
                    email.getValue().trim(),
                    password.getValue(),
                    customerName.getValue().trim(),
                    customerSecondName.getValue().trim(),
                    phone.getValue().trim());

            CustomNotification.showSuccessMessage("Account successfully created. You can log in now.");
            UI.getCurrent().navigate(LoginView.class);
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        } catch (Exception exception) {
            showError("Account could not be created.");
        }
    }

    private boolean hasEmptyRequiredFields() {
        return isBlank(username.getValue())
                || isBlank(email.getValue())
                || isBlank(password.getValue())
                || isBlank(confirmPassword.getValue())
                || isBlank(customerName.getValue())
                || isBlank(customerSecondName.getValue())
                || isBlank(phone.getValue());
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private void showError(String message) {
        error.setText(message);
        error.setVisible(true);
    }
}
