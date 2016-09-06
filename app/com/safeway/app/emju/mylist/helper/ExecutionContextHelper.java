package com.safeway.app.emju.mylist.helper;

import com.safeway.app.emju.logging.Logger;
import com.safeway.app.emju.logging.LoggerFactory;

import play.libs.Akka;
import scala.concurrent.ExecutionContext;

public class ExecutionContextHelper {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionContextHelper.class);

    public static ExecutionContext getContext(final String contextName) {
        ExecutionContext context = null;
        try {
            context = Akka.system().dispatchers().lookup(contextName);
        }
        catch (Exception e) {
            LOGGER.warn("Using default context as unable to create context " + contextName);
            context = Akka.system().dispatchers().defaultGlobalDispatcher();
        }

        return context;
    }

}
