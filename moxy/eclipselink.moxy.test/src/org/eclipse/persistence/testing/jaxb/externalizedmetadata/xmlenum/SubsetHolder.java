/*******************************************************************************
 * Copyright (c) 2011, 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *   Denise Smith  - Dec 2012
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlenum;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SubsetHolder {

    public String name;
    public CoinSubset coin;

    public boolean equals(Object obj){
        if(!(obj instanceof SubsetHolder)){
            return false;
        }
        SubsetHolder compare = (SubsetHolder)obj;
        if(!name.equals(compare.name)){
            return false;
        }
        if(!coin.equals(compare.coin)){
            return false;
        }
        return true;
    }
}
