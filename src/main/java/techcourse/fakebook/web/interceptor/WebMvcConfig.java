package techcourse.fakebook.web.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import techcourse.fakebook.web.argumentresolver.OptionalSessionUserArgumentResolver;
import techcourse.fakebook.web.argumentresolver.SessionUserArgumentResolver;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private final AlreadyLoginedInterceptor alreadyLoginedInterceptor;
    private final NotLoginedInterceptor notLoginedInterceptor;
    private final VisitorTrackingInterceptor visitorTrackingInterceptor;

    private final SessionUserArgumentResolver sessionUserArgumentResolver;
    private final OptionalSessionUserArgumentResolver optionalSessionUserArgumentResolver;

    public WebMvcConfig(AlreadyLoginedInterceptor alreadyLoginedInterceptor, NotLoginedInterceptor notLoginedInterceptor, VisitorTrackingInterceptor visitorTrackingInterceptor, SessionUserArgumentResolver sessionUserArgumentResolver, OptionalSessionUserArgumentResolver optionalSessionUserArgumentResolver) {
        this.alreadyLoginedInterceptor = alreadyLoginedInterceptor;
        this.notLoginedInterceptor = notLoginedInterceptor;
        this.visitorTrackingInterceptor = visitorTrackingInterceptor;
        this.sessionUserArgumentResolver = sessionUserArgumentResolver;
        this.optionalSessionUserArgumentResolver = optionalSessionUserArgumentResolver;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(alreadyLoginedInterceptor)
                .addPathPatterns("/")
                .addPathPatterns("/users/**")
                .addPathPatterns("/api/users/**")
                .addPathPatterns("/api/login");

        registry.addInterceptor(notLoginedInterceptor)
                .addPathPatterns("/users/**")
                .addPathPatterns("/api/users/**")
                .addPathPatterns("/newsfeed")
                .addPathPatterns("/logout")
                .addPathPatterns("/friendships/candidates")
                .addPathPatterns("/api/chats");

        registry.addInterceptor(visitorTrackingInterceptor)
                .addPathPatterns("/users/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(sessionUserArgumentResolver);
        resolvers.add(optionalSessionUserArgumentResolver);
    }
}
