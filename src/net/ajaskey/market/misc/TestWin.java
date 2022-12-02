/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Original author : Andy Askey (ajaskey34@gmail.com)
 */
package net.ajaskey.market.misc;

import java.awt.EventQueue;

import javax.swing.JFrame;

public class TestWin {

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        try {
          final TestWin window = new TestWin();
          window.frame.setVisible(true);
        }
        catch (final Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  private JFrame frame;

  /**
   * Create the application.
   */
  public TestWin() {
    this.initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    this.frame = new JFrame();
    this.frame.setBounds(100, 100, 450, 300);
    this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }

}
