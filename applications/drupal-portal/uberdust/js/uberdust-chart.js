var chart;
var divContainer;
var titleText;
var subtitleText;
var yaxisText;
var uberdustSeries = [];

$(document).ready(function() {
    console.log('making chart');
    chart = new Highcharts.Chart({
        chart: {
            renderTo: divContainer,
            defaultSeriesType: 'spline',
            zoomType: 'x',
            spacingRight: 20
        },
        title: {
            text: titleText
        },
        subtitle: {
            text: subtitleText
        },
        xAxis: {
            type: 'datetime',
            dateTimeLabelFormats: {
                day: '%e %b',
                month: '%e %b'
            },
            tickPixelInterval: 400,
            maxZoom: 1000
        },
        yAxis: {
            title: {
                text: yaxisText
            },
            min: 0.6,
            startOnTick: false,
            showFirstLabel: false
        },
        tooltip: {
            shared: true
        },
        legend: {
            enabled: false
        },
        plotOptions: {
            series: {
                lineWidth: 1,
                marker: {
                    enabled: false,
                    states: {
                        hover: {
                            enabled: true,
                            radius: 5
                        }
                    }
                }
            }
        },
        series: uberdustSeries
    });
    console.log('chart made!')
});