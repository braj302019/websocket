(function() {
	var socket = new WebSocket("ws://localhost:8080/websocket/actions/stock");
	socket.onmessage = onMessage;
	var stocks = [];

	function onMessage(event) {
		var data = JSON.parse(event.data);
		if (data.action === "change") {
			printMessage(data);
		}
	}

	function renderColumn(data, type, full, meta) {
		if (full.change < 0) {
			return '<div style="background-color:#ECE981">' + data + '</div>';
		} else if (data > 0) {
			return '<div style="background-color:#90EE90">' + data + '</div>';
		} else {
			return data;
		}
	}

	function printMessage(data) {
		var time = new Date(data.time);
		var hours = time.getHours() % 12;
		var minutes = time.getMinutes();
		var seconds = time.getSeconds();
		var timestamp = ((hours < 10 ? '0' : '') + hours) + ':' + ((minutes < 10 ? '0' : '') + minutes) + ':' + ((seconds < 10 ? '0' : '') + seconds);

		data.time = timestamp;
		var index = stocks.findIndex(s => s.id == data.id);
		if (index == -1) {
			stocks.push(data);
		} else {
			stocks[index] = data;
		}

		$(document).ready(function() {
			var table = $('#stockTable').dataTable({
				paging : false,
				searching : false,
				retrieve : true,
				data : stocks,
				columns : [
					{
						data : "name"
					},
					{
						data : "price",
						render : function(data, type, full, meta) {
							return renderColumn(data, type, full, meta);
						}
					},
					{
						data : "time",
						render : function(data, type, full, meta) {
							return renderColumn(data, type, full, meta);
						}
					},
					{
						data : "change",
						render : function(data, type, full, meta) {
							if (data < 0) {
								return '<i class="fa fa-caret-down" aria-hidden="true" style="color:red"></i>';
							} else if (data > 0) {
								return '<i class="fa fa-caret-up" aria-hidden="true" style="color:green"></i>';
							} else {
								return '';
							}
						}
					},
					{
						data : "change",
						render : function(data, type, full, meta) {
							return renderColumn(data + '%', type, full, meta);
						}
					},
					{
						data : "min"
					},
					{
						data : "max"
					}
				]
			});

			if (index != -1) {
				table.fnUpdate(data, index, undefined, false);
			}
		});
	}

})();