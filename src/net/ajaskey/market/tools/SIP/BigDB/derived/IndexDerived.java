package net.ajaskey.market.tools.SIP.BigDB.derived;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import net.ajaskey.common.TextUtils;
import net.ajaskey.common.Utils;
import net.ajaskey.market.tools.SIP.BigDB.FiletypeEnum;
import net.ajaskey.market.tools.SIP.BigDB.MarketTools;
import net.ajaskey.market.tools.SIP.BigDB.dataio.FieldData;

public class IndexDerived {

  public static void main(String[] args) {
    final List<String> tickers = new ArrayList<>();
    tickers.add(" msft");
    tickers.add(" GE ");

    IndexDerived id = new IndexDerived(" test index", tickers, 2020, 2);
    // add(id);
    id = new IndexDerived(" test index", tickers, 2020, 1);

    System.out.println(id.valid);
    System.out.println(id.indexList.size());
    for (final String s : id.indexList) {
      System.out.println(s);
    }
  }

  /**
   *
   * @param s
   * @return
   */
  private static boolean isStringValid(String s) {
    if (s != null) {
      if (s.length() > 0) {
        return true;
      }
    }
    return false;
  }

  List<CompanyDerivedData> cddList;

  List<IndexDerived> idList;
  List<String>       indexList;
  String             listName;

  boolean valid;

  /**
   * Constructor
   *
   * @param name Index name
   */
  public IndexDerived(String name, int yr, int qtr) {
    this.indexList = new ArrayList<>();
    this.valid = this.build(name, null);
  }

  /**
   * Constructor
   *
   * @param name   Index name
   * @param inList List of ticker making up index
   */
  public IndexDerived(String name, List<String> inList, int yr, int qtr) {
    this.indexList = new ArrayList<>();
    this.cddList = new ArrayList<>();
    this.valid = this.build(name, inList);
    if (this.valid) {
      FieldData.setQMemory(yr, qtr, FiletypeEnum.BIG_BINARY);
      final CompanyDerivedData cdd = new CompanyDerivedData(yr, qtr);
      for (final String ticker : this.indexList) {
        final FieldData fd = MarketTools.getFromMemory(ticker, yr, qtr);
        if (fd.isDataValid()) {
          final CompanyDerived cd = new CompanyDerived(fd);
          if (cd.isValid()) {
            cdd.dList.add(cd);
          }
        }
      }
      this.cddList.add(cdd);
    }
  }

  /**
   * Constructor - hidden
   */
  @SuppressWarnings("unused")
  private IndexDerived() {
  }

  /**
   *
   * @param addList List of tickers to add
   */
  public void addToList(List<String> addList) {
    try {
      for (final String s : addList) {
        this.checkAndAdd(s);
      }
    }
    catch (final Exception e) {
      System.out.println(FieldData.getWarning(e));
    }
  }

  /**
   *
   * @param s String to check against existing List
   * @return
   */
  public boolean isInList(String s) {
    try {
      if (s != null) {
        if (s.length() > 0) {
          for (final String t : this.indexList) {
            if (s.trim().toUpperCase().equals(t)) {
              return true;
            }
          }
        }
      }
    }
    catch (final Exception e) {
      System.out.println(FieldData.getWarning(e));
    }
    return false;
  }

  /**
   *
   * @param fullFileName
   * @return
   */
  public void readList(String fullFileName) {
    try {
      final List<String> inList = TextUtils.readTextFile(fullFileName, true);
      for (final String s : inList) {
        this.checkAndAdd(s);
      }
    }
    catch (final Exception e) {
      System.out.println(FieldData.getWarning(e));
    }
  }

  /**
   *
   * @param path
   */
  public void writeList(String path) {
    final String fname = String.format("%s/IndexList-%s.txt", path, this.listName);
    Utils.makeDirs(path);
    try (PrintWriter pw = new PrintWriter(fname)) {
      for (final String s : this.indexList) {
        pw.println(s);
      }
    }
    catch (final Exception e) {
      System.out.println(FieldData.getWarning(e));
    }
  }

  private void add(IndexDerived id) {
    this.idList.add(id);
  }

  /**
   * Used by constructors
   *
   * @param n    Name of index
   * @param list List of tickers to add to index
   * @return TRUE if all is well, FALSE otherwise
   */
  private boolean build(String n, List<String> list) {

    try {
      if (n == null) {
        System.out.println("Warning. IndexDerived Constructor with NULL name.");
        return false;
      }
      if (n.length() < 1) {
        System.out.println("Warning. IndexDerived Constructor with zero length name.");
        return false;
      }

      this.listName = n;
      if (list != null) {
        for (final String s : list) {
          this.checkAndAdd(s);
        }
      }
    }
    catch (final Exception e) {
      System.out.println(FieldData.getConstructorError(e));
      return false;
    }

    return true;
  }

  /**
   *
   * @param s
   */
  private void checkAndAdd(String s) {
    if (IndexDerived.isStringValid(s)) {
      if (!this.isInList(s)) {
        this.indexList.add(s.trim().toUpperCase());
      }
    }
  }

}
