package com.evi.teamfinderauth.listeners;

import com.evi.teamfinderauth.domain.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

@Getter
@Setter
public class OnAccountDeleteCompleteEvent  extends ApplicationEvent {

    private String appUrl;
    private Locale locale;
    private User user;

    public OnAccountDeleteCompleteEvent(User user,Locale locale , String appUrl){
        super(user);
        this.user = user;
        this.locale = locale;
        this.appUrl = appUrl;
    }
}
