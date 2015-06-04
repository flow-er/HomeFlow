package kookmin.cs.flower.homeflow.data;

/**
 * Created by sloth on 2015-05-26.
 */
/**
 * @author Jongho Lim, sloth@kookmin.ac.kr
 * @author Jinsung choi, bugslife102401@nate.com
 * @version 0.0.1
 * @brief an Class is inner class of Device class
 * @details Device class �� inner class�̴�. Device�� ��ɵ��� ���� �� �ִ�.
 */
public class Function {

  private String functionName;
  private int functionId;

  public Function(String funcName, int funcId) {
    functionName = funcName;
    functionId = funcId;
  }

  public int getId() {
    return functionId;
  }

  public String getName() {
    return functionName;
  }

  public String toString() { return functionName; }
}
