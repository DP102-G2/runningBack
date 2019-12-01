package com.g2.runningback;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.g2.runningback.Common.Common;
import com.g2.runningback.Common.CommonTask;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class SaleFragment extends Fragment {
    DatePickerDialog.OnDateSetListener start_dateListener, end_dateListener;
    private Activity activity;
    private TextView sale_startTime, sale_endTime;
    private static int year, month, day, year2, month2, day2;
    int dayTime = 3600 * 1000 * 24;
    Calendar calendar = Calendar.getInstance();
    int sumSales = 0;
    PieChart pieChart;
    private List<Sale> saleList = new ArrayList<>();
    private CommonTask saleGetAllTask;
    Gson gson;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        gson = Common.getTimeStampGson();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("查詢銷售");
        return inflater.inflate(R.layout.fragment_sale, container, false);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pieChart = view.findViewById(R.id.pieChart);
        sale_startTime = view.findViewById(R.id.sale_start);
        sale_startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        activity,
                        start_dateListener,
                        SaleFragment.year, SaleFragment.month, SaleFragment.day);

                calendar.set(year2, month2, day2);
                datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        sale_endTime = view.findViewById(R.id.sale_end);
        sale_endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        activity,
                        end_dateListener,
                        SaleFragment.year2, SaleFragment.month2, SaleFragment.day2);

                Calendar calendarstart = Calendar.getInstance();
                datePickerDialog.getDatePicker().setMaxDate(calendarstart.getTimeInMillis());
                Calendar calendarend = Calendar.getInstance();
                calendarend.set(year, month, day);
                datePickerDialog.getDatePicker().setMinDate(calendarend.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        start_dateListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int month, int day) {
                SaleFragment.year = year;
                SaleFragment.month = month;
                SaleFragment.day = day;
                updateDisplay();
                saleList = getSaleList();
                sumSales = 0;

                if (saleList.size() > 0) {
                    for (int i = 0; i <= saleList.size() - 1; i++) {
                        sumSales += saleList.get(i).getSumPrice();
                    }
                }
                pieShow();
            }
        };

        end_dateListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year2, int month2, int day2) {
                SaleFragment.year2 = year2;
                SaleFragment.month2 = month2;
                SaleFragment.day2 = day2;
                updateDisplay();
                saleList = getSaleList();
                sumSales = 0;
                if (saleList.size() > 0) {
                    for (int i = 0; i <= saleList.size() - 1; i++) {
                        sumSales += saleList.get(i).getSumPrice();
                    }
                } else {

                }
                pieShow();
            }
        };

        showNow();
        saleList = getSaleList();
        if (saleList != null) {
            if (saleList.size() > 0) {
                for (int i = 0; i <= saleList.size() - 1; i++) {
                    sumSales += saleList.get(i).getSumPrice();
                }
            }
        }
        pieShow();
}

    private void pieShow(){
        /* 設定可否旋轉 */
        pieChart.setRotationEnabled(true);

        /* 設定圓心文字大小 */
        pieChart.setCenterTextSize(20);
        pieChart.setCenterText("銷售總額\n"+sumSales + "元");

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, Highlight highlight) {
                Log.d(TAG, "entry: " + entry.toString() + "; highlight: " + highlight.toString());
                PieEntry pieEntry = (PieEntry) entry;
                String text = pieEntry.getLabel() + "\n" + pieEntry.getValue();
                Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });

        List<PieEntry> pieEntries = salepieList();

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");

        pieDataSet.setValueTextColor(Color.BLUE);
        pieDataSet.setValueTextSize(20);
        pieDataSet.setSliceSpace(2);

        /* 使用官訂顏色範本，顏色不能超過5種，否則官定範本要加顏色 */
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
    }

    private List<PieEntry> salepieList() {
        List<PieEntry> salesEntries = new ArrayList<>();
        String label = "";
        for(int i = 0; i <= saleList.size() - 1; i++){
            int cat_no = saleList.get(i).getCat_no();
            switch (cat_no){
                case 1:
                label = "鞋子";
                break;
                case 2:
                label = "衣服";
                break;
                case 3:
                label = "帽子";
                break;
                case 4:
                label = "襪子";
                break;
            }
            salesEntries.add(new PieEntry(saleList.get(i).getSumPrice(), label));
        }
        return salesEntries;
    }

    private void showNow() {
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) - 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        year2 = calendar.get(Calendar.YEAR);
        month2 = calendar.get(Calendar.MONTH);
        day2 = calendar.get(Calendar.DAY_OF_MONTH);
        updateDisplay();
    }

    private void updateDisplay() {
        sale_startTime.setText(new StringBuilder().append(year).append("-")
                .append(pad(month + 1)).append("-").append(pad(day)));
        sale_endTime.setText(new StringBuilder().append(year2).append("-")
                .append(pad(month2 + 1)).append("-").append(pad(day2)));
    }

    private String pad(int number) {
        if (number >= 10) {
            return String.valueOf(number);
        } else {
            return "0" + number;
        }
    }

    private List<Sale> getSaleList() {
        List<Sale> sales = new ArrayList<>();

        JsonObject jsonObject = new JsonObject();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S Z");
        calendar.set(year, month, day);
        String startDate = df.format(calendar.getTime());
        calendar.set(year2, month2, day2);
        String endDate = df.format(calendar.getTime());

        String url = Common.URL_SERVER + "/SaleServlet";
        jsonObject.addProperty("action", "getAll");
        jsonObject.addProperty("startDate", gson.toJson(startDate));
        jsonObject.addProperty("endDate", gson.toJson(endDate));
        String jsonOut = jsonObject.toString();

        if (Common.networkConnected(activity)) {
            try {
                saleGetAllTask = new CommonTask(url, jsonOut);
                String jsonIn = saleGetAllTask.execute().get();
                Type listType = new TypeToken<List<Sale>>() {
                }.getType();
                Log.d(TAG, "jsonIn: " + jsonIn);
                sales = gson.fromJson(jsonIn, listType);
                Log.d(TAG, "users" + sales);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, "no network connection available");
        }
        return sales;
    }


}
