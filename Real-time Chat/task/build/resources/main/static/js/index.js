
var socket;
var username;
var initMessages;
var users;

const createWebSocketConnection = () => {
console.log("new code")
 //   const chatClient = new WebSocketChatClient('http://localhost:28852/ws');
//chatClient.connect(username)
    // WebSocket endpoint
//     const socketUrl = 'http://localhost:28852/ws'; // Replace with your server URL in production
//
// // Create a SockJS connection
//     const socket = new SockJS(socketUrl);
//
// // Create a STOMP client
//     const stompClient = new StompJs.Client({
//         webSocketFactory: () => socket,
//         reconnectDelay: 5000, // Reconnect every 5 seconds if disconnected
//         debug: (str) => {
//             console.log(str); // Debug messages from STOMP.js
//         },
//         connectHeaders: {
//             username: username, // Send username as a header
//         },
//
//     });
//
// // Handle connection
//     stompClient.onConnect = () => {
//         console.log('Connected to WebSocket server');
//
//         // Subscribe to a topic
//         // stompClient.subscribe('/topic/public', (message) => {
//         //     console.log('Received:', message.body);
//         // });
//
//         // Send a test message
//         stompClient.publish({
//             destination: '/app/message',
//             body: JSON.stringify({ sender: 'User', content: 'Hello, World!' }),
//         });
//
//     };
//
// // Handle errors
//     stompClient.onStompError = (frame) => {
//         console.error('Broker error:', frame.headers['message']);
//         console.error('Details:', frame.body);
//     };
//
// // Start the connection
//     stompClient.activate();





   socket = new WebSocket(`http://localhost:28852/ws?username=${username}`);


    socket.onopen = function () {
        console.log("WebSocket connection opened");
    };

    socket.onmessage = function (event) {

        var textMessage = event.data;
        console.log("Received message: " + textMessage);
        let textMessages = textMessage.split(",");
        if(textMessages[0] == "new message") {
            let message = textMessages[1];
            let sender = textMessages[2];
            let date = textMessages[3];
            displayMessage(sender,message,date)
        }else if(textMessages[0] == "users") {
            users = textMessages.slice(1);
            users = users.filter(user => user !== username);
            console.log(users);
            renderUserList();
        }


    };

    socket.onclose = function () {
        console.log("WebSocket connection closed");
    };

}

const displayMessage = (sender,message,date) =>{
    const container = document.getElementById("messages")


    const messageContainer = document.createElement("div")



    messageContainer.classList.add("message-container")

    const senderElement = document.createElement("div");
    senderElement.classList.add("sender")
    senderElement.textContent = sender
    messageContainer.appendChild(senderElement)

    const dateElement = document.createElement("div");
    dateElement.classList.add("date")
    dateElement.textContent = date
    messageContainer.appendChild(dateElement)

    const messageElement = document.createElement("div");
    messageElement.classList.add("message")
    messageElement.textContent = message;
    messageContainer.appendChild(messageElement)

// insert created tag at the end of the existing container
    container.appendChild(messageContainer)
    messageContainer.scrollIntoView({"behavior": "smooth"})
}

const fetchMessages = async () => {
    try {
        // API-Aufruf zum Endpunkt '/messages/all'
        const response = await fetch('http://localhost:28852/messages/all');

        // Prüfen, ob der Aufruf erfolgreich war
        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        // Umwandeln der Antwort in JSON
        const messages = await response.json();

        // Verarbeitung oder Anzeige der Daten
        console.log('Messages:', messages);
       return messages
    } catch (error) {
        console.error('Error fetching messages:', error);
    }
}


const login = async () => {
     username = document.getElementById("input-username").value;

    let chat = document.getElementById("chat");
    chat.innerHTML = `
    <div id="sidebar">
 
        <div id="users">
  
</div>
    </div>
    <div id="messages-container">
        <div id="messages"></div>
        <div id="input-container">
            <input id="input-msg" />
            <button id="send-msg-btn" onClick="onSendClick()">Send</button>
        </div>
    </div>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.5.0/sockjs.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
    `
    document.getElementById("login").remove();
    createWebSocketConnection()
    initMessages = await fetchMessages()
    fetchUsers()
    console.log(initMessages)
    initMessages.forEach(message => displayMessage(message.sender,message.content,message.date))
}

const renderUserList = () => {
    const userList = document.getElementById("users"); // Reference the dynamic user list
    userList.innerHTML = ""; // Clear existing users
    users.forEach(user => {
        const userItem = document.createElement("div");
        userItem.classList.add("user");
        userItem.textContent = user;
        userList.appendChild(userItem);
    });
}


const onSendClick = () => {
    const message = getMessage();
    if (message) {
        socket.send(message + "," + username);
        document.getElementById('input-msg').innerText = "";
        document.getElementById('input-msg').innerHTML = "";
        document.getElementById('input-msg').value = "";

        return true;
    }
    return false;

}

const getMessage = () => {
    return document.getElementById("input-msg").value;
}

function deleteTextArea() {
    const textArea = document.getElementById('input-msg');
    if (textArea) {
        textArea.remove(); // Remove the textarea from the DOM
    }
}

const fetchUsers = async () => {
    try {
        // API-Aufruf zum Endpunkt '/messages/all'
        const response = await fetch('http://localhost:28852/users/all');

        // Prüfen, ob der Aufruf erfolgreich war
        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        // Umwandeln der Antwort in JSON
        users = await response.json();

        // Verarbeitung oder Anzeige der Daten
        console.log('Users:', users);
        renderUserList()
    } catch (error) {
        console.error('Error fetching messages:', error);
    }
}