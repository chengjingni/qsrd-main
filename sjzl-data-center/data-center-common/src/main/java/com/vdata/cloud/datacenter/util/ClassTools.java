package com.vdata.cloud.datacenter.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/**
 * 通过包名获取class
 *
 * @author zhangdi
 * @date 2020-09-15 15:09:16
 */
public class ClassTools {
	private static final Logger log = LoggerFactory.getLogger(ClassTools.class);
	private static final String RESOURCE_PATTERN = "/**/*.class";
	public  static Set<Class<?>> findPathMatchingResources(String locationPattern)   {
		// 第一个class类的集合
		Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
		try {
			String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
					ClassUtils.convertClassNameToResourcePath(locationPattern) + RESOURCE_PATTERN;
			ResourcePatternResolver resourceLoader = new PathMatchingResourcePatternResolver();
			Resource[] source = resourceLoader.getResources(pattern);
			MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resourceLoader);
			//System.out.println("length:" + source.length);
			for (Resource resource : source) {
				if (resource.isReadable()) {
					MetadataReader reader = readerFactory.getMetadataReader(resource);
					String className = reader.getClassMetadata().getClassName();
					classes.add(Class.forName(className));
				}
			}
		}catch (Exception e){
			log.error("寻找符合条件的包失败",e);
		}
		return classes;
	}

}
