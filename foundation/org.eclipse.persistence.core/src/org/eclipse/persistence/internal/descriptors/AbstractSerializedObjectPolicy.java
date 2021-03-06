/*******************************************************************************
 * Copyright (c) 2013, 2015  Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     24 April 2013-2.5.1 ailitche
 *       SerializedObjectPolicy initial API and implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.descriptors;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.SerializedObjectPolicy;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * The base class for SerializedObjectPolicy.
 *
 * @author Andrei Ilitchev
 * @since EclipseLink 2.5.1
 */
public abstract class AbstractSerializedObjectPolicy implements SerializedObjectPolicy {
    protected ClassDescriptor descriptor;
    protected DatabaseField field;

    @Override
    public ClassDescriptor getDescriptor() {
        return this.descriptor;
    }

    @Override
    public void setDescriptor(ClassDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public DatabaseField getField() {
        return this.field;
    }

    @Override
    public void setField(DatabaseField field) {
        this.field = field;
    }

    @Override
    public void initializeField(AbstractSession session) {
        if (this.field == null) {
            session.getIntegrityChecker().handleError(DescriptorException.serializedObjectPolicyFieldNotSet(this.descriptor));
            return;
        }
        if (this.descriptor.isChildDescriptor()) {
            SerializedObjectPolicy parentPolicy = this.descriptor.getInheritancePolicy().getParentDescriptor().getSerializedObjectPolicy();
            if (parentPolicy != null && parentPolicy.getField() == this.field) {
                return;
            }
        }
        this.field = this.descriptor.buildField(this.field);
        this.descriptor.getFields().add(this.field);
    }

    @Override
    public AbstractSerializedObjectPolicy clone() {
       try {
            return (AbstractSerializedObjectPolicy) super.clone();
        } catch (CloneNotSupportedException exception) {
            throw new InternalError(exception.getMessage());
        }
    }
}
