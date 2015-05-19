package kookmin.cs.flower.homeflow;

import java.util.ArrayList;

/**
 * @brief class for listview Customizing
 * @details This class implies construction of each row in listview.
 * @author Jinsung Choi, bugslife102401@nate.com
 * @version 0.0.4
 * @date 2015-05-17
 */
public class MyCustomDTO4 {
  int btn;
  String text;
  ArrayList<String> childList;

  /**
   * @brief constructor of MyCustomDTO class
   * @param text
   * @param childList
   */
  public MyCustomDTO4(int btn, String text, ArrayList<String> childList) {
    this.btn = btn;
    this.text = text;
    this.childList = childList;
  }

  public int getBtn() { return btn; }

  public void setBtn(int Btn) { this.btn = btn; }

  /**
   * @brief method for getting text
   * @return
   */
  public String getFlowText() { return text; }

  /**
   * @brief method for setting text
   * @param text
   */
  public void setFlowText(String text) { this.text = text; }

  /**
   * @brief method for getting childlist
   * @return
   */
  public Object getChildList() { return childList; }

  /**
   * @brief method for setting childlist
   * @param childList
   */
  public void setChildList(ArrayList<String> childList) { this.childList = childList; }
}