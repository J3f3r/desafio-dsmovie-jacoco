
package com.devsuperior.dsmovie.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final MediaType DSMOVIE_V1 =
            MediaType.parseMediaType(
                    "application/vnd.devsuperior.dsmovie-v1+json"
            );

    @Override
    public void extendMessageConverters(
            List<HttpMessageConverter<?>> converters) {

        boolean jackson3Found = false;

        for (HttpMessageConverter<?> converter : converters) {

            if (converter instanceof JacksonJsonHttpMessageConverter jsonConverter) {

                List<MediaType> supportedMediaTypes =
                        new ArrayList<>(jsonConverter.getSupportedMediaTypes());

                if (!supportedMediaTypes.contains(DSMOVIE_V1)) {
                    supportedMediaTypes.add(DSMOVIE_V1);
                }

                jsonConverter.setSupportedMediaTypes(supportedMediaTypes);

                jackson3Found = true;
            }
        }

        // Garante a existência de um conversor Jackson 3
        // caso nenhum tenha sido registrado.
        if (!jackson3Found) {

            JacksonJsonHttpMessageConverter jsonConverter =
                    new JacksonJsonHttpMessageConverter();

            List<MediaType> supportedMediaTypes =
                    new ArrayList<>(jsonConverter.getSupportedMediaTypes());

            if (!supportedMediaTypes.contains(DSMOVIE_V1)) {
                supportedMediaTypes.add(DSMOVIE_V1);
            }

            jsonConverter.setSupportedMediaTypes(supportedMediaTypes);

            converters.add(0, jsonConverter);
        }
    }
}
