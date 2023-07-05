package com.evi.teamfinderauth.listeners;

import com.evi.teamfinderauth.domain.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

@Getter
@Setter
public class OnEmailChangeCompleteEvent extends ApplicationEvent {

    private String appUrl;
    private Locale locale;
    private User user;
    private String email;

    public OnEmailChangeCompleteEvent(User user,Locale locale,String email , String appUrl){
        super(user);
        this.user = user;
        this.email = email;
        this.locale = locale;
        this.appUrl = appUrl;
    }
}