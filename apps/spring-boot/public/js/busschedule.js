angular.module('busschedule', [])
    .value('hslScheduleURL', 'http://www.pubtrans.it/hsl/reittiopas/departure-api?')
    .controller('ListController', ['$scope', '$http', '$attrs', '$interval', 'hslScheduleURL',
        function ($scope, $http, $attrs, $interval, baseUrl) {
            moment.locale('fi');

            $scope.setStop = function (stopnumber, maxresults) {
                var now = moment().format('YYYY-MM-DDTHH:mm:ss'),
                    url = baseUrl
                        + 'stops[]=' + stopnumber
                        + '&time=' + now
                        + '&max=' + (maxresults || 10);

                $scope.code = null;
                $scope.response = null;
                delete $scope.error;

                $http.get(url)
                    .success(function (data, status) {
                        $scope.status = status;
                        $scope.data = processData(data);
                    })
                    .error(function (data, status) {
                        $scope.error = data || "Request failed";
                        $scope.status = status;
                    });
            }

            // http://www.pubtrans.it
            // pääskyskujan pysäkki 2133210, turuntien 2133204
            // Häiriöt: http://www.pubtrans.it/hsl/reittiopas/disruption-api?dt=2015-01-10T16:42:15
            $scope.setStop($attrs.stopnumber, $attrs.maxresults);
            $interval(function () {
                $scope.setStop($attrs.stopnumber, $attrs.maxresults);
            }, 60000);

            var processData = function (data) {

                var result = [];
                angular.forEach(data, function (value) {
                    var datVal = new Date((value.rtime || value.time) * 1000);
                    if (datVal.getTime() > new Date().getTime() + 43200000) {
                        // api failure giving next days schedule if happening "right now", checking for +12h
                        return;
                    }
                    var date = moment(datVal);
                    result.push({
                        line: value.line,
                        info: value.info,
                        time: date.format('H:mm'),
                        until: date.fromNow()
                    });
                });
                return result;
            };
        }
    ]);
/*
 {"dest":"Leppävaara",
 "id":"1964271699",
 "info":"",
 "line":"26",
 "route":"2026  2",
 "rtime":"",
 "stop":"2133210",
 "stopname":"Pääskyskuja",
 "time":1420908540}
 }
 */