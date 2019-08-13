package UMC.Web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * UMC功能注册
 */
@Target({ElementType.TYPE,ElementType.PACKAGE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Mapping {

    /**
     * 注册的model
     *
     * @return
     */
    String model() default "";

    /**
     * 注册的指令
     *
     * @return
     */
    String cmd() default "";

    /**
     * 功能描述
     *
     * @return
     */
    String desc() default "";

    WebAuthType auth() default WebAuthType.all;
}

