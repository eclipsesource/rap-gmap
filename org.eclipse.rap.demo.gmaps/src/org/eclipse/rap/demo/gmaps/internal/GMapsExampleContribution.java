/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.demo.gmaps.internal;

import org.eclipse.rap.examples.IExampleContribution;
import org.eclipse.rap.examples.IExamplePage;


public class GMapsExampleContribution implements IExampleContribution {

  public String getId() {
    return "gmaps";
  }

  public String getTitle() {
    return "Google Maps";
  }

  public IExamplePage createPage() {
    return new GMapsExamplePage();
  }
}
