package org.apache.jmeter.modifiers;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.engine.event.IterationEvent;
import org.apache.jmeter.engine.event.IterationListener;
import org.apache.jmeter.processor.PreProcessor;
import org.apache.jmeter.testelement.property.BooleanProperty;
import org.apache.jmeter.testelement.property.CollectionProperty;
import org.apache.jmeter.testelement.property.PropertyIterator;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;

/**
 * @author Administrator
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 */
public class UserParameters extends ConfigTestElement implements Serializable, PreProcessor,IterationListener
{

    public static final String NAMES = "UserParameters.names";
    public static final String THREAD_VALUES = "UserParameters.thread_values";
    public static final String PER_ITERATION = "UserParameters.per_iteration";
    private int counter = 0;

    public CollectionProperty getNames()
    {
        return (CollectionProperty) getProperty(NAMES);
    }

    public CollectionProperty getThreadLists()
    {
        return (CollectionProperty) getProperty(THREAD_VALUES);
    }

    /**
     * The list of names of the variables to hold values.  This list must come in
     * the same order as the sub lists that are given to setThreadLists(List).
     */
    public void setNames(Collection list)
    {
        setProperty(new CollectionProperty(NAMES, list));
    }

    /**
         * The list of names of the variables to hold values.  This list must come in
         * the same order as the sub lists that are given to setThreadLists(List).
         */
    public void setNames(CollectionProperty list)
    {
        setProperty(list);
    }

    /**
     * The thread list is a list of lists.  Each list within the parent list is a
     * collection of values for a simulated user.  As many different sets of 
     * values can be supplied in this fashion to cause JMeter to set different 
     * values to variables for different test threads.
     */
    public void setThreadLists(Collection threadLists)
    {
        setProperty(new CollectionProperty(THREAD_VALUES, threadLists));
    }

    /**
         * The thread list is a list of lists.  Each list within the parent list is a
         * collection of values for a simulated user.  As many different sets of 
         * values can be supplied in this fashion to cause JMeter to set different 
         * values to variables for different test threads.
         */
    public void setThreadLists(CollectionProperty threadLists)
    {
        setProperty(threadLists);
    }

    private synchronized CollectionProperty getValues()
    {
        CollectionProperty threadValues = (CollectionProperty) getProperty(THREAD_VALUES);
        if (threadValues.size() > 0)
        {
            return (CollectionProperty) threadValues.get(JMeterContextService.getContext().getThreadNum() % threadValues.size());
        }
        else
        {
            return new CollectionProperty("noname", new LinkedList());
        }
    }
    
    public boolean isPerIteration()
    {
        return getPropertyAsBoolean(PER_ITERATION);
    }
    
    public void setPerIteration(boolean perIter)
    {
        setProperty(new BooleanProperty(PER_ITERATION,perIter));
    }

    public void process()
    {
        if(!isPerIteration())
        {
            setValues();
        }
    }

    private void setValues()
    {
        PropertyIterator namesIter = getNames().iterator();
        PropertyIterator valueIter = getValues().iterator();
        JMeterVariables jmvars = JMeterContextService.getContext().getVariables();
        while (namesIter.hasNext() && valueIter.hasNext())
        {
            String name = namesIter.next().getStringValue();
            String value = valueIter.next().getStringValue();
            jmvars.put(name, value);
        }
    }

    /**
     * @see org.apache.jmeter.testelement.ThreadListener#iterationStarted(int)
     */
    public void iterationStart(IterationEvent event)
    {
        if(isPerIteration())
        {
            setValues();
        }
    }

    /**
     * @see org.apache.jmeter.testelement.ThreadListener#setJMeterVariables(org.apache.jmeter.threads.JMeterVariables)
     */
    public void setJMeterVariables(JMeterVariables jmVars)
    {}

}
