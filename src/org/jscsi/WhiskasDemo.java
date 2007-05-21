/*
 * Copyright 2007 Marc Kramis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * $Id: JSCSIDevice.java 2498 2007-03-05 12:32:43Z lemke $
 * 
 */

package org.jscsi;

import java.util.Random;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

/**
 * <h1>WhiskasDemo</h1>
 * 
 * <p>
 * Creates some log events to demonstrate the WhiskasDevice.
 * </p>
 * 
 * @author Bastian Lemke
 * 
 */
public class WhiskasDemo {

  private final Device raid0, target1, target2;

  /** Used block size. * */
  private static final int BLOCK_SIZE = 8192;

  /** Number of blocks on the dummy device. * */
  private static final int BLOCK_COUNT = 1000;

  private final byte[] data;

  private final Random random;

  /**
   * Constructor to create an WhiskasDevice. The Device has to be initialized
   * before it can be used.
   * 
   * @throws Exception
   *           if any error occurs
   */
  public WhiskasDemo() throws Exception {

    target1 = new WhiskasDevice(new DummyDevice(BLOCK_SIZE, BLOCK_COUNT));
    target2 = new WhiskasDevice(new DummyDevice(BLOCK_SIZE, BLOCK_COUNT));
    raid0 = new WhiskasDevice(
        new Raid0Device(new Device[] { target1, target2 }));
    raid0.open();

    data = new byte[BLOCK_SIZE];
    for (int i = 0; i < data.length; i++) {
      data[i] = 0;
    }

    random = new Random(System.currentTimeMillis());
  }

  private void setUp() throws Exception {

    /*
     * Wait some seconds, because WhiskasDevice has to be configured between
     * opening the SocketHubAppender and writing/reading blocks.
     */
    Thread.sleep(10000);

    raid0.write(BLOCK_COUNT - 2, data);
    raid0.write(BLOCK_COUNT - 1, data);
    raid0.read(BLOCK_COUNT - 2, data);
    raid0.read(BLOCK_COUNT - 1, data);
  }

  private void tearDown() throws Exception {

    raid0.close();
  }

  private void sequentialWrite() throws Exception {

    for (int i = 0; i < BLOCK_COUNT; i++) {
      raid0.write(i, data);
      Thread.sleep(10);
    }
  }

  private void sequentialRead() throws Exception {

    for (int i = 0; i < BLOCK_COUNT; i++) {
      raid0.read(i, data);
      Thread.sleep(10);
    }
  }

  private void randomWrite() throws Exception {

    for (int i = 0; i < BLOCK_COUNT; i++) {
      raid0.write(random.nextInt((int) BLOCK_COUNT), data);
      Thread.sleep(10);
    }
  }

  private void randomRead() throws Exception {

    for (int i = 0; i < BLOCK_COUNT; i++) {
      raid0.read(random.nextInt((int) BLOCK_COUNT), data);
      Thread.sleep(10);
    }
  }

  /**
   * Main method to start the demo.
   * 
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {

    WhiskasDemo demo = new WhiskasDemo();

    demo.setUp();

    demo.sequentialWrite();
    demo.sequentialRead();
    demo.randomWrite();
    demo.randomRead();

    demo.tearDown();
  }
}
