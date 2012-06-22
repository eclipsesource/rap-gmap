/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.demo.gmaps.internal;

import org.eclipse.rap.examples.ExampleUtil;
import org.eclipse.rap.examples.IExamplePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;

import com.eclipsesource.widgets.gmaps.GMap;
import com.eclipsesource.widgets.gmaps.LatLng;
import com.eclipsesource.widgets.gmaps.MapAdapter;


public class GMapsExamplePage implements IExamplePage {

  private static final LatLng INIT_CENTER = new LatLng( 33.0, 5.0 );
  private static final int INIT_ZOOM = 2;
  private static final int INIT_TYPE = GMap.TYPE_HYBRID;

  private GMap gmap;
  private Text addressText;
  private Text latText;
  private Text lonText;
  private boolean internalChange = false;

  public void createControl( Composite parent ) {
    parent.setLayout( ExampleUtil.createMainLayout( 3 ) );
    Composite mapArea = createMapArea( parent );
    GridData mapData = ExampleUtil.createFillData();
    mapData.horizontalSpan = 2;
    mapArea.setLayoutData( mapData );
    Composite controlsArea = createControlsArea( parent );
    controlsArea.setLayoutData( ExampleUtil.createFillData() );
  }

  private Composite createMapArea( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( ExampleUtil.createGridLayout( 1, false, true, true ) );
    GMap map = createMap( composite );
    map.setLayoutData( ExampleUtil.createFillData() );
    return composite;
  }

  private Composite createControlsArea( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( ExampleUtil.createGridLayout( 2, false, true, true ) );
    createMapTypeControl( composite );
    createCenterControl( composite );
    createZoomControl( composite );
    createAddressControl( composite );
    createMarkerControl( composite );
    return composite;
  }

  private GMap createMap( Composite parent ) {
    gmap = new GMap( parent, SWT.BORDER );
    gmap.setCenter( INIT_CENTER );
    gmap.setZoom( INIT_ZOOM );
    gmap.setType( INIT_TYPE );
    return gmap;
  }

  private void createCenterControl( Composite parent ) {
    ExampleUtil.createHeading( parent, "Location and Zoom", 2 );
    new Label( parent, SWT.None ).setText( "Lat:" );
    latText = new Text( parent, SWT.BORDER );
    latText.setLayoutData( ExampleUtil.createHorzFillData() );
    latText.setText( Float.toString( ( float )INIT_CENTER.latitude ) );
    new Label( parent, SWT.None ).setText( "Lon:" );
    lonText = new Text( parent, SWT.BORDER );
    lonText.setLayoutData( ExampleUtil.createHorzFillData() );
    lonText.setText( Float.toString( ( float )INIT_CENTER.longitude ) );
    ModifyListener listener = new ModifyListener() {
      public void modifyText( ModifyEvent event ) {
        LatLng newCenter = getLatLonFromTextFields();
        if( !internalChange && newCenter != null ) {
          // Prevent the map listeners from setting text again, as it would reset caret position 
          internalChange = true;
          gmap.setCenter( newCenter );
          internalChange = false;
        }
      }
    };
    gmap.addMapListener( new MapAdapter() {
      public void centerChanged() {
        if( !internalChange ) {
          LatLng center = gmap.getCenter();
          // We have to prevent the modify listeners from setting an "incomplete" new center: 
          internalChange = true; 
          latText.setText( String.valueOf( center.latitude ) );
          lonText.setText( String.valueOf( center.longitude ) );
          internalChange = false;
        }
      }
    } );
    latText.addModifyListener( listener );
    lonText.addModifyListener( listener );
  }

  private void createZoomControl( Composite parent ) {
    new Label( parent, SWT.NONE ).setText( "Zoom:" );
    final Scale zoomScale = new Scale( parent, SWT.NONE );
    zoomScale.setMinimum( 0 );
    zoomScale.setMaximum( 20 );
    zoomScale.setPageIncrement( 2 );
    zoomScale.setSelection( INIT_ZOOM );
    zoomScale.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        gmap.setZoom( zoomScale.getSelection() );
      }
    } );
    gmap.addMapListener( new MapAdapter() {
      @Override
      public void zoomChanged() {
        zoomScale.setSelection( gmap.getZoom() );
      }
    } );
    zoomScale.setLayoutData( ExampleUtil.createHorzFillData() );
  }

  private void createMapTypeControl( Composite parent ) {
    ExampleUtil.createHeading( parent, "Map Type", 2 );
    new Label( parent, SWT.NONE ).setText( "Type:" );
    final Combo type = new Combo( parent, SWT.DROP_DOWN | SWT.READ_ONLY );
    type.setItems( new String[]{
      "ROADMAP",
      "SATELLITE",
      "HYBRID",
      "TERRAIN"
    } );
    type.setText( "HYBRID" );
    type.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        int index = type.getSelectionIndex();
        if( index != -1 ) {
          gmap.setType( index );
        }
      }
    } );
  }

  private void createAddressControl( Composite parent ) {
    ExampleUtil.createHeading( parent, "Address", 2 );
    addressText = new Text( parent, SWT.BORDER | SWT.SINGLE );
    GridData addressTextData = ExampleUtil.createHorzFillData();
    addressTextData.horizontalSpan = 2;
    addressText.setLayoutData( addressTextData );
    addressText.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetDefaultSelected( SelectionEvent e ) {
        gmap.gotoAddress( addressText.getText() );
      }
    } );
    Button goButton = new Button( parent, SWT.PUSH );
    goButton.setText( "Go" );
    GridData layoutData = new GridData( SWT.RIGHT, SWT.CENTER, false, false );
    layoutData.horizontalSpan = 2;
    goButton.setLayoutData( layoutData );
    goButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        gmap.gotoAddress( addressText.getText() );
      }
    } );
  }

  private void createMarkerControl( Composite parent ) {
    ExampleUtil.createHeading( parent, "Marker Support", 2 );
//    final InputDialog markerDialog
//      = new InputDialog( parent.getShell(), "Marker Name", "Enter Name", null, null );
    new Label( parent, SWT.NONE ).setText( "Text:" );
    final Text markerText = new Text( parent, SWT.BORDER );
    markerText.setLayoutData( ExampleUtil.createHorzFillData() );
    Button addMarkerButton = new Button( parent, SWT.PUSH );
    GridData layoutData = new GridData( SWT.RIGHT, SWT.CENTER, false, false );
    layoutData.horizontalSpan = 2;
    addMarkerButton.setLayoutData( layoutData );
    addMarkerButton.setText( "Add Marker" );
    addMarkerButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        String result = markerText.getText();
        gmap.addMarker( result );
      }
    } );
  }

  private LatLng getLatLonFromTextFields() {
    LatLng result = null;
    double lat = getValueFromText( latText );
    double lon = getValueFromText( lonText );
    if( lat >= 0 && lon >= 0 ) {
      result = new LatLng( lat, lon );
    }
    return result;
  }

  private double getValueFromText( Text text ) {
    double result = -1;
    try {
      result = Double.parseDouble( text.getText() );
      text.setBackground( null );
    } catch( NumberFormatException e ) {
      text.setBackground( text.getDisplay().getSystemColor( SWT.COLOR_RED ) );
    }
    return result;
  }
}
