package com.example.ui.dialog;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;

import java.util.function.Function;

public final class DepositDialog {
    private DepositDialog() {
    }

    public static void open(Function<Double, Boolean> depositHandler) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Deposit");

        NumberField amount = new NumberField("Amount");
        amount.setPlaceholder("0.00");
        amount.setMin(50);
        amount.setMax(10000);
        amount.setStep(0.01);
        amount.setPrefixComponent(new Span("$"));
        amount.setWidthFull();

        Button confirm = new Button("Deposit");
        confirm.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
        confirm.addClickShortcut(Key.ENTER);

        Button cancel = new Button("Cancel", event -> dialog.close());
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancel.addClickShortcut(Key.ESCAPE);

        confirm.addClickListener(event -> {
            if (!depositHandler.apply(amount.getValue())) {
                amount.setInvalid(true);
                amount.setErrorMessage("Error occurred.");
                return;
            }

            dialog.close();
        });

        HorizontalLayout actions = new HorizontalLayout(confirm, cancel);
        actions.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        VerticalLayout layout = new VerticalLayout(amount, actions);
        layout.setPadding(false);
        layout.setSpacing(true);

        dialog.add(layout);
        dialog.open();
    }
}
