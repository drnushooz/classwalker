package com.datastax.support;

import java.lang.instrument.*;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.*;

/**
 * Created by abhinavchawade on 11/25/15.
 */
public class ClasswalkerAgent
{
    //private static final Logger log = LoggerFactory.getLogger(ClasswalkerAgent.class);
    public static void premain(String agentArgs,Instrumentation inst)
    {
        //BasicConfigurator.configure();
        inst.addTransformer(new ClassnamePrinter());
        //log.info("======== Loaded Classwalker agent =======");
        System.out.println("======== Loaded Classwalker agent =======");
    }
}
