package com.staryea.autoconfigure.es;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = MyEsProperties.ES_PREFIX)
public class MyEsProperties {
    public static final String ES_PREFIX = "staryea.es";

    private String username ;
    private String password ;
    private String restHosts;
    private Map<String, String> properties = new HashMap<>();

    
    public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


    public String getRestHosts() {
		return restHosts;
	}

	public void setRestHosts(String restHosts) {
		this.restHosts = restHosts;
	}

	public Map<String, String> getProperties() {
        return this.properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

}
