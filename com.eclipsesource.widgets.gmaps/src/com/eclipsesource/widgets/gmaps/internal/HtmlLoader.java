/*******************************************************************************
 * Copyright (c) 2010, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package com.eclipsesource.widgets.gmaps.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.eclipse.swt.browser.Browser;


public class HtmlLoader {

  public static void load( Browser browser, String htmlFile ) {
    browser.setText( getHtmlContent( htmlFile ) );
  }

  private static String getHtmlContent( String url ) {
    StringBuilder html = new StringBuilder();
    html.append( getTextFromResource( url, "UTF-8" ) );
    inlineScripts( html );
    return html.toString();
  }

  private static String getTextFromResource( String resourceName, String charset ) {
    try {
      return getTextFromResourceChecked( resourceName, charset );
    } catch( IOException exception ) {
      String message = "Could not read rext from resource: " + resourceName;
      throw new IllegalArgumentException( message, exception );
    }
  }

  private static String getTextFromResourceChecked( String resourceName, String charset )
    throws IOException
  {
    InputStream inputStream = HtmlLoader.class.getClassLoader().getResourceAsStream( resourceName );
    if( inputStream == null ) {
      throw new IllegalArgumentException( "Resource not found: " + resourceName );
    }
    try {
      return getTextFromInputStream( inputStream, charset );
    } finally {
      inputStream.close();
    }
  }

  private static String getTextFromInputStream( InputStream inputStream, String charset )
    throws IOException
  {
    StringBuilder builder = new StringBuilder();
    BufferedReader reader = new BufferedReader( new InputStreamReader( inputStream, charset ) );
    String line = reader.readLine();
    while( line != null ) {
      builder.append( line );
      builder.append( '\n' );
      line = reader.readLine();
    }
    return builder.toString();
  }

  private static void inlineScripts( StringBuilder html ) {
    String srcAttrStr = "src=\"./";
    String quotStr = "\"";
    String tagStr = "<script ";
    String closingTagStr = "</script>";
    String newTagStr = "<script type=\"text/javascript\">";
    int offset = html.length();
    while( ( offset = html.lastIndexOf( tagStr, offset ) ) != -1 ) {
      int closeTag = html.indexOf( closingTagStr, offset );
      int srcAttr = html.indexOf( srcAttrStr, offset );
      if( srcAttr != -1 && srcAttr < closeTag ) {
        int srcAttrStart = srcAttr + srcAttrStr.length();
        int srcAttrEnd = html.indexOf( quotStr, srcAttrStart );
        if( srcAttrEnd != -1 ) {
          String filename = html.substring( srcAttrStart, srcAttrEnd );
          StringBuffer newScriptTag = new StringBuffer();
          newScriptTag.append( newTagStr );
          newScriptTag.append( getTextFromResource( filename, "UTF-8" ) );
          newScriptTag.append( closingTagStr );
          html.replace( offset, closeTag + closingTagStr.length(), newScriptTag.toString() );
        }
      }
      offset--;
    }
  }

}
