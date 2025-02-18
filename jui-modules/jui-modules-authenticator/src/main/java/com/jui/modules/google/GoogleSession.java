package com.jui.modules.google;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoogleSession {
	
    @SerializedName("sub")
    private String sub;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("given_name")
    private String givenName;
    
    @SerializedName("family_name")
    private String familyName;
    
    @SerializedName("picture")
    private String picture;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("email_verified")
    private boolean emailVerified;

}
