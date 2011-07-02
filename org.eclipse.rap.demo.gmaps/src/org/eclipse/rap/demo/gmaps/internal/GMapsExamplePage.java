package org.eclipse.rap.demo.gmaps.internal;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.rap.examples.ExampleUtil;
import org.eclipse.rap.examples.IExamplePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Text;

import com.eclipsesource.widgets.gmaps.GMap;
import com.eclipsesource.widgets.gmaps.LatLng;
import com.eclipsesource.widgets.gmaps.MapAdapter;


public class GMapsExamplePage implements IExamplePage {

  static final private LatLng INIT_CENTER = new LatLng( 33.0, 5.0 );
  static final private int INIT_ZOOM = 2;
  static final private int INIT_TYPE = GMap.TYPE_HYBRID;

  private GMap gmap;
  private Text addressText;
  private Text latText;
  private Text lonText;

  public void createControl( Composite parent ) {
    parent.setLayout( ExampleUtil.createMainLayout( 1 ) );
    Composite mainGroup = createMainGroup( parent );
    mainGroup.setLayoutData( ExampleUtil.createFillData() );
    mainGroup.setLayout( ExampleUtil.createGridLayout( 1, false, 10, 10 ) );
    Control addressControl = createAddressControl( mainGroup );
    addressControl.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    SashForm sashForm = new SashForm( mainGroup, SWT.HORIZONTAL );    
    sashForm.setLayoutData( ExampleUtil.createFillData() );
    createMap( sashForm );
    createControls( sashForm );
    sashForm.setWeights( new int[] { 7, 2 } );
  }

  private void createMap( Composite parent ) {
    gmap = new GMap( parent, SWT.BORDER );
    gmap.setCenter( INIT_CENTER );
    gmap.setZoom( INIT_ZOOM );
    gmap.setType( INIT_TYPE );
    attachListenersToMap();
  }

  private void createControls( Composite parent ) {
    Composite controls = new Composite( parent, SWT.BORDER );
    controls.setLayout( new GridLayout( 1, true ) );
    createCenterControl( parent.getDisplay(), controls );
    createZoomControl( controls );
    createMapTypeControl( controls );
    createMarkerControl( parent.getDisplay(), controls );
    createResolveControl( parent.getDisplay(), controls );
  }

  private Composite createMainGroup( Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setText( "Google Maps" );
    return group;
  }

  private void createCenterControl( Display display, Composite parent ) {
    new Label( parent, SWT.None ).setText( "Location:" );
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayoutData( ExampleUtil.createHorzFillData() );
    composite.setLayout( ExampleUtil.createGridLayout( 2, false, 0, 5 ) );
    new Label( composite, SWT.None ).setText( "Lat:" );
    latText = new Text( composite, SWT.BORDER );
    latText.setLayoutData( ExampleUtil.createHorzFillData() );
    latText.setText( Float.toString( ( float )INIT_CENTER.latitude ) );
    new Label( composite, SWT.None ).setText( "Lon:" );
    lonText = new Text( composite, SWT.BORDER );
    lonText.setLayoutData( ExampleUtil.createHorzFillData() );
    lonText.setText( Float.toString( ( float )INIT_CENTER.longitude ) );
    ModifyListener listener = new ModifyListener() {
      public void modifyText( ModifyEvent event ) {
        LatLng newCenter = getLatLonFromTextFields();
        if( newCenter != null ) {
          gmap.setCenter( newCenter );
        }
      }
    };
    latText.addModifyListener( listener );
    lonText.addModifyListener( listener );
  }

  private void createZoomControl( Composite parent ) {
    new Label( parent, SWT.NONE ).setText( "Zoom:" );
    final Slider zoom = new Slider( parent, SWT.NORMAL );
    zoom.setMinimum( 0 );
    zoom.setMaximum( 30 );
    zoom.setSelection( INIT_ZOOM );
    zoom.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
    zoom.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        int zoomLevel = zoom.getSelection();
        if( zoomLevel >= 0 && zoomLevel <= 20 ) {
          gmap.setZoom( zoomLevel );
        }
      }
    } );
    gmap.addMapListener( new MapAdapter() {
      public void zoomChanged() {
        zoom.setSelection( gmap.getZoom() );              
      }
    } );
  }

  private void createMapTypeControl( Composite parent ) {
    new Label( parent, SWT.None ).setText( "Type:" );
    final Combo type = new Combo( parent, SWT.DROP_DOWN | SWT.READ_ONLY );
    type.setItems( new String[]{
      "ROADMAP",
      "SATELLITE",
      "HYBRID",
      "TERRAIN"
    } );
    type.setText( "HYBRID" );
    type.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        int index = type.getSelectionIndex();
        if( index != -1 ) {
          gmap.setType( index );
        }
      }
    } );
  }

  private Control createAddressControl( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 3, false ) );
    new Label( composite, SWT.NONE ).setText( "Address:" );
    addressText = new Text( composite, SWT.BORDER | SWT.SINGLE );
    GridData addressTextData = new GridData( SWT.FILL, SWT.CENTER, true, false );
    addressText.setLayoutData( addressTextData );
    addressText.addSelectionListener( new SelectionAdapter() {
      public void widgetDefaultSelected( SelectionEvent e ) {
        gmap.gotoAddress( addressText.getText() );
      }
    } );
    Button goButton = new Button( composite, SWT.PUSH );
    goButton.setText( "Go" );
    goButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        gmap.gotoAddress( addressText.getText() );
      }
    } );
    return composite;
  }

  private void attachListenersToMap() {
    gmap.addMapListener( new MapAdapter() {
      public void centerChanged() {
        LatLng center = gmap.getCenter();
        latText.setText( Float.toString( (float) center.latitude ) );
        lonText.setText( Float.toString( (float) center.longitude ) );
      }
      public void addressResolved() {
        addressText.setText( gmap.getAddress() );
      }
    } );
  }

  private void createMarkerControl( Display display, Composite parent ) {
    final InputDialog markerDialog
      = new InputDialog( parent.getShell(), "Marker Name", "Enter Name", null, null );
    Button addMarker = new Button( parent, SWT.PUSH );
    addMarker.setText( "Add marker" );
    addMarker.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        markerDialog.open();
        String result = markerDialog.getValue();
        if( result != null && result.length() > 0 ) {
          gmap.addMarker( result );
        }
      }
    } );    
  }

  private void createResolveControl( Display display, Composite parent ) {
    Button resolveAddressButton = new Button( parent, SWT.PUSH );
    resolveAddressButton.setText( "Resolve location" );
    resolveAddressButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        gmap.resolveAddress();
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
