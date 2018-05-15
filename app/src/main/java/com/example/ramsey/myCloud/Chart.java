package com.example.ramsey.myCloud;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Chart extends AppCompatActivity {

    private LineChart lineChart;
    private LineData data;
    private ArrayList<String> xVals;
    private LineDataSet dataSet;
    private ArrayList<Entry> yVals;
    private Random random;
    List<PieEntry> entries = new ArrayList<PieEntry>();

    private static final String TAG = "Chart";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);


        setContentView(R.layout.activity_chart);

        Toolbar toolbar = (Toolbar) findViewById(R.id.chart_tb);
        toolbar.setTitle("交互图表");//设置Toolbar标题
        toolbar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色
        setSupportActionBar(toolbar);//使toolbar支持ActionBar的特性

//        lineChart = new LineChart(this);
//        setContentView(lineChart);




        generateLineChart();

        generatePieData();



//        BarChart barchart = (BarChart) findViewById(R.id.barChart);
//
//        initsBar(barchart);


    }

    private void generateLineChart(){

        // Tag used to cancel the request
        String tag_string_req = "req_pie";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LINECHART, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Line Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {

                        JSONArray dbfp = jObj.getJSONArray("底板分拼");
                        JSONArray db1 = jObj.getJSONArray("底板I");
                        JSONArray db2 = jObj.getJSONArray("底板II");
                        JSONArray cw = jObj.getJSONArray("侧围");
                        JSONArray zongp = jObj.getJSONArray("总拼");
                        JSONArray zhuangp = jObj.getJSONArray("装配");
                        JSONArray bj = jObj.getJSONArray("报交");
                        JSONArray time = jObj.getJSONArray("时间点");

                        LineChart lineChart= (LineChart) findViewById(R.id.lineChart);

                        //创建描述信息
                        Description description = new Description();
                        description.setText("三个月各工段问题数量");
                        description.setTextColor(Color.RED);
                        description.setTextSize(20);
                        lineChart.setDescription(description);//设置图表描述信息
                        lineChart.setNoDataText("没有数据");//没有数据时显示的文字
                        lineChart.setNoDataTextColor(Color.BLUE);//没有数据时显示文字的颜色
                        lineChart.setDrawGridBackground(false);//chart 绘图区后面的背景矩形将绘制
                        lineChart.setDrawBorders(false);//禁止绘制图表边框的线
                        //lineChart.setBorderColor(); //设置 chart 边框线的颜色。
                        //lineChart.setBorderWidth(); //设置 chart 边界线的宽度，单位 dp。
                        //lineChart.setLogEnabled(true);//打印日志
                        //lineChart.notifyDataSetChanged();//刷新数据
                        //lineChart.invalidate();//重绘


/**
 * Entry 坐标点对象  构造函数 第一个参数为x点坐标 第二个为y点
 */
                        ArrayList<Entry> values1 = new ArrayList<>();
                        ArrayList<Entry> values2 = new ArrayList<>();
                        ArrayList<Entry> values3 = new ArrayList<>();
                        ArrayList<Entry> values4 = new ArrayList<>();
                        ArrayList<Entry> values5 = new ArrayList<>();
                        ArrayList<Entry> values6 = new ArrayList<>();
                        ArrayList<Entry> values7 = new ArrayList<>();


                        for (int i = 0; i < dbfp.length(); i++) {
                            Log.d(TAG, "onResponse: "+dbfp.get(i));
                            values1.add(new Entry( i,  dbfp.getInt(i), time.get(i)));
                        }

                        for (int i = 0; i < db1.length(); i++) {
                            Log.d(TAG, "onResponse: "+db1.get(i));
                            values2.add(new Entry( i,  db1.getInt(i)));
                        }

                        for (int i = 0; i < db2.length(); i++) {
                            Log.d(TAG, "onResponse: "+db2.get(i));
                            values3.add(new Entry( i,  db2.getInt(i)));
                        }

                        for (int i = 0; i < cw.length(); i++) {
                            Log.d(TAG, "onResponse: "+ cw.get(i));
                            values4.add(new Entry( i,  cw.getInt(i)));
                        }

                        for (int i = 0; i < zongp.length(); i++) {
                            Log.d(TAG, "onResponse: "+zongp.get(i));
                            values5.add(new Entry( i,  zongp.getInt(i)));
                        }

                        for (int i = 0; i < zhuangp.length(); i++) {
                            Log.d(TAG, "onResponse: "+zhuangp.get(i));
                            values6.add(new Entry( i,  zhuangp.getInt(i)));
                        }

                        for (int i = 0; i < bj.length(); i++) {
                            Log.d(TAG, "onResponse: "+bj.get(i));
                            values7.add(new Entry( i,  bj.getInt(i)));
                        }

                        Log.d(TAG, "onResponse: "+ values1);


                        //LineDataSet每一个对象就是一条连接线
                        LineDataSet set1;
                        LineDataSet set2;
                        LineDataSet set3;
                        LineDataSet set4;
                        LineDataSet set5;
                        LineDataSet set6;
                        LineDataSet set7;

                        //判断图表中原来是否有数据
                        if (lineChart.getData() != null &&
                                lineChart.getData().getDataSetCount() > 0) {
                            //获取数据1
                            set1 = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
                            set1.setValues(values1);
                            set2 = (LineDataSet) lineChart.getData().getDataSetByIndex(1);
                            set2.setValues(values2);
                            set3 = (LineDataSet) lineChart.getData().getDataSetByIndex(2);
                            set3.setValues(values3);
                            set4 = (LineDataSet) lineChart.getData().getDataSetByIndex(3);
                            set4.setValues(values4);
                            set5 = (LineDataSet) lineChart.getData().getDataSetByIndex(4);
                            set5.setValues(values5);
                            set6 = (LineDataSet) lineChart.getData().getDataSetByIndex(5);
                            set6.setValues(values6);
                            set7 = (LineDataSet) lineChart.getData().getDataSetByIndex(6);
                            set7.setValues(values7);
                            //刷新数据
                            lineChart.getData().notifyDataChanged();
                            lineChart.notifyDataSetChanged();
                        } else {
                            //设置数据1  参数1：数据源 参数2：图例名称
                            set1 = new LineDataSet(values1, "底板总拼");
                            set1.setColor(Color.BLUE);
                            set1.setCircleColor(Color.BLACK);
                            set1.setLineWidth(1f);//设置线宽
                            set1.setCircleRadius(3f);//设置焦点圆心的大小
                            set1.enableDashedHighlightLine(10f, 5f, 0f);//点击后的高亮线的显示样式
                            set1.setHighlightLineWidth(2f);//设置点击交点后显示高亮线宽
                            set1.setHighlightEnabled(true);//是否禁用点击高亮线
                            set1.setHighLightColor(Color.RED);//设置点击交点后显示交高亮线的颜色
                            set1.setValueTextSize(9f);//设置显示值的文字大小
                            set1.setDrawFilled(false);//设置禁用范围背景填充

                            //设置数据2
                            set2 = new LineDataSet(values2, "底板I");
                            set2.setColor(Color.GREEN);
                            set2.setCircleColor(Color.GRAY);
                            set2.setLineWidth(1f);
                            set2.setCircleRadius(3f);
                            set2.setValueTextSize(10f);

                            set3 = new LineDataSet(values3,"底板II");
                            set3.setColor(Color.RED);

                            set4 = new LineDataSet(values4,"侧围");
                            set4.setColor(Color.YELLOW);

                            set5 = new LineDataSet(values5,"底板II");
                            set5.setColor(Color.LTGRAY);

                            set6 = new LineDataSet(values6,"底板II");
                            set6.setColor(Color.BLACK);

                            set7 = new LineDataSet(values7,"底板II");
                            set7.setColor(Color.DKGRAY);




                            //格式化显示数据
                            final DecimalFormat mFormat = new DecimalFormat("###,###,##0");
                            set1.setValueFormatter(new IValueFormatter() {
                                @Override
                                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                                    return mFormat.format(value);
                                }
                            });
                            if (Utils.getSDKInt() >= 18) {
                                // fill drawable only supported on api level 18 and above
                                Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.fade_red);
                                set1.setFillDrawable(drawable);//设置范围背景填充
                            } else {
                                set1.setFillColor(Color.BLACK);
                            }


                            //获取此图表的x轴
                            XAxis xAxis1 = lineChart.getXAxis();
                            xAxis1.setEnabled(true);//设置轴启用或禁用 如果禁用以下的设置全部不生效
                            xAxis1.setDrawAxisLine(true);//是否绘制轴线
                            xAxis1.setDrawGridLines(true);//设置x轴上每个点对应的线
                            xAxis1.setDrawLabels(true);//绘制标签  指x轴上的对应数值
                            xAxis1.setPosition(XAxis.XAxisPosition.BOTTOM);//设置x轴的显示位置

                            xAxis1.setValueFormatter((value, axis) -> values1.get((int)      value).getData()+"");
                            //xAxis.setTextSize(20f);//设置字体
                            //xAxis.setTextColor(Color.BLACK);//设置字体颜色
                            //设置竖线的显示样式为虚线
                            //lineLength控制虚线段的长度
                            //spaceLength控制线之间的空间
                            xAxis1.enableGridDashedLine(10f, 10f, 0f);
                            //        xAxis.setAxisMinimum(0f);//设置x轴的最小值
                            //        xAxis.setAxisMaximum(10f);//设置最大值
                            xAxis1.setAvoidFirstLastClipping(true);//图表将避免第一个和最后一个标签条目被减掉在图表或屏幕的边缘
                            xAxis1.setLabelRotationAngle(10f);//设置x轴标签的旋转角度
                            //        设置x轴显示标签数量  还有一个重载方法第二个参数为布尔值强制设置数量 如果启用会导致绘制点出现偏差
                            //        xAxis.setLabelCount(10);
                            //        xAxis.setTextColor(Color.BLUE);//设置轴标签的颜色
                            //        xAxis.setTextSize(24f);//设置轴标签的大小
                            //        xAxis.setGridLineWidth(10f);//设置竖线大小
                            //        xAxis.setGridColor(Color.RED);//设置竖线颜色
                            //        xAxis.setAxisLineColor(Color.GREEN);//设置x轴线颜色
                            //        xAxis.setAxisLineWidth(5f);//设置x轴线宽度
                            //        xAxis.setValueFormatter();//格式化x轴标签显示字符


                            //获取右边的轴线
                            YAxis rightAxis=lineChart.getAxisRight();
                            //设置图表右边的y轴禁用
                            rightAxis.setEnabled(false);
                            //获取左边的轴线
                            YAxis leftAxis = lineChart.getAxisLeft();
                            //设置网格线为虚线效果
                            leftAxis.enableGridDashedLine(10f, 10f, 0f);

                            //添加限制线
                            LimitLine ll =new LimitLine(80f,"警戒线");
                            ll.setLineColor(Color.RED);
                            ll.setLineWidth(4f);
                            ll.setTextColor(Color.BLACK);
                            ll.setTextSize(12f);
                            leftAxis.addLimitLine(ll);
                            //是否绘制0所在的网格线
                            leftAxis.setDrawZeroLine(false);

                            lineChart.setTouchEnabled(true); // 设置是否可以触摸
                            lineChart.setDragEnabled(true);// 是否可以拖拽
                            lineChart.setScaleEnabled(false);// 是否可以缩放 x和y轴, 默认是true
                            lineChart.setScaleXEnabled(true); //是否可以缩放 仅x轴
                            lineChart.setScaleYEnabled(true); //是否可以缩放 仅y轴
                            lineChart.setPinchZoom(true);  //设置x轴和y轴能否同时缩放。默认是否
                            lineChart.setDoubleTapToZoomEnabled(true);//设置是否可以通过双击屏幕放大图表。默认是true
                            lineChart.setHighlightPerDragEnabled(true);//能否拖拽高亮线(数据点与坐标的提示线)，默认是true
                            lineChart.setDragDecelerationEnabled(true);//拖拽滚动时，手放开是否会持续滚动，默认是true（false是拖到哪是哪，true拖拽之后还会有缓冲）
                            lineChart.setDragDecelerationFrictionCoef(0.99f);//与上面那个属性配合，持续滚动时的速度快慢，[0,1) 0代表立即停止。

                            Legend l = lineChart.getLegend();//图例

                            l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_INSIDE);//设置图例的位置
                            l.setTextSize(10f);//设置文字大小
                            l.setForm(Legend.LegendForm.CIRCLE);//正方形，圆形或线
                            l.setFormSize(10f); // 设置Form的大小
                            l.setWordWrapEnabled(true);//是否支持自动换行 目前只支持BelowChartLeft, BelowChartRight, BelowChartCenter
                            l.setFormLineWidth(10f);//设置Form的宽度

                            //自定义的MarkerView对象
                            MyMarkerView mv = new MyMarkerView(getApplicationContext(), R.layout.custom_marker_view);
                            mv.setChartView(lineChart);
                            lineChart.setMarker(mv);




                            //保存LineDataSet集合
                            ArrayList<ILineDataSet> dataSets1 = new ArrayList<>();
                            dataSets1.add(set1); // add the datasets
                            dataSets1.add(set2);
                            dataSets1.add(set3);
                            dataSets1.add(set4);
                            dataSets1.add(set5);
                            dataSets1.add(set6);
                            dataSets1.add(set7);
                            //创建LineData对象 属于LineChart折线图的数据集合
                            LineData data1 = new LineData(dataSets1);
                            // 添加到图表中
                            lineChart.setData(data1);
                            //绘制图表
                            lineChart.invalidate();
                        }


                    }else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Pie Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap();


                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }




    private void generatePieData() {
        // Tag used to cancel the request
        String tag_string_req = "req_pie";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_PIECHART, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "pie Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        PieChart pieChart = (PieChart) findViewById(R.id.pieChart);

                        int dbfp = jObj.getInt("底板分拼");
                        int db1 = jObj.getInt("底板I");
                        int db2 = jObj.getInt("底板II");
                        int cw = jObj.getInt("侧围");
                        int zongp = jObj.getInt("总拼");
                        int zhuangp = jObj.getInt("装配");
                        int bj = jObj.getInt("报交");

//                        int dbfp = jObj.getInt("\\u5e95\\u677f\\u5206\\u62fc");
//                        int db1 = jObj.getInt("\\u5e95\\u677fI");
//                        int db2 = jObj.getInt("\\u5e95\\u677fII");
//                        int cw = jObj.getInt("\\u4fa7\\u56f4");
//                        int zongp = jObj.getInt("\\u603b\\u62fc");
//                        int zhuangp = jObj.getInt("\\u88c5\\u914d");
//                        int bj = jObj.getInt("\\u62a5\\u4ea4");

                        Log.d(TAG, "onResponse: "+dbfp+bj);

                        dbfp = dbfp*100/(dbfp+db1+db2+cw+zongp+zhuangp+bj);
                        db1 = db1*100/(dbfp+db1+db2+cw+zongp+zhuangp+bj);
                        db2 = db2*100/(dbfp+db1+db2+cw+zongp+zhuangp+bj);
                        cw = cw*100/(dbfp+db1+db2+cw+zongp+zhuangp+bj);
                        zongp = zongp*100/(dbfp+db1+db2+cw+zongp+zhuangp+bj);
                        zhuangp = zhuangp*100/(dbfp+db1+db2+cw+zongp+zhuangp+bj);
                        bj = bj*100/(dbfp+db1+db2+cw+zongp+zhuangp+bj);


                        entries.add(new PieEntry( (float) dbfp, "底板分拼"));
                        entries.add(new PieEntry( (float) db1, "底板I"));
                        entries.add(new PieEntry( (float) db2, "底板II"));
                        entries.add(new PieEntry( (float) cw, "侧围"));
                        entries.add(new PieEntry( (float) zongp, "总拼"));
                        entries.add(new PieEntry( (float) zhuangp, "装配"));
                        entries.add(new PieEntry( (float) bj, "报交"));

                        Log.d(TAG, "onResponse: "+entries);

                        pieChart.setUsePercentValues(false);

                        pieChart.getDescription().setEnabled(false);
                        pieChart.setExtraOffsets(5, 10, 5, 5);

                        pieChart.setDragDecelerationFrictionCoef(0.95f);


                        //设置piecahrt图表点击Item高亮是否可用
                        pieChart.setHighlightPerTapEnabled(true);

                        pieChart.setDrawEntryLabels(true);
                        //设置pieChart是否只显示饼图上百分比不显示文字（true：下面属性才有效果）
                        //设置pieChart图表文本字体颜色
                        pieChart.setEntryLabelColor(Color.GRAY);
                        pieChart.setEntryLabelTextSize(10f);

                        //绘制中间文字
                        pieChart.setCenterText(generateCenterSpannableText());
                        pieChart.setExtraOffsets(20.f, 0.f, 20.f, 0.f);

                        //旋转
                        pieChart.setRotationEnabled(true);
                        pieChart.setHighlightPerTapEnabled(true);

                        pieChart.setDrawHoleEnabled(false);
                        pieChart.setHoleColor(Color.WHITE);

                        pieChart.setTransparentCircleColor(Color.WHITE);
                        pieChart.setTransparentCircleAlpha(110);

                        pieChart.setHoleRadius(58f);
                        pieChart.setTransparentCircleRadius(61f);

                        pieChart.setDrawCenterText(true);


                        //默认动画
                        pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

                        Legend l = pieChart.getLegend();
                        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
                        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
                        l.setOrientation(Legend.LegendOrientation.VERTICAL);
                        l.setDrawInside(false);
                        l.setEnabled(false);

                        PieDataSet set = new PieDataSet(entries, "Election Results");
                        set.setColors(ColorTemplate.VORDIPLOM_COLORS );

                        //引线位置
                        set.setValueLinePart1OffsetPercentage(80.f);
                        set.setValueLinePart1Length(0.2f);
                        set.setValueLinePart2Length(0.4f);
                        set.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);


                        PieData data = new PieData(set);

                        //值使用百分比格式
                        data.setValueFormatter(new PercentFormatter());

                        //值的字体大小
                        data.setValueTextSize(11f);
                        pieChart.setData(data);

                        pieChart.invalidate(); // refresh
//                        int count = 10;
//
//                        ArrayList<PieEntry> entries1 = new ArrayList<PieEntry>();
//
//                        for(int i = 0; i < count; i++) {
//                            entries1.add(new PieEntry( (float) salary[i], months[i]));
//                        }






                    }else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Pie Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap();


                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }




    //绘制中心文字
    private SpannableString generateCenterSpannableText() {
        SpannableString s = new SpannableString("上月工段问题比例");
        //s.setSpan(new RelativeSizeSpan(1.5f), 0, 14, 0);
        //s.setSpan(new StyleSpan(Typeface.NORMAL), 14, s.length() - 15, 0);
        //s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, s.length() - 15, 0);
        //s.setSpan(new RelativeSizeSpan(.65f), 14, s.length() - 15, 0);
        //s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
        //s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length()-17, s.length(), 0);
        return s;
    }

    private void initsBar(BarChart barChart){


        ArrayList<IBarDataSet> threebardata = addBarData();


        float groupSpace = 0.07f;
        float barSpace = 0.02f;
        float barWidth = 0.29f;
        // (0.02 + 0.29) * 3 + 0.07 = 1.00 -> interval per "group"

        BarData bardata = new BarData(threebardata);
        bardata.setBarWidth(barWidth); // set the width of each bar
        barChart.setData(bardata);
        barChart.groupBars(0.5f, groupSpace, barSpace); // perform the "explicit" grouping
        barChart.invalidate();

        barChart.getLegend().setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT);//设置注解的位置在左上方
        barChart.getLegend().setForm(Legend.LegendForm.CIRCLE);//这是左边显示小图标的形状



        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴的位置
        barChart.getXAxis().setDrawGridLines(false);//不显示网格
        barChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return (int)value + "月" ;
            }
        });

        barChart.getAxisRight().setEnabled(false);//右侧不显示Y轴
        barChart.getAxisLeft().setAxisMinValue(0.0f);//设置Y轴显示最小值，不然0下面会有空隙
        barChart.getAxisLeft().setDrawGridLines(false);//不设置Y轴网格

        bardata.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return entry.getY()+"元";
            }
        });

        Description description_bar = new Description();
        description_bar.setText("BarChart");
        description_bar.setTextColor(Color.RED);
        description_bar.setTextSize(5);
        barChart.setDescription(description_bar);//设置描述
        barChart.animateXY(1000, 2000);//设置动画
    }


    private ArrayList<IBarDataSet> addBarData(){

        List<BarEntry> yVals = new ArrayList<>();//Y轴方向第一组数组
        List<BarEntry> yVals2 = new ArrayList<>();//Y轴方向第二组数组
        List<BarEntry> yVals3 = new ArrayList<>();//Y轴方向第三组数组

        Random random = new Random();


        for (int i = 0; i < 12; i++) {//添加数据源
            yVals.add(new BarEntry( i + 1 , random.nextInt(10000), i));
            yVals2.add(new BarEntry(i + 1 , random.nextInt(10000), i));
            yVals3.add(new BarEntry(i + 1 , random.nextInt(10000), i));

        }

        BarDataSet barDataSet = new BarDataSet(yVals, "小明每月支出");
        barDataSet.setColor(Color.RED);//设置第一组数据颜色

        BarDataSet barDataSet2 = new BarDataSet(yVals2, "小花每月支出");
        barDataSet2.setColor(Color.GREEN);//设置第二组数据颜色

        BarDataSet barDataSet3 = new BarDataSet(yVals3, "小蔡每月支出");
        barDataSet3.setColor(Color.YELLOW);//设置第三组数据颜色

        ArrayList<IBarDataSet> barDataList = new ArrayList<>();//IBarDataSet 接口很关键，是添加多组数据的关键结构，LineChart也是可以采用对应的接口类，也可以添加多组数据
        barDataList.add(barDataSet);
        barDataList.add(barDataSet2);
        barDataList.add(barDataSet3);

        return barDataList;
    }


    @Override
    public void onBackPressed () {
        Intent i = new Intent(Chart.this, User.class);
        startActivity(i);
        finish();
    }
}
