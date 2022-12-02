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

package net.ajaskey.market.tools.options.workbench;

public class TestOptionTruthdata {

  public final static int CALL = 10;
  public final static int PUT  = 20;

  public static void main(String[] args) {
    final TestOptionTruthdata tset1 = new TestOptionTruthdata(TestOptionTruthdata.CALL, 100.0, 100.0, 0.1, 0.01, 1.0, 4.4852);
    final OptionsProcessor op = new OptionsProcessor("TSET1 CALL", tset1);
    System.out.printf("%nTSET1:%n%s%n-------------%n", tset1);
    System.out.println(op.getPrice());
    System.out.println(op);
//      System.out.println("------------------------");
//      final TestOptionTruthdata tset2 = new TestOptionTruthdata(TestOptionTruthdata.PUT, 100.0, 100.0, 0.1, 0.01, 1.0, 4.4852);
//      OptionsProcessor op1 = new OptionsProcessor("TSET1 PUT", tset2);
//      System.out.printf("%nTSET2:%n", tset2);
//      System.out.println(op1.getPrice());
//      System.out.println(op1);
  }

  public double expectedResult;
  public double intRate;
  public double iv;
  public double strike;

  public int type;

  public double ul;

  public double years;

  /**
   *
   * @param t
   * @param u
   * @param s
   * @param i
   * @param ir
   * @param yrs
   * @param er
   */
  public TestOptionTruthdata(int t, double u, double s, double i, double ir, double yrs, double er) {
    this.type = TestOptionTruthdata.CALL;
    if (t > 10) {
      this.type = TestOptionTruthdata.PUT;
    }
    this.ul = u;
    this.strike = s;
    this.iv = i;
    this.intRate = ir;
    this.years = yrs;
    this.expectedResult = er;
  }

  @Override
  public String toString() {
    String ret = "";
    int t = 0;
    if (this.type == TestOptionTruthdata.CALL) {
      t = TestOptionTruthdata.CALL;
    }
    else if (this.type == TestOptionTruthdata.PUT) {
      t = TestOptionTruthdata.PUT;
    }
    ret = String.format("%s%n  %.2f %.2f%n  %.4f, %.4f, %.3f%n  %.2f%n", t, this.ul, this.strike, this.iv, this.intRate, this.years,
        this.expectedResult);
    return ret;
  }
}
