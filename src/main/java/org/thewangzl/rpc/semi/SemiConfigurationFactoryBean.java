package org.thewangzl.rpc.semi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.thewangzl.rpc.semi.annotation.*;
import org.thewangzl.rpc.semi.type.WrapperType;
import org.thewangzl.rpc.semi.util.ReflectUtil;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

import static org.springframework.util.StringUtils.tokenizeToStringArray;

public class SemiConfigurationFactoryBean implements InitializingBean, ApplicationContextAware, FactoryBean<SemiConfiguration> {

    private static Logger log = LoggerFactory.getLogger(SemiConfigurationFactoryBean.class);

    private static final ResourcePatternResolver RESOURCE_PATTERN_RESOLVER = new PathMatchingResourcePatternResolver();
    private static final MetadataReaderFactory METADATA_READER_FACTORY = new CachingMetadataReaderFactory();

    private ApplicationContext applicationContext;

    private SemiConfiguration configuration;

    private String semiBeanPackage;

    private String typeHandlersPackage;

    private void build()  {
        configuration = new SemiConfiguration();
        Set<Class> classes = scanClass(semiBeanPackage,SemiBean.class);
        for(Class clazz : classes){
            Set<Ref> refs = this.resolveRef(clazz);
            configuration.getSemiBeanRegistry().put(clazz.getName(), refs);
        }
        if(StringUtils.hasText(typeHandlersPackage)) {
            classes = scanClass(typeHandlersPackage, WrapperType.class);
            for(Class clazz : classes){
                configuration.getTypeHandlerRegistry().register(clazz);
            }

        }
    }

    private Set<Class> scanClass(String dealPackage,Class<? extends Annotation> assignableType){
        Set<Class> classes = new HashSet<>();
        String[] packagePatternArray = tokenizeToStringArray(dealPackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
        try {
            for (String packagePattern : packagePatternArray) {
                Resource[] resources = RESOURCE_PATTERN_RESOLVER.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                        + ClassUtils.convertClassNameToResourcePath(packagePattern) + "/**/*.class");
                for (Resource resource : resources) {
                    MetadataReader metadataReader = METADATA_READER_FACTORY.getMetadataReader(resource);
                    Class<?> clazz = ClassUtils.getDefaultClassLoader().loadClass(metadataReader.getClassMetadata().getClassName());
                    if (clazz.isAnnotationPresent(assignableType)) {
                        classes.add(clazz);
                    }
                }
            }
        }catch (IOException e){
            log.error(e.getMessage());
        }catch (ClassNotFoundException e){
            log.error(e.getMessage());
        }
        return classes;
    }

    private Set<Ref> resolveRef(Class<?> clazz) {
        Set<Ref> refs = new HashSet<>();
        try {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                SemiProperty semiField = field.getAnnotation(SemiProperty.class);
                if (semiField == null) {
                    continue;
                }
                Ref ref = new Ref();
                ref.setField(field.getName());
                ref.setRpcClass(semiField.rpcClass());
                Object rpcBean = applicationContext.getBean(semiField.rpcClass());
                Method method = getMethod(rpcBean, semiField.rpcMethod());
                Method listMethod = getMethod(rpcBean, semiField.rpcListMethod());

                ref.setMethod(method);
                ref.setListMethod(listMethod);
                //
                parseArg(field, ref);
                parseRefKey(field, ref);
                if(ref.getRefKey() == null){
                    log.error(clazz.getName() +"."+ field.getName()+" not support");
                    continue;
                }
                refs.add(ref);
            }
        }catch (ClassNotFoundException e){
            log.error(e.getMessage());
        }
        return refs;
    }

    private void parseArg(Field field, Ref ref) {
        ExtArg[] extArgs = field.getAnnotationsByType(ExtArg.class);
        if(extArgs != null){
            for(ExtArg extArg : extArgs){
                String value = extArg.value();
                int index = extArg.index();
                ArgMapping argMapping = new ArgMapping();
                argMapping.setIndex(index);
                argMapping.setValue(value);
                ref.addArg(argMapping);
            }
        }
    }

    private void parseRefKey(Field field, Ref ref) throws ClassNotFoundException {
        Type type = ReflectUtil.getActualType(field.getGenericType());
        if(type == null){
            return ;
        }
        Class<?> refClass = ClassUtils.getDefaultClassLoader().loadClass(type.getTypeName());
        Field[] refTypeFields = refClass.getDeclaredFields();
        for (Field refTypeField : refTypeFields) {
            RefKey refKey = refTypeField.getAnnotation(RefKey.class);
            if (refKey != null) {
                ref.setRefClass(field.getGenericType().getTypeName());
                ref.setRefKey(refTypeField.getName());
                ref.setRefKeyIndex(refKey.index());
                return;
            }
        }
    }

    private Method getMethod(Object rpcBean, String name){
        Method[] allOfMethods = rpcBean.getClass().getDeclaredMethods();
        for(Method method : allOfMethods) {
            if(method.getName().equals(name)) return method;
        }
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() {
        if(this.semiBeanPackage == null){
            log.error("semiBeanPackage not set");
            return;
        }
        this.build();
    }

    public void setSemiBeanPackage(String semiBeanPackage) {
        this.semiBeanPackage = semiBeanPackage;
    }

    public void setTypeHandlersPackage(String typeHandlersPackage) {
        this.typeHandlersPackage = typeHandlersPackage;
    }

    @Override
    public SemiConfiguration getObject(){
        if(configuration == null){
            this.afterPropertiesSet();
        }
        return configuration;
    }

    @Override
    public Class<? extends SemiConfiguration> getObjectType() {
        return configuration == null ? SemiConfiguration.class : configuration.getClass();
    }
}
