package com.example.ui.view.charts;

import com.example.ui.layout.MainLayout;
import com.example.ui.presenter.ChartPresenter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.AxisType;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.component.charts.model.Tooltip;
import com.vaadin.flow.component.charts.model.XAxis;
import com.vaadin.flow.component.charts.model.YAxis;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "service/revenue-per-month", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class RevenueChart extends VerticalLayout {
     private final ChartPresenter presenter;

     public RevenueChart(ChartPresenter presenter) {
         this.presenter = presenter;

         setSizeFull();
         add(createChart());
     }

     private Component createChart() {
         Chart chart = new Chart(ChartType.AREASPLINE);
         chart.setSizeFull();

         Configuration conf = chart.getConfiguration();
         configureAxes(conf);
         configureTooltip(conf);
         conf.addSeries(createSeries());

         chart.drawChart();
         return chart;
     }

     private void configureAxes(Configuration conf) {
         XAxis x = conf.getxAxis();
         x.setType(AxisType.DATETIME);

         YAxis y = conf.getyAxis();
         y.setTitle("Revenue");
     }

     private void configureTooltip(Configuration conf) {
         Tooltip tooltip = new Tooltip();
         tooltip.setXDateFormat("%d-%m-%Y");
         tooltip.setPointFormat("Value: {point.y}");
         conf.setTooltip(tooltip);
     }

     private DataSeries createSeries() {
         DataSeries series = new DataSeries();
         presenter.getRevenuePerMonthData()
                 .forEach(point -> series.add(new DataSeriesItem(point.timestamp(), point.value())));
         return series;
     }
}
