package app.food.patient_app.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import app.food.patient_app.R;
import app.food.patient_app.model.GetWeeklyPercentageModel;
import app.food.patient_app.model.MoodTrackerBarChartModel;
import app.food.patient_app.util.Constant;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.github.mikephil.charting.utils.ColorTemplate.LIBERTY_COLORS;
import static com.github.mikephil.charting.utils.ColorTemplate.createColors;


public class LineChartFragment extends Fragment {
    View mView;
    /*    AnyChartView anyChartView;
        Cartesian cartesian;
        List<DataEntry> seriesData;*/
    BarChart barChart;
    ArrayList<BarEntry> entries;
    WebView webViewGraph;
    private static final String TAG = "LineChartFragment";

    /* public static final int[] LIBERTY_COLORS = {
             Color.rgb(207, 248, 246), Color.rgb(148, 212, 212), Color.rgb(136, 180, 187),
             Color.rgb(118, 174, 175), Color.rgb(85, 121, 166)
     };*/
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_chart_common, container, false);
        Constant.setSession(getActivity());
        Constant.progressDialog(getActivity());
        //   initialization();
        weview();
        return mView;
    }

    private void weview() {
        webViewGraph = (WebView) mView.findViewById(R.id.webViewGraph);
        webViewGraph.getSettings().setJavaScriptEnabled(true);
        webViewGraph.getSettings().setLoadWithOverviewMode(true);
        webViewGraph.getSettings().setUseWideViewPort(true);

        webViewGraph.getSettings().setDomStorageEnabled(true);
        webViewGraph.getSettings().setBuiltInZoomControls(true);
        webViewGraph.getSettings().setPluginState(WebSettings.PluginState.ON);
        webViewGraph.getSettings().setBuiltInZoomControls(false);
        webViewGraph.getSettings().setDisplayZoomControls(false);

        webViewGraph.setVerticalScrollBarEnabled(false);
        webViewGraph.setHorizontalScrollBarEnabled(false);
        webViewGraph.setWebViewClient(new HelloWebViewClient());
        Log.e(TAG, "webview Graph URL:- " + Constant.BASE_URL+"linechartmoodmonthly?id=" + Constant.mUserId + "&date=" + Constant.currentDate());
        webViewGraph.loadUrl(Constant.BASE_URL+"linechartmoodmonthly?id=" + Constant.mUserId + "&date=" + Constant.currentDate());

        Constant.progressBar.dismiss();

    }


    private class HelloWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Constant.progressDialog(getActivity());
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            //Page load finished
            super.onPageFinished(view, url);
            Constant.progressBar.dismiss();
        }
    }

    private void barChart() {
        Call<MoodTrackerBarChartModel> modelCall = Constant.apiService.getMoodChart(Constant.mUserId);
        modelCall.enqueue(new Callback<MoodTrackerBarChartModel>() {
            @Override
            public void onResponse(Call<MoodTrackerBarChartModel> call, Response<MoodTrackerBarChartModel> response) {
                entries.add(new BarEntry(response.body().getRad_mood(), 0));
                entries.add(new BarEntry(response.body().getGood_mood(), 1));
                entries.add(new BarEntry(response.body().getMeh_mood(), 2));
                entries.add(new BarEntry(response.body().getBad_mood(), 3));
                entries.add(new BarEntry(response.body().getAwful_mood(), 4));

                barchartDraw();
            }

            @Override
            public void onFailure(Call<MoodTrackerBarChartModel> call, Throwable t) {

            }
        });


    }

    private void barchartDraw() {
        BarDataSet bardataset = new BarDataSet(entries, "Cells");

        bardataset.calcMinMax(0, 100);
        barChart.setMinimumHeight(0);
        ArrayList<String> labels = new ArrayList<String>();
        labels.add("rad");
        labels.add("good");
        labels.add("meh");
        labels.add("bad");
        labels.add("awful");


        BarData data = new BarData(labels, bardataset);

        barChart.setData(data); // set the data and list of lables into chart

        barChart.setDescription("");  // set the description

        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);

        barChart.animateY(3500);
    }

   /* private void initialization() {
        barChart = (BarChart) mView.findViewById(R.id.barchart);

        entries = new ArrayList<>();
        anyChartView = mView.findViewById(R.id.any_chart_view);
        anyChartView.setProgressBar(mView.findViewById(R.id.progress_bar));
        cartesian = AnyChart.line();
        cartesian.animation(true);
        cartesian.padding(0d, 0d, 10d, 0d);
        cartesian.crosshair().enabled(true);
        cartesian.crosshair()
                .yLabel(false)
                // TODO ystroke
                .yStroke((Stroke) null, null, null, (String) null, (String) null);
        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);

        // cartesian.title("Trend of Sales of the Most Popular Products of ACME Corp.");

        //cartesian.yAxis(0).title("Number of Bottles Sold (thousands)");
        //   cartesian.xAxis(0).labels().padding(10d, 10d, 10d, 10d);
        getWeeklyDataCallAPI();
        // APIDataSetInGraph();
        barChart();
    }

    private void getWeeklyDataCallAPI() {
        seriesData = new ArrayList<>();
        Call<GetWeeklyPercentageModel> modelCall = Constant.apiService.getWeeklyData(Constant.mUserId, Constant.currentDate());
        modelCall.enqueue(new Callback<GetWeeklyPercentageModel>() {
            @Override
            public void onResponse(Call<GetWeeklyPercentageModel> call, Response<GetWeeklyPercentageModel> response) {
                List<GetWeeklyPercentageModel.ResultBean> mResponse = response.body().getResult();
                for (int i = 0; i < response.body().getResult().size(); i++) {
                    seriesData.add(new CustomDataEntry(mResponse.get(i).getDate().replace("2019-", ""), mResponse.get(i).getCallduration(), mResponse.get(i).getSocialtime(), mResponse.get(i).getWorktime()));

                }
                APIDataSetInGraph();

            }

            @Override
            public void onFailure(Call<GetWeeklyPercentageModel> call, Throwable t) {

            }
        });
    }

    private void APIDataSetInGraph() {
        Log.e(TAG, "APIDataSetInGraph: ");
        Set set = Set.instantiate();
        set.data(seriesData);
        Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");
        Mapping series2Mapping = set.mapAs("{ x: 'x', value: 'value2' }");
        Mapping series3Mapping = set.mapAs("{ x: 'x', value: 'value3' }");
        Mapping series4Mapping = set.mapAs("{ x: 'x', value: 'value4' }");

        Line series1 = cartesian.line(series1Mapping);
        series1.name("Call");
        series1.hovered().markers().enabled(true);
        series1.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series1.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);

        Line series2 = cartesian.line(series2Mapping);
        series2.name("Social App");
        series2.hovered().markers().enabled(true);
        series2.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series2.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);

        Line series3 = cartesian.line(series3Mapping);
        series3.name("Working");
        series3.hovered().markers().enabled(true);
        series3.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series3.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);
       *//* Line series4 = cartesian.line(series4Mapping);
        series4.name("Testing");
        series4.hovered().markers().enabled(true);
        series4.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series4.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);
*//*
        cartesian.legend().enabled(true);
        cartesian.legend().fontSize(13d);
        cartesian.legend().padding(0d, 0d, 10d, 0d);

        anyChartView.setChart(cartesian);
    }

    private class CustomDataEntry extends ValueDataEntry {

        CustomDataEntry(String x, Number value, Number value2, Number value3) {
            super(x, value);
            setValue("value2", value2);
            setValue("value3", value3);
            //   setValue("value4", value4);
        }

    }*/

}
