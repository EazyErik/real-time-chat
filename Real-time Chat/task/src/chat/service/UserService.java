package chat.service;

import chat.data.Message;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
@Service
public class UserService {

    List<String> users = Collections.synchronizedList(new LinkedList<>());

    public void addUser(String user){
        users.add(user);

    }
    public void deleteUser(String user){
        users.remove(user);
    }

    public List<String> getAllUsers(){
        return users;
    }


    public String getAllUsersAsString() {
        StringBuilder result = new StringBuilder();
        for(String user : users){
           result.append(user).append(",");
        }
        result.deleteCharAt(result.length()-1);
        return result.toString();
    }
}
