/*
 * Copyright (c) 2019 Martin Davis.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v20.html
 * and the Eclipse Distribution License is available at
 *
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.locationtech.jts.operation.buffer;

import org.locationtech.jts.geom.Geometry;

import org.locationtech.jts.geom.LineString;
import test.jts.GeometryTestCase;

public class VariableBufferTest extends GeometryTestCase {

  private static final double DEFAULT_TOLERANCE = 1.0e-6;

  public VariableBufferTest(String name) {
    super(name);
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(VariableBufferTest.class);
  }
  
  public void testZeroWidth() {
    checkBuffer("LINESTRING( 0 0, 6 6, 10 10)",
        0, 0,
        "POLYGON EMPTY");
  }

  public void testZeroLength() {
    checkBuffer("LINESTRING( 10 10, 10 10 )",
        0, 0,
        "POLYGON EMPTY");
  }

  public void testSegmentInverseDist() {
    checkBuffer("LINESTRING (100 100, 200 100)",
        10, 1,
        "POLYGON ((200.09 99.00405823463417, 100.9 90.04058234634172, 100 90, 98.04909677983872 90.19214719596769, 96.1731656763491 90.76120467488714, 94.44429766980397 91.68530387697454, 92.92893218813452 92.92893218813452, 91.68530387697454 94.44429766980397, 90.76120467488713 96.1731656763491, 90.19214719596769 98.04909677983872, 90 100, 90.19214719596769 101.95090322016128, 90.76120467488713 103.8268343236509, 91.68530387697454 105.55570233019603, 92.92893218813452 107.07106781186548, 94.44429766980397 108.31469612302546, 96.1731656763491 109.23879532511287, 98.04909677983872 109.80785280403231, 100 110, 100.9 109.95941765365829, 200.09 100.99594176536583, 200.19509032201614 100.98078528040323, 200.3826834323651 100.9238795325113, 200.5555702330196 100.83146961230254, 200.70710678118655 100.70710678118655, 200.83146961230256 100.55557023301961, 200.92387953251128 100.38268343236508, 200.98078528040324 100.19509032201613, 201 100, 200.98078528040324 99.80490967798387, 200.92387953251128 99.61731656763492, 200.83146961230256 99.44442976698039, 200.70710678118655 99.29289321881345, 200.5555702330196 99.16853038769746, 200.3826834323651 99.0761204674887, 200.09 99.00405823463417))"
        );
  }

  public void testSegmentSameDist() {
    checkBuffer("LINESTRING (100 100, 200 100)",
        10, 10,
        "POLYGON ((90 100, 90.19214719596769 101.95090322016128, 90.76120467488713 103.8268343236509, 91.68530387697454 105.55570233019603, 92.92893218813452 107.07106781186548, 94.44429766980397 108.31469612302546, 96.1731656763491 109.23879532511287, 98.04909677983872 109.80785280403231, 100 110, 200 110, 200 110, 201.95090322016128 109.80785280403231, 203.8268343236509 109.23879532511287, 205.55570233019603 108.31469612302546, 207.07106781186548 107.07106781186548, 208.31469612302544 105.55570233019603, 209.23879532511287 103.8268343236509, 209.8078528040323 101.95090322016128, 210 100, 209.8078528040323 98.04909677983872, 209.23879532511287 96.1731656763491, 208.31469612302544 94.44429766980397, 207.07106781186548 92.92893218813452, 205.55570233019603 91.68530387697454, 203.8268343236509 90.76120467488713, 201.95090322016128 90.19214719596769, 200 90, 100 90, 100 90, 98.04909677983872 90.19214719596769, 96.1731656763491 90.76120467488714, 94.44429766980397 91.68530387697454, 92.92893218813452 92.92893218813452, 91.68530387697454 94.44429766980397, 90.76120467488713 96.1731656763491, 90.19214719596769 98.04909677983872, 90 100))"
        );
  }

  public void testOneSegment() {
    checkBuffer("LINESTRING (100 100, 200 100)",
        10, 30,
"POLYGON ((98 109.79795897113272, 194 129.39387691339815, 194.14729033951616 129.42355841209692, 200 130, 205.85270966048384 129.42355841209692, 211.4805029709527 127.7163859753386, 216.66710699058808 124.94408836907635, 221.21320343559643 121.21320343559643, 224.94408836907635 116.66710699058807, 227.7163859753386 111.4805029709527, 229.42355841209692 105.85270966048385, 230 100, 229.42355841209692 94.14729033951615, 227.7163859753386 88.5194970290473, 224.94408836907635 83.33289300941193, 221.21320343559643 78.78679656440357, 216.66710699058808 75.05591163092365, 211.4805029709527 72.2836140246614, 205.85270966048384 70.57644158790309, 200 70, 194 70.60612308660184, 98 90.20204102886728, 96.1731656763491 90.76120467488714, 94.44429766980397 91.68530387697454, 92.92893218813452 92.92893218813452, 91.68530387697454 94.44429766980397, 90.76120467488713 96.1731656763491, 90.19214719596769 98.04909677983872, 90 100, 90.19214719596769 101.95090322016128, 90.76120467488713 103.8268343236509, 91.68530387697454 105.55570233019603, 92.92893218813452 107.07106781186548, 94.44429766980397 108.31469612302546, 96.1731656763491 109.23879532511287, 98 109.79795897113272))"
        );
  }

  public void testSegments2() {
    checkBuffer("LINESTRING( 0 0, 40 40, 60 -20)",
        10, 20,
        "POLYGON ((53.52863576494982 45.80469132164433, 78.37960104024995 -12.113919503248614, 78.47759065022574 -12.346331352698204, 79.61570560806462 -16.098193559677433, 80 -20, 79.61570560806462 -23.901806440322567, 78.47759065022574 -27.653668647301796, 76.62939224605091 -31.111404660392044, 74.14213562373095 -34.14213562373095, 71.11140466039204 -36.629392246050905, 67.6536686473018 -38.477590650225736, 63.90180644032257 -39.61570560806461, 60 -40, 56.09819355967743 -39.61570560806461, 52.34633135269821 -38.477590650225736, 48.88859533960796 -36.629392246050905, 45.85786437626905 -34.14213562373095, 43.370607753949095 -31.111404660392044, 40.56467086974921 -24.718896226748868, 31.314401806419635 13.379424895487343, 6.456226258387812 -7.636566145886759, 5.555702330196018 -8.314696123025454, 3.8268343236509 -9.238795325112866, 1.950903220161283 -9.807852804032304, 0 -10, -1.9509032201612866 -9.807852804032303, -3.8268343236509033 -9.238795325112864, -5.555702330196022 -8.314696123025453, -7.071067811865477 -7.071067811865475, -8.314696123025454 -5.55570233019602, -9.238795325112868 -3.8268343236508966, -9.807852804032304 -1.9509032201612837, -10 0, -9.807852804032304 1.9509032201612861, -9.238795325112868 3.826834323650899, -8.314696123025453 5.555702330196022, -7.636566145886759 6.456226258387811, 28.75793640390754 49.5044428085851, 29.59042683391263 50.40957316608737, 31.821250844443494 52.240363117601376, 34.366379598327015 53.60076277898068, 37.12800522487612 54.4384927541594, 40 54.721359549995796, 42.87199477512389 54.4384927541594, 45.633620401672985 53.60076277898068, 48.17874915555651 52.240363117601376, 50.40957316608737 50.40957316608737, 52.240363117601376 48.17874915555651, 53.52863576494982 45.80469132164433))"
        );
  }

  public void testLargeDistance() {
    checkBuffer("LINESTRING( 0 0, 10 10)",
        1, 200,
        "POLYGON ((-190 10, -186.1570560806461 49.01806440322572, -174.77590650225736 86.53668647301798, -156.29392246050907 121.11404660392043, -131.42135623730948 151.4213562373095, -101.11404660392039 176.29392246050907, -66.53668647301795 194.77590650225736, -29.018064403225637 206.1570560806461, 10 210, 49.018064403225665 206.1570560806461, 86.53668647301797 194.77590650225736, 121.11404660392046 176.29392246050904, 151.4213562373095 151.42135623730948, 176.29392246050904 121.11404660392043, 194.77590650225736 86.53668647301795, 206.1570560806461 49.01806440322565, 210 10, 206.15705608064607 -29.018064403225743, 194.7759065022573 -66.53668647301808, 176.29392246050904 -101.11404660392043, 151.42135623730948 -131.42135623730954, 121.11404660392037 -156.2939224605091, 86.536686473018 -174.77590650225733, 49.01806440322566 -186.1570560806461, 10 -190, -29.018064403225736 -186.15705608064607, -66.53668647301807 -174.7759065022573, -101.11404660392043 -156.29392246050904, -131.42135623730954 -131.42135623730948, -156.2939224605091 -101.11404660392039, -174.77590650225736 -66.53668647301794, -186.1570560806461 -29.018064403225672, -190 10))"
        );
  }

  private void checkBuffer(String wkt, double startDist, double endDist, 
      String wktExpected) {
    LineString<?> geom = (LineString<?>) read(wkt);
    Geometry result = VariableBuffer.buffer(geom, startDist, endDist);
    //System.out.println(result);
    checkBuffer(result, wktExpected);
  }

  private void checkBuffer(Geometry actual, String wktExpected) {
    Geometry expected = read(wktExpected);
    checkEqual(expected, actual, DEFAULT_TOLERANCE);
  }
}
