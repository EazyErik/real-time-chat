//package chat.websocket;
//
//import chat.data.Message;
//import chat.service.MessageService;
//import chat.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.event.EventListener;
//import org.springframework.messaging.simp.SimpMessageSendingOperations;
//import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.messaging.SessionConnectedEvent;
//import org.springframework.web.socket.messaging.SessionDisconnectEvent;
//
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//@Component
//public class WebSocketChatEventListener {
//    //todo:nach WebSocketChatEventListener Bibliothek googeln vs. TextWebSocketHandler(alte Loesung)
//
//    @Autowired
//    private SimpMessageSendingOperations messagingTemplate;
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private MessageService messageService;
//
//    @EventListener
//    public void connected(SessionConnectedEvent event) {
//        System.out.println(event);
//        String username = extractUsername("" + event.getMessage().getHeaders());
//        System.out.println("User connected: " + username);
//
//        if (username != null) {
//            userService.addUser(username);
//
//
//            messagingTemplate.convertAndSend("/user",userService.getAllUsers() );
//        }
//    }
//
//    public static String extractUsername(String input) {
//        // Define the regex pattern
//        String regex = "username=\\[([^\\]]+)\\]";
//
//        // Compile the pattern
//        Pattern pattern = Pattern.compile(regex);
//
//        // Match the pattern in the input string
//        Matcher matcher = pattern.matcher(input);
//
//        // Extract and return the username if found
//        if (matcher.find()) {
//            return matcher.group(1); // Group 1 contains the captured username
//        }
//
//        // Return null if no username is found
//        return null;
//    }
//
//
////    @EventListener
////    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
////        System.out.println("Received a new web socket connection");
////    }
//
//    @EventListener
//    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
//
//        System.out.println(event);
//        String username = extractUsername("" + event.getMessage().getHeaders());
//
//
//        System.out.println("User disconnected: " + username);
//
//
//        if (username != null) {
//            userService.deleteUser(username);
//
//
////
//            messagingTemplate.convertAndSend("/user",userService.getAllUsers() );
//        }
//    }
//}
