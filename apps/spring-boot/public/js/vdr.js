angular.module('vdr', [])
    .value('apiUrl', '/vdr')
    .controller('ListController', ['$scope', '$http', 'apiUrl',
        function ($scope, $http, baseUrl) {
            moment.locale('fi');
            $scope.channels = [];
            $scope.programs = [];
            $http.get(baseUrl)
                .success(function (data, status) {
                    $scope.status = status;
                    $scope.channels = data;
                })
                .error(function (data, status) {
                    $scope.error = data || "Request failed";
                    $scope.status = status;
                });

            $scope.getEPG = function (id) {
                $scope.programs = [];
                delete $scope.error;
                console.log('calling: ' + baseUrl + '/' + id);
                $http.get(baseUrl + '/' + id)
                    .success(function (data, status) {
                        console.log('success');
                        $scope.programs = formatter(data);
                        //$scope.status = status;
                    })
                    .error(function (data, status) {
                        console.log('error');
                        $scope.programs = [];
                        $scope.error = data || "Request failed";
                    });
            };
            // TODO: move to a service?
            var formatter = function (programs) {
                var values = [];
                angular.forEach(programs, function (program) {
                    //"startTime":1427479500000,"endTime":1427482800000
                    var start = moment(new Date(program.startTime));
                    var end = moment(new Date(program.endTime));
                    values.push({
                        name: program.title,
                        start: start.format('H:mm'),
                        end: end.format('H:mm'),
                        desc: program.shortText
                    });
                });
                return values;

            };

        }
    ]);
