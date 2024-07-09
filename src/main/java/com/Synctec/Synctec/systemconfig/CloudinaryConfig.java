package com.Synctec.Synctec.systemconfig;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CloudinaryConfig {
    private final PropertiesConfiguration configuration;

//    private String cloudName = configuration.getString("cloudinary.name");
    private String cloudName = "dzuygn6kw";
//    private String apiKey = configuration.getString("cloudinary.apikey");
    private String apiKey = "236147183542475";
//    private String apiSecret = configuration.getString("cloudinary.apikey");
    private String apiSecret = "ejpiqwFaGKETpzDQXFAHN0ubs3M";

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }




}
