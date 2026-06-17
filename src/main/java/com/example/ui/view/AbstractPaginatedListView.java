package com.example.ui.view;

import com.example.data.entity.PageSize;
import com.example.ui.CustomNotification;
import com.example.ui.dialog.ConfirmDialogs;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;

public abstract class AbstractPaginatedListView extends VerticalLayout {
    protected final TextField filter = new TextField();
    protected final Button submitFilter = new Button("Submit");

    private Paragraph pageNumberParagraph;
    private Component navigationComponents;
    private PageSize lastPageSize = PageSize.PAGE_SIZE_10;

    private NumberField pageSelector = new NumberField();
    private boolean updatingPageSelector;

    protected void initializeListView(String filterPlaceholder, Component content, Component... actions) {
        configureFilter(filterPlaceholder);

        add(
                createTopBar(actions),
                content,
                createBottomBar()
        );

        setSizeFull();
    }

    private void configureFilter(String placeholder) {
        filter.setPlaceholder(placeholder);
        filter.setClearButtonVisible(true);
        filter.setWidth("320px");
        filter.addValueChangeListener(e -> {
            String value = e.getValue();
            if (value == null || value.isBlank()) {
                onFilterChanged(value);
            }
        });

        submitFilter.addClickListener(e -> onFilterChanged(filter.getValue()));
        submitFilter.addClickShortcut(Key.ENTER);
    }

    private Component createTopBar(Component... actions) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();

        HorizontalLayout searchControls = new HorizontalLayout(filter, submitFilter);

        if (actions.length == 0) {
            layout.setJustifyContentMode(JustifyContentMode.START);
            layout.add(searchControls);
            return layout;
        }

        layout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        layout.add(searchControls, new HorizontalLayout(actions));
        return layout;
    }

    private Component createBottomBar() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setAlignSelf(Alignment.CENTER);
        layout.setVerticalComponentAlignment(Alignment.CENTER);
        layout.setWidthFull();
        layout.setJustifyContentMode(JustifyContentMode.BETWEEN);

        Paragraph pageSizeParagraph = new Paragraph("Page Size");

        ComboBox<PageSize> pageSize = new ComboBox<>();
        pageSize.setWidth("100px");
        pageSize.setItems(PageSize.values());
        pageSize.setValue(PageSize.PAGE_SIZE_10);
        pageSize.setItemLabelGenerator(PageSize::getLabel);
        pageSize.addValueChangeListener(e -> {
            if (e.getValue() == PageSize.PAGE_SIZE_ALL) {
                ConfirmDialogs.showAllRecords(
                        event -> {
                            onPageSizeChange(e.getValue());
                            lastPageSize = e.getValue();
                        },
                        event -> pageSize.setValue(lastPageSize)
                );
                return;
            }

            lastPageSize = e.getValue();
            onPageSizeChange(e.getValue());
        });

        HorizontalLayout pageSizeLayout = new HorizontalLayout(pageSizeParagraph, pageSize);
        pageSizeLayout.setAlignItems(Alignment.CENTER);

        navigationComponents = createNavigationButtons();

        layout.add(pageSizeLayout, navigationComponents);
        return layout;
    }

    private Component createNavigationButtons() {
        pageNumberParagraph = new Paragraph("Page 1 of 2");

        pageSelector = new NumberField();
        pageSelector.setWidth("60px");
        pageSelector.setValue(1d);
        pageSelector.setStep(1);

        Button firstPage = new Button("<<", e -> firstPage());
        Button previousPage = new Button("<", e -> previousPage());
        Button nextPage = new Button(">", e -> nextPage());
        Button lastPage = new Button(">>", e -> lastPage());

        pageSelector.addValueChangeListener(e -> {
            if (updatingPageSelector || e.getValue() == null) {
                return;
            }

            onPageSelectorChange(e.getValue().intValue());
        });

        HorizontalLayout layout = new HorizontalLayout(
                firstPage,
                previousPage,
                new Paragraph("Page "),
                pageSelector,
                pageNumberParagraph,
                nextPage,
                lastPage
        );
        layout.setAlignItems(Alignment.CENTER);
        return layout;
    }

    public void setPageSelectorValue(int pageNumber){
        updatingPageSelector = true;
        try {
            pageSelector.setValue((double) pageNumber);
        }
        finally {
            updatingPageSelector = false;
        }
    }

    public void setNavigationComponentsVisible(boolean visible) {
        navigationComponents.setVisible(visible);
    }

    public void setPageNumberParagraph(String value) {
        pageNumberParagraph.setText(value);
    }

    public void showSuccess(String message) {
        CustomNotification.showSuccessMessage(message);
    }

    public void showError(String message) {
        CustomNotification.showErrorMessage(message);
    }

    protected abstract void onFilterChanged(String value);

    protected abstract void onPageSizeChange(PageSize size);

    protected abstract void nextPage();

    protected abstract void previousPage();

    protected abstract void firstPage();

    protected abstract void lastPage();

    protected void onPageSelectorChange(int number) {
    }
}
