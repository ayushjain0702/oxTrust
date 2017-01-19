
"use strict";

$(function () {
  
//-----------------------
  //- Dashboard > Authentication Requests Chart -
  //-----------------------
	try{
	

  var authChartData = JSON.parse($("#authenticationChartJson").val());  
  console.log("authentication chart data");
  console.log(authChartData);

	  var authenticationRequestsChartData = {
		labels : authChartData.labels,
		datasets : [
	
		{
			label : "Successful Login",
			fill : false,
			backgroundColor : "#00BE79",
			data : authChartData.success
		}, {
			label : "Failed Attempts",
			fill : false,
			backgroundColor : "#F39C12",
			data : authChartData.failure
		}
	
		]
	};

  var authenticationRequestsChartOptions = {
      responsive: true,
      scales: {
          yAxes: [{
              ticks: {
                  suggestedMin: 0,
                  suggestedMax: 1000
              }
          }]
      }
  };
  console.log(authenticationRequestsChartData);
  console.log(authenticationRequestsChartOptions);

  // Get context with jQuery - using jQuery's .get() method.
  var authenticationRequestsChartCanvas = $("#authenticationRequestsChart").get(0).getContext("2d");
  // This will get the first returned node in the jQuery collection.
  //Create the line chart
  var authenticationRequestsChart = new Chart(authenticationRequestsChartCanvas, {
    type: 'line',
    data: authenticationRequestsChartData,
    options:  authenticationRequestsChartOptions
  });

	}catch(error){
		console.log(error);
	}

});