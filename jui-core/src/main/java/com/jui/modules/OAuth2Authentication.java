package com.jui.modules;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

@Builder
@Getter
@Accessors(fluent = true)
public class OAuth2Authentication {
	
	String clientId;
	String clientSecret;
	String callbackUrl;
	String callbackUrlContext;

}
