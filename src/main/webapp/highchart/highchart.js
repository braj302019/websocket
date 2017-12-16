(function() {

	Highcharts.setOptions({
		global : {
			useUTC : false
		}
	});

	// Create the chart
	var stockChart = Highcharts.stockChart('container', {
		chart : {
			events : {
				load : function() {
					//nothing
				}
			}
		},

		rangeSelector : {
			buttons : [ {
				count : 1,
				type : 'minute',
				text : '1M'
			}, {
				count : 5,
				type : 'minute',
				text : '5M'
			}, {
				type : 'all',
				text : 'All'
			} ],
			inputEnabled : false,
			selected : 0
		},

		title : {
			text : 'Live random data'
		},

		exporting : {
			enabled : false
		},

		series : [ {
			name : 'Random data',
			data : (function() {
				// generate an array of random data
				var data = [],
					time = (new Date()).getTime(),
					i;

				for (i = -999; i <= 0; i += 1) {
					data.push([
						time + i * 1000,
						Math.round(Math.random() * 100)
					]);
				}
				return data;
			}())
		} ],
	});

	socket = new WebSocket("ws://localhost:8080/websocket/actions/highchart"),
	socket.onmessage = function(event) {
		var point = JSON.parse(event.data);
		var series = stockChart.series[0];
		var x = new Date(point.x).getTime(),
			y = point.y;
		series.addPoint([ x, y ], true, true);
	};
})();