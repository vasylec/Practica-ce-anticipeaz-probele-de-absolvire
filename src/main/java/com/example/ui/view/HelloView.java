package com.example.ui.view;

import com.example.data.entity.Customer;
import com.example.service.AppUserService;
import com.example.ui.layout.MainLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Value;

@Route(value = "", layout = MainLayout.class)
@PermitAll
public class HelloView extends VerticalLayout {
    private final String backgroundImage;

    private final AppUserService userService;

    public HelloView(
            @Value("${image.background}") String backgroundImage,
            AppUserService userService) {
        this.backgroundImage = backgroundImage;
        this.userService = userService;

        H1 h1 = new H1("Welcome back, " + getName() + " !");
        h1.addClassName("glass-layout");

        add(h1);

        setBackground();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
    }

    private String getName() {
        Customer customer = userService.getCustomer();

        if (customer != null) {
            return customer.getInfo();
        } else {
            return "Admin";
        }
    }

    private void setBackground() {
        getStyle()
                .set("background-image",
                        "linear-gradient(" +
                                "rgba(0,0,0,0.5)," +
                                "rgba(0,0,0,0.5)" +
                                "), url('" + backgroundImage + "')")
                .set("background-size", "cover")
                .set("background-position", "center");
    }
}
