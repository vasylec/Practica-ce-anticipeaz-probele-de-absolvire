package com.example.ui;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class CustomNotification {
    public static void showSuccessMessageLongDuration(String message){
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        HorizontalLayout layout = getHorizontalLayout(message, notification);

        notification.add(layout);
        notification.setDuration(600000);
        notification.open();
    }

    public static void showSuccessMessage(String message){
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        HorizontalLayout layout = getHorizontalLayout(message, notification);

        notification.add(layout);
        notification.setDuration(5000);
        notification.open();
    }

    public static void showErrorMessage(String message){
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR, NotificationVariant.LUMO_PRIMARY);
        HorizontalLayout layout = getHorizontalLayout(message, notification);

        notification.add(layout);
        notification.setDuration(5000);
        notification.open();
    }

    private static HorizontalLayout getHorizontalLayout(String message, Notification notification) {
        HorizontalLayout layout = new HorizontalLayout(
                new Text(message),
                new Button(VaadinIcon.CLOSE_SMALL.create(), event -> {
                    notification.close();})
        );

        layout.setWidthFull();
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        layout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        return layout;
    }
}
