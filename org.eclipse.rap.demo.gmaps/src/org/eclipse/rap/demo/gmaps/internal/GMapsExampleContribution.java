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
