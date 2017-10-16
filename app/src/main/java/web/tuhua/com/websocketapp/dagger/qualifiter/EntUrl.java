package web.tuhua.com.websocketapp.dagger.qualifiter;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by yangtufa on 2017/7/24.
 */

@Qualifier
@Documented
@Retention(RUNTIME)
public @interface EntUrl {
}
