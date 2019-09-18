package com.liuwang.tempcontrol;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.com.newland.nle_sdk.responseEntity.SensorDataPageDTO;
import cn.com.newland.nle_sdk.responseEntity.base.BaseResponseEntity;
import cn.com.newland.nle_sdk.util.NCallBack;
import cn.com.newland.nle_sdk.util.NetWorkBusiness;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
import retrofit2.Call;
import retrofit2.Response;


public class ChartActivity extends AppCompatActivity {
    private LineChartView line_chart;
    private NetWorkBusiness netWorkBusiness;
    private List<Integer> count=new ArrayList<>();
    private String accessToken;
    private String[] date={"0-1","2-3","3-4","4-5","5-6","6-7","7-8","8-9"};
//    String[] date = {"10-22","11-22","12-22","1-22","6-22","5-23","5-22","6-22","5-23","5-22"};//X轴的标注
//    int[] score= {50,42,90,33,10,74,22,18,79,20};//图表的数据点

    private List<PointValue> mPointValues = new ArrayList<PointValue>();//X轴对应的Y值
    private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();//X轴的值
//    private String historyData="http://api.nlecloud.com/devices/42149/Datas/Grouping";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        line_chart=findViewById(R.id.line_chart);

        Bundle bundle = getIntent().getExtras();
        accessToken = bundle.getString("accessToken");   //获得传输秘钥
        netWorkBusiness = new NetWorkBusiness(accessToken,"http://api.nlecloud.com:80/");   //进行登录连接

        getHistorySensorData();


    }

    /**
     * 设置X轴的显示
     */
    private void getAxisXLables() {
        for (int i=0;i<date.length;i++){
            //为每个x轴的标注填充数据
            mAxisXValues.add(new AxisValue(i).setLabel(date[i]));
        }
    }

    /**
     * 图表每个点的显示
     */
    private void getAxisPoints() {
        for (int i=0;i<count.size()/*score.length*/;i++){
            mPointValues.add(new PointValue(i, count.get(i)/*score[i]*/));
        }
    }

    private void initLineChart() {
        Line line=new Line(mPointValues).setColor(Color.parseColor("#FFCD41"));//折线的颜色（橙色）
        List<Line> lines=new ArrayList<Line>();
        /**
         * 折线图上每个数据点的形状  这里是圆形
         * （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
         */
        line.setShape(ValueShape.CIRCLE);
        line.setCubic(false);//曲线是否平滑，即是曲线还是折线
        line.setFilled(false);//是否填充曲线的面积
        line.setHasLabels(true);//曲线的数据坐标是否加上备注
//      line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        line.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
        lines.add(line);
        LineChartData data = new LineChartData();
        data.setLines(lines);

        //坐标轴
        Axis axisX = new Axis(); //X轴
        axisX.setHasTiltedLabels(true);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
        axisX.setTextColor(Color.GRAY);  //设置字体颜色
        //axisX.setName("date");  //表格名称
        axisX.setTextSize(10);//设置字体大小
        axisX.setMaxLabelChars(8); //最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisXValues.length
        axisX.setValues(mAxisXValues);  //填充X轴的坐标名称
        data.setAxisXBottom(axisX); //x 轴在底部
        //data.setAxisXTop(axisX);  //x 轴在顶部
        axisX.setHasLines(true); //x 轴分割线

        // Y轴是根据数据的大小自动设置Y轴上限(在下面我会给出固定Y轴数据个数的解决方案)
        Axis axisY = new Axis();  //Y轴
        axisY.setName("");//y轴标注
        axisY.setTextSize(10);//设置字体大小
        data.setAxisYLeft(axisY);  //Y轴设置在左边
        //data.setAxisYRight(axisY);  //y轴设置在右边


        //设置行为属性，支持缩放、滑动以及平移
        line_chart.setInteractive(true);
        line_chart.setZoomType(ZoomType.HORIZONTAL);
        line_chart.setMaxZoom((float) 2);//最大方法比例
        line_chart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        line_chart.setLineChartData(data);
        line_chart.setVisibility(View.VISIBLE);
        /**注：下面的7，10只是代表一个数字去类比而已
         * 当时是为了解决X轴固定数据个数。见（http://forum.xda-developers.com/tools/programming/library-hellocharts-charting-library-t2904456/page2）;
         */
        Viewport v = new Viewport(line_chart.getMaximumViewport());
        v.left = 0;
        v.right = 7;
        line_chart.setCurrentViewport(v);
        /**
         * Viewport v = new Viewport(lineChart.getMaximumViewport());
         * v.left = 0;
         * v.right = 7;
         * lineChart.setCurrentViewport(v);
         * 这4句代码可以设置X轴数据的显示个数（x轴0-7个数据）。
         *
         * 1 当数据点个数小于（29）的时候，缩小到极致hellochart默认的是所有显示。
         *
         * 2 当数据点个数大于（29）的时候。
         *
         * 2.1 若不设置axisX.setMaxLabelChars(int count)这句话,则会自动适配X轴所能显示的尽量合适的数据个数。
         *
         * 2.2 若设置axisX.setMaxLabelChars(int count)这句话,33个数据点测试，
         *
         * 2.2.1 若 axisX.setMaxLabelChars(10); 里面的10大于v.right= 7; 里面的7，则 刚开始X轴显示7条数据，然后缩放的时候X轴的个数会保证大于7小于10
         *
         * 2.2.2 若小于v.right= 7;中的7,反正我感觉是这两句都好像失效了的样子 - -! 若这儿不设置 v.right= 7; 这句话，则图表刚开始就会尽可能的显示所有数据，交互性太差
         */

    }

    public void getHistorySensorData(){
        netWorkBusiness.getSensorData("42149", "nl_finger", "3", "3", "", "",
                "DESC", "20", "1", new NCallBack<BaseResponseEntity<SensorDataPageDTO>>() {
                    @Override
                    protected void onResponse(BaseResponseEntity<SensorDataPageDTO> response) {
                        Log.d("-----------------", "----------");
                    }

                    @Override
                    public void onResponse(Call<BaseResponseEntity<SensorDataPageDTO>> call, Response<BaseResponseEntity<SensorDataPageDTO>> response) {
//                        Log.d("-----------------", "我的Call<BaseResponseEntity<SensorDataPageDTO>> call, Response<BaseResponseEntity<SensorDataPageDTO>> response");
                        BaseResponseEntity baseResponseEntity=response.body();
                        if (baseResponseEntity!=null){
                            final Gson gson=new Gson();
                            JSONObject jsonObject=null;
                            String msg=gson.toJson(baseResponseEntity);
//                            Log.d("msg", "onResponse________msg:"+msg);
                            try {
                                jsonObject = new JSONObject(msg);   //解析数据.
                                JSONObject resultObj = (JSONObject) jsonObject.get("ResultObj");
//                                Log.d("resultObj", "onResponse________resultObj:"+resultObj);
                                JSONArray dataPoints = resultObj.optJSONArray("DataPoints");
//                                Log.d("dataPoints", "onResponse________dataPoints:"+dataPoints);
//                                JSONObject dataPoints = resultObj.optJSONObject("DataPoints");
                                Toast.makeText(ChartActivity.this,"这只是一行测试代码",Toast.LENGTH_SHORT).show();
                                for (int i=0;i<dataPoints.length();i++){
                                    JSONObject jsonObject1 = dataPoints.optJSONObject(i);
//                                    Log.d("jsonObject1", "onResponse________jsonObject1:"+jsonObject1);
                                    JSONArray pointDTO = jsonObject1.optJSONArray("PointDTO");
//                                    Log.d("pointDTO", "onResponse________pointDTO:"+pointDTO);
                                    for (int j=0;j<pointDTO.length();j++){
                                        JSONObject jsonObject2 = pointDTO.optJSONObject(i);
//                                        Log.d("jsonObject2", "onResponse________jsonObject2:"+jsonObject2);
                                        int value = jsonObject2.optInt("Value");
                                        count.add(value);
                                        Log.d("-----------------", "接收到的Value的数据："+value);
                                    }
                                }
                                Log.d("-----------------", "count集合中的数据："+count);
                                getAxisXLables();//获取x轴的标注
                                getAxisPoints();//获取坐标点
                                initLineChart();//初始化

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                //在子线程中执行Http请求，并将最终的请求结果回调到okhttp3.Callback中
////                HttpUtil.sendOkHttpRequest(historyData, new Callback() {
////                    @Override
////                    public void onFailure(Call call, IOException e) {
////                        //在这里进行异常情况处理
////                    }
////                    @Override
////                    public void onResponse(Call call, @NonNull Response response) throws IOException {
////                        //得到服务器返回的具体内容
////                        String ponse= response.body().string();
////                        Log.d("", "onResponse: ");
//////                        JSONWithGSON(ponse);
////                        //显示UI界面，调用的showResponse方法
////                        // showResponse(responseData);
////                    }
////                }, accessToken);
//            }
//        }).start();
    }
}
