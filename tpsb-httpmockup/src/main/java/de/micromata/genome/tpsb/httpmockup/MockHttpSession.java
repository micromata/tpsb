//
// Copyright (C) 2010-2016 Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package de.micromata.genome.tpsb.httpmockup;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

/**
 * Simple mock implementation of HttpSession that implements most basic operations.
 * 
 * @author Tim Fennell
 * @since Stripes 1.1.1
 */
public class MockHttpSession implements HttpSession
{
  private long creationTime = System.currentTimeMillis();

  private String sessionId = String.valueOf(new Random().nextLong());

  private ServletContext context;

  private Map<String, Object> attributes = new HashMap<String, Object>();

  /** Default constructor which provides the session with access to the context. */
  public MockHttpSession(ServletContext context)
  {
    this.context = context;

  }

  /** Returns the time in milliseconds when the session was created. */
  @Override
  public long getCreationTime()
  {
    return this.creationTime;
  }

  /** Returns an ID that was randomly generated when the session was created. */
  @Override
  public String getId()
  {
    return this.sessionId;
  }

  /** Always returns the current time. */
  @Override
  public long getLastAccessedTime()
  {
    return System.currentTimeMillis();
  }

  /** Provides access to the servlet context within which the session exists. */
  @Override
  public ServletContext getServletContext()
  {
    return this.context;
  }

  /** Sets the servlet context within which the session exists. */
  public void setServletContext(ServletContext context)
  {
    this.context = context;
  }

  /** Has no effect. */
  @Override
  public void setMaxInactiveInterval(int i)
  {
  }

  /** Always returns Integer.MAX_VALUE. */
  @Override
  public int getMaxInactiveInterval()
  {
    return Integer.MAX_VALUE;
  }

  /** Deprecated method always returns null. */
  @Override
  public HttpSessionContext getSessionContext()
  {
    return null;
  }

  /** Returns the value of the named attribute from an internal Map. */
  @Override
  public Object getAttribute(String key)
  {
    return this.attributes.get(key);
  }

  /** Deprecated method. Use getAttribute() instead. */
  @Override
  public Object getValue(String key)
  {
    return getAttribute(key);
  }

  /** Returns an enumeration of all the attribute names in the session. */
  @Override
  public Enumeration getAttributeNames()
  {
    return Collections.enumeration(this.attributes.keySet());
  }

  /** Returns a String[] of all the attribute names in session. Deprecated. */
  @Override
  public String[] getValueNames()
  {
    return this.attributes.keySet().toArray(new String[this.attributes.size()]);
  }

  /** Stores the value in session, replacing any existing value with the same key. */
  @Override
  public void setAttribute(String key, Object value)
  {
    this.attributes.put(key, value);
  }

  /** Stores the value in session, replacing any existing value with the same key. */
  @Override
  public void putValue(String key, Object value)
  {
    setAttribute(key, value);
  }

  /** Removes any value stored in session with the key supplied. */
  @Override
  public void removeAttribute(String key)
  {
    this.attributes.remove(key);
  }

  /** Removes any value stored in session with the key supplied. */
  @Override
  public void removeValue(String key)
  {
    removeAttribute(key);
  }

  /** Clears the set of attributes, but has no other effect. */
  @Override
  public void invalidate()
  {
    this.attributes.clear();
  }

  /** Always returns false. */
  @Override
  public boolean isNew()
  {
    return false;
  }
}