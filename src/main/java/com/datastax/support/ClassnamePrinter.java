package com.datastax.support;

import java.lang.instrument.*;
import java.security.ProtectionDomain;
import java.net.*;
import java.io.*;

import org.slf4j.*;

/**
 * Created by abhinavchawade on 11/25/15.
 */
public class ClassnamePrinter implements ClassFileTransformer
{
    //private static final Logger log = LoggerFactory.getLogger(ClassnamePrinter.class);
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException
    {
        System.out.printf("%s is being loaded by %s from %s\n", className.replace('/', '.'), loader.getClass().getCanonicalName(), findJarByClass(loader, className));
        //log.info(className + " is being loaded by " + loader.getClass().getCanonicalName());
        return classfileBuffer;
    }

    /**
     * Return name of the jar in current classloader resources which supplies the class using reflection
     * @param loader The classloader in current context
     * @param className Name of the class for which jar name needs to be determined
     * @return Path of the jar or directory
     */
    private String findJarByClass(ClassLoader loader,String className)
    {
        String classLocation = new String("");
        try
        {
            URL location = loader.getResource(className+".class");
            if(location == null)
                classLocation = "(none)";
            else
                classLocation = location.getPath();

            if(classLocation.startsWith("file:"))
                classLocation = classLocation.substring(classLocation.indexOf("file:")+5,classLocation.length());

            if(classLocation.contains(".jar"))
                classLocation = classLocation.substring(0,classLocation.indexOf('!'));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return classLocation;
    }
}
