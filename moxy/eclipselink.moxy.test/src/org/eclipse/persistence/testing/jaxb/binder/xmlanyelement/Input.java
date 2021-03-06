/*******************************************************************************
 * Copyright (c) 1998, 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Denise Smith - October 2012
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.binder.xmlanyelement;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.platform.xml.XMLComparer;
import org.w3c.dom.Node;

@XmlRootElement(name = "root")
public class Input {
    @XmlAnyElement
    protected Object[] elements;

    public boolean equals(Object obj){
        if(obj instanceof Input){
            Input compareObject = (Input)obj;
            if(elements == null){
              return compareObject.elements == null;
            }
            int length = elements.length;
            if(length != compareObject.elements.length){
                return false;
            }
            boolean equals = true;
            for(int i=0; i<length; i++){
                Object next = elements[i];
                Object nextCompare = compareObject.elements[i];
                if(next instanceof Node && nextCompare instanceof Node){
                    XMLComparer comparer = new XMLComparer();
                    equals = comparer.isNodeEqual((Node)next, (Node)nextCompare);
                }else{
                    equals = next.equals(nextCompare);
                }
                if(!equals){
                    return false;
                }
            }
            return equals;
        }
        return false;
    }
}
