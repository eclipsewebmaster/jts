
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
package org.locationtech.jts.index.hprtree;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.index.ItemVisitor;
import org.locationtech.jts.index.SpatialIndexTester;
import org.locationtech.jts.util.AssertionFailedException;

import junit.framework.TestCase;



/**
 * @version 1.17
 */
public class HPRtreeTest extends TestCase {
  private GeometryFactory factory = new GeometryFactory();

  public HPRtreeTest(String Name_) {
    super(Name_);
  }

  public static void main(String[] args) {
    String[] testCaseName = {HPRtreeTest.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

  public void xtestEmptyTreeUsingListQuery()  
  {
    HPRtree tree = new HPRtree();
    List list = tree.query(new Envelope(0, 0, 1, 1));
    assertTrue(list.isEmpty());
  }
  
  public void xtestEmptyTreeUsingItemVisitorQuery()  
  {
    HPRtree tree = new HPRtree(0);
    tree.query(new Envelope(0,0,1,1), new ItemVisitor() {
      public void visitItem(Object item) {
        assertTrue("Should never reach here", true);
      }
    });  
  }

  public void xtestSpatialIndex()
  throws Exception
  {
    SpatialIndexTester tester = new SpatialIndexTester();
    tester.setSpatialIndex(new HPRtree());
    tester.init();
    tester.run();
    assertTrue(tester.isSuccess());
  }

  public void xtestDisallowedInserts() {
    HPRtree t = new HPRtree(3);
    t.insert(new Envelope(0, 0, 0, 0), new Object());
    t.insert(new Envelope(0, 0, 0, 0), new Object());
    t.query(new Envelope());
    try {
      t.insert(new Envelope(0, 0, 0, 0), new Object());
      assertTrue(false);
    }
    catch (IllegalStateException e) {
      assertTrue(true);
    }
  }

  public void xtestQuery() throws Throwable {
    ArrayList geometries = new ArrayList();
    geometries.add(factory.createLineString(new Coordinate[]{
        new Coordinate(0, 0), new Coordinate(10, 10)}));
    geometries.add(factory.createLineString(new Coordinate[]{
        new Coordinate(20, 20), new Coordinate(30, 30)}));
    geometries.add(factory.createLineString(new Coordinate[]{
        new Coordinate(20, 20), new Coordinate(30, 30)}));
    HPRtree t = new HPRtree(3);
    for (Iterator i = geometries.iterator(); i.hasNext(); ) {
      Geometry g = (Geometry) i.next();
      t.insert(g.getEnvelopeInternal(), new Object());
    }
    t.query(new Envelope(5, 6, 5, 6));
    try {
      assertEquals(1, t.query(new Envelope(5, 6, 5, 6)).size());
      assertEquals(0, t.query(new Envelope(20, 30, 0, 10)).size());
      assertEquals(2, t.query(new Envelope(25, 26, 25, 26)).size());
      assertEquals(3, t.query(new Envelope(0, 100, 0, 100)).size());
    }
    catch (Throwable x) {
      //STRtreeDemo.printSourceData(geometries, System.out);
      //STRtreeDemo.printLevels(t, System.out);
      throw x;
    }
  }

  public void testQuery10() throws Throwable {
    HPRtree t = new HPRtree(10);
    for (int i = 0; i < 10; i++ ) {
      t.insert(new Envelope(i, i+1, i, i+1), i);
    }
    t.query(new Envelope(0,1,0,1));
    assertEquals(3, t.query(new Envelope(5, 6, 5, 6)).size());
    assertEquals(2, t.query(new Envelope(9, 10, 9, 10)).size());
    assertEquals(0, t.query(new Envelope(25, 26, 25, 26)).size());
    assertEquals(10, t.query(new Envelope(0, 10, 0, 10)).size());
  }

  public void testQuery100() throws Throwable {
    HPRtree t = new HPRtree(100);
    for (int i = 0; i < 100; i++ ) {
      t.insert(new Envelope(i, i+1, i, i+1), i);
    }
    t.query(new Envelope(0,1,0,1));
    assertEquals(3, t.query(new Envelope(5, 6, 5, 6)).size());
    assertEquals(3, t.query(new Envelope(9, 10, 9, 10)).size());
    assertEquals(3, t.query(new Envelope(25, 26, 25, 26)).size());
    assertEquals(11, t.query(new Envelope(0, 10, 0, 10)).size());
  }


}
