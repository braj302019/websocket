(function() {
	window.onload = init;
	var socket = new WebSocket("ws://localhost:8080/websocket/actions/device");
	socket.onmessage = onMessage;

	function onMessage(event) {
		var device = JSON.parse(event.data);
		if (device.action === "add") {
			printDeviceElement(device);
		}
		if (device.action === "remove") {
			document.getElementById(device.id).remove();
		}
		if (device.action === "toggle") {
			var node = document.getElementById(device.id);
			var statusText = node.children[2];
			var toggleDevice = document.createElement("a");
			toggleDevice.setAttribute("data-id", device.id);
			toggleDevice.setAttribute("class", "toggleDevice");
			toggleDevice.setAttribute("href", "#");
			if (device.status === "On") {
				toggleDevice.innerHTML = " (Turn off)";
				statusText.innerHTML = "Status: " + device.status;
			} else if (device.status === "Off") {
				toggleDevice.innerHTML = " (Turn on)";
				statusText.innerHTML = "Status: " + device.status;
			}
			statusText.appendChild(toggleDevice);
		}
	}

	function addDevice(name, type, description) {
		var DeviceAction = {
			action : "add",
			name : name,
			type : type,
			description : description
		};
		socket.send(JSON.stringify(DeviceAction));
	}

	function removeDevice(element) {
		var id = element;
		var DeviceAction = {
			action : "remove",
			id : id
		};
		socket.send(JSON.stringify(DeviceAction));
	}

	function toggleDevice(element) {
		var id = element;
		var DeviceAction = {
			action : "toggle",
			id : id
		};
		socket.send(JSON.stringify(DeviceAction));
	}

	function printDeviceElement(device) {
		var content = document.getElementById("content");

		var deviceDiv = document.createElement("div");
		deviceDiv.setAttribute("id", device.id);
		deviceDiv.setAttribute("class", "device " + device.type);
		content.appendChild(deviceDiv);

		var deviceName = document.createElement("span");
		deviceName.setAttribute("class", "deviceName");
		deviceName.innerHTML = device.name;
		deviceDiv.appendChild(deviceName);

		var deviceType = document.createElement("span");
		deviceType.innerHTML = "<b>Type:</b> " + device.type;
		deviceDiv.appendChild(deviceType);

		var deviceStatus = document.createElement("span");
		deviceStatus.setAttribute("id", device.id);
		
		var toggleDevice = document.createElement("a");
		toggleDevice.setAttribute("data-id", device.id);
		toggleDevice.setAttribute("class", "toggleDevice");
		toggleDevice.setAttribute("href", "#");
		if (device.status === "On") {
			toggleDevice.innerHTML = " (Turn off)";
			deviceStatus.innerHTML = "<b>Status:</b> " + device.status;
		} else if (device.status === "Off") {
			toggleDevice.innerHTML = " (Turn on)";
			deviceStatus.innerHTML = "<b>Status:</b> " + device.status;
		}
		deviceStatus.appendChild(toggleDevice);
		deviceDiv.appendChild(deviceStatus);

		var deviceDescription = document.createElement("span");
		deviceDescription.innerHTML = "<b>Comments:</b> " + device.description;
		deviceDiv.appendChild(deviceDescription);

		var removeDevice = document.createElement("a");
		removeDevice.setAttribute("data-id", device.id);
		removeDevice.setAttribute("class", "removeDevice");
		removeDevice.setAttribute("href", "#");
		removeDevice.innerHTML = "Remove device";
		
		var removeDeviceSpan = document.createElement("span");
		removeDeviceSpan.setAttribute("class", "removeDevice");
		removeDeviceSpan.appendChild(removeDevice);
		deviceDiv.appendChild(removeDeviceSpan);
	}

	function showForm() {
		document.getElementById("addDeviceForm").style.display = '';
	}

	function hideForm() {
		document.getElementById("addDeviceForm").style.display = "none";
	}

	function formSubmit() {
		var form = document.getElementById("addDeviceForm");
		var name = form.elements["device_name"].value;
		var type = form.elements["device_type"].value;
		var description = form.elements["device_description"].value;
		hideForm();
		document.getElementById("addDeviceForm").reset();
		addDevice(name, type, description);
	}

	function init() {
		hideForm();
	}

	var addADevice = document.getElementById("addADevice");
	addADevice.onclick = function() {
		showForm();
	};

	var cancel = document.getElementById("cancel");
	cancel.onclick = function() {
		hideForm();
	};

	var add = document.getElementById("add");
	add.onclick = function() {
		formSubmit();
	};

	$('#content').on('click', 'a.removeDevice', function() {
		var deviceId = $(this).data('id');
		removeDevice(deviceId);
	});

	$('#content').on('click', 'a.toggleDevice', function() {
		var deviceId = $(this).data('id');
		toggleDevice(deviceId);
	});
})();