package chat.service;

import chat.data.Message;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class MessageService {

    List<Message> messages = Collections.synchronizedList(new LinkedList<>());

  public void addMessage(String sender,String content,Instant date){
      messages.add(new Message(sender,content, date));

  }

  public List<Message> getAllMessages(){
      return messages;
  }




}
