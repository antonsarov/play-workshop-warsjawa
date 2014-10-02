var app = angular.module('gitHubJobsMapApp', ["leaflet-directive"]);

app.factory('GHJM', function($http, $timeout) {

    var jobsService = {
        jobs: [],
        query: function (query) {
            $http({method: 'GET', url: '/jobs?q='+query}).
                success(function (data) {
					console.log(data);
                    jobsService.jobs = data;
                }).
				error(function(data, status, headers, config) {
				// called asynchronously if an error occurs
				// or server returns response with an error status.
					console.log("error");
					console.log(data);
				});
        }
    };

    return jobsService;
});

app.controller('Search', function($scope, $http, $timeout, GHJM) {

    $scope.search = function() {
        GHJM.query($scope.query);
    };

});

app.controller('Jobs', function($scope, $http, $timeout, GHJM) {
	$scope.jobs = [];
    $scope.markers = [];

    $scope.$watch(
        // This function returns the value being watched.
        function() {
            return GHJM.jobs;
        },
        // This is the change listener, called when the value returned from the above function changes
        function(jobs) {
            $scope.jobs = jobs;

            $scope.markers = jobs.map(function(job) {
                return {
                    lng: job.coordinates.lng,
                    lat: job.coordinates.lat,
                    message: '<a href=\"'+job.url+'\">'+job.title+'</a>',
                    focus: true,
                    draggable: true
                }
            });

        }
    );

});
