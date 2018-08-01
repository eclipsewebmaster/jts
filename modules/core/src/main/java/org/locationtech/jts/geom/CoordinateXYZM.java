/*
 * Copyright (c) 2016 Vivid Solutions.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.locationtech.jts.geom;

/**
 * Coordinate subclass supporting XYZM ordinate.
 * <p>
 * This data object is suitable for use with coordinate sequences dimension 4, measures 1.
 *
 * @since 1.16
 */
public class CoordinateXYZM extends Coordinate {
  private static final long serialVersionUID = -8763329985881823442L;

/** Default constructor */
  public CoordinateXYZM() {
    super();
    this.m = 0.0;
  }

  public CoordinateXYZM(double x, double y, double z, double m) {
    super(x, y, z);
    this.m = m;
  }

  public CoordinateXYZM(Coordinate coord) {
    super(coord);
    m = getM();
  }

  public CoordinateXYZM(CoordinateXYZM coord) {
    super(coord);
    m = coord.m;
  }

  public CoordinateXYZM copy() {
    return new CoordinateXYZM(this);
  }

  /** The m-measure. */
  private double m;

  /** The m-measure, if available. */
  public double getM() {
    return m;
  }

  public void setM(double m) {
    this.m = m;
  }

  public double getOrdinate(int ordinateIndex)
  {
    switch (ordinateIndex) {
    case X: return x;
    case Y: return y;
    case Z: return getZ(); // sure to delegate to subclass rather than offer direct field access
    case M: return getM(); // sure to delegate to subclass rather than offer direct field access
    }
    throw new IllegalArgumentException("Invalid ordinate index: " + ordinateIndex);
  }
  
  @Override
  public void setOrdinate(int ordinateIndex, double value) {
      switch (ordinateIndex) {
      case X:
        x = value;
        break;
      case Y:
        y = value;
        break;
      case Z:
        z = value;
        break;
      case M:
        m = value;
        break;
      default:
        throw new IllegalArgumentException("Invalid ordinate index: " + ordinateIndex);
    }
  }
  
  public String toString() {
    String stringRep = x + " " + y + " " + getZ() + " m=" + m;
    return stringRep;
  }
}
