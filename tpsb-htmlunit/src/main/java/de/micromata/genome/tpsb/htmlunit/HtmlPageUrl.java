/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   16.09.2008
// Copyright Micromata 16.09.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.tpsb.htmlunit;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Mark a HtmlPageBase with a URL-Part. baseUrl of the application with HtmlPageUrl will be the full URL.
 * 
 * @author roger
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface HtmlPageUrl {
  public String value();
}
