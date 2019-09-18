package com.kunlan.game.hellochart;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;

public class MainActivity extends AppCompatActivity {

    ColumnChartView columnChartView=null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        columnChartView=(ColumnChartView) findViewById(R.id.test_content);
        generateDefaultData();
        Button button=findViewById(R.id.btn1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,Main2Activity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void generateDefaultData(){
        //定义有多少个柱子
        int numColumns = 8;
        //定义表格实现类
        ColumnChartData columnChartData;
        //Column 是下图中柱子的实现类
        List<Column> columns =new ArrayList<>();

        //循环初始化每根柱子，
        for(int i=0;i<numColumns;i++){
            //SubcolumnValue 是下图中柱子中的小柱子的实现类，下面会解释我说的是什么
            List<SubcolumnValue>  values=new ArrayList<>();

//            //每一根柱子中有两根小柱子
//            values.add(new SubcolumnValue((float) Math.random() * 50f + 5, ChartUtils.pickColor()));
//            values.add(new SubcolumnValue((float) Math.random() * 50f + 5, ChartUtils.pickColor()));

            //初始化数据值，同时也初始化动画值
            values.add(new SubcolumnValue((float) Math.random() * 50f + 5,
                    ChartUtils.pickColor()).setTarget((float) Math.random() * 100));
            values.add(new SubcolumnValue((float) Math.random() * 50f + 5,
                    ChartUtils.pickColor()).setTarget((float) Math.random() * 100));

            //初始化Column
            Column column = new Column(values); //为柱体赋值
            column.setHasLabels(true);     //显示柱体的坐标值
            //添加的地方，选中时出现label
            column.setHasLabelsOnlyForSelected(true);
            columns.add(column);   //添加柱体
        }

        /*************************************添加X,Y坐标轴*********************************/


//        //给表格添加写好数据的柱子
//        columnChartData = new ColumnChartData(columns);
//        //给画表格的View添加要画的表格
//        columnChartView.setColumnChartData(columnChartData);


        columnChartData = new ColumnChartData(columns);

        /********************************添加坐标轴**********************/
        Axis axisBootom = new Axis();
        Axis axisLeft = new Axis();
        /*******************************数组列对应填入名称*************/

        List<AxisValue> axisValuess=new ArrayList<>();
        for(int i=0;i<numColumns;i++){
            axisValuess.add(new AxisValue(i).setLabel("lable"+i));
        }

        axisBootom.setValues(axisValuess);
        /*******************************命名坐标轴**********************/
        axisBootom.setName("横坐标X");
        axisLeft.setName("纵坐标Y");

        /************************************添加经纬线**************************/
        axisBootom.setHasLines(true);         //添加横线
        axisLeft.setHasLines(true);           //添加纵线

        /**************************************设置坐标轴位置*************************/
        columnChartData.setAxisXBottom(axisBootom);
        columnChartData.setAxisYLeft(axisLeft);

       //添加的地方，选中时变粗。通常情况下点击变粗会立马缩回去，现在会变粗停住
        columnChartView.setValueSelectionEnabled(true);
        /**************************************添加柱状图坐标轴的属性***********************************************/
        columnChartView.setColumnChartData(columnChartData);
        /**********************************开启柱状图动画**************************************/
        columnChartView.startDataAnimation(2000);

    }
}
