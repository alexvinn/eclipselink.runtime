/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.objects;

import java.lang.reflect.Method;

import org.eclipse.persistence.internal.jpa.metadata.MetadataHelper;

/**
 * An object to hold onto a valid EJB 3.0 decorated method.
 * 
 * @author Guy Pelletier
 * @since TopLink 10.1.3/EJB 3.0 Preview
 */
public class MetadataMethod extends MetadataAccessibleObject {
    protected Method m_setMethod;
    
    /**
     * INTERNAL:
     */
    public MetadataMethod(Method getMethod, Method setMethod) {
        super(getMethod);
        
        m_setMethod = setMethod;
        
        setName(getMethod.getName());
        setAttributeName(MetadataHelper.getAttributeNameFromMethodName(getMethod.getName()));
        setRelationType(MetadataHelper.getGenericReturnType(getMethod));
    }
    
    /**
     * INTERNAL:
     */
    public MetadataMethod(Method getMethod) {
        this(getMethod, MetadataHelper.getSetMethod(getMethod, getMethod.getDeclaringClass()));
    }
    
    /**
     * INTERNAL:
     */
    public String getSetMethodName() {
        return m_setMethod.getName();
    }
}
