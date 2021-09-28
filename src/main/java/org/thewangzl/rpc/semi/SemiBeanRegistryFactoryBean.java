package org.thewangzl.rpc.semi;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
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
import org.thewangzl.rpc.semi.annotation.RefKey;
import org.thewangzl.rpc.semi.annotation.SemiBean;
import org.thewangzl.rpc.semi.annotation.SemiProperty;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.util.StringUtils.tokenizeToStringArray;

public class SemiBeanRegistryFactoryBean implements InitializingBean, ApplicationContextAware, FactoryBean<SemiBeanRegistry> {

    private static final ResourcePatternResolver RESOURCE_PATTERN_RESOLVER = new PathMatchingResourcePatternResolver();
    private static final MetadataReaderFactory METADATA_READER_FACTORY = new CachingMetadataReaderFactory();

    private ApplicationContext applicationContext;

    @Value("${semi.deal-package}")
    private String dealPackage;

    private SemiBeanRegistry semiBeanRegistry;

    private void build(String dealPackage)  {
        semiBeanRegistry = new SemiBeanRegistry();
        Set<Class> classes = scanClass(dealPackage);
        for(Class clazz : classes){
            Set<Ref> refs = this.resolveRef(clazz);
            semiBeanRegistry.put(clazz, refs);
        }
    }

    private Set<Class> scanClass(String dealPackage){
        Set<Class> classes = new HashSet<>();
        String[] packagePatternArray = tokenizeToStringArray(dealPackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
        try {
            for (String packagePattern : packagePatternArray) {
                Resource[] resources = RESOURCE_PATTERN_RESOLVER.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                        + ClassUtils.convertClassNameToResourcePath(packagePattern) + "/**/*.class");
                for (Resource resource : resources) {
                    MetadataReader metadataReader = METADATA_READER_FACTORY.getMetadataReader(resource);
                    Class<?> clazz = ClassUtils.getDefaultClassLoader().loadClass(metadataReader.getClassMetadata().getClassName());
                    if (clazz.isAnnotationPresent(SemiBean.class)) {
                        classes.add(clazz);
                    }
                }
            }
        }catch (IOException e){
            System.err.println(e.getMessage());
        }catch (ClassNotFoundException e){
            System.err.println(e.getMessage());
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
                ref.setArgs(semiField.args());
                Type refType = field.getType();
                Class<?> refClass = ClassUtils.getDefaultClassLoader().loadClass(refType.getTypeName());
                Field[] refTypeFields = refClass.getDeclaredFields();
                for (Field refTypeField : refTypeFields) {
                    RefKey refKey = refTypeField.getAnnotation(RefKey.class);
                    if (refKey != null) {
                        ref.setRefClass(field.getGenericType().getTypeName());
                        ref.setRefKey(refTypeField.getName());
                    }
                }
                refs.add(ref);
            }
        }catch (ClassNotFoundException e){
            System.err.println(e.getMessage());
        }
        return refs;
    }

    private Method getMethod(Object rpcBean, String name) throws ClassNotFoundException {
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
    public void afterPropertiesSet() throws Exception {
        this.build(this.dealPackage);
    }

    public void setDealPackage(String dealPackage) {
        this.dealPackage = dealPackage;
    }

    @Override
    public SemiBeanRegistry getObject() throws Exception {
        if(semiBeanRegistry == null){
            this.afterPropertiesSet();
        }
        return semiBeanRegistry;
    }


    @Override
    public Class<? extends SemiBeanRegistry> getObjectType() {
        return semiBeanRegistry == null ? SemiBeanRegistry.class : semiBeanRegistry.getClass();
    }
}
