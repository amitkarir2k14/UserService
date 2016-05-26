package com.ibm.internal.assignment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Configuration
@EnableWebMvc
//@ComponentScan(basePackages = { "com.ibm.internal.assignment" })
public class WebConfig extends WebMvcConfigurerAdapter {

//	@Bean
//	public ViewResolver cnViewResolver(ContentNegotiationManager cnm) {
//	  ContentNegotiatingViewResolver cnvr =
//	      new ContentNegotiatingViewResolver();
//	  cnvr.setContentNegotiationManager(cnm);
//	  return cnvr;
//	}
//
//	@Override
//	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
//		configurer.defaultContentType(MediaType.APPLICATION_JSON);
//	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable(); // this enables handling static requests with
								// container's
								// default request handler and not spring
								// servlet dispatcher
	}
}
