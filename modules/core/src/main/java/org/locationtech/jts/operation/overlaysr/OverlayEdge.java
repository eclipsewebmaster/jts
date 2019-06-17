package org.locationtech.jts.operation.overlaysr;

import java.util.Comparator;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Location;
import org.locationtech.jts.geom.TopologyException;
import org.locationtech.jts.geomgraph.Label;
import org.locationtech.jts.geomgraph.Position;
import org.locationtech.jts.noding.SegmentString;
import org.locationtech.jts.util.Assert;

public class OverlayEdge extends HalfEdge {

  /**
   * Gets a {@link Comparator} which sorts by the origin Coordinates.
   * 
   * @return a Comparator sorting by origin coordinate
   */
  public static Comparator<OverlayEdge> nodeComparator() {
    return new Comparator<OverlayEdge>() {
      @Override
      public int compare(OverlayEdge e1, OverlayEdge e2) {
        return e1.orig().compareTo(e2.orig());
      }
    };
  }
  
  private SegmentString segString;
  
  /**
   * <code>true</code> indicates direction is forward along segString
   * <code>false</code> is reverse direction
   * The label must be interpreted accordingly.
   */
  private boolean direction;
  private Coordinate dirPt;
  private OverlayLabel label;

  private boolean isInResult = false;

  public OverlayEdge(Coordinate orig, Coordinate dirPt, boolean direction, OverlayLabel label, SegmentString segString) {
    super(orig);
    this.dirPt = dirPt;
    this.direction = direction;
    this.segString = segString;
    this.label = label;
  }

  public Coordinate directionPt() {
    return dirPt;
  }
  
  public OverlayLabel getLabel() {
    return label;
  }

  public Coordinate getCoordinate() {
    return orig();
  }
  
  public Coordinate[] getCoordinates() {
    return segString.getCoordinates();
  }
  
  public OverlayEdge symOE() {
    return (OverlayEdge) sym();
  }
  
  public boolean isInResult() {
    return isInResult;
  }
  
  public void removeFromResult() {
    isInResult = false;
  }
  /**
   * Scan around node and propagate labels until fully populated.
   * @param node node to compute labelling for
   */
  public void computeLabelling() {
    propagateAreaLabels(0);
    propagateAreaLabels(1);
  }

  private void propagateAreaLabels(int geomIndex) {
   // initialize currLoc to location of last L side (if any)
   int currLoc = findLocStart(geomIndex);

    // no labelled sides found, so nothing to propagate
    if (currLoc == Location.NONE) return;

    OverlayEdge e = this;
    do {
      OverlayLabel label = e.getLabel();
      // set null ON values to be in current location
      if (! label.hasLocation(geomIndex, Position.ON))
          label.setLocation(geomIndex, Position.ON, currLoc);
      // set side labels (if any)
      if (label.isArea()) {
        int leftLoc   = label.getLocation(geomIndex, Position.LEFT);
        int rightLoc  = label.getLocation(geomIndex, Position.RIGHT);
        // if there is a right location, that is the next location to propagate
        if (rightLoc != Location.NONE) {
//Debug.print(rightLoc != currLoc, this);
          if (rightLoc != currLoc)
            throw new TopologyException("side location conflict", e.getCoordinate());
          if (leftLoc == Location.NONE) {
            Assert.shouldNeverReachHere("found single null side (at " + e.getCoordinate() + ")");
          }
          currLoc = leftLoc;
        }
        else {
          /** 
           * RHS is null - LHS must be null too.
           * This must be an edge from the other geometry, which has no location
           * labelling for this geometry.  This edge must lie wholly inside or outside
           * the other geometry (which is determined by the current location).
           * Assign both sides to be the current location.
           */
          Assert.isTrue(label.getLocation(geomIndex, Position.LEFT) == Location.NONE, "found single null side");
          label.setLocationBothSides(geomIndex, currLoc);
        }
      }
      e = (OverlayEdge) e.oNext();
    } while (e != this);
  }

  private int findLocStart(int geomIndex) {
    int locStart = Location.NONE;
    // Edges are stored in CCW order around the node.
    // As we move around the ring we move from the R to the L side of the edge
    OverlayEdge e = this;
    do {
      OverlayLabel label = e.getLabel();
      if (label.isArea(geomIndex) 
          && label.hasLocation(geomIndex, Position.LEFT))
        locStart = label.getLocation(geomIndex, Position.LEFT);
      e = (OverlayEdge) e.oNext();
    } while (e != this);
    return locStart;
  }

  public void markInResultArea(int overlayOpCode) {
    if (label.isArea()
        //&& ! edge.isInteriorAreaEdge()
        && OverlaySR.isResultOfOp(
              label.getLocation(0, Position.RIGHT),
              label.getLocation(1, Position.RIGHT),
              overlayOpCode)) {
      isInResult  = true;  
    }
  }

  public void nodeMergeSymLabels() {
    OverlayEdge e = this;
    do {
      OverlayLabel label = e.getLabel();
      OverlayLabel labelSym = ((OverlayEdge) e.sym()).getLabel();
      label.merge(labelSym);
      labelSym.merge(label);
      e = (OverlayEdge) e.oNext();
    } while (e != this);
  }
  
  public String toString() {
    Coordinate orig = orig();
    Coordinate dest = dest();
    return "OE("+orig.x + " " + orig.y
        + ", "
        + dest.x + " " + dest.y
        + ")" + label;
  }
}
