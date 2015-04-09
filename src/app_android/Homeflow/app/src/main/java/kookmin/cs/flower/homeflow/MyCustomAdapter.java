package kookmin.cs.flower.homeflow;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import kookmin.cs.flower.homeflow.Service.SocketService;

/**
 * @author Jinsung Choi, bugslife102401@nate.com
 * @version 0.0.2
 * @brief class overriding BaseAdapter
 * @details This class needs for listview customizing.
 * @date 2015-04-08
 */
public class MyCustomAdapter extends BaseAdapter {

  Context ctx;
  int layout;
  ArrayList<MyCustomDTO> list;
  LayoutInflater inf;

  public MyCustomAdapter(Context ctx, int layout, ArrayList<MyCustomDTO> list) {
    this.ctx = ctx;
    this.layout = layout;
    this.list = list;

    inf = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  }

  @Override
  public int getCount() {
    return list.size();
  }

  @Override
  public Object getItem(int position) {
    return list.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = inf.inflate(layout, null);
    }

    ImageView clock_img = (ImageView) convertView.findViewById(R.id.clock_img);
    TextView dash_list_txt = (TextView) convertView.findViewById(R.id.dash_list_txt);
    final Button enable_btn = (Button) convertView.findViewById(R.id.enable_btn);
    Button unfold_btn = (Button) convertView.findViewById(R.id.unfold_btn);

    MyCustomDTO dto = list.get(position);

    clock_img.setImageResource(dto.getImgIcon());
    dash_list_txt.setText(dto.getFlowText());

    enable_btn.setId(dto.getBtn1());
    unfold_btn.setId(dto.getBtn2());

    final TextView tv = dash_list_txt;
    enable_btn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        {
          Log.i("mytag", tv.getText().toString());
          if (view.getId() == R.id.enable_btn) {
            Intent intent = new Intent(ctx, SocketService.class);
            intent.putExtra("selectItem", tv.getText());
            ctx.startService(intent);
          }
          enable_btn.setText("활성");
        }
      }
    });

    return convertView;
  }
}
