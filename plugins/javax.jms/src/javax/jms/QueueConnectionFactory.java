/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License).  You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the license at
 * https://glassfish.dev.java.net/public/CDDLv1.0.html or
 * glassfish/bootstrap/legal/CDDLv1.0.txt.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at glassfish/bootstrap/legal/CDDLv1.0.txt.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 */



package javax.jms;

/** A client uses a <CODE>QueueConnectionFactory</CODE> object to create
  * <CODE>QueueConnection</CODE> objects with a point-to-point JMS provider.
  *
  * <P><CODE>QueueConnectionFactory</CODE> can be used to create a
  * <CODE>QueueConnection</CODE>, from which specialized queue-related objects
  * can be  created. A more general, and recommended,  approach
  * is to use the <CODE>ConnectionFactory</CODE> object.
  *
  *<P> The <CODE>QueueConnectionFactory</CODE> object
  * can be used to support existing code that already uses it.
  *
  * @version     1.1 - February 2, 2002
  * @author      Mark Hapner
  * @author      Rich Burridge
  * @author      Kate Stout
  *
  * @see         javax.jms.ConnectionFactory
  */

public interface QueueConnectionFactory extends ConnectionFactory {

    /** Creates a queue connection with the default user identity.
      * The connection is created in stopped mode. No messages
      * will be delivered until the <code>Connection.start</code> method
      * is explicitly called.
      *
      .
      *
      * @return a newly created queue connection
      *
      * @exception JMSException if the JMS provider fails to create the queue
      *                         connection due to some internal error.
      * @exception JMSSecurityException  if client authentication fails due to
      *                         an invalid user name or password.
      */

    QueueConnection
    createQueueConnection() throws JMSException;


    /** Creates a queue connection with the specified user identity.
      * The connection is created in stopped mode. No messages
      * will be delivered until the <code>Connection.start</code> method
      * is explicitly called.
      *
      * @param userName the caller's user name
      * @param password the caller's password
      *
      * @return a newly created queue connection
      *
      * @exception JMSException if the JMS provider fails to create the queue
      *                         connection due to some internal error.
      * @exception JMSSecurityException  if client authentication fails due to
      *                         an invalid user name or password.
      */

    QueueConnection
    createQueueConnection(String userName, String password)
                         throws JMSException;
}
