package com.datastax.support;

import org.objectweb.asm.*;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.net.URL;
import java.security.ProtectionDomain;

//import org.slf4j.*;

/**
 * Created by abhinavchawade on 11/25/15.
 */
public class ClassnamePrinter implements ClassFileTransformer
{
    //private static final Logger log = LoggerFactory.getLogger(ClassnamePrinter.class);
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException
    {
        ClassReader classReader = new ClassReader(classfileBuffer);
        ClassWriter classWriter = new ClassWriter(classReader,0);
        SerialVersionUIDReader classVisitor = new SerialVersionUIDReader(classWriter);
        classReader.accept(classVisitor,0);
        System.out.printf("%s(%d) is being loaded by %s from %s%n", className.replace('/', '.'), classVisitor.getClassUID(), loader.getClass().getCanonicalName(), findJarByClass(loader, className));
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

class SerialVersionUIDReader extends ClassVisitor
{
    private long classUID;

    public SerialVersionUIDReader(final ClassVisitor cv)
    {
        super(Opcodes.ASM5,cv);
    }

    @Override
    public FieldVisitor visitField(int access, String fieldName, String desc, String signature, Object value)
    {
        classUID = 1l;
        if(fieldName.equals("serialVersionUID"))
            classUID = (Long)value;
         return super.visitField(access, fieldName, desc, signature, value);
    }

    public long getClassUID()
    {
        return classUID;
    }
}
