package com.kunlan.game.hellochart;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;

public class Main2Activity extends AppCompatActivity {
//    private LineChartView chart;        //显示线条的自定义View
//    private LineChartData data;          // 折线图封装的数据类
//    private int numberOfLines = 3;         //线条的数量
//    private int maxNumberOfLines = 4;     //最大的线条数据
//    private int numberOfPoints = 12;     //点的数量

//    float[][] randomNumbersTab = new float[maxNumberOfLines][numberOfPoints]; //二维数组，线的数量和点的数量
//
//    private boolean hasAxes = true;       //是否有轴，x和y轴
//    private boolean hasAxesNames = true;   //是否有轴的名字
//    private boolean hasLines = true;       //是否有线（点和点连接的线）
//    private boolean hasPoints = true;       //是否有点（每个值的点）
//    private ValueShape shape = ValueShape.CIRCLE;    //点显示的形式，圆形，正方向，菱形
//    private boolean isFilled = false;                //是否是填充
//    private boolean hasLabels = false;               //每个点是否有名字
//    private boolean isCubic = true;                 //是否是立方的，线条是直线还是弧线
//    private boolean hasLabelForSelected = true;       //每个点是否可以选择（点击效果）
//    private boolean pointsHaveDifferentColor;           //线条的颜色变换
//    private boolean hasGradientToTransparent = false;      //是否有梯度的透明



    public LineChartView lineChart;           //定义折线图
    public ColumnChartView columnChartView;         //定义柱状图
/**************************************************数据添加***************************************/
//    private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();   //x轴方向的坐标数据
//    private List<AxisValue> mAxisYValues = new ArrayList<AxisValue>();  //y轴方向的坐标数据
//    private List<TemperatureBean> listBlood = new ArrayList<TemperatureBean>();//数据

    public List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();          //定义X轴坐标集合（折线图，柱状图）
    public List<PointValue> mPointValues_line = new ArrayList<PointValue>();        //定义数据集合（折线图）
    public List<PointValue> mPointValues_line_max = new ArrayList<PointValue>();        //定义警报上限（折线图）
    public List<PointValue> mPointValues_line_min = new ArrayList<PointValue>();        //定义警报下限（折线图）
    public float[] mPointValues_column;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
//        initView();
//        initData();
//        initEvent();
        lineChart = findViewById(R.id.chart);
        String []aa=new String[]{"1月","2月","3月","4月","5月"};
        float []bb=new float[]{30,40,50,60,70};
        initPointData(5,aa,bb,50,40);
        initLineChart(100,0,"温度");

        Button button=findViewById(R.id.btn2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Main2Activity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    /*******************************************折线图***************************************************************************************************/
    //初始化坐标点
//    public void initPointData(int number_point,String[] timeX ,float[] dataY){
//        for(int i=0;i<number_point;i++){
//        mAxisXValues.add(new AxisValue(i).setLabel(timeX[i]));
//        mPointValues_line.add(new PointValue(i ,dataY[i]));
//        mPointValues_column = dataY;
//        }
//    }
    public void initPointData(int number_point,String[] timeX ,float[] dataY,float max,float min){
        for(int i=0;i<number_point;i++){
            mAxisXValues.add(new AxisValue(i).setLabel(timeX[i]));//添加X轴坐标数据
            mPointValues_line.add(new PointValue(i ,dataY[i]));//添加点的数据（折线图）
            mPointValues_line_max.add(new PointValue(i ,max));//添加警报上限（折线图）
            mPointValues_line_min.add(new PointValue(i ,min));//添加警报上限（折线图）
            mPointValues_column = dataY;                    //添加点的数据（柱状图）
        }
    }
    //初始化折线图
    public void initLineChart(int maxY,int minY,String nameY){
        List<Line> lines = new ArrayList<Line>();//定义线集合
        Line line = new Line(mPointValues_line).setColor(Color.parseColor("#FFCD41"));  //定义设置数据折线的颜色（橙色）
        Line line_max = new Line(mPointValues_line_max).setColor(Color.parseColor("#FFC60000")); //定义设置警报上限折线（红色）
        Line line_min = new Line(mPointValues_line_min).setColor(Color.parseColor("#FF0007DA")); //定义设置警报下限折线（蓝色）
        //设置上下限折线
        line_max.setHasPoints(false);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
        line_max.setStrokeWidth(1);//设置线的宽度
        line_max.setPointRadius(1);// 设置节点半径
        line_min.setHasPoints(false);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
        line_min.setStrokeWidth(1);//设置线的宽度
        line_min.setPointRadius(1);// 设置节点半径
        //设置数据折线
        line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
        line.setCubic(false);//曲线是否平滑，即是曲线还是折线
        line.setFilled(false);//是否填充曲线的面积
        line.setHasLabels(true);//曲线的数据坐标是否加上备注
        line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        line.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）

        line.setStrokeWidth(1);//设置线的宽度
        line.setPointRadius(3);// 设置节点半径

        lines.add(line);//添加数据折线到线集合中
        lines.add(line_max);//添加上限折线到线集合中
        lines.add(line_min);//添加下限折线到线集合中
        LineChartData data = new LineChartData();
        data.setLines(lines);
        data.setValueLabelBackgroundColor(0x00ffffff);// 设置数据背景颜色
        data.setValueLabelTextSize(5); //设置数据文字大小
        //坐标轴
        Axis axisX = new Axis(); //X轴
        axisX.setHasTiltedLabels(true);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
        axisX.setTextColor(Color.GRAY);  //设置字体颜色
        axisX.setName("X轴");  //表格名称
        axisX.setTextSize(8);//设置字体大小
        axisX.setMaxLabelChars(6); //最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数5<=x<=mAxisXValues.length
        axisX.setValues(mAxisXValues);  //填充X轴的坐标名称
        data.setAxisXBottom(axisX); //x 轴在底部
        //data.setAxisXTop(axisX);  //x 轴在顶部
        axisX.setHasLines(true); //x 轴分割线

        // Y轴是根据数据的大小自动设置Y轴上限(在下面我会给出固定Y轴数据个数的解决方案)
        Axis axisY = new Axis();  //Y轴
        axisY.setName(nameY);//y轴标注
        axisY.setTextSize(8);//设置字体大小
        data.setAxisYLeft(axisY);  //Y轴设置在左边
        //data.setAxisYRight(axisY);  //y轴设置在右边
//        Viewport port = initViewPort(0,10);//初始化X轴10个间隔坐标
        //设置行为属性，支持缩放、滑动以及平移
//        lineChart.setCurrentViewportWithAnimation(port);
        lineChart.setInteractive(true);
        lineChart.setValueSelectionEnabled(true);//添加的地方，选中时变粗。通常情况下点击变粗会立马缩回去，现在回变粗停住停住
        lineChart.setZoomType(ZoomType.HORIZONTAL);  //平移
        lineChart.setMaxZoom((float) 20);//最大方法比例
        lineChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChart.setLineChartData(data);
        lineChart.setVisibility(View.VISIBLE);
        /**注：下面的5，0只是代表一个数字去类比而已
         * 当时是为了解决X轴固定数据个数。见（http://forum.xda-developers.com/tools/programming/library-hellocharts-charting-library-t2904456/page2）;
         */
        Viewport v = new Viewport(lineChart.getMaximumViewport());
        v.bottom = minY;
        v.top = maxY;
        //固定Y轴的范围,如果没有这个,Y轴的范围会根据数据的最大值和最小值决定,这不是我想要的
        lineChart.setMaximumViewport(v);// 这个方法前设置固定刻度，后设置滑动刻度
        //这2个属性的设置一定要在lineChart.setMaximumViewport(v);这个方法之后,不然显示的坐标数据是不能左右滑动查看更多数据的
        v.left = 0;
        v.right = 7;
        lineChart.setCurrentViewport(v);
    }




//    private void initView() {
//        //实例化
//        chart = (LineChartView) findViewById(R.id.chart);
//    }
//
//    private void initData() {
//        // Generate some random values.
//        generateValues();   //设置四条线的值数据
//        generateData();    //设置数据
//
//        // Disable viewport recalculations, see toggleCubic() method for more info.
//        chart.setViewportCalculationEnabled(false);
//
//        chart.setZoomType(ZoomType.HORIZONTAL);//设置线条可以水平方向收缩，默认是全方位缩放
//        resetViewport();   //设置折线图的显示大小
//    }
//
//    private void initEvent() {
//        chart.setOnValueTouchListener(new ValueTouchListener());
//
//    }
//
//    /**
//     * 图像显示大小
//     */
//    private void resetViewport() {
//        // Reset viewport height range to (0,100)
//        final Viewport v = new Viewport(chart.getMaximumViewport());
//        v.bottom = 0;
//        v.top = 100;
//        v.left = 0;
//        v.right = numberOfPoints - 1;
//        chart.setMaximumViewport(v);
//        chart.setCurrentViewport(v);
//    }
//
//    /**
//     * 设置四条线条的数据
//     */
//    private void generateValues() {
////        for (int i = 0; i < maxNumberOfLines; ++i) {
////            for (int j = 0; j < numberOfPoints; ++j) {
////                randomNumbersTab[i][j] = (float) Math.random() * 100f;
////            }
//
//                //TemperatureBean类里面包含一个时间字符串，一个最高温度和一个最低温度
//                //设置一周内的时间和数据
//                listBlood.add(new TemperatureBean("2017-5-1", 35, 20));
//                listBlood.add(new TemperatureBean("2017-5-2", 41, 28));
//                listBlood.add(new TemperatureBean("2017-5-3", 30, 20));
//                listBlood.add(new TemperatureBean("2017-5-4", 29, 15));
//
//                listBlood.add(new TemperatureBean("2017-5-5", 25, 10));
//                listBlood.add(new TemperatureBean("2017-5-6", 30, 20));
//                listBlood.add(new TemperatureBean("2017-5-7", 25, 21));
////            }
////        }
//                //设置x轴坐标 ，显示的是时间5-1,5-2.。。。
//                for (int i = 0; i < 7; i++) {
//                    //获取年月日中的月日
//                    String data = listBlood.get(i).getData();
//                    String[] split = data.split("-");
//                    data = split[1] + " - " + split[2];
//                    mAxisXValues.add(new AxisValue(i*(12/7)).setLabel(data));
//                }
//
//                //设置y轴坐标，显示的是数值0、1、2、3、4、5、6...50。。。
//                for (int i = 0; i < 50; i++) {
//                    mAxisYValues.add(new AxisValue(i * 2).setLabel("" + i));
//                }
//    }
//
//    /**
//     * 配置数据
//     */
//    private void generateData() {
//        //存放线条对象的集合
//        List<Line> lines = new ArrayList<Line>();
//        //把数据设置到线条上面去
//        for (int i = 0; i < numberOfLines; ++i) {
//
//            List<PointValue> values = new ArrayList<PointValue>();
//            for (int j = 0; j < numberOfPoints; ++j) {
//                values.add(new PointValue(j, randomNumbersTab[i][j]));
//            }
//
//
//            /*******************************设置线条属性******************************************/
//            Line line = new Line(values);
//            line.setColor(ChartUtils.COLORS[i]);
//            line.setShape(shape);
//            line.setCubic(isCubic);
//            line.setFilled(isFilled);
//            line.setHasLabels(hasLabels);
//            line.setHasLabelsOnlyForSelected(hasLabelForSelected);
//            line.setHasLines(hasLines);
//            line.setHasPoints(hasPoints);
////            line.setHasGradientToTransparent(hasGradientToTransparent);
//            if (pointsHaveDifferentColor) {
//                line.setPointColor(ChartUtils.COLORS[(i + 1) % ChartUtils.COLORS.length]);
//            }
//            lines.add(line);
//        }
//
//
//        /**************************************配置折线图属性**************************************/
//        data = new LineChartData(lines);
////        if (hasAxes) {
////            Axis axisX = new Axis();
////            Axis axisY = new Axis().setHasLines(true);
////            if (hasAxesNames) {
////                axisX.setTextColor(Color.BLACK);//设置x轴字体的颜色
////                axisY.setTextColor(Color.BLACK);//设置y轴字体的颜色
////                axisX.setName("Axis X");
////                axisY.setName("Axis Y");
////            }
////            data.setAxisXBottom(axisX);
////            data.setAxisYLeft(axisY);
////        }
//
//
//        if (hasAxes) {
//            Axis axisX = new Axis();
//            Axis axisY = new Axis().setHasLines(true);
//            if (hasAxesNames) {
//                axisX.setName("时间");//x轴坐标显示的标题
//                axisY.setName("温度/C");//y轴坐标显示的标题
//            }
//
//            //对x轴，数据和属性的设置
//            axisX.setTextSize(8);//设置字体的大小
//            axisX.setHasTiltedLabels(false);//x坐标轴字体是斜的显示还是直的，true表示斜的
//            axisX.setTextColor(Color.BLACK);//设置字体颜色
//            axisX.setHasLines(true);//x轴的分割线
//            axisX.setValues(mAxisXValues); //设置x轴各个坐标点名称
//
//            //对Y轴 ，数据和属性的设置
//            axisY.setTextSize(10);
//            axisY.setHasTiltedLabels(false);//true表示斜的
//            axisY.setTextColor(Color.BLACK);//设置字体颜色
//            axisY.setValues(mAxisYValues); //设置x轴各个坐标点名称
//
//
//            data.setAxisXBottom(axisX);//x轴坐标线的文字，显示在x轴下方
//            //data.setAxisXTop();      //显示在x轴上方
//            data.setAxisYLeft(axisY);   //显示在y轴的左边，也可以设置在右边
//
//        }
//
//        else {
//            data.setAxisXBottom(null);
//            data.setAxisYLeft(null);
//        }
//
//        data.setBaseValue(Float.NEGATIVE_INFINITY);
//        chart.setLineChartData(data);
//
//    }
//
//    /**
//     * 触摸监听类
//     */
//    private class ValueTouchListener implements LineChartOnValueSelectListener {
//
//        @Override
//        public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
//            Toast.makeText(Main2Activity.this, "Selected: " + value, Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onValueDeselected() {
//
//
//        }
//
//    }

}