/**
 * @namespace kookmin.cs.homeflow
 * @brief java package of HomeFlow app
 * @details HomeFlow의 java file 집합
 */
package kookmin.cs.homeflow;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import kookmin.cs.homeflow.data.Appliance;
import kookmin.cs.homeflow.data.Workflow;
import kookmin.cs.homeflow.filestream.XMLInput;
import kookmin.cs.homeflow.filestream.XMLOutput;

/**
 * @author Jongho Lim, sloth@kookmin.ac.kr
 * @author Jinsung choi, bugslife102401@nate.com
 * @version 0.0.1
 * @brief an Activity is editer for workflow
 * @details workflow를 등록하는 액티비티 클래스이다. spinner 로 work를 추가하고 등록 버튼으로 workflow의 xml 파일을 만든다.
 * @todo develop UI, List Item Click, Button Click etc...
 */
public class AddFlowActivity extends ActionBarActivity
    implements AdapterView.OnItemSelectedListener, View.OnClickListener {

  private ArrayAdapter<Appliance> spinAdapter;
  private ArrayAdapter<Appliance> flowAdapter;
  private ArrayList<Appliance> funcList;
  private ArrayList<Appliance> flow;
  private Button btnSave;
  private Boolean initSelect;

  /**
   * @brief Activity init
   * @details spinner로 보여줄 데이터 초기화, 빈 flow 만듦, 버튼 리스너 등록 등.
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_make_flow);

    // 등록 버튼 리스너 set
    btnSave = (Button) findViewById(R.id.btn_save);
    btnSave.setOnClickListener(this);

    // spinner init
    Spinner spin = (Spinner) findViewById(R.id.spin);
    initSelect = false;

    // find device file path
    String[] assetlist = null;
    try {
      assetlist = getAssets().list("device");
    } catch (IOException e) {
      e.printStackTrace();
    }

    // set spinner data
    funcList = new ArrayList<Appliance>();
    Appliance temp = null;
    try {
      for (int i = 0; i < assetlist.length; i++) {
        funcList.add(new XMLInput().parse(getAssets().open("device/" + assetlist[i]), temp));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    // set spinner adapter
    spinAdapter =
        new ArrayAdapter<Appliance>(this, android.R.layout.simple_spinner_dropdown_item, funcList);
    spin.setAdapter(spinAdapter);
    spin.setOnItemSelectedListener(this);

    // ListView init
    ListView list = (ListView) findViewById(R.id.flow_list);

    // set empty flow adapter
    flow = new ArrayList<Appliance>();
    flowAdapter =
        new ArrayAdapter<Appliance>(this, android.R.layout.simple_expandable_list_item_1, flow);
    list.setAdapter(flowAdapter);
  }

  /**
   * @brief click event of save button
   * @details 등록 버튼 클릭시 실행되는 메소드이다. 만든 workflow data로 " "에 xml 파일로 저장하고 액티비티를 종료한다.
   */
  @Override
  public void onClick(View v) {
    Workflow workflow = new Workflow();

    for (int i = 0; i < flow.size(); i++) {
      workflow.addWork(flow.get(i).toString(), flow.get(i).getFuncId(0));
    }

    File
        file =
        new File(Environment.getExternalStorageDirectory().getPath() + "/HomeFlow/workflow/flow");
    int fileId = 1;

    while (new File(file.getPath() + fileId + ".xml").exists()) {
      fileId++;
    }

    file = new File(file.getPath() + fileId + ".xml");
    workflow.setName("flow" + fileId);

    try {
      FileOutputStream fos = new FileOutputStream(file);
      new XMLOutput().writeXml(fos, workflow);
    } catch (IOException e) {
      e.printStackTrace();
    }

    finish();
  }

  /**
   * @brief select event of spinner item
   * @details spinner item를 선택하면 실행되는 메소드이다. 선택한 item을 ArrayList인 flow에 추가하고 변경된 adapter 내용을 업데이트
   * 한다.
   */
  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    if (initSelect) {
      Appliance func = new Appliance();
      func.setName(funcList.get(position).toString());
      func.addFunction(funcList.get(position).toString(), funcList.get(position).getFuncId(0));

      flow.add(func);
      flowAdapter.notifyDataSetChanged();
    } else {
      initSelect = true;
    }
  }

  @Override
  public void onNothingSelected(AdapterView<?> parent) {

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_login, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }
}