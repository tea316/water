package lt.com.water;

import android.app.Activity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/9/26 0026.
 */
public class MyWater extends Activity {

    /********************ViewPager**********************/
    private ViewPager list_pager;

    private List<View> list_view;

    private viewpageAdapter adpter;

    private String uid;

    private String ViewId;

    private LinearLayout LinNO;



    Handler handler;

    private ImageView mimagBack,mimagAdd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initGetUid();
        initView();
        initDataOkhttp();

    }

    private void initView() {
        list_pager = (ViewPager) findViewById(R.id.list_pager);
        list_view = new ArrayList<>();
        LinNO= (LinearLayout) this.findViewById(R.id.LinNO);
        mimagAdd= (ImageView) this.findViewById(R.id.imagAdd);
        mimagAdd.setOnClickListener(addViews);
        mimagBack=(ImageView) this.findViewById(R.id.imagBack);
        mimagBack.setOnClickListener(imageBack);


    }

    private void initGetUid() {

        try {
            Intent Intent=this.getIntent();
            uid=Intent.getStringExtra("uid");

        }catch (Exception e){
            e.printStackTrace();

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(this);

    }

    private void On_off_switch(final Map map, final String valuesName) {

        if(map.get("viewid")!=null){
            OkHttpUtils.get("http://nanchang.shlantian.cn:8080/water/view_kaiguan")
                    .params(map)
                    .tag(this)//
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {

                             TastyToast.makeText(getApplicationContext(), valuesName + "", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);

                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {

                            TastyToast.makeText(getApplicationContext(), "服务器数据异常", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                            super.onError(call, response, e);
                        }
                    });
        }else{
            TastyToast.makeText(getApplicationContext(), "暂无生态景，无法提供服务", TastyToast.LENGTH_SHORT, TastyToast.CONFUSING);
        }

    }


    View.OnClickListener imageBack=new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            MyWater.this.finish();

        }
    };



    View.OnClickListener addViews=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent=new Intent();
            intent.putExtra("url","http://nanchang.shlantian.cn:8080/water/view/appH5/add.html");
            intent.setClass(MyWater.this, WaterActivity.class);
            startActivity(intent);

        }
    };


    public void initDataOkhttp() {

        final Map map = new HashMap();
        map.put("uid",uid);
        //布水
        // http://121.43.108.95:8081/water/app_queryshishi
        //掌上植物
        //http://121.43.158.34:8080/botany/app_queryshishi
        OkHttpUtils.get("http://nanchang.shlantian.cn:8080/water/app_queryshishi")
                .params(map)
                .tag(this)//
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {

                        JSONObject JS = null;
                        try {
                            JS = new JSONObject(s);

                            if ("数据获取成功".equals(JS.get("msg").toString())) {

                                list_pager.setVisibility(View.VISIBLE);
                                LinNO.setVisibility(View.GONE);
                                ViewId = JS.getJSONArray("data").getJSONObject(0).get("id").toString();
                                for (int i = 0; i < JS.getJSONArray("data").length(); i++) {
                                    View view = LayoutInflater.from(MyWater.this).inflate(R.layout.items, null);
                                    TextView mtemers = (TextView) view.findViewById(R.id.temers);
                                    TextView mtimerWeek = (TextView) view.findViewById(R.id.timerWeek);
                                    TextView mttimer = (TextView) view.findViewById(R.id.mtimers);
                                    TextView mtextXL = (TextView) view.findViewById(R.id.textXL);
                                    final LinearLayout Grop = (LinearLayout) view.findViewById(R.id.Grop);
                                    final LinearLayout GropPH = (LinearLayout) view.findViewById(R.id.GropPH);
                                    TextView  mpingWd = (TextView) view.findViewById(R.id.pingWd);
                                    TextView  mpingPh = (TextView) view.findViewById(R.id.pingPh);
                                    LinearLayout mlinw = (LinearLayout) view.findViewById(R.id.linw);
                                    LinearLayout mlinp = (LinearLayout) view.findViewById(R.id.linp);
                                    final TextView mtextphp = (TextView) view.findViewById(R.id.textphp);
                                    final TextView mtextphw = (TextView) view.findViewById(R.id.textphw);
                                    DoughnutView doughnutView = (DoughnutView) view.findViewById(R.id.doughnutView);
                                    DoughnutView doughnutViewb = (DoughnutView) view.findViewById(R.id.doughnutViewb);
                                    SlideSwitch mslidswitcha = (SlideSwitch) view.findViewById(R.id.slidswitcha);
                                    SlideSwitch mslidswitchb = (SlideSwitch) view.findViewById(R.id.slidswitchb);
                                    SlideSwitch mslidswitchc = (SlideSwitch) view.findViewById(R.id.slidswitchc);

                                    int dataSetSize = JS.getJSONObject("dataSet").length();
                                    int jiluSize = JS.getJSONArray("data").getJSONObject(i).getJSONArray("jiluList").length();

                                    //is_kg: 1开  0关
                                    //type 1水位 2加热量 3灯光
                                    //viewid  生态景 Id
                                    //水位
                                    mslidswitcha.setSlideListener(new SlideSwitch.SlideListener() {
                                        @Override
                                        public void open() {
                                            Map map = new HashMap();
                                            map.put("viewid", ViewId);
                                            map.put("type", "1");
                                            map.put("is_kg", "1");
                                            On_off_switch(map, "水位已开启");

                                        }

                                        @Override
                                        public void close() {

                                            Map map = new HashMap();
                                            map.put("viewid", ViewId);
                                            map.put("type", "1");
                                            map.put("is_kg", "0");
                                            On_off_switch(map, "水位已关闭");

                                        }
                                    });

                                    //加热量
                                    mslidswitchb.setSlideListener(new SlideSwitch.SlideListener() {
                                        @Override
                                        public void open() {
                                            Map map = new HashMap();
                                            map.put("viewid", ViewId);
                                            map.put("type", "2");
                                            map.put("is_kg", "1");
                                            On_off_switch(map, "加热量已打开");

                                        }

                                        @Override
                                        public void close() {

                                            Map map = new HashMap();
                                            map.put("viewid", ViewId);
                                            map.put("type", "2");
                                            map.put("is_kg", "0");
                                            On_off_switch(map, "加热量已关闭");

                                        }
                                    });

                                    //灯光
                                    mslidswitchc.setSlideListener(new SlideSwitch.SlideListener() {
                                        @Override
                                        public void open() {
                                            Map map = new HashMap();
                                            map.put("viewid", ViewId);
                                            map.put("type", "3");
                                            map.put("is_kg", "1");
                                            On_off_switch(map, "灯光已开启");
                                        }

                                        @Override
                                        public void close() {

                                            Map map = new HashMap();
                                            map.put("viewid", ViewId);
                                            map.put("type", "3");
                                            map.put("is_kg", "0");
                                            On_off_switch(map, "灯光已关闭");
                                        }
                                    });


                                    mlinw.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            mtextphp.setBackgroundColor(Color.parseColor("#FFFFFF"));
                                            mtextphp.setTextColor(Color.parseColor("#00BF86"));
                                            mtextphw.setBackgroundColor(Color.parseColor("#00BF86"));
                                            mtextphw.setTextColor(Color.parseColor("#FFFFFF"));
                                            Grop.setVisibility(View.VISIBLE);
                                            GropPH.setVisibility(View.GONE);


                                        }
                                    });

                                    mlinp.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            mtextphp.setBackgroundColor(Color.parseColor("#00BF86"));
                                            mtextphp.setTextColor(Color.parseColor("#FFFFFF"));
                                            mtextphw.setBackgroundColor(Color.parseColor("#FFFFFF"));
                                            mtextphw.setTextColor(Color.parseColor("#00BF86"));
                                            Grop.setVisibility(View.GONE);
                                            GropPH.setVisibility(View.VISIBLE);

                                        }
                                    });
                                    Calendar calendar = Calendar.getInstance();
                                    int year;
                                    int moth;
                                    int day;
                                    int hour;
                                    int min;
                                    year = calendar.get(Calendar.YEAR);
                                    moth = calendar.get(Calendar.MONTH);
                                    day = calendar.get(Calendar.DAY_OF_MONTH);
                                    hour = calendar.get(Calendar.HOUR_OF_DAY);
                                    min = calendar.get(Calendar.MINUTE);

                                    int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
                                    if (Calendar.MONDAY == weekDay) {
                                        mtimerWeek.setText("周  一");
                                    }
                                    if (Calendar.TUESDAY == weekDay) {
                                        mtimerWeek.setText("周  二");
                                    }
                                    if (Calendar.WEDNESDAY == weekDay) {
                                        mtimerWeek.setText("周  三");
                                    }
                                    if (Calendar.THURSDAY == weekDay) {
                                        mtimerWeek.setText("周  四");
                                    }
                                    if (Calendar.FRIDAY == weekDay) {
                                        mtimerWeek.setText("周  五");
                                    }
                                    if (Calendar.SATURDAY == weekDay) {
                                        mtimerWeek.setText("周  六");
                                    }
                                    if (Calendar.SUNDAY == weekDay) {
                                        mtimerWeek.setText("周  日");
                                    }

                                    List<DrawBean> DrawBeana = new ArrayList<>();
                                    List<DrawBean> DrawBeanb = new ArrayList<>();
                                    DrawBean baena;
                                    DrawBean baenb;
                                    /*********************************历史数据——温度*********************************************/



                                    for (int j = 0; j < jiluSize; j++) {

                                        Float wenduG = Float.parseFloat(JS.getJSONArray("data").getJSONObject(i).getJSONArray("jiluList").getJSONObject(j).getString("wendu_gao").toString());

                                        Float wenduD = Float.parseFloat(JS.getJSONArray("data").getJSONObject(i).getJSONArray("jiluList").getJSONObject(j).getString("wendu_di").toString());

                                        baena = new DrawBean();

                                        baena.currentValue = j + wenduG + j / 3;

                                        baena.currentValueB = j + wenduD + j / 3;

                                        baena.maxValue = "40";

                                        String datatimer = JS.getJSONArray("data").getJSONObject(i).getJSONArray("jiluList").getJSONObject(j).getString("create_time").toString();

                                        String spStr[] = datatimer.split("-");

                                        baena.xName = spStr[1] + "/" + spStr[2];

                                        DrawBeana.add(baena);


                                    }

                                    DrawView viewa = new DrawView(MyWater.this);
                                    viewa.initData(DrawBeana, "温度");
                                    Grop.addView(viewa);




                                    //  Grop.addView(viewa);
                                    /*********************************历史数据——PH*********************************************/

                                    List<Map<String,String>> dataLSPH=new ArrayList<Map<String, String>>();

                                    for (int k = 0; k < jiluSize; k++) {

                                        Float wenduG = Float.parseFloat(JS.getJSONArray("data").getJSONObject(i).getJSONArray("jiluList").getJSONObject(k).getString("ph_gao").toString());

                                        Float wenduD = Float.parseFloat(JS.getJSONArray("data").getJSONObject(i).getJSONArray("jiluList").getJSONObject(k).getString("ph_di").toString());

                                        baenb = new DrawBean();

                                        baenb.currentValue = k + wenduG + k / 3;

                                        baenb.currentValueB = k + wenduD + k / 3;

                                        baenb.maxValue = "10";

                                        String datatimer = JS.getJSONArray("data").getJSONObject(i).getJSONArray("jiluList").getJSONObject(k).getString("create_time").toString();

                                        String spStr[] = datatimer.split("-");

                                        baenb.xName = spStr[1] + "/" + spStr[2];

                                        DrawBeanb.add(baenb);

                                    }

                                    DrawView viewb = new DrawView(MyWater.this);
                                    viewb.initData(DrawBeanb, "ph");
                                    GropPH.addView(viewb);

                                    /*****************************************圆圈****************************************************/
                                    if (!"null".equals(JS.getJSONArray("data").getJSONObject(i).get("json").toString())) {

                                        Double flw = Double.parseDouble(JS.getJSONArray("data").getJSONObject(i).getJSONObject("json").getString("wendu").toString());

                                        Double mpw=flw/40*100;

                                        String a=String.valueOf(mpw);

                                        Float b=  Float.parseFloat(a);

                                        doughnutView.setValue(36 * b / 10, "%");

                                        //转化一位小数

                                        //格式 new DecimalFormat(".0");
                                        DecimalFormat df = new DecimalFormat(".0");

                                        mpingWd.setText("温度："+ df.format(flw));

                                        Double flp = Double.parseDouble(JS.getJSONArray("data").getJSONObject(i).getJSONObject("json").getString("ph").toString());

                                        Double mph=flp/14*100;

                                        String c=String.valueOf(mph);

                                        Float d=  Float.parseFloat(c);

                                        doughnutViewb.setValue(36 * d / 10, "%");

                                        mpingPh.setText("PH：" + flp);

                                    }


                                    //加热量开关
                                    SlideSwitch.SlideListener HotListener = new SlideSwitch.SlideListener() {
                                        @Override
                                        public void open() {
                                            Map map = new HashMap();
                                            map.put("viewid", ViewId);
                                            map.put("type", "2");
                                            map.put("is_kg", "0");
                                            On_off_switch(map, "加热量已打开");

                                        }

                                        @Override
                                        public void close() {

                                            Map map = new HashMap();
                                            map.put("viewid", ViewId);
                                            map.put("type", "2");
                                            map.put("is_kg", "1");
                                            On_off_switch(map, "加热量已关闭");

                                        }
                                    };

                                    //时间
                                    mtemers.setText("" + year + "-" + (moth + 1) + "-" + day );
                                   String StringHour="";
                                    String StringMin ="";
                                    if(hour<10){
                                        StringHour="0"+hour;

                                    }else{
                                        StringHour=""+hour;
                                    }

                                    if(min<10){
                                        StringMin="0"+min;

                                    }else{
                                        StringMin=""+min;
                                    }

                                    mttimer.setText("" + StringHour + ":" + min);
                                    //序列号
                                    if(JS.getJSONArray("data").getJSONObject(i).isNull("json")){
                                        mtextXL.setText("");
                                    }else{

                                        mtextXL.setText(JS.getJSONArray("data").getJSONObject(i).getJSONObject("json").getString("no").toString());
                                    }
                                    list_view.add(view);

                                }

                                adpter = new viewpageAdapter(list_view);
                                list_pager.setAdapter(adpter);
                            } else {
                                list_pager.setVisibility(View.GONE);
                                LinNO.setVisibility(View.VISIBLE);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                });
    }



}
