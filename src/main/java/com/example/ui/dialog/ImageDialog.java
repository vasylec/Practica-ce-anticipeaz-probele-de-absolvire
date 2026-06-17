package com.example.ui.dialog;

import com.example.data.entity.Vehicle;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;

public final class ImageDialog {
    private ImageDialog() {
    }

    public static void open(Vehicle vehicle) {
        open(vehicle == null ? null : vehicle.getImage());
    }

    public static void open(String imageUrl) {
        Dialog dialog = new Dialog();
        dialog.setMaxWidth("95vw");
        dialog.setMaxHeight("95vh");

        if (imageUrl == null || imageUrl.isBlank()) {
            VerticalLayout layout = new VerticalLayout();
            layout.setPadding(true);
            layout.setSpacing(true);
            layout.setAlignItems(FlexComponent.Alignment.END);

            Button button = new Button(VaadinIcon.CLOSE_SMALL.create(), e -> dialog.close());
            button.addClickShortcut(Key.ESCAPE);

            Span span = new Span("This vehicle does not have image !");
            span.getStyle().setFontSize("18px");
            span.getStyle().setPadding("20px");

            layout.add(button, span);
            dialog.add(layout);
            dialog.open();
            return;
        }

        Image image = new Image();
        image.setHeight("60vh");
        image.setMaxWidth("85vw");
        image.getStyle().set("object-fit", "contain");
        image.getStyle().set("display", "none");
        image.getStyle().setBorderRadius("15px");

        ProgressBar loading = new ProgressBar();
        loading.setIndeterminate(true);
        loading.setWidth("220px");

        Span loadingText = new Span("Loading image...");
        Span errorText = new Span("Image could not be loaded.");
        errorText.setVisible(false);

        VerticalLayout imageContainer = new VerticalLayout();
        imageContainer.setWidth("100%");
        imageContainer.setHeight("100px");
        imageContainer.setPadding(true);
        imageContainer.setSpacing(true);
        imageContainer.setAlignItems(FlexComponent.Alignment.CENTER);
        imageContainer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        imageContainer.add(loadingText, loading, errorText, image);

        HorizontalLayout imageViewport = new HorizontalLayout(imageContainer);
        imageViewport.setWidth("100%");
        imageViewport.setMaxWidth("90vw");
        imageViewport.setMaxHeight("78vh");
        imageViewport.setPadding(false);
        imageViewport.setSpacing(false);
        imageViewport.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        imageViewport.setAlignItems(FlexComponent.Alignment.CENTER);
        imageViewport.getStyle().set("overflow", "hidden");

        image.getElement().addEventListener("load", event -> {
            loadingText.setVisible(false);
            loading.setVisible(false);
            errorText.setVisible(false);
            image.getStyle().set("display", "block");
            imageContainer.setHeight(null);
        });

        image.getElement().addEventListener("error", event -> {
            loadingText.setVisible(false);
            loading.setVisible(false);
            errorText.setVisible(true);
        });

        configureImageZoom(image, imageViewport, imageContainer);
        image.setSrc(imageUrl);

        Button button = new Button(VaadinIcon.CLOSE_SMALL.create());
        button.addClickListener(e -> dialog.close());
        button.addClickShortcut(Key.ESCAPE);

        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.setAlignItems(FlexComponent.Alignment.END);
        layout.add(button, imageViewport);

        dialog.add(layout);
        dialog.open();
    }

    private static void configureImageZoom(Image image, HorizontalLayout imageViewport, VerticalLayout imageContainer) {
        image.getStyle().setCursor("zoom-in");

        image.addClickListener(event -> {
            boolean zoomed = "true".equals(image.getElement().getAttribute("data-zoomed"));

            if (zoomed) {
                image.getElement().setAttribute("data-zoomed", "false");
                image.setWidth(null);
                image.setHeight("60vh");
                image.setMaxWidth("85vw");
                image.getStyle().set("cursor", "zoom-in");
                imageContainer.setWidth("100%");
                imageViewport.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
                imageViewport.setAlignItems(FlexComponent.Alignment.CENTER);
                imageViewport.getStyle().set("overflow", "hidden");
                imageViewport.getElement().executeJs("this.scrollLeft = 0; this.scrollTop = 0;");
            } else {
                image.getElement().setAttribute("data-zoomed", "true");
                image.setWidth("140vw");
                image.setHeight("auto");
                image.setMaxWidth("none");
                image.getStyle().set("cursor", "zoom-out");
                imageContainer.setWidth(null);
                imageViewport.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
                imageViewport.setAlignItems(FlexComponent.Alignment.START);
                imageViewport.getStyle().set("overflow", "auto");
            }
        });
    }
}
