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
 *     8/12/2015-2.6 Mythily Parthasarathy
 *       - 474752: Creation of TireDetail
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.orphanremoval;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="OR_TIREDETAIL")
public class TireDetail {
    @Id
    @Column(name="TIRE_ID")
    private int tireId;
    @Column(nullable=true)
    private String detail;

    public void setTireId(int id) {
        this.tireId = id;
    }
    
    public int getTireId() {
        return tireId;
    }
    
    public void setDetail(String detail) {
        this.detail = detail;
    }
    
    public String getDetail() {
        return this.detail;
    }
}
