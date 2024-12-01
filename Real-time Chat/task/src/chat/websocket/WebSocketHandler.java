package chat.websocket;

import chat.service.MessageService;
import chat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private final MessageService messageService;
    private final UserService userService;
   //todo: convert current code to STOMP protocol, use this class: WebSocketChatEventListener

    // Constructor injection
    public WebSocketHandler(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }



    // Track all active WebSocket sessions
    private static final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

    @Override
    public void afterConnectionEstablished(WebSocketSession session
                                           ) throws Exception {

        String uri = session.getUri().toString();
        String username = null;

        if (uri.contains("username=")) {
            System.out.println("1");
            username = uri.split("username=")[1];

        }

        if (username != null) {
            System.out.println("2");
            // Store the username in session attributes for future reference
            session.getAttributes().put("username", username);
            System.out.println("User connected: " + username);
        }
        
        userService.addUser(username);
        sessions.add(session); // Add session to the set
        broadcastMessage("users," + userService.getAllUsersAsString());

        System.out.println("New connection established: " + session.getId());

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String[] elements = message.getPayload().split(",");
        String userMessage = elements[0];
        String userName = elements[1];
        Instant date = Instant.now();

        messageService.addMessage(userName,userMessage,date);
        // Broadcast message to all connected clients
        broadcastMessage("new message," +userMessage + "," + userName + "," + date);
    }




    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {


        String uri = session.getUri().toString();
        String username = null;

        if (uri.contains("username=")) {
            System.out.println("1");
            username = uri.split("username=")[1];

        }

        if (username != null) {
            System.out.println("2");
            // Store the username in session attributes for future reference
            session.getAttributes().put("username", username);
            System.out.println("User disconnected: " + username);
        }
        System.out.println("username: " + username);

        userService.deleteUser(username);
        broadcastMessage("users," + userService.getAllUsersAsString());
        sessions.remove(session);
      // Remove session from the set
        System.out.println("Connection closed: " + session.getId());
    }

    // Helper method to broadcast a message to all sessions
    private void broadcastMessage(String message) throws IOException {
        synchronized (sessions) {
            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(message));
                }
            }
        }
    }
}

