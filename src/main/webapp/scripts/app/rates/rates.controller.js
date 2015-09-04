'use strict';

angular.module('hnbappApp')
  .controller('RatesController', function ($scope, Currency) {
    $scope.currencies = [
      { name:'USD', selected:true },
      { name:'EUR', selected:true },
      { name:'CHF', selected:true },
      { name:'JPY', selected:true }
    ];

    var getSelectedCurrencies = function() {
      var selectedCurrencies = [];
      for (var i = $scope.currencies.length - 1; i >= 0; i--) {
        if ($scope.currencies[i].selected) {
          selectedCurrencies.push($scope.currencies[i].name);
        }
      }
      return selectedCurrencies;
    };

    $scope.resolutions = [
      {label: 'Day', value: 'day'},
      {label: 'Month (avg)', value: 'month'}
    ];
    $scope.selectedResolution = $scope.resolutions[0];
    $scope.selectResolution = function (resolution) {
      $scope.selectedResolution = resolution;
      $scope.updateRates();
    };

    $scope.time = {
      today: new Date(),
      startTime: new Date(),
      endTime: new Date()
    };
    $scope.time.startTime.setYear($scope.time.today.getYear() - 1);

    $scope.$watch('time.startTime', function(){
      if ($scope.time.startTime) {
        $scope.updateRates();
      }
    });
    $scope.$watch('time.endTime', function(){
      if ($scope.time.endTime) {
        $scope.updateRates();
      }
    });
    $scope.open = function() {
      $scope.status.opened = true;
    };
    $scope.status = {
      opened: false
    };
    $scope.format = 'dd/MM/yyyy';
    $scope.dateOptions = {
      formatYear: 'yy',
      startingDay: 1
    };

    var dataIndex = {date: 0};
    var dateIndex = {};
    var chartData = [['Date']];

    var clearChart = function() {
      $scope.chart.data = undefined;
      dataIndex = {date: 0};
      dateIndex = {};
      chartData = [['Date']];
    };

    var addDataPoint = function(point) {
      var name = point.name;
      var date = point.date;
      var rate = point.rate;

      if (!dataIndex[name]) {
        chartData[0].push(name);
        dataIndex[name] = chartData[0].length - 1;
      }
      if (!dateIndex[date]) {
        chartData.push([new Date(date)]);
        dateIndex[date] = chartData.length - 1;
      }
      chartData[dateIndex[date]][dataIndex[name]] = rate;
    };


    $scope.updateRates = function() {
      Currency.queryRates({currencies: getSelectedCurrencies(),
                            start: $scope.time.startTime.getTime(),
                            end: $scope.time.endTime.getTime(),
                            resolution: $scope.selectedResolution.value
                          }).
        $promise.then(
          function(data) {
            clearChart();
            if (data.length > 0) {
              chartData = [['Date']];
              for (var i = 0; i < data.length; i++) {
                addDataPoint(data[i]);
              }
              $scope.chart.data = chartData;
            };
          }
        );
    };

    $scope.chart = {
      "type": "LineChart",
      "cssStyle": "height:600px;",
      "data": undefined,
      "options": {
        "fill": 20,
        "displayExactValues": true
      },
      "formatters": {},
      "displayed": true
    };
});

