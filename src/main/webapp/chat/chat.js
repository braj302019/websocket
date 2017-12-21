$(document).ready(
		function() {
			var socket = new WebSocket(
					"ws://localhost:8080/websocket/actions/chat");
			socket.onmessage = onMessage;
			var messageCount = 0;

			function onMessage(event) {
				var data = JSON.parse(event.data);
				if (data.action === "add") {
					printMessage(data);
					messageCount++;
				}
			}

			function addMessage(message) {
				var action = {
					action : "add",
					message : message
				};
				socket.send(JSON.stringify(action));
			}

			function printMessage(data) {
				var when = new Date(data.when);
				var hours = when.getHours() % 12;
				var minutes = when.getMinutes();
				var seconds = when.getSeconds();
				var timestamp = ((hours < 10 ? '0' : '') + hours) + ':'
						+ ((minutes < 10 ? '0' : '') + minutes) + ':'
						+ ((seconds < 10 ? '0' : '') + seconds);

				var chatMessage = $('<div>').addClass('chatMessage');
				if (messageCount % 2 == 0) {
					chatMessage.addClass('even');
				}
				var chatMessageTimestamp = $('<div>').addClass(
						'chatMessageTimestamp').text(timestamp);
				var chatMessageText = $('<div>').addClass('chatMessageText')
						.text(data.message);
				chatMessage.append(chatMessageTimestamp);
				chatMessage.append(chatMessageText);

				$('.chatInnerContainer').append(chatMessage);
			}

			function formSubmit() {
				var message = $('#chatInput').val() || '';
				if (message.length > 0) {
					addMessage(message);
					$('#chatInput').val('');
				}
			}

			$('#chatButton').on('click', function() {
				formSubmit();
			});

			$('#chatButton').on('keydown', function() {
				if (event.which == 13) {
					formSubmit();
				}
			});

		});