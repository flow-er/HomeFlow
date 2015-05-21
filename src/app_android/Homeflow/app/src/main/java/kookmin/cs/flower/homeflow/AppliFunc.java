package kookmin.cs.flower.homeflow;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by cjs on 2015-05-21.
 */
public class AppliFunc extends Fragment implements View.OnClickListener {

  ListView listView;
  /**
   * @brief method for showing login.xml layout
   * @details This method sets clicked-events on login_btn.
   * @param inflater
   * @param container
   * @param savedInstanceState
   * @return rootView
   */
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    final View rootView = inflater.inflate(R.layout.applifunc, container, false);

    Button appli_func_btn = (Button) rootView.findViewById(R.id.appli_func_btn);
    appli_func_btn.setOnClickListener(this);

    ArrayList<String> appliFuncList = new ArrayList<String>();
    appliFuncList.add("기능1");
    appliFuncList.add("기능2");
    appliFuncList.add("기능3");
    appliFuncList.add("기능4");
    appliFuncList.add("기능5");

    listView = (ListView) rootView.findViewById(R.id.appli_func_list);

    final ArrayAdapter
        appliFuncAdapter = new ArrayAdapter<String>(rootView.getContext(), R.layout.applifuncrow, appliFuncList);
    listView.setAdapter(appliFuncAdapter);

    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        TextView tv = (TextView)view.findViewById(R.id.appli_func_txt);
        int txtColor = tv.getCurrentTextColor();
        if(txtColor == 0xFF2C486D)
          tv.setTextColor(0xFFCA93E8);
        else if(txtColor == 0xFFCA93E8)
          tv.setTextColor(0xFF2C486D);
      }
    });

    return rootView;
  }

  @Override
  public void onClick(View v) {
    if(v.getId() == R.id.appli_func_btn) {
      WorkEntry workEntry = new WorkEntry();
      getFragmentManager().beginTransaction().replace(R.id.realtabcontent, workEntry).commit();
    }
  }
}
