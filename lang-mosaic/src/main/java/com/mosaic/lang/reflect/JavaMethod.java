package com.mosaic.lang.reflect;

import com.mosaic.lang.Immutable;
import com.mosaic.lang.Validate;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.io.Serializable;
import java.lang.reflect.Method;

import static com.mosaic.lang.Validate.isTrue;

/**
 *
 */
public class JavaMethod<T> implements Serializable, Immutable {
    private static final long serialVersionUID = 1288335541622L;
    
//    private static final Logger LOG = LoggerFactory.getLogger( JavaMethod.class );

    static <T> JavaMethod<T> toJavaMethod( JavaClass<T> owningClass, Method method) {
        if ( method == null ) { return null; }

        return new JavaMethod<T>( owningClass, method );
    }


    private JavaClass<T> owningClass;
    private Method targetMethod;
    
    private JavaMethod( JavaClass<T> owningClass, Method method ) {
        isTrue( owningClass.isInstanceOf(method.getDeclaringClass()), "%s must be an instance of %s", owningClass, method.getDeclaringClass() );

        this.owningClass = owningClass;
        this.targetMethod = method;
    }

    @SuppressWarnings({"unchecked"})
    public T interceptMethodCall( final T wrappedObject, final Aspect aspect ) {
        Validate.notNull(wrappedObject, "wrappedObject" );
        Validate.notNull( aspect, "aspect" );



        T proxy = (T) Enhancer.create( owningClass.getJDKClass(), new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method invokedMethod, Object[] args, MethodProxy proxy) throws Throwable {
                if ( !targetMethod.equals(invokedMethod) ) {
//                    if ( LOG.isTraceEnabled() ) {
//                        LOG.trace( "Forwarded " + invokedMethod + " from " +System.identityHashCode(obj) + " to " + System.identityHashCode(wrappedObject));
//                    }

                    return proxy.invoke(wrappedObject, args);
                }

//                if ( LOG.isTraceEnabled() ) {
//                    LOG.trace( "Intercepted " + invokedMethod + " from " +System.identityHashCode(obj) + " to " + System.identityHashCode(wrappedObject));
//                }
                
                return aspect.intercepted(wrappedObject, args, JavaMethod.this );
            }
        });

//        if ( LOG.isTraceEnabled() ) {
//            LOG.trace( "Proxying " + wrappedObject.getClass() + " from " +System.identityHashCode(proxy) + " to " + System.identityHashCode(wrappedObject));
//        }

        return proxy;
    }


    public Object invoke( Object o ) {
        return invoke( o, null );
    }

    public Object invoke(Object o, Object[] args) {
        try {
            return targetMethod.invoke(o, args);
        } catch (Exception e) {
            throw ReflectionException.recast(e);
        }
    }

//    public boolean equals(Object o) {
//        return EqualsBuilder.reflectionEquals( this, o );
//    }

    public String toString() {
        return targetMethod.toString();
    }

    public int hashCode() {
        return targetMethod.hashCode();
    }
}
