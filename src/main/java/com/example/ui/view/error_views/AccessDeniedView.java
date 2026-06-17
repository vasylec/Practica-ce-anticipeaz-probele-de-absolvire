package com.example.ui.view.error_views;

import com.example.ui.layout.MainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll;

@ParentLayout(MainLayout.class)
@PermitAll
public class AccessDeniedView extends Div implements HasErrorParameter<AccessDeniedException> {
    @Override
    public int setErrorParameter(BeforeEnterEvent beforeEnterEvent, ErrorParameter<AccessDeniedException> errorParameter) {



        Div message = new Div();
        message.setText("Access Denied");

        message.getStyle().set("text-align", "center");

        message.getStyle().set("color", "red");

        message.getStyle().set("font-size", "24px");

        message.getStyle().set("margin-top", "50px");

        removeAll();
        add(message);


        return 403;

    }
}
