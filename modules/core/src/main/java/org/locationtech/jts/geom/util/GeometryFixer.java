/*
 * Copyright (c) 2021 Martin Davis.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v20.html
 * and the Eclipse Distribution License is available at
 *
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.locationtech.jts.geom.util;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateArrays;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.operation.buffer.BufferOp;
import org.locationtech.jts.operation.overlayng.OverlayNG;
import org.locationtech.jts.operation.overlayng.OverlayNGRobust;

/**
 * Fixes a geometry to be a valid geometry, while preserving as much as 
 * possible of the shape and location of the input.
 * Validity is determined according to {@link Geometry#isValid()}.
 * 
 * <h2>Rules</h2>
 * <ol>
 * <li>Vertices with non-finite X or Y ordinates are removed 
 * (as per {@link Coordinate#isValid()}.</li>
 * <li>Repeated points are removed</li>
 * <li>Empty elements are removed from collections</li>
 * 
 * 
 * 
 * @author mdavis
 *
 */
public class GeometryFixer {

  public static Geometry fix(Geometry geom) {
    GeometryFixer ri = new GeometryFixer(geom);
    return ri.getResult();
  }
  
  private Geometry geom;
  private GeometryFactory factory;
  private boolean isKeepCollapsed = false;

  public GeometryFixer(Geometry geom) {
    this.geom = geom;
    this.factory = geom.getFactory();
  }
  
  public void setKeepCollapsed(boolean isKeepCollapsed) {
    this.isKeepCollapsed  = isKeepCollapsed;
  }
  
  public Geometry getResult() {
    /**
     *  Truly empty geometries are simply copied.
     *  Geometry collections with elements are evaluated on a per-element basis.
     */
    if (geom.getNumGeometries() == 0) {
      return geom.copy();
    }
    
    if (geom instanceof Point)              return fixPoint((Point) geom);
    //  LinearRing must come before LineString
    if (geom instanceof LinearRing)         return fixLinearRing((LinearRing) geom);
    if (geom instanceof LineString)         return fixLineString((LineString) geom);
    if (geom instanceof Polygon)            return fixPolygon((Polygon) geom);
    if (geom instanceof MultiPoint)         return fixMultiPoint((MultiPoint) geom);
    if (geom instanceof MultiLineString)    return fixMultiLineString((MultiLineString) geom);
    if (geom instanceof MultiPolygon)       return fixMultiPolygon((MultiPolygon) geom);
    if (geom instanceof GeometryCollection) return fixCollection((GeometryCollection) geom);
    throw new UnsupportedOperationException(geom.getClass().getName());
  }

  private Point fixPoint(Point geom) {
    Geometry pt = fixPointElement(geom);
    if (pt == null)
      return factory.createPoint();
    return (Point) pt;
  }

  private Point fixPointElement(Point geom) {
    if (geom.isEmpty() || ! isValidPoint(geom)) {
      return null;
    }
    return (Point) geom.copy();
  }

  private static boolean isValidPoint(Point pt) {
    Coordinate p = pt.getCoordinate();
    return p.isValid();
  }

  private Geometry fixMultiPoint(MultiPoint geom) {
    List<Point> pts = new ArrayList<Point>();
    for (int i = 0; i < geom.getNumGeometries(); i++) {
      Point pt = (Point) geom.getGeometryN(i);
      if (pt.isEmpty()) continue;
      Point fixPt = fixPointElement(pt);
      if (fixPt != null) {
        pts.add(fixPt);
      }
    }
    return factory.createMultiPoint(GeometryFactory.toPointArray(pts));
  }

  private Geometry fixLinearRing(LinearRing geom) {
    Coordinate[] pts = geom.getCoordinates();
    Coordinate[] ptsFix = fixCoordinates(pts);
    if (ptsFix.length <= 3) {
      return factory.createLinearRing();
    }
    // TODO: check for flat ring -> EMPTY ?
    return factory.createLinearRing(ptsFix);
  }

  private Geometry fixLineString(LineString geom) {
    Geometry fix = fixLineStringElement(geom);
    if (fix == null)
      return factory.createLineString();
    return fix;
  }
  
  private Geometry fixLineStringElement(LineString geom) {
    if (geom.isEmpty()) return null;
    Coordinate[] pts = geom.getCoordinates();
    Coordinate[] ptsFix = fixCoordinates(pts);
    if (isKeepCollapsed && ptsFix.length == 1) {
      return factory.createPoint(ptsFix[0]);
    }
    if (ptsFix.length <= 1) {
      return null;
    }
    return factory.createLineString(ptsFix);
  }

  private static Coordinate[] fixCoordinates(Coordinate[] pts) {
    Coordinate[] ptsClean = CoordinateArrays.removeRepeatedAndInvalidPoints(pts);
    return CoordinateArrays.copyDeep(ptsClean);
  }
  
  private Geometry fixMultiLineString(MultiLineString geom) {
    List<Geometry> fixed = new ArrayList<Geometry>();
    boolean isMixed = false;
    for (int i = 0; i < geom.getNumGeometries(); i++) {
      LineString line = (LineString) geom.getGeometryN(i);
      if (line.isEmpty()) continue;
      
      Geometry fix = fixLineStringElement(line);
      if (fix == null) continue;
      
      if (! (fix instanceof LineString)) {
        isMixed = true;
      }
      fixed.add(fix);
    }
    if (fixed.size() == 1) {
      return fixed.get(0);
    }
    if (isMixed) {
      return factory.createGeometryCollection(GeometryFactory.toGeometryArray(fixed));
    }
    return factory.createMultiLineString(GeometryFactory.toLineStringArray(fixed));
  }

  private Geometry fixPolygon(Polygon geom) {
    Geometry fix = fixPolygonElement(geom);
    if (fix == null)
      return factory.createPolygon();
    return fix;
  }
  
  private Geometry fixPolygonElement(Polygon geom) {
    LinearRing shell = geom.getExteriorRing();
    Geometry fixShell = fixRing(shell);
    if (fixShell.isEmpty()) {
      if (isKeepCollapsed) {
        return fixLineString(shell);
      }
      //-- if not allowing collapses then return empty polygon
      return null;      
    }
    // if no holes then done
    if (geom.getNumInteriorRing() == 0) {
      return fixShell;
    }
    Geometry fixHoles = fixHoles(geom);
    Geometry result = removeHoles(fixShell, fixHoles);
    return result;
  }

  private Geometry removeHoles(Geometry shell, Geometry holes) {
    if (holes == null) 
      return shell;
    return OverlayNGRobust.overlay(shell, holes, OverlayNG.DIFFERENCE);
  }

  private Geometry fixHoles(Polygon geom) {
    List<Geometry> holes = new ArrayList<Geometry>();
    for (int i = 0; i < geom.getNumInteriorRing(); i++) {
      Geometry holeRep = fixRing(geom.getInteriorRingN(i));
      if (holeRep != null) {
        holes.add(holeRep);
      }
    }
    if (holes.size() == 0) return null;
    if (holes.size() == 1) {
      return holes.get(0);
    }
    Geometry holesUnion = OverlayNGRobust.union(holes);
    return holesUnion;
  }

  private Geometry fixRing(LinearRing ring) {
    //-- always execute fix, since it may remove repeated coords etc
    Geometry poly = factory.createPolygon(ring);
    // TOD: check if buffer removes invalid coordinates
    return BufferOp.fixPolygonal(poly, true);
  }

  private Geometry fixMultiPolygon(MultiPolygon geom) {
    List<Geometry> polys = new ArrayList<Geometry>();
    for (int i = 0; i < geom.getNumGeometries(); i++) {
      Polygon poly = (Polygon) geom.getGeometryN(i);
      Geometry polyFix = fixPolygonElement(poly);
      if (polyFix != null && ! polyFix.isEmpty()) {
        polys.add(polyFix);
      }
    }
    Geometry result = OverlayNGRobust.union(polys);
    return result;    
  }

  private Geometry fixCollection(GeometryCollection geom) {
    Geometry[] geomRep = new Geometry[geom.getNumGeometries()];
    for (int i = 0; i < geom.getNumGeometries(); i++) {
      geomRep[i] = fix(geom.getGeometryN(i));
    }
    return factory.createGeometryCollection(geomRep);
  }
}