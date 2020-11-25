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
package org.locationtech.jts.operation.overlayng;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.TopologyException;
import org.locationtech.jts.noding.SegmentNode;

import junit.textui.TestRunner;
import test.jts.GeometryTestCase;

/**
 * Tests {@link OverlayNGRobust}.
 * 
 * @author mdavis
 *
 */
public class OverlayNGRobustTest extends GeometryTestCase {

  public static void main(String[] args) {
    TestRunner.run(OverlayNGRobustTest.class);
  }

  public OverlayNGRobustTest(String name) { super(name); }
  
  /**
   * Try a failure case to exercise all overlay heuristics
   */
  public void testInvalidGeomUnion() {
    Geometry a = read("MULTIPOLYGON (((10 20, 20 20, 20 10, 10 10, 10 20)), ((15 25, 25 25, 25 14, 15 14, 15 25)))");
    Geometry b = read("POLYGON ((10 30, 30 30, 30 10, 10 10, 10 30))");
    checkOverlayFail(a, b, OverlayNG.UNION);
  }
  
  /**
   * This test case should succeed by using Snapping
   */
  public void testSnappingUnion() {
    Geometry a = read("POLYGON ((305353.17217811686 254662.96357893807, 305381.46877743956 254662.96357893807, 305355.9999841164 254650.3999988427, 305348.6999841096 254646.59999883917, 305343.69998410495 254643.99999883675, 305337.3999840991 254640.79999883377, 305325.3999840879 254634.599998828, 305318.4999840815 254630.99999882464, 305311.1999840747 254627.1999988211, 305311.281112409 254627.0485592637, 305304.9999840689 254623.99999881812, 305292.2999840571 254617.49999881207, 305279.49998404516 254610.99999880602, 305267.0999840336 254604.59999880005, 305261.5999840285 254601.69999879735, 305261.63325920445 254601.6314910822, 305256.49998402374 254599.19999879503, 305251.399984019 254596.39999879242, 305251.0859505601 254595.24049063656, 305235.99998400465 254588.29999878487, 305226.5000159958 254583.3999987803, 305230.2274214533 254576.13633686322, 305178.9000159515 254558.49999875712, 305176.9000159496 254557.59999875628, 305168.2000159415 254552.8999987519, 305158.80001593276 254547.89999874725, 305162.74450293585 254540.37795376458, 305153.700015928 254545.29999874483, 305144.30001591926 254540.69999874054, 305143.00001591805 254538.99999873896, 305115.30311758886 254493.95272753935, 305115.83242013416 254502.95087080973, 305117.9000158947 254505.79999870804, 305117.8000158946 254507.89999871, 305116.4000158933 254507.9999987101, 305116.100015893 254507.89999871, 305116.50001589337 254514.09999871577, 305112.8539721245 254514.41431283377, 305113.90001589095 254515.39999871698, 305114.00001589104 254517.09999871856, 305114.3000158913 254520.39999872164, 305111.3136017478 254528.97389739184, 305113.90001589095 254530.29999873086, 305118.00001589477 254532.4999987329, 305117.9712506782 254532.6725900323, 305121.8000158983 254534.59999873486, 305112.20001588936 254552.999998752, 305100.6185283701 254554.56490414738, 305100.90727455297 254560.08316453052, 305136.6000159121 254551.09999875023, 305136.90001591237 254551.09999875023, 305137.30001591274 254551.19999875032, 305137.5000159129 254551.19999875032, 305138.0000159134 254551.29999875042, 305138.40001591376 254551.3999987505, 305139.0000159143 254551.5999987507, 305139.6000159149 254551.99999875107, 305148.300015923 254556.39999875517, 305147.69433216826 254574.07545846544, 305149.3000159239 254574.8999987724, 305156.5000159306 254560.69999875917, 305162.4000159361 254563.79999876206, 305173.30001594627 254569.39999876727, 305191.4000159631 254578.89999877612, 305194.17825385765 254580.89965313557, 305195.0000159665 254581.29999877836, 305195.30001596676 254580.7999987779, 305206.6000159773 254586.79999878348, 305210.8579817029 254589.6415565389, 305216.3000159863 254592.4999987888, 305216.7000159867 254592.0999987884, 305229.4000159985 254598.69999879456, 305230.9000159999 254599.4999987953, 305244.0045397212 254607.68546976737, 305245.7999840138 254607.09999880238, 305260.3999840274 254614.59999880937, 305282.2999840478 254626.09999882008, 305305.099984069 254637.99999883116, 305318.4999840815 254644.99999883768, 305336.39998409816 254654.29999884634, 305351.09998411185 254661.79999885333, 305353.17217811686 254662.96357893807))");
    Geometry b = read("POLYGON ((305353.2092755222 254662.96357893807, 305381.9765468015 254662.96357893807, 305355.9999841164 254650.3999988427, 305348.6999841096 254646.59999883917, 305343.69998410495 254643.99999883675, 305337.3999840991 254640.79999883377, 305325.3999840879 254634.599998828, 305318.4999840815 254630.99999882464, 305311.1999840747 254627.1999988211, 305311.3154457364 254626.98447038594, 305304.9999840689 254623.99999881812, 305292.2999840571 254617.49999881207, 305279.49998404516 254610.99999880602, 305267.0999840336 254604.59999880005, 305261.5999840285 254601.69999879735, 305261.67110205657 254601.55357932782, 305256.49998402374 254599.19999879503, 305251.399984019 254596.39999879242, 305250.97991546144 254594.84897642606, 305235.99998400465 254588.29999878487, 305226.5000159958 254583.3999987803, 305230.40001599945 254575.79999877323, 305232.8243219372 254576.5620151067, 305237.9999840065 254575.19999877267, 305238.11680192675 254574.94040339434, 305178.9000159515 254558.49999875712, 305176.9000159496 254557.59999875628, 305168.2000159415 254552.8999987519, 305158.80001593276 254547.89999874725, 305163.10001593677 254539.6999987396, 305165.5226725255 254537.9669023306, 305164.3000159379 254537.29999873738, 305165.7000159392 254534.59999873486, 305188.8669453651 254513.64134592563, 305189.40001596126 254512.59999871437, 305189.960568551 254511.38880472587, 305153.700015928 254545.29999874483, 305144.30001591926 254540.69999874054, 305143.00001591805 254538.99999873896, 305118.93366550544 254491.23934128025, 305119.0000158957 254492.49999869565, 305117.1000158939 254492.89999869603, 305116.58729513956 254491.96878548746, 305115.20001589216 254492.19999869537, 305115.8237575254 254502.80360646098, 305117.9000158947 254505.79999870804, 305117.8000158946 254507.89999871, 305116.4000158933 254507.9999987101, 305116.100015893 254507.89999871, 305116.50001589337 254514.09999871577, 305112.90950390266 254514.40952561153, 305113.90001589095 254515.39999871698, 305114.00001589104 254517.09999871856, 305114.3000158913 254520.39999872164, 305111.4399527217 254528.61114782153, 305121.8000158983 254534.59999873486, 305112.20001588936 254552.999998752, 305100.5853566005 254553.9309547733, 305100.5969873565 254554.15323144238, 305136.6000159121 254551.09999875023, 305136.90001591237 254551.09999875023, 305137.30001591274 254551.19999875032, 305137.5000159129 254551.19999875032, 305138.0000159134 254551.29999875042, 305138.40001591376 254551.3999987505, 305139.0000159143 254551.5999987507, 305139.6000159149 254551.99999875107, 305148.300015923 254556.39999875517, 305147.5068930194 254573.97920592956, 305149.3000159239 254574.8999987724, 305156.5000159306 254560.69999875917, 305162.4000159361 254563.79999876206, 305173.30001594627 254569.39999876727, 305191.4000159631 254578.89999877612, 305194.8764979971 254581.23982335738, 305195.0000159665 254581.29999877836, 305195.30001596676 254580.7999987779, 305206.6000159773 254586.79999878348, 305212.2218734041 254590.35794409915, 305216.3000159863 254592.4999987888, 305216.7000159867 254592.0999987884, 305229.4000159985 254598.69999879456, 305230.9000159999 254599.4999987953, 305244.3482277927 254607.57339757012, 305245.7999840138 254607.09999880238, 305260.3999840274 254614.59999880937, 305282.2999840478 254626.09999882008, 305305.099984069 254637.99999883116, 305318.4999840815 254644.99999883768, 305336.39998409816 254654.29999884634, 305351.09998411185 254661.79999885333, 305353.2092755222 254662.96357893807))");
    checkUnionSuccess(a, b);
  }
  
  /**
   * Tests correct ordering of {@link SegmentNode#compareTo(Object)}.
   * 
   * See https://trac.osgeo.org/geos/ticket/1051
   */
  public void testSegmentNodeOrderingIntersection() {
    Geometry a = read("POLYGON ((654948.3853299792 1794977.105854025, 655016.3812220972 1794939.918901604, 655016.2022581929 1794940.1099794197, 655014.9264068712 1794941.4254068714, 655014.7408834674 1794941.6101225375, 654948.3853299792 1794977.105854025))");
    Geometry b = read("POLYGON ((655103.6628454948 1794805.456674405, 655016.20226 1794940.10998, 655014.8317182435 1794941.5196832407, 655014.8295602322 1794941.5218318563, 655014.740883467 1794941.610122538, 655016.6029214273 1794938.7590508445, 655103.6628454948 1794805.456674405))");
    checkOverlaySuccess(a, b, OverlayNG.INTERSECTION);
  }
  
  // MD 2020-09-14 There is no known test case that requires Snap-Rounding to succeed.
  
  public static void checkUnionSuccess(Geometry a, Geometry b) {
    checkOverlaySuccess(a, b, OverlayNG.UNION );
  }
  
  public static void checkOverlaySuccess(Geometry a, Geometry b, int opCode) {
    try {
      OverlayNGRobust.overlay(a, b, opCode );
    }
    catch (Throwable ex) {
      fail("Overlay fails with an error: " + ex);
    }
  }
  
  public static void checkOverlayFail(Geometry a, Geometry b, int opCode) {
    try {
      OverlayNGRobust.overlay(a, b, opCode );
      fail("Overlay was expected to fail");
    }
    catch (Throwable ex) {
      // do nothing - expected result
    }
  }
}
