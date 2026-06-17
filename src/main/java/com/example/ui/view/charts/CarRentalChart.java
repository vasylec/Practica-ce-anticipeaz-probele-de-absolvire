package com.example.ui.view.charts;

import com.example.ui.layout.MainLayout;
import com.example.ui.presenter.ChartPresenter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.component.charts.model.Tooltip;
import com.vaadin.flow.component.charts.model.style.SolidColor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "service/car-rental", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class CarRentalChart extends VerticalLayout {
     private final ChartPresenter presenter;

     public CarRentalChart(ChartPresenter presenter) {
         this.presenter = presenter;

         configureLayout();
         add(createTitle(), createChart());
     }

     private void configureLayout() {
         setSizeFull();
         setAlignItems(Alignment.CENTER);
         setJustifyContentMode(JustifyContentMode.CENTER);
     }

     private Component createTitle() {
         H1 title = new H1("Car Rental Chart");
         title.getStyle()
                 .set("margin-bottom", "50px")
                 .set("margin-top", "50px");
         return title;
     }

     private Component createChart() {
         Chart chart = new Chart(ChartType.PIE);
         chart.setSizeFull();

         Tooltip tooltip = new Tooltip();
         tooltip.setPointFormat("<b>{point.percentage:.1f}%</b><br><b>{point.y}</b>");

         chart.getConfiguration().setTooltip(tooltip);
         chart.getConfiguration().addSeries(createSeries());
         chart.getConfiguration().getChart().setBackgroundColor(new SolidColor(0, 0, 0, 0));
         chart.drawChart();

         return chart;
     }

     private DataSeries createSeries() {
         DataSeries series = new DataSeries();
         presenter.getCarRentalData()
                 .forEach(point -> series.add(new DataSeriesItem(point.name(), point.value())));
         return series;
     }
}
