// $Header$
/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
*/

package org.apache.jmeter.control;

import java.io.Serializable;

import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.testelement.property.StringProperty;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterThread;
import org.apache.jmeter.threads.JMeterVariables;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

/**
 * @version   $Revision$
 */
public class WhileController extends GenericController implements Serializable
{
    private static Logger log = LoggingManager.getLoggerForClass();
	private final static String CONDITION = "WhenController.condition"; // $NON-NLS-1$

    public WhileController()
    {
    }


    /* (non-Javadoc)
     * @see org.apache.jmeter.control.Controller#isDone()
     */
    public boolean isDone()
    {
        return false;//conditionTrue() ? super.isDone() : false;
    }

    /*
     * Evaluate the condition, which can be:
     * blank or LAST = was the last sampler OK?
     * ALL = were all samplers OK?
     * otherwise, evaluate the JavaScript condition
     */
    private boolean conditionTrue()
    {
    	String cnd = getCondition();//
    	if (cnd.length() == 0 || "LAST".equalsIgnoreCase(cnd)) {
        	JMeterVariables threadVars = 
        		JMeterContextService.getContext().getVariables();
        	// Use !false rather than true, so that null is treated as true 
       	    return !"false".equalsIgnoreCase(threadVars.get(JMeterThread.LAST_SAMPLE_OK));
    	}
    	if ("ALL".equalsIgnoreCase(cnd)) {
        	JMeterVariables threadVars = 
        		JMeterContextService.getContext().getVariables();
        	// Use !false rather than true, so that null is treated as true 
       	    return !"false".equalsIgnoreCase(threadVars.get(JMeterThread.ALL_SAMPLES_OK));
    	}
        return IfController.evaluateCondition(cnd);
    }

	/* (non-Javadoc)
     * @see org.apache.jmeter.control.GenericController#nextIsNull()
     */
    protected Sampler nextIsNull() throws NextIsNullException
    {
        reInitialize();
        if (conditionTrue())
        {
            return next();
        }
        else
        {
            setDone(true);
            return null;
        }
    }

	/**
	 * @param string the condition
	 */
	public void setCondition(String string) {
		setProperty(new StringProperty(CONDITION, string));
	}


	/**
	 * @return the condition
	 */
	public String getCondition() {
		return getPropertyAsString(CONDITION);
	}
}