package guru.springframework.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

import java.io.Serializable;

@Component("user")
@Scope(value = "session")
public class UserBean implements Serializable
{

    private String name;
    private String address;
    private String sessionId;


    public UserBean()
    {
        sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
