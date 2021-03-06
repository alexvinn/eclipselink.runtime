/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.persistence.json.bind.internal.unmarshaller;

import org.eclipse.persistence.json.bind.internal.RuntimeTypeInfo;
import org.eclipse.persistence.json.bind.model.ClassModel;
import org.eclipse.persistence.json.bind.model.JsonBindingModel;

/**
 * @author Roman Grigoriadi
 */
public interface CurrentItem<T> extends RuntimeTypeInfo {

    /**
     * Class model containing property for this item.
     * @return class model
     */
    ClassModel getClassModel();

    /**
     * Item wrapper. Null only in case of a root item.
     * @return wrapper item of this item
     */
    CurrentItem<?> getWrapper();

    /**
     * Binding model of this item in wrapper. May be JavaBean property or a container like collection.
     * @return wrapper model.
     */
    JsonBindingModel getWrapperModel();

}
