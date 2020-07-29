package ru.lanit.citrus;

import com.consol.citrus.dsl.endpoint.CitrusEndpoints;
import com.consol.citrus.http.client.HttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.lanit.interceptor.CitrusHttpInterceptor;

import java.util.Collections;

@Configuration
public class EndpointConfig {

    @Bean(name = "restClient")
    public HttpClient restClient() {
        HttpClient httpClient = CitrusEndpoints.http()
                .client()
                .requestUrl("https://reqres.in/api/")
                .build();
        httpClient.getEndpointConfiguration().setClientInterceptors(Collections
                .singletonList(new CitrusHttpInterceptor()));
        return httpClient;
    }
}
