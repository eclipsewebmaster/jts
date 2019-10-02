/*
 * Copyright (c) 2019 Martin Davis.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.locationtech.jts.operation.overlayng;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateArrays;
import org.locationtech.jts.geom.CoordinateList;
import org.locationtech.jts.geom.Envelope;

/**
 * Clips a line (array of points) to a box (envelope).
 * Uses a variant of Cohen-Sutherland clipping.
 * 
 * @author Martin Davis
 *
 */
public class LineClipper {
  
  private static final int BOX_LEFT = 3;
  private static final int BOX_TOP = 2;
  private static final int BOX_RIGHT = 1;
  private static final int BOX_BOTTOM = 0;
  
  private Envelope clipEnv;
  private double clipEnvMinY;
  private double clipEnvMaxY;
  private double clipEnvMinX;
  private double clipEnvMaxX;

  
  public LineClipper(Envelope clipEnv) {
    this.clipEnv = clipEnv;
    clipEnvMinY = clipEnv.getMinY();
    clipEnvMaxY = clipEnv.getMaxY();
    clipEnvMinX = clipEnv.getMinX();
    clipEnvMaxX = clipEnv.getMaxX();
  }

  public boolean isClippedCompletely(Envelope env) {
    return ! env.intersects(clipEnv);
  }
  
  /**
   * Clips a list of points to the rectangle box.
   * 
   * @param ring
   * @param env
   * @return
   */
  public Coordinate[] clip(Coordinate[] pts) {
    boolean isRing = CoordinateArrays.isRing(pts);
    
    for (int edgeIndex = 0; edgeIndex < 4; edgeIndex++) {
      boolean closeRing = edgeIndex == 3 && isRing;
      pts = clipToBoxEdge(pts, edgeIndex, closeRing);
      if (pts.length == 0) return pts;
    }
    return pts;
  }

  /**
   * Clips line to the axis-parallel line defined by a single box edge.
   * 
   * @param pts
   * @param edgeIndex
   * @param closeRing 
   * @return
   */
  private Coordinate[] clipToBoxEdge(Coordinate[] pts, int edgeIndex, boolean closeRing) {
    // TODO: is it possible to avoid copying array 4 times?
    CoordinateList ptsClip = new CoordinateList();

    Coordinate p0 = pts[pts.length - 1];
    for (int i = 0; i < pts.length; i++) {
      Coordinate p1 = pts[i];
      if ( isInsideEdge(p1, edgeIndex) ) {
        if ( ! isInsideEdge(p0, edgeIndex) ) {
          Coordinate intPt = intersection(p0, p1, edgeIndex);
          ptsClip.add( intPt, false);
        }
        // TODO: avoid copying so much?
        ptsClip.add( p1.copy(), false);
        
      } else if ( isInsideEdge(p0, edgeIndex) ) {
        Coordinate intPt = intersection(p0, p1, edgeIndex);
        ptsClip.add( intPt, false);
      }
      // else p0-p1 is outside box, so it is dropped
      
      p0 = p1;
    }
    // add closing point if required
    if (closeRing && ptsClip.size() > 0) {
      Coordinate start = ptsClip.get(0);
      if (! start.equals2D(ptsClip.get(ptsClip.size() - 1))) {
        ptsClip.add( start.copy() );
      }
    }
    return ptsClip.toCoordinateArray();
  }
  
  /**
   * Computes the intersection point of a segment 
   * with an edge of the clip box.
   * The segment must be known to intersect the edge.
   * 
   * @param a first endpoint of the segment
   * @param b second endpoint of the segment
   * @param edgeIndex index of box edge
   * @return the intersection point with the box edge
   */
  private Coordinate intersection(Coordinate a, Coordinate b, int edgeIndex) {
    switch (edgeIndex) {
    case BOX_BOTTOM:
      return new Coordinate(intersectionLineY(a, b, clipEnvMinY), clipEnvMinY);
    case BOX_RIGHT:
      return new Coordinate(clipEnvMaxX, intersectionLineX(a, b, clipEnvMaxX));
    case BOX_TOP:
      return new Coordinate(intersectionLineY(a, b, clipEnvMaxY), clipEnvMaxY);
    case BOX_LEFT:
    default:
      return new Coordinate(clipEnvMinX, intersectionLineX(a, b, clipEnvMinX));
    }
  }

  private double intersectionLineY(Coordinate a, Coordinate b, double y) {
    double m = (b.x - a.x) / (b.y - a.y);
    double intercept = (y - a.y) * m;
    return a.x + intercept;
  }

  private double intersectionLineX(Coordinate a, Coordinate b, double x) {
    double m = (b.y - a.y) / (b.x - a.x);
    double intercept = (x - a.x) * m;
    return a.y + intercept;
  }

  private boolean isInsideEdge(Coordinate p, int edgeIndex) {
    switch (edgeIndex) {
    case BOX_BOTTOM: // bottom
      return p.y > clipEnvMinY;
    case BOX_RIGHT: // right
      return p.x < clipEnvMaxX;
    case BOX_TOP: // top
      return p.y < clipEnvMaxY;
    case BOX_LEFT:
    default: // left
      return p.x > clipEnvMinX;
    }
  }

}