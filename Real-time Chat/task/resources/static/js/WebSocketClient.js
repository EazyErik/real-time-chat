// class WebSocketChatClient {
//     constructor(url) {
//         this.url = url;
//         this.stompClient = null;
//         this.username = null;
//     }
//
//     connect(username) {
//         this.username = username;
//         const socket = new WebSocket(this.url);
//         this.stompClient = Stomp.over(socket);
//
//         this.stompClient.connect({}, this.onConnected.bind(this), this.onError.bind(this));
//     }
//
//     onConnected() {
//         // Subscribe to the Public Topic
//         this.stompClient.subscribe('/topic/public', this.onMessageReceived.bind(this));
//
//         // Tell your username to the server
//         this.stompClient.send("/app/chat.addUser",
//             {},
//             JSON.stringify({sender: this.username, type: 'JOIN'})
//         );
//
//         console.log('Connected to WebSocket');
//     }
//
//     onError(error) {
//         console.log('Error while connecting to WebSocket: ' + error);
//     }
//
//     onMessageReceived(payload) {
//         const message = JSON.parse(payload.body);
//         // Handle the received message (e.g., update UI)
//         console.log('Received message:', message);
//     }
//
//     sendMessage(messageContent) {
//         if (this.stompClient) {
//             const chatMessage = {
//                 sender: this.username,
//                 content: messageContent,
//                 type: 'CHAT'
//             };
//             this.stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
//         }
//     }
//
//     disconnect() {
//         if (this.stompClient !== null) {
//             this.stompClient.disconnect();
//         }
//         console.log("Disconnected");
//     }
// }
//
// // Usage
// // const chatClient = new WebSocketChatClient('ws://localhost:8080/ws');
// // chatClient.connect('John');
//
// // To send a message
// //chatClient.sendMessage('Hello, everyone!');
//
// // To disconnect
// // chatClient.disconnect();