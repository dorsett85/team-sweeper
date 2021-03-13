package com.cphillipsdorsett.teamsweeper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.io.File;
import java.io.IOException;

public class BundleManifestArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public BundleManifest resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new ClassPathResource("static/bundles/manifest.json").getFile();
        return objectMapper.readValue(file, BundleManifest.class);
    }

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterType().equals(BundleManifest.class);
    }
}
