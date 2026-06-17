package com.example.ui.dialog;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;

public final class ConfirmDialogs {
    private ConfirmDialogs() {
    }

    public static void deleteDialog(
            String header,
            String text,
            ComponentEventListener<ConfirmDialog.ConfirmEvent> listener
    ) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader(header);
        dialog.setText(text);
        dialog.setConfirmButtonTheme("error primary");
        dialog.setCancelable(true);
        dialog.setConfirmText("Delete");
        dialog.addConfirmListener(listener);
        dialog.open();
    }

    public static void logoutDialog(ComponentEventListener<ConfirmDialog.ConfirmEvent> listener) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Log out");
        dialog.setText("Are you sure you want to log out ?");
        dialog.setConfirmButtonTheme("error primary");
        dialog.setCancelable(true);
        dialog.setConfirmText("Log out");
        dialog.addConfirmListener(listener);
        dialog.open();
    }

    public static void showAllRecords(ComponentEventListener<ConfirmDialog.ConfirmEvent> confirmListener,
                                      ComponentEventListener<ConfirmDialog.CancelEvent> cancelListener){
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("List all records");
        dialog.setText("Are you sure you want to list all records \n(this might take time) ?");
        dialog.setConfirmButtonTheme("error primary");
        dialog.setCancelable(true);
        dialog.setConfirmText("List all records");
        dialog.addConfirmListener(confirmListener);
        dialog.addCancelListener(cancelListener);
        dialog.open();
    }
}
