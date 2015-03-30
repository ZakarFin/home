angular.module('lights', [])
    .value('apiUrl', '/api')
    .controller('ListController', ['$scope', '$http', 'apiUrl',
        function ($scope, $http, baseUrl) {

            $http.get(baseUrl)
                .success(function (data, status) {
                    $scope.status = status;
                    $scope.data = data;
                })
                .error(function (data, status) {
                    $scope.error = data || "Request failed";
                    $scope.status = status;
                });

            $scope.sendCmd = function (id, cmd) {
                delete $scope.error;
                console.log('calling: ' + baseUrl + '/' + id + '/' + cmd);
                $http.post(baseUrl + '/' + id + '/' + cmd)
                    .success(function (data, status) {
                        console.log('success');
                        //$scope.status = status;
                    })
                    .error(function (data, status) {
                        console.log('error');
                        $scope.error = data || "Request failed";
                    });
            }
        }
    ]);
